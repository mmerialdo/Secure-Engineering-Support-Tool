/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="control-messages.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import { Component, Input } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import {ValidationService} from './validationservice';

@Component({
  selector: 'control-messages',
  template: `<div *ngIf="errorMessage !== null" class="message">{{ errorMessage }}</div>`,
  styles: ['.message{ color: red; }']
})
export class ControlMessagesComponent {
  @Input() control: FormControl;
  constructor() { }

  get errorMessage() {
    for (const propertyName in this.control.errors) {
      if (this.control.errors.hasOwnProperty(propertyName) && this.control.touched) {
        return ValidationService.getValidatorErrorMessage(propertyName, this.control.errors[propertyName]);
      }
    }

    return null;
  }
}
