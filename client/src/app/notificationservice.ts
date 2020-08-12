/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="notificationservice.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Injectable} from '@angular/core';
import {DataService} from './dataservice';
import {Observable} from 'rxjs';
import {ConfigService} from './configservice';
import {Subject} from 'rxjs/internal/Subject';
import {Observer} from 'rxjs/internal/types';
import {map} from 'rxjs/operators';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';

@Injectable()
export class NotificationService {
  private ipServer;
  private subjectPermission: Subject<MessageEvent>;
  private subjectSession: Subject<MessageEvent>;
  private wsPermission: WebSocket;
  private wsSession: WebSocketSubject<any>;

  constructor(private configService: ConfigService) {
    this.ipServer = this.configService.getConfiguration().ipServer;
  }

  public connectPermission(url): Subject<MessageEvent> {
    if (!this.subjectPermission) {
      this.wsPermission = new WebSocket(url);
      this.subjectPermission = this.create(this.wsPermission);
    }
    return this.subjectPermission;
  }

  public connectSession(url): WebSocketSubject<any> {
    if (!this.wsSession) {
      this.wsSession = webSocket(url);
      return this.wsSession;
    }
    return this.wsSession;
  }

  private create(ws: WebSocket): Subject<MessageEvent> {

    let observable = Observable.create((obs: Observer<MessageEvent>) => {
      ws.onmessage = obs.next.bind(obs);
      ws.onerror = obs.error.bind(obs);
      ws.onclose = obs.complete.bind(obs);
      return ws.close.bind(ws);
    });
    let observer = {
      next: (data: Object) => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(JSON.stringify(data));
        }
      }
    };
    return Subject.create(observer, observable);
  }

  listenToTheSocketPermission(): Subject<Permission> {
    return <Subject<Permission>>this.connectPermission('ws://' + this.ipServer + ':9098/pushPermission').pipe(map(
      (response: MessageEvent): Permission => {
        const tokenLogged = JSON.parse(response.data);
        if (tokenLogged.userId === sessionStorage.getItem('loggedUserId')) {
          const tokenPermission = new Permission(tokenLogged.userProfile, tokenLogged.read, tokenLogged.update, tokenLogged.create, tokenLogged.view);
          //   this.dataService.setAuthzToken(JSON.stringify(tokenPermission));
          return tokenPermission;
        }
        ;
      }
    ));
  }

  listenToTheSocketSession(): Observable<any> {
    return this.connectSession('ws://' + this.ipServer + ':9095/pushSession').asObservable();
  }

  closeSocketPermission() {
    this.wsPermission.close();
  }

  closeSocketSession() {
    this.wsSession.unsubscribe();
  }
}

export class Permission {

  constructor(
    public profile: string,
    public read: string,
    public update: string,
    public create: string,
    public view: string) {
  }
}


