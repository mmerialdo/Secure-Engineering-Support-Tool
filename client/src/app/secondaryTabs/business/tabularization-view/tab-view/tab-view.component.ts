/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="tab-view.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, Input, OnChanges, OnInit} from '@angular/core';

@Component({
  selector: 'app-tab-view',
  templateUrl: './tab-view.component.html'
})
export class TabViewComponent {
  @Input()
  public serverAsset: any;

  openTabs = [false, false, false, false, false];

  onTabOpen(e) {

    // I had to implement this strange logic in order to use ngOnchanges in the children components
    const clone = [...this.openTabs];
    clone[e.index] = true;
    this.openTabs = clone;

  }

  onTabClosed(e) {
    // I had to implement this strange logic in order to use ngOnchanges in the children components
    const clone = [...this.openTabs];
    clone[e.index] = false;
    this.openTabs = clone;
  }
}



