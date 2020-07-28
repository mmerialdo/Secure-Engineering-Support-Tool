/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="scenario.component.ts"
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
import {FormBuilder, Validators} from '@angular/forms';
import {DataService} from '../../dataservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';
import {PermissionType} from '../../permission-type.class';
import {Permission} from '../../permission.class';
import {MessageService} from 'primeng/primeng';
import {LockService} from '../../shared/service/lock-service';
import {ModelObject} from '../../model-object';

@Component({
  selector: 'app-scenario',
  templateUrl: './scenario.component.html',
  styleUrls: ['./scenario.component.scss']
})
export class ScenarioComponent implements OnInit, OnDestroy {

  // models
  public assetModel: any;
  public vulnerabilityModel: any;
  public threatModel: any;
  public riskModel: any;

  // data used for the table
  public scenarioData = [];

  // to show the scenario form
  public displayEditScenario = false;

  // selected Row after double click

  public selectedRowDoubleClick;


  // form attributes

  public assetName;
  public impactScope;
  public scenarioClass;
  public intrinsicImpact;
  public intrinsicLikelihood;
  public intrinsicSeriousness;
  public calculatedImpact;
  public calculatedLikelihood;
  public calculatedSeriousness;
  public assetSecondaryCategory;
  public assetPrimaryCategory;
  public threatEvent;
  public threatPlace;
  public threatTime;
  public threatAccess;
  public threatProcess;
  public threatActor;
  public eventDescription;
  public placeDescription;
  public timeDescription;
  public accessDescription;
  public processDescription;
  public actorDescription;
  // public expertConfinability;
  // public expertImpact;
  // public expertLikelihood;
  // public accept;
  public expertImpactDescription;
  public expertLikelihoodDescription;
  public excluded;
  public sestDescription;
  public userDescription;
  public primaryAssetCategory;
  public requirements = [];
  // to show the requirements names instead of the IDs
  public requirementsList: any;
  public assetDescription;
  public nameVulnerability;
  public catalogueIDVuln;
  public exploitabilityVuln;
  public scoreVuln;
  public scoringTypeVuln;
  public descriptionVuln;
  public nameThreat;
  public catalogueIDThreat;
  public likelihoodThreat;
  public scoreThreat;
  public descriptionThreat;

  public scenarioForm: any;

  private permission: Permission;
  public enableSaveButton = true;

  // to block the interactions in the page
  public blocked = false;
  public blockedMessage = false;

  // Information forms
  public showScopeInfo = false;
  public showClassInfo = false;
  public showScenarioResultInfo = false;
  public showExcludeScenarioInfo = false;
  public showIntrisincImpactInfo = false;
  public showIntrisincLikelihoodInfo = false;
  public showIntrisincSeriousnessInfo = false;
  public showCalculatedImpactInfo = false;
  public showCalculatedLikelihoodInfo = false;
  public showCalculatedSeriousnessInfo = false;
  public showExpertImpactInfo = false;
  public showExpertLikelihoodInfo = false;

  // to show messages
  msgs: Message[] = [];

  private subscriptions: Subscription[] = [];

  constructor(private dataService: DataService,
              private formBuilder: FormBuilder,
              private lockService: LockService) {

    this.scenarioForm = this.formBuilder.group({

      'sceRes': ['', Validators.required],
      'usDesc': ['', Validators.required],
      'conExp': ['', Validators.required],
      'impExp': ['', Validators.required],
      'likExp': ['', Validators.required],
      'usImpDesc': ['', Validators.required],
      'usLikDesc': ['', Validators.required]

    });
  }

  ngOnInit() {

    this.getAsset();
  }

  createScenarioData() {


    if ((this.assetModel.nodes.length > 0) && (this.vulnerabilityModel.vulnerabilities.length > 0) && (this.threatModel.threats.length > 0)) {

      this.setPermission(this.riskModel.identifier);

      for (const i in this.riskModel.scenarios) {


        const assetID = this.riskModel.scenarios[i].assetId;
        const vulnID = this.riskModel.scenarios[i].vulnerabilityId;
        const threatID = this.riskModel.scenarios[i].threatId;


        const indexAsset = this.assetModel.nodes.findIndex((j) => j.identifier === assetID);
        const indexVuln = this.vulnerabilityModel.vulnerabilities.findIndex((k) => k.identifier === vulnID);
        const indexThr = this.threatModel.threats.findIndex((z) => z.identifier === threatID);


        if (indexThr !== -1) {

          let scenDescr;
          const sestDesc = this.riskModel.scenarios[i].description;
          const userDesc = this.riskModel.scenarios[i].userDescription;

          if ((this.riskModel.scenarios[i].userDescription !== null) && (this.riskModel.scenarios[i].userDescription !== undefined) && (this.riskModel.scenarios[i].userDescription !== '')) {

            scenDescr = this.riskModel.scenarios[i].userDescription;
          } else {
            scenDescr = this.riskModel.scenarios[i].description;
          }


          this.scenarioData.push({
            'assetId': this.riskModel.scenarios[i].assetId,
            'assetName': this.assetModel.nodes[indexAsset].name,
            'assetSecondaryCategory': this.assetModel.nodes[indexAsset].category,
            'impactScope': this.riskModel.scenarios[i].impactScope,
            'vulnerabilityCode': this.vulnerabilityModel.vulnerabilities[indexVuln].catalogueId,
            'vulnerabilityDescription': this.vulnerabilityModel.vulnerabilities[indexVuln].description,
            'threatCat': this.threatModel.threats[indexThr].catalogueId,
            'threatEvent': this.threatModel.threats[indexThr].event.description,
            'threatPlace': this.threatModel.threats[indexThr].place.description,
            'threatTime': this.threatModel.threats[indexThr].time.description,
            'threatAccess': this.threatModel.threats[indexThr].access.description,
            'threatProcess': this.threatModel.threats[indexThr].process.description,
            'threatActor': this.threatModel.threats[indexThr].actor.description,
            'scenarioDescription': scenDescr,
            'scenarioClass': this.threatModel.threats[indexThr].threatClass,
            'calculatedImpact': this.riskModel.scenarios[i].calculatedImpact,
            'calculatedLikelihood': this.riskModel.scenarios[i].calculatedLikelihood,
            'calculatedSeriousness': this.riskModel.scenarios[i].calculatedSeriousness,
            'intrinsicImpact': this.riskModel.scenarios[i].intrinsicImpact,
            'intrinsicLikelihood': this.riskModel.scenarios[i].intrinsicLikelihood,
            'intrinsicSeriousness': this.riskModel.scenarios[i].intrinsicSeriousness,
            'accept': this.riskModel.scenarios[i].scenarioResult,
            'excluded': this.riskModel.scenarios[i].excluded,
            'expertImpact': this.riskModel.scenarios[i].expertImpact,
            'expertLikelihood': this.riskModel.scenarios[i].expertLikelihood,
            'expertImpactDescription': this.riskModel.scenarios[i].expertImpactDescription,
            'expertLikelihoodDescription': this.riskModel.scenarios[i].expertLikelihoodDescription,
            'expertConfinability': this.riskModel.scenarios[i].expertConfinability,
            'userDescription': userDesc,
            'sestDescription': sestDesc,
            'assetRequirements': this.assetModel.nodes[indexAsset].relatedRequirementsIds,
            'assetPrimaryCategory': this.assetModel.nodes[indexAsset].primaryCategories[0],
            'assetDescription': this.assetModel.nodes[indexAsset].description,
            'catalogueIDVuln': this.vulnerabilityModel.vulnerabilities[indexVuln].catalogueId,
            'nameVulnerability': this.vulnerabilityModel.vulnerabilities[indexVuln].name,
            'exploitabilityVuln': this.vulnerabilityModel.vulnerabilities[indexVuln].score.exploitability,
            'scoreVuln': this.vulnerabilityModel.vulnerabilities[indexVuln].score.score,
            'scoringTypeVuln': this.vulnerabilityModel.vulnerabilities[indexVuln].score.scoringType,
            'nameThreat': this.threatModel.threats[indexThr].name,
            'catalogueIDThreat': this.threatModel.threats[indexThr].catalogueId,
            'likelihoodThreat': this.threatModel.threats[indexThr].score.likelihood,
            'scoreThreat': this.threatModel.threats[indexThr].score.score,
            'descriptionThreat': this.threatModel.threats[indexThr].description,
            'idScenario': this.riskModel.scenarios[i].identifier,
            'threatId': threatID,
            'vulnerabilityId': vulnID,
            'palliation': this.riskModel.scenarios[i].palliation,
            'prevention': this.riskModel.scenarios[i].prevention,
            'confining': this.riskModel.scenarios[i].confining,
            'dissuasion': this.riskModel.scenarios[i].dissuasion


          });
        }

      }
      this.scenarioData = [...this.scenarioData];
    }
  }

  doubleClick(event, selectedRow) {


    this.selectedRowDoubleClick = selectedRow;

    this.assetName = selectedRow.assetName;
    this.scenarioClass = selectedRow.scenarioClass;
    this.impactScope = selectedRow.impactScope;
    this.intrinsicImpact = selectedRow.intrinsicImpact;
    this.intrinsicLikelihood = selectedRow.intrinsicLikelihood;
    this.intrinsicSeriousness = selectedRow.intrinsicSeriousness;
    this.calculatedImpact = selectedRow.calculatedImpact;
    this.calculatedLikelihood = selectedRow.calculatedLikelihood;
    this.calculatedSeriousness = selectedRow.calculatedSeriousness;
    this.assetPrimaryCategory = selectedRow.assetPrimaryCategory;
    this.assetSecondaryCategory = selectedRow.assetSecondaryCategory;
    this.threatEvent = selectedRow.threatEvent;
    this.threatPlace = selectedRow.threatPlace;
    this.threatTime = selectedRow.threatTime;
    this.threatAccess = selectedRow.threatAccess;
    this.threatProcess = selectedRow.threatProcess;
    this.threatActor = selectedRow.threatActor;
    this.expertImpactDescription = selectedRow.expertImpactDescription;
    this.expertLikelihoodDescription = selectedRow.expertLikelihoodDescription;
    // this.expertConfinability=selectedRow.expertConfinability;
    // this.expertImpact=selectedRow.expertImpact;
    // this.expertLikelihood=selectedRow.expertLikelihood;
    // this.accept=selectedRow.accept;
    this.excluded = selectedRow.excluded;

    this.scenarioForm.controls['sceRes'].setValue(selectedRow.accept);
    this.scenarioForm.controls['conExp'].setValue(selectedRow.expertConfinability);
    this.scenarioForm.controls['impExp'].setValue(selectedRow.expertImpact);
    this.scenarioForm.controls['likExp'].setValue(selectedRow.expertLikelihood);
    this.sestDescription = selectedRow.sestDescription;
    this.userDescription = selectedRow.userDescription;
    this.primaryAssetCategory = selectedRow.primaryAssetCategory;
    this.requirements = selectedRow.relatedRequirementsIds;


    const requirementsName = [];
    for (const req in selectedRow.assetRequirements) {

      const reqInd = this.requirementsList.findIndex(reqI => reqI.identifier === selectedRow.assetRequirements[req]);

      if (reqInd !== -1) {

        requirementsName.push(this.requirementsList[reqInd].id);
      }

    }

    this.requirements = requirementsName;
    this.assetDescription = selectedRow.assetDescription;
    this.catalogueIDVuln = selectedRow.catalogueIDVuln;
    this.nameVulnerability = selectedRow.nameVulnerability;
    this.exploitabilityVuln = selectedRow.exploitabilityVuln;
    this.scoreVuln = selectedRow.scoreVuln;
    this.scoringTypeVuln = selectedRow.scoringTypeVuln;
    this.descriptionVuln = selectedRow.vulnerabilityDescription;
    this.nameThreat = selectedRow.nameThreat,
      this.catalogueIDThreat = selectedRow.catalogueIDThreat;
    this.likelihoodThreat = selectedRow.likelihoodThreat;
    this.scoreThreat = selectedRow.scoreThreat;
    this.descriptionThreat = selectedRow.descriptionThreat;

    const indexScen = this.scenarioData.findIndex((k) => k.idScenario === selectedRow.idScenario);

    this.eventDescription = this.scenarioData[indexScen].threatEvent;
    this.placeDescription = this.scenarioData[indexScen].threatPlace;
    this.timeDescription = this.scenarioData[indexScen].threatTime;
    this.accessDescription = this.scenarioData[indexScen].threatAccess;
    this.processDescription = this.scenarioData[indexScen].threatProcess;
    this.actorDescription = this.scenarioData[indexScen].threatActor;

    this.displayEditScenario = true;
  }


  getAsset() {

    this.blocked = true;
    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadAsset(JSON.stringify(a)).subscribe((response: ModelObject) => {

        this.assetModel = JSON.parse(response.jsonModel);

        this.getVulnerabilityModel();

      }, err => {
        this.blocked = false;
        throw err;
      }));

  }

  getVulnerabilityModel() {

    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadVulnerabilityModel(JSON.stringify(a)).subscribe((response: ModelObject) => {

        this.vulnerabilityModel = JSON.parse(response.jsonModel);
        this.getThreatModel();
      }));

  }

  getRiskModel() {

    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadRiskModel(JSON.stringify(a)).subscribe((response: ModelObject) => {

        this.riskModel = JSON.parse(response.jsonModel);

        const a = {
          'filterMap': {
            'IDENTIFIER': sessionStorage.getItem('sysprojectId'),
            'PROJECT': sessionStorage.getItem('idProject')
          }
        };
        this.getRequirements(JSON.stringify(a));

        this.createScenarioData();
        this.lockService.addLock(response.objectIdentifier, response.lockedBy);
      }));
  }

  getThreatModel() {

    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadThreatModel(JSON.stringify(a)).subscribe((response: ModelObject) => {

        this.threatModel = JSON.parse(response.jsonModel);

        this.getRiskModel();

      }));

  }

  getRequirements(idsystemproject) {

    this.subscriptions.push(
      this.dataService.loadRequirementsById(idsystemproject).subscribe(response => {
        this.requirementsList = response;
        this.blocked = false;
      }));
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

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  clearMessage() {
    this.msgs = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  sendScenario() {
    const newScenario = this.riskModel;

    newScenario.scenarios = [];
    newScenario.scenarios.push({
      'assetId': this.selectedRowDoubleClick.assetId,
      'calculatedImpact': this.selectedRowDoubleClick.calculatedImpact,
      'calculatedLikelihood': this.selectedRowDoubleClick.calculatedLikelihood,
      'calculatedSeriousness': this.selectedRowDoubleClick.calculatedSeriousness,
      'description': this.selectedRowDoubleClick.sestDescription,
      'excluded': this.excluded,
      'expertConfinability': this.scenarioForm.value.conExp,
      'expertImpact': this.scenarioForm.value.impExp,
      'expertLikelihood': this.scenarioForm.value.likExp,
      'family': null,
      'impactScope': this.selectedRowDoubleClick.impactScope,
      'intrinsicImpact': this.selectedRowDoubleClick.intrinsicImpact,
      'intrinsicLikelihood': this.selectedRowDoubleClick.intrinsicLikelihood,
      'intrinsicSeriousness': this.selectedRowDoubleClick.intrinsicSeriousness,
      'scenarioResult': this.scenarioForm.value.sceRes,
      'threatId': this.selectedRowDoubleClick.threatId,
      'expertImpactDescription': this.scenarioForm.value.usImpDesc,
      'expertLikelihoodDescription': this.scenarioForm.value.usLikDesc,
      'userDescription': this.scenarioForm.value.usDesc,
      'vulnerabilityId': this.selectedRowDoubleClick.vulnerabilityId,
      'identifier': this.selectedRowDoubleClick.idScenario
    });

    let completeList = {};

    completeList = {'jsonModel': (JSON.stringify(newScenario, null, 2)), 'objectIdentifier': this.riskModel.identifier};

    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateRiskScenario(JSON.stringify(completeList, null, 2)).subscribe(response => {
        this.blocked = false;
        if ((JSON.parse(JSON.stringify(response))).otherModelsStatus === 'UPDATED') {

          // to avoid duplicates
          this.scenarioData = [];
          this.getAsset();
        } else {

          // to avoid duplicates
          this.scenarioData = [];
          this.getRiskModel();
        }

      }, err => {
        this.blocked = false;
        throw err;
      }));


    this.displayEditScenario = false;

  }

  closeEditFom() {

    this.displayEditScenario = false;
    this.scenarioForm.reset();
  }

  setPermission(s) {

    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.enableSaveButton = (this.permission.create.findIndex(element => element === PermissionType.RiskModel) >= 0);
  }

  infoImpactScope() {

    this.showScopeInfo = true;
  }

  infoClassScenario() {

    this.showClassInfo = true;
  }

  infoScenarioResult() {
    this.showScenarioResultInfo = true;
  }

  infoExcludeScenario() {
    this.showExcludeScenarioInfo = true;
  }

  infoIntrisincImpact() {
    this.showIntrisincImpactInfo = true;
  }

  infoIntrisincLikelihood() {
    this.showIntrisincLikelihoodInfo = true;
  }

  infoIntrisincSeriousness() {
    this.showIntrisincSeriousnessInfo = true;
  }

  infoCalculatedImpact() {
    this.showCalculatedImpactInfo = true;
  }

  infoCalculatedLikelihood() {
    this.showCalculatedLikelihoodInfo = true;
  }

  infoCalculatedSeriousness() {
    this.showCalculatedSeriousnessInfo = true;
  }

  infoExpertImpact() {
    this.showExpertImpactInfo = true;
  }

  infoExpertLikelihood() {
    this.showExpertLikelihoodInfo = true;
  }

  isLockedByCurrentUser() {
    return this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername');
  }

  ngOnDestroy() {
    this.lockService.removeLock(this.riskModel.identifier);
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
