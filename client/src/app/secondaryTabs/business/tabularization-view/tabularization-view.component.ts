/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="tabularization-view.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, Input, OnChanges, OnDestroy, OnInit} from '@angular/core';
import {DataService} from '../../../dataservice';
import {select, Store} from '@ngrx/store';
import {
  clearView,
  storeServerAsset,
  switchToGraphView
} from '../../../shared/store/actions/assets.actions';
import {fetchServerAsset, selectValid} from '../../../shared/store/reducers/assets.reducer';
import {Observable} from 'rxjs';
import {Subscription} from 'rxjs/internal/Subscription';
import {Message} from 'primeng/primeng';
import {take} from 'rxjs/operators';
import {LockService} from '../../../shared/service/lock-service';
import {ModelObject} from '../../../model-object';

@Component({
  selector: 'app-tabularization-view',
  templateUrl: './tabularization-view.component.html',
  styleUrls: ['./tabularization-view.component.scss']
})
export class TabularizationViewComponent implements OnInit, OnDestroy {

  @Input() enableSaveButton: boolean;

  serverAsset: any;
  nodes: any;
  edges: any;
  organizations: any;
  blocked = false;
  loaded = false;
  // to show messages
  msgsAsset: Message[] = [];
  blockedMessage = false;
  isValid$: Observable<boolean>;

  private subscriptions: Subscription[] = [];

  constructor(private dataService: DataService,
              private lockService: LockService,
              private store: Store<any>) {
  }

  ngOnInit(): void {
    this.isValid$ = this.store.pipe(select(selectValid));
    this.blocked = true;
    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadAsset(JSON.stringify(a)).subscribe((response: ModelObject) => {

        this.serverAsset = JSON.parse(response.jsonModel);
        console.log('Load Asset Model');
        console.log(this.serverAsset);
        this.loaded = true;
        this.store.dispatch(storeServerAsset(this.serverAsset));

        this.lockService.addLock(response.objectIdentifier, response.lockedBy);
        this.blocked = false;

      }, err => {
        this.blocked = false;
        console.log(err);
        throw err;
      }));
  }


  changeView(): void {
    this.store.dispatch(switchToGraphView());
  }

  saveAssetModel(): void {

    this.blocked = true;
    this.store.pipe(select(fetchServerAsset)).pipe(take(1)).subscribe(data => {
      this.serverAsset = data;
    });

    const finalJSON = ({
      'creationTime': this.serverAsset.creationTime,
      'updateTime': this.serverAsset.updateTime,
      'identifier': this.serverAsset.identifier,
      'edges': this.serverAsset.edges,
      'nodes': this.serverAsset.nodes,
      'graphJson': this.serverAsset.graphJson,
      'objType': 'AssetModel'
    });

    console.log(finalJSON);
    const completeList = {
      'jsonModel': (JSON.stringify(finalJSON, null, 2)),
      'objectIdentifier': this.serverAsset.identifier
    };

    this.dataService.updateAsset(JSON.stringify(completeList, null, 2)).subscribe(result => {

      this.showSuccess();
      this.blocked = false;
    }, err => {
      this.blocked = false;
      throw err;
    });
  }


  showSuccess() {
    this.blockedMessage = true;
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'success', summary: 'Save Successful!', detail: 'Asset Model Saved'});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  clearMessage() {
    this.msgsAsset = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  isLockedByCurrentUser() {
    return this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername');
  }

  ngOnDestroy() {
    console.log('ngOnDestroy');
    console.log(this.serverAsset);

    this.lockService.removeLock(this.serverAsset.identifier);

    this.subscriptions.forEach(s => s.unsubscribe());

    this.store.dispatch(clearView());
  }
}
