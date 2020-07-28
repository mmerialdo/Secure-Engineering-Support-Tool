/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="threatment.component.ts"
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
import {TreeNode, MenuItem, SelectItem, ConfirmationService, Tree, MessageService} from 'primeng/primeng';
import {DataService} from '../../dataservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';
import {PermissionType} from '../../permission-type.class';
import {Permission} from '../../permission.class';
import {LockService} from '../../shared/service/lock-service';
import {ModelObject} from '../../model-object';


@Component({
  selector: 'app-treatment',
  templateUrl: './treatment.component.html',
  styleUrls: ['./treatment.component.scss'],
  providers: [ConfirmationService]
})
export class TreatmentComponent implements OnInit, OnDestroy {

  // treatment
  public treatmentModel: any;

  public showTable = false;

  public secondTreatmentModel;

  public countCoveredScenario = 0;
  public hideCoveredScenario = true;
  public countValue = 0;
  public hideValue = true;
  public countSeriouDetails = 0;
  public showSerious = true;
  public countRiskDetails = 0;
  public showRisk = true;

  public displayTreatInfo = false;

  public treatitems: MenuItem[];

  // details main Table

  public detailsInfo = false;
  public safeguardCode;
  public safeguardTitle;
  public safeguardComment;
  public safeguardAddInfo;

  // to show the spinner

  public blocked = false;
  public blockedMessage = false;
  public blockeCalculate = false;

  // messages

  msgsTreatment: Message[] = [];

  // it says if the data are changed
  public isChanged = false;


  public permission;
  public enableSaveButton = true;

  // audit
  public org = true;
  public orgTable = true;
  public results = [];

  public rv1List: SelectItem[];
  public rv2List: SelectItem[];
  public rv1DetailsList: SelectItem[];
  public rv2DetailsList: SelectItem[];

  // to map modified questionnaire
  public mapQuestionnaires = [];

  // to show more information
  public question;
  public code;

  private treatmentId;

  private subscriptions: Subscription[] = [];

  constructor(private confirmationService: ConfirmationService,
              private dataService: DataService,
              private lockService: LockService) {

    this.rv1List = [];

    this.rv1List.push({label: '', value: ''});
    this.rv1List.push({label: '1', value: '1'});
    this.rv1List.push({label: '2', value: '2'});
    this.rv1List.push({label: '3', value: '3'});
    this.rv1List.push({label: '4', value: '4'});

    this.rv2List = [];
    this.rv2List.push({label: '', value: ''});
    this.rv2List.push({label: '1', value: '1'});

    this.rv1DetailsList = [];

    this.rv1DetailsList.push({label: '', value: ''});
    this.rv1DetailsList.push({label: '1', value: '1'});
    this.rv1DetailsList.push({label: '2', value: '2'});
    this.rv1DetailsList.push({label: '3', value: '3'});
    this.rv1DetailsList.push({label: '4', value: '4'});

    this.rv2DetailsList = [];
    this.rv2DetailsList.push({label: '', value: ''});
    this.rv2DetailsList.push({label: '1', value: '1'});


    this.treatitems = [
      {
        label: 'Calculate & Save', icon: 'fa fa-fw fa-floppy-o', command: (event => {

          this.editTreatment(this.treatmentModel);

        })
      },
      {
        label: 'Calculate', icon: 'fa fa-fw fa-calculator', command: (event) => {


          this.calculateTreatment(this.treatmentModel);
        }
      }


    ];
  }

  ngOnInit() {

    this.getTreatmentModel();
  }

  open(event) {

    if (event.node.data.type === 'PrimaryAssetCategory') {

      this.countCoveredScenario++;
      this.hideUnhideCoveredScenario();
    }

    if (event.node.data.type === 'SafeguardScope') {

      this.countValue++;
      this.hideUnhideValue();
    }

  }

  close(event) {

    // console.log(event.node.data.type)
    if (event.node.data.type === 'PrimaryAssetCategory') {


      this.countCoveredScenario--;
      this.hideUnhideCoveredScenario();
    }

    if (event.node.data.type === 'SafeguardScope') {

      this.countValue--;
      this.hideUnhideValue();
    }

  }

  hideUnhideCoveredScenario() {
    if (this.countCoveredScenario === 0) {

      this.hideCoveredScenario = true;

    } else {

      this.hideCoveredScenario = false;

    }
  }

  hideUnhideValue() {
    if (this.countValue === 0) {

      this.hideValue = true;
    } else {

      this.hideValue = false;

    }
  }

  openDetails(event) {

    // console.log(event)

    if (event.node.data.type === 'Asset') {

      this.countSeriouDetails++;
      this.hideUnhideSerious();

    }

    if (event.node.data.type === 'RiskScenario') {

      this.countRiskDetails++;
      this.hideUnhideRisk();


    }

  }

  hideUnhideRisk() {
    if (this.countSeriouDetails === 0) {

      this.showRisk = true;

    } else {

      this.showRisk = false;

    }
  }

  hideUnhideSerious() {
    if (this.countSeriouDetails === 0) {

      this.showSerious = true;

    } else {

      this.showSerious = false;

    }
  }


  closeDetails(event) {


    // console.log(event.node.data.type)
    if (event.node.data.type === 'Asset') {


      this.countSeriouDetails--;
      this.hideUnhideSerious();
    }

    if (event.node.data.type === 'RiskScenario') {

      this.countRiskDetails--;
      this.hideUnhideRisk();
    }

  }

  setPermission(s) {
    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.enableSaveButton = (this.permission.create.findIndex(element => element === PermissionType.RiskTreatmentModel) >= 0);
  }

  lostDrop(node) {

    // console.log(this.treatmentModel)
  }

  focusDropDetails(event, node) {

    /*console.log(node)
    console.log("-----")
    console.log(node)
    console.log(event)*/

    if (event.value < node.currentValue) {

      // console.log(node.data.target)

      alert('Wrong value');

      node.data.targetValue = node.currentValue;

      // console.log(node.data.target)

    } else {

      node.targetValue = event.value;
    }

    if (node.type === 'Safeguard') {

      for (const i in this.secondTreatmentModel) {
        this.propagateValueSafeguards(this.secondTreatmentModel[i], node, node.targetValue);
      }
    } else {

      for (const j in this.secondTreatmentModel) {
        this.propagateValueGASF(this.secondTreatmentModel[j], node, node.targetValue);
      }

    }
  }

  focusDrop(event, node) {

    this.isChanged = true;

    if (event.value < node.currentValue) {

      alert('Wrong value');

      node.targetValue = node.currentValue;

    } else {

      node.targetValue = event.value;
    }

    const treatmentModelAray = this.treatmentModel.data;
    if (node.type === 'Safeguard') {

      // console.log(node.data.targetValue)

      for (const i in treatmentModelAray) {
        this.propagateValueSafeguards(treatmentModelAray[i], node, node.targetValue);
      }
    } else {

      for (const j in treatmentModelAray) {
        this.propagateValueGASF(treatmentModelAray[j], node, node.targetValue);
      }

    }

  }

  propagateValueSafeguards(model, node, value: string) {

    // for(let i in this.treatmentModel)
    /*console.log("propagateValueSafeguards")
    console.log(node)
    console.log(model)
    console.log(model.data.safeguardIdentifier)
    console.log(node.data.safeguardIdentifier)*/

    if (model.data.safeguardIdentifier === node.safeguardIdentifier) {

      console.log('setting safeguard value');
      model.data.targetValue = value;

    } else if (model.children !== null) {

      for (const j in model.children) {

        this.propagateValueSafeguards(model.children[j], node, value);
      }

    }

    // console.log(this.treatmentModel)
  }

  propagateValueGASF(model, node, value: string) {

    if (model.data.securityRequirementCatalogueId === node.data.securityRequirementCatalogueId) {


      model.data.targetValue = value;

    } else if (model.children !== null) {

      for (const j in model.children) {

        this.propagateValueGASF(model.children[j], node, value);
      }

    }

  }

  nodeSelect(event) {


  }

  nodeUnselect(event) {


  }

  info(node) {

    // console.log(node);

    console.log(node);
    if (node.type === 'PrimaryAssetCategory') {

      if (!this.isChanged) {

        this.getTreatmentModelDetails(node.primaryAssetCategory);

      } else {


        this.confirmationService.confirm({
          message: 'You have UNSAVED updates. Please note they will be lost if you continue. Do you really want to continue?',
          header: 'Close Confirmation',
          icon: 'fa fa-trash',
          accept: () => {

            this.getTreatmentModelDetails(node.primaryAssetCategory);

          }
        });


      }


    }

    if ((node.type === 'Safeguard')) {

      // this.catalogueIDDetails=node.data.safeguardCatalogueId;
      this.safeguardCode = node.safeguardCatalogueId;
      this.safeguardTitle = node.name;
      this.safeguardComment = node.userDescription;
      this.safeguardAddInfo = node.description;

      this.detailsInfo = true;
    }

    if ((node.type === 'SecurityRequirement')) {

      // this.catalogueIDDetails=node.data.safeguardCatalogueId;
      this.safeguardCode = node.securityRequirementCatalogueId;
      this.safeguardTitle = node.name;
      this.safeguardComment = node.userDescription;
      this.safeguardAddInfo = node.description;

      this.detailsInfo = true;

    }

  }

  // to manage notification messages
  showFailed(s: string) {
    this.msgsTreatment = [];
    this.msgsTreatment.push({severity: 'error', summary: 'Error!', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  showSuccess() {
    this.msgsTreatment = [];
    this.msgsTreatment.push({severity: 'success', summary: 'Save Successful!', detail: 'Treatment Model Saved'});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  clearMessage() {
    this.msgsTreatment = [];
    this.blocked = false;
    this.blockedMessage = false;
  }


  getTreatmentModel() {

    this.blocked = true;
    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadTreatementModel(JSON.stringify(a)).subscribe((response: ModelObject) => {

        console.log('get Treatment');
        console.log(response);
        this.blocked = false;
        this.treatmentModel = <TreeNode[]>JSON.parse(response.jsonModel);
        this.treatmentId = response.objectIdentifier;

        this.showTable = true;

        this.lockService.addLock(this.treatmentId, response.lockedBy);
        this.subscriptions.push(
          this.lockService.lockedBy.subscribe((user) => {
            console.log(user);
            this.treatitems[0].disabled = !this.enableSaveButton ||
              !(this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername'));
          }));
      }, err => {
        this.blocked = false;
        throw err;
      }));

  }

  getTreatmentModelDetails(s) {

    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject'), 'ASSET_CATEGORY': s
      }
    };

    this.subscriptions.push(
      this.dataService.loadTreatementModelDetails(JSON.stringify(a)).subscribe(response => {
        console.log('get Details secondTreatmentModel');
        console.log(response);

        this.secondTreatmentModel = <TreeNode[]>response;

        this.displayTreatInfo = true;
      }));
  }

  editTreatment(s) {
    let treatmentArray = s.data;
    for (const i in treatmentArray) {
      this.removeMeta(treatmentArray[i].children);
    }
    const a = {
      'objectIdentifier': this.treatmentId,
      'jsonModel': (JSON.stringify(s))
    };
    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateTreatment(JSON.stringify(a)).subscribe(response => {
        this.isChanged = false;
        this.blocked = false;
        this.blockedMessage = true;
        this.showSuccess();
        this.getTreatmentModel();
      }, err => {
        this.blocked = false;
        this.blockedMessage = false;
        throw err;
      }));
  }

  calculateTreatment(s) {
    let treatmentArray = s.data;
    for (const i in treatmentArray) {
      this.removeMeta(treatmentArray[i].children);
    }
    const a = {
      'objectIdentifier': this.treatmentId,
      'jsonModel': (JSON.stringify(s))
    };

    this.blockeCalculate = true;
    this.blocked = true;
    this.subscriptions.push(
      this.dataService.calculateTreatment(JSON.stringify(a)).subscribe(response => {

        console.log('Calculate');
        console.log(response);
        this.isChanged = false;
        this.treatmentModel = <TreeNode[]>response;
        this.blocked = false;
        this.blockeCalculate = false;
        // this.getTreatmentModel();
      }, err => {
        this.blocked = false;
        this.blockeCalculate = false;
        throw err;
      }));

  }

  editTreatmentDetail(s) {
    for (const i in s) {
      this.removeMeta(s[i].children);
    }
    const a = {
      'objectIdentifier': this.treatmentId,
      'jsonModel': (JSON.stringify(s))
    };
    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateTreatmentDetail(JSON.stringify(a)).subscribe(response => {
        this.displayTreatInfo = false;
        this.blockedMessage = false;
        this.blocked = false;
        this.showSuccess();
        this.getTreatmentModel();
      }, err => {
        this.displayTreatInfo = false;
        this.blockedMessage = false;
        this.blocked = false;
        throw err;
      }));
  }

  calculateTreatmentDetail(s) {
    for (const i in s) {
      this.removeMeta(s[i].children);
    }
    const a = {
      'objectIdentifier': this.treatmentId,
      'jsonModel': (JSON.stringify(s))
    };

    this.blockeCalculate = true;
    this.blocked = true;
    this.subscriptions.push(
      this.dataService.calculateTreatmentDetail(JSON.stringify(a)).subscribe(response => {
        if (response) {
          this.treatmentModel = <TreeNode[]>response;
        }
        this.displayTreatInfo = false;
        this.blocked = false;
        this.blockeCalculate = false;
      }, err => {
        this.blocked = false;
        this.blockeCalculate = false;
        throw err;
      }));
  }

  removeMeta(obj) {
    for (const prop in obj) {
      if (prop === 'parent') {
        delete obj[prop];
      } else if (typeof obj[prop] === 'object') {
        this.removeMeta(obj[prop]);
      }
    }
  }

  isLockedByCurrentUser() {
    return this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername');
  }

  ngOnDestroy() {
    this.lockService.removeLock(this.treatmentId);
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
