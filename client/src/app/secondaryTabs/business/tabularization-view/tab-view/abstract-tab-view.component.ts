/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="abstract-tab-view.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Store} from '@ngrx/store';
import {validateFalse, validateTrue} from '../../../../shared/store/actions/assets.actions';
import {MessageService} from 'primeng/api';

export abstract class AbstractTabViewComponent {

  public cols: Array<{ field: string, header: string }>;
  public dataRows: Array<any> = [];
  protected model: any;

  public disableFlag = false;
  protected store: Store<any>;
  protected messageService: MessageService;

  constructor(field: string, header: string, store: Store<any>, messageService: MessageService) {
    this.store = store;
    this.messageService = messageService;
    this.cols = [{field: field, header: header}];
  }

  protected resetTable(): void {
    this.dataRows = [];
    this.cols.length = 1;
  }

  protected setColumns(dataColumns: any[]): void {
    dataColumns.forEach(data => {
      this.cols.push({field: data.identifier, header: data.name});
    });
  }

  protected setRows(rowMapper: any[], objectsMap: Map<any, any[]>): void {
    rowMapper.forEach(row => {
      const dataObj = this.convertToObject(this.cols);
      // this.model = dataObj;
      if (this.cols[0]) {
        dataObj[this.cols[0].field] = row.name;
      }
      objectsMap.get(row).forEach(parent => {
        if (parent) {
          dataObj[parent.identifier] = true;
        }
      });
      dataObj.id = row.identifier;
      this.dataRows.push(dataObj);
    });
    this.model = this.convertToObject(this.cols);
  }

  protected setSimpleRows(rowMapper: any[]): void {
    rowMapper.forEach(row => {
      const dataObj = this.convertToObject(this.cols);
      // this.model = dataObj;
      if (this.cols[0]) {
        dataObj[this.cols[0].field] = row.name;
      }
      dataObj.id = row.identifier;
      this.dataRows.push(dataObj);
    });
    this.model = this.convertToObject(this.cols);
  }

  protected getModelLabel(identifier: string): string {
    return this.cols.filter(el => el.field === identifier)[0].header;
  }

  protected convertToObject(cols: Array<{ field: string, header: string }>): any {
    return cols.reduce((o, key) => ({...o, [key.field]: false}), {});
  }

  disableCheckBox() {
    this.disableFlag = true;
    setTimeout(() => {
      this.disableFlag = false;
    }, 2000);
  }

  clear() {
    this.cols = [];
    this.dataRows = [];
    this.model = {};
  }

  //Checking if all the nodes except Organizations have a parent
  validate(serverAsset: any) {
    // const nodeCount = serverAsset.nodes.filter(n => n.nodeType !== 'Organization').length;
    const nodesOrphan = serverAsset.nodes.filter(n => n.nodeType !== 'Organization')
      .filter(node => node.parents.length === 0);

    if (nodesOrphan.length > 0) {
      this.store.dispatch(validateFalse());

      let msg = '';
      nodesOrphan.forEach(node => {
        msg = msg.concat(node.name).concat('\n');
      });
      this.messageService.add({key: 'linksMissing',severity: 'warn', summary: 'Node links', detail: 'Node links missing for : ' + msg});
    } else {
      this.store.dispatch(validateTrue());
    }
  }
}
