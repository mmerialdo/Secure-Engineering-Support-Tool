/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="add-row.directive.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import { Directive, Input, HostListener } from '@angular/core';
import { Table } from 'primeng/table';

@Directive({
  selector: '[appAddRow]'
})
export class AddRowDirective {

  @Input() table: Table;
  @Input() newRow: any;

  @HostListener('click', ['$event'])
  onClick(event: Event) {

    // Insert a new row
    this.table.value.push(this.newRow);

    // Set the new row in edit mode
    this.table.initRowEdit(this.newRow);

    event.preventDefault();
  }

}
