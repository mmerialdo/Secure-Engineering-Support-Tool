/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="message.service.ts"
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
import { Message } from 'primeng/components/common/api';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  defaultDismissAction = 'Hide';
  defaultErrorDismissAction = 'Close';
  defaultDuration = 5000; // ms

  // to show messages
  msgs: Message[] = [];
  // to block the interactions in the page
  blocked = false;
  blockedMessage = false;

  showInfoMessage(message: string, dismissAction?: string, duration?: number) {
    const dismissActionName = dismissAction || this.defaultDismissAction;
    this.msgs = [];
    this.msgs.push({ severity: 'info', summary: message, detail: 'Info' });

    setTimeout(() => { this.clearMessage(); }, duration);
  }

  clearMessage() {
    this.msgs = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  showErrorMessage(message: string, errorsList?: string[], duration?: number) {
    this.msgs = [];
    this.msgs.push({ severity: 'error', summary: message, detail: 'Error' });

    setTimeout(() => { this.clearMessage(); }, duration);
  }
}
