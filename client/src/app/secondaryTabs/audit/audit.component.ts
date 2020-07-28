/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="audit.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {MenuItem, MessageService, SelectItem} from 'primeng/primeng';
import {TreeNode} from 'primeng/api';
import {DataService} from '../../dataservice';

import {Message} from 'primeng/api';
import {Subscription} from 'rxjs';
import {PermissionType} from '../../permission-type.class';
import {Permission} from '../../permission.class';
import {LockService} from '../../shared/service/lock-service';
import {ModelObject} from '../../model-object';
import {Score} from '../../tabs/taxonomies-management/taxonomiesManagement.model';


@Component({
  selector: 'app-audit',
  templateUrl: './audit.component.html',
  styleUrls: ['./audit.component.scss']
})
export class AuditComponent implements OnInit, OnDestroy {

  private count = 0;
  private hideRow = true;

  public orgTable = true;
  private orgResult = false;

  private tableHeader;
  public audititems: MenuItem[];
  private questionsAnswers: TreeNode[];
  private auditIdentifier;
  private auditList: any;
  private questionnairesNumber;
  private currentQuestionnaire = 0;
  // to map modified questionnaire
  private mapQuestionnaires = [];

  private rv1List: SelectItem[];
  private rv2List: SelectItem[];

  // to show more information
  public displayInfo = false;
  public displayInfoISO = false;
  public question;
  public code;
  public comment;
  public addInf;
  public source;
  public iso_info: ISOControl[];

  // to block the interactions in the page
  public blocked = false;
  public blockedMessage = false;
  // to show messages
  msgs: Message[] = [];
  // to manage the permissions
  private permission: Permission;
  private dataToCheck = [];
  private thereIsAChange = false;

  private isLoading = false;
  private enableSaveButton = true;

  private subscriptions: Subscription[] = [];

  constructor(private dataService: DataService,
              private lockService: LockService) {

    this.audititems = [
      {
        label: 'Save', icon: 'fa fa-fw fa-user-plus', command: (event => {
          this.saveAudit(this.questionsAnswers, true);
        })
      },
      {
        label: 'Show Questions', icon: 'fa fa-fw fa-user-plus', command: (event => {
          this.orgTable = true;
          this.orgResult = false;
        }),
        items: []
      }
    ];
  }

  ngOnInit() {

    this.rv1List = [];
    this.rv1List.push({label: '', value: ''});
    this.rv1List.push({label: '1', value: '1'});

    this.rv2List = [];
    this.rv2List.push({label: '', value: ''});
    this.rv2List.push({label: '1', value: '1'});
    this.rv2List.push({label: '2', value: '2'});
    this.rv2List.push({label: '3', value: '3'});
    this.rv2List.push({label: '4', value: '4'});

    this.loadAuditList();
  }

  setPermission() {

    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.enableSaveButton = (this.permission.create.findIndex(element => element === PermissionType.Audit) >= 0);
  }

  loadAuditList(): void {
    const projectId = {
      'filterMap': {
        'IDENTIFIER': sessionStorage.getItem('idProject'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadAudit(JSON.stringify(projectId)).subscribe(response => {
        this.auditList = response;
        this.auditIdentifier = this.auditList.identifier;
        this.setPermission();

        if (this.auditList.sestQuestionnaireModel) {
          this.questionnairesNumber = this.auditList.sestQuestionnaireModel.length - 1;

          let x = 0;
          while (x <= this.questionnairesNumber) {
            this.mapQuestionnaires.push({'data': null, 'children': null});
            (<MenuItem[]>this.audititems[1].items).push({
              label: this.clearLabel(this.auditList.sestQuestionnaireModel[x].ix + '-'
                + this.auditList.sestQuestionnaireModel[x].type), /*icon: 'fa-mail-forward',*/command: (event) => {

                this.changeQuestionnaire(event);

              }
            });
            x++;
          }
          const identifierDefault = this.auditList.sestQuestionnaireModel[0].identifier;
          this.loadQuestionnaireForTree(identifierDefault);
          this.lockService.addLock(this.auditIdentifier, response.lockedBy);
          this.subscriptions.push(
            this.lockService.lockedBy.subscribe((user) => {
              this.audititems[0].disabled = !this.enableSaveButton ||
                !(this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername'));
            }));
        }
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  clearLabel(questionnaireName: string): string {
    return questionnaireName.replace('MEHARI_', '');
  }

  loadQuestionnaireForTree(s): void {

    const a = {
      'filterMap': {
        'IDENTIFIER': s,
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.loadQuestionnaireForTree(JSON.stringify(a)).subscribe((response: ModelObject) => {
        this.mapQuestionnaires[this.currentQuestionnaire] = <TreeNode[]>JSON.parse(response.jsonModel);
        this.mapQuestionnaires[this.currentQuestionnaire].data.identifier = s;
        this.mapQuestionnaires[this.currentQuestionnaire].children = this.createQuestionsArray(<TreeNode[]>JSON.parse(response.jsonModel));
        this.blocked = false;
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  changeQuestionnaire(event) {

    let index = -1;
    for (const i in this.audititems[1].items) {
      if (event.item.textContent === (<MenuItem>this.audititems[1].items[i]).label) {

        index = Number(i);
      }
    }
    this.mapQuestionnaires[this.currentQuestionnaire].children = this.questionsAnswers;
    this.currentQuestionnaire = index;
    if (this.mapQuestionnaires[this.currentQuestionnaire].data === null) {

      const a = this.auditList.sestQuestionnaireModel[this.currentQuestionnaire].identifier;
      this.questionsAnswers = [];
      this.loadQuestionnaireForTree(a);
    } else {
      this.questionsAnswers = [];
      this.questionsAnswers = this.mapQuestionnaires[this.currentQuestionnaire].children;
    }

    this.orgTable = true;
    this.orgResult = false;
  }

  createQuestionsArray(questionnaire): TreeNode[] {
    for (const i in this.dataToCheck) {
      for (const j in questionnaire.children) {
        this.propagateValue(questionnaire.children[j], this.dataToCheck[i]);
      }
    }
    this.questionsAnswers = questionnaire.children;
    this.tableHeader = questionnaire.data.type;

    return this.questionsAnswers;
  }

  propagateValue(model, rowData) {
    this.audititems[0].disabled = true;
    if (model.data.category === rowData.category) {
      model.data = rowData;
    } else if (model.children != null) {
      for (let j in model.children) {
        this.propagateValue(model.children[j], rowData);
      }
    }

    this.audititems[0].disabled = false;
  }

  rightArrow() {

    if (this.currentQuestionnaire !== this.questionnairesNumber) {
      this.mapQuestionnaires[this.currentQuestionnaire].children = this.questionsAnswers;
      this.currentQuestionnaire = this.currentQuestionnaire + 1;

      this.changeCurrentQuestionnaire();
    }
  }

  leftArrow() {

    if (this.currentQuestionnaire !== 0) {
      this.mapQuestionnaires[this.currentQuestionnaire].children = this.questionsAnswers;
      this.currentQuestionnaire = this.currentQuestionnaire - 1;

      this.changeCurrentQuestionnaire();
    }
  }

  private changeCurrentQuestionnaire() {

    if (this.mapQuestionnaires[this.currentQuestionnaire].data === null) {

      const a = this.auditList.sestQuestionnaireModel[this.currentQuestionnaire].identifier;
      this.questionsAnswers = [];
      this.loadQuestionnaireForTree(a);
    } else {
      this.questionsAnswers = [];
      this.tableHeader = this.mapQuestionnaires[this.currentQuestionnaire].data.type;
      this.questionsAnswers = this.mapQuestionnaires[this.currentQuestionnaire].children;
    }
  }

  hideUnhide() {
    this.hideRow = (this.count === 0);
  }

  saveAudit(array, load) {

    this.thereIsAChange = false;

    if (load) {
      this.mapQuestionnaires[this.currentQuestionnaire].children = array;
    }

    for (const i in this.mapQuestionnaires) {
      this.removeMeta(this.mapQuestionnaires[i].children);
    }

    const data = {'questionnaires': this.mapQuestionnaires};

    const completeAudit = {
      'objectIdentifier': this.auditIdentifier,
      'jsonModel': JSON.stringify(data)
    };

    this.blocked = true;

    this.subscriptions.push(
      this.dataService.editAudit(JSON.stringify(completeAudit)).subscribe(response => {
        const a = this.auditList.sestQuestionnaireModel[this.currentQuestionnaire].identifier;

        this.questionsAnswers = [];
        this.loadQuestionnaireForTree(a);

        this.showSuccess();
      }));
  }

  // to remove cycles for the JSON
  removeMeta(obj) {
    for (let prop in obj) {
      if (prop === 'parent')
        delete obj[prop];
      else if (typeof obj[prop] === 'object')
        this.removeMeta(obj[prop]);
    }
  }

  info(rowData) {
    this.question = rowData.value;
    this.code = rowData.category;
    this.comment = rowData.commentValue;
    this.addInf = undefined;
    if (rowData.description) {
      this.addInf = rowData.description;
      if (rowData.v4) {
        this.addInf = this.addInf.concat('\n\n' + rowData.v4);
      }
    }
    const sourceObj = rowData.v5 ? JSON.parse(rowData.v5) : undefined;
    if (sourceObj) {
      this.source = '';
      if (sourceObj['2009']) {
        Object.keys(sourceObj['2009']).forEach(key =>
          this.source = this.source.concat(sourceObj['2009'][key] + '; '));
      }
      if (sourceObj['2010']) {
        this.source = this.source.concat('\n');
        Object.keys(sourceObj['2010']).forEach(key =>
          this.source = this.source.concat(sourceObj['2010'][key] + '; '));
      }
    } else {
      this.source = undefined;
    }

    this.displayInfo = true;
  }


  infoISO(rowData) {
    this.iso_info = JSON.parse(rowData.iso13_info);
    this.displayInfoISO = true;
  }

  onChangeSelectedGroup(event, rowNode) {
    rowNode.parent.data.v1 = rowNode.node.data.v1;
  }

  focusDrop(event, rowNode) {
    this.thereIsAChange = true;
    if (rowNode && rowNode.parent) {
      this.calculateAndPropagateValue(rowNode.parent);
    }
  }

  private calculateAndPropagateValue(node: any) {
    if (node) {
      const answerValue = this.calculateValue(node);
      const intAnswerValue = Math.round(answerValue).toString();
      if (!node.data.v1 || node.data.v1 != intAnswerValue) {
        node.data.v1 = intAnswerValue;
        if (node.parent.data.v4 && node.parent.data.v4 === node.data.category) {
          node.parent.data.v1 = intAnswerValue;
        }
        this.questionsAnswers = [...this.questionsAnswers];
      }
    }
  }

  private calculateValue(node: TreeNode) {
    let sumYes = 0;
    let sumAll = 0;
    let min = 0;
    let max = 5;
    node.children.forEach(question => {
        const weightQuestion = question.data.weight ? question.data.weight : 0;
        const minQuestion = +question.data.min;
        const maxQuestion = +question.data.max;
        const value = +question.data.v1;
        if (weightQuestion !== 'x') {
          if (value && value === 1) {
            sumYes += +weightQuestion; // sum the weight value of all answers with 1
            if (minQuestion) {
              min = (min > minQuestion) ? min : minQuestion;
            }
          }
          if ((!value || value === 0) && maxQuestion) {
            max = (max < maxQuestion) ? max : maxQuestion;
          }
          sumAll += +weightQuestion; // sum the weight of all questions
        }
      }
    );

    const average = (4 * sumYes) / sumAll;
    // if there is no min and no max return average
    if (!min && max >= 5) {
      return average;
    }

    if (max < min || max < average) {
      return max;
    } else if (min >= average) {
      return min;
    } else if (min < average) {
      return average;
    }
  }

  open() {
    this.count--;
    this.hideUnhide();
  }

  close() {
    this.count++;
    this.hideUnhide();
  }

  showFailed(s: string) {
    this.msgs = [];
    this.msgs.push({severity: 'error', summary: 'Error!', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  showSuccess() {
    this.msgs = [];
    this.msgs.push({severity: 'success', summary: 'Save Successful!', detail: 'Audit Saved'});

    this.blockedMessage = true;
    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  clearMessage() {
    this.msgs = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  isLockedByCurrentUser(): boolean {
    return this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername');
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
    this.lockService.removeLock(this.auditIdentifier);
  }
}

export class GasfSources {

  2009: GasfSource[];
  2010: GasfSource[];
}

export class ISOControl {
  ClauseId: string;
  Clause: string;
  ObjectiveId: string;
  Objective: string;
  ControlId: string;
  Control: string;
  ControlJson: string;
}

export class GasfSource {
  [key: string]: { value: string }
}
