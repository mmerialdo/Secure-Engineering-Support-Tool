/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="vulnerabilities.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, NgZone, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';
import {TreeNode, ConfirmationService, MessageService} from 'primeng/primeng';
import {DataService} from '../../dataservice';
import {SelectItem} from 'primeng/primeng';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';
import {Permission} from '../../permission.class';
import {PermissionType} from '../../permission-type.class';
import {UUID} from 'angular2-uuid';
import {select, Store} from '@ngrx/store';
import {take} from 'rxjs/operators';
import {
  fetchCopiedVulnerabilitiesStatus,
  selectVulnerabilitiesFromCache
} from '../../shared/store/reducers/vulnerabilities.reducer';
import {copyVulnerabilities} from '../../shared/store/actions/vulnerabilities.actions';
import {Observable} from 'rxjs/internal/Observable';
import {LockService} from '../../shared/service/lock-service';
import {ModelObject} from '../../model-object';

declare var draw2d: any;

declare var CollapsibleShape: any;

declare var clickCollapsible: any;

declare var window: any;

@Component({
  selector: 'app-vulnerabilities',
  templateUrl: './vulnerabilities.component.html',
  styleUrls: ['./vulnerabilities.component.scss'],
  providers: [ConfirmationService]
})
export class VulnerabilitiesComponent implements OnInit, OnDestroy {

  private permission: Permission;
  public enableSaveButton = true;
  public thereAreChanges = false;

  canvas: any;
  // asset edges
  public edges: any;
  // asset nodes
  public nodes: any;
  // id selected Asset
  public currentId;
  // List vulnerabilities of the selected asset
  public stringVul;

  // keeps elements position
  private mapPosition = new Map<Number, String>();
  private maxColumns = 5;

  files: TreeNode[];
  public selectedFiles = [];
  public mehariVulns: TreeNode[];
  public selectedVulnArray = [];
  public tot: any;
  public mehari;
  public vulnerabilityModel;
  public riskModel;
  // list of all associated vulnerabilities
  public vulnerabilitiesList = [];
  // it is used for temporary modifies
  public modifiedVulnerabilitiesList = [];
  // to show different repository div
  public repository = 'mehari';
  // list vulnerabilities repositories
  public repositoryList = [];
  // selected Repository
  public selectedRepository;
  public selectedAsset;
  // array that contains the association assets id and vulnerabilities for the vulnerabilities model
  public idAssets = [];
  // it is used for temporary modifies
  public oldIdVul;
  // they are used to show vulnerabilities' details
  public showDetails = false;
  public affectedCategories = [];
  public associatedThreats = [];
  public descriptionVul;
  public nameVul;
  public catalogueVul;
  public scoreVul;
  public scoringVul;
  public exploiVul;
  public avaVul;
  public intVul;
  public conVul;
  public effVul;
  public descriptionScore;
  // they are used to show Assets' details
  public showAssetDetails = false;
  public nameAsset;
  public primaryAssetCategory;
  public secondCat;
  public confidentiality;
  public integrity;
  public availability;
  public efficiency;
  public showSelectedRequirements = [];
  // they are used to show vulnerabilities' details in the form
  public affectedCategoriesForm = [];
  public associatedThreatsForm = [];
  public nameVulForm;
  public catalogueVulForm;
  public catalogueIDVulForm;
  public scoringVulForm;

  public selectedIntegrityVul;
  public integrityList = [];
  public selectedEfficiencyVul;
  public efficiencyList = [];
  public selectedConfidentialityVul;
  public confidentialityList = [];
  public selectedAvailabilityVul;
  public availabilityList = [];
  public selectedScore;
  public scoreList = [];
  public selectedExploitability;
  public exploitabilityList = [];

  public scoreDescriptionForm;
  // to show Edit Vulnerabilities form
  public displayEditVulnerability = false;
  // to show associated Vulnerability List in the form
  public associatedVulnerabilities: SelectItem[];
  // selected associated vulnerability
  public selectedVulnerability;
  // form to edit the vulnerabilities
  vulnerabilityForm: any;
  public blocked = false;

  // to show messages
  msgsVuln: Message[] = [];
  // to show the requirements names
  public requirementsList: any;
  public copied$: Observable<boolean>;

  public static WARN_NO_ASSET_MATCH = 'It is not possible to add all these Vulnerabilities. Some of their Secondary Categories are different from the Asset\'s Category';
  public static INFO_SERVER_FILTER = 'Vulnerabilities will be filtered during saving considering the existing risk scenario taxonomy';

  private subscriptions: Subscription[] = [];

  constructor(private store: Store<any>,
              private zone: NgZone,
              private formBuilder: FormBuilder,
              private dataService: DataService,
              private confirmationService: ConfirmationService,
              private messageService: MessageService,
              private lockService: LockService) {

    window.angularComponentRef = {
      zone: this.zone,
      componentFn: (value) => this.clickVuln(value),

      // componentDBClick:(event) => this.dbClick(event),
      // connectionPolicy:(value) => this.managementConnection(value),
      removeComponent: (value) => this.removeAsset(value),
      // removeConnection:(value) => this.removeLink(value),
      // componentEdge:(the) => this.clickOnEdge(the),
      component: this
    };

    this.vulnerabilityForm = this.formBuilder.group({

      'descriptionVuln': ['', Validators.required],
      'scoVul': ['', Validators.required],
      'explVul': ['', Validators.required],
      'confVul': ['', Validators.required],
      'intVul': ['', Validators.required],
      'avaVul': ['', Validators.required],
      'effVul': ['', Validators.required],
      'scoredesVul': ['', Validators.required]
    });

    this.repositoryList = ['ALL', 'MEHARI', 'CUSTOM'];
    this.integrityList = ['', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
    this.confidentialityList = ['', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
    this.efficiencyList = ['', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
    this.availabilityList = ['', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
    // this.exploitabilityList=["","VERY_LOW","LOW","MEDIUM","HIGH","VERY_HIGH"];
    // this.scoreList=["","LOW","MEDIUM","HIGH","CRITICAL"];
    this.exploitabilityList = ['VERY_LOW', 'LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH'];
    this.scoreList = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  }

  ngOnInit() {
    this.copied$ = this.store.pipe(select(fetchCopiedVulnerabilitiesStatus));

    this.getAsset();
    const uuid = UUID.UUID();
  }

  addVulnerabilitiesToCache(): void {
    const cachedVulnerabilities = JSON.parse(JSON.stringify(this.associatedVulnerabilities));
    this.store.dispatch(copyVulnerabilities({cachedVulnerabilities}));
    this.displayEditVulnerability = false;
    this.messageService.add({key: 'tc', severity: 'info', summary: 'Info Message', detail: 'Vulnerabilities Copied'});
  }

  fetchVulnerabilitiesToAll(): void {

    const figure = this.canvas.getFigures().data;

    const list = this.associatedVulnerabilities.map(item => ({name: item.label, data: item.value}));
    this.idAssets.forEach(asset => {
      if (!asset.vulnerabilities || asset.vulnerabilities.length <= 0) {
        let text = '';
        const foundFigure = figure.find(fig => fig.id == asset.identifier);
        list.forEach(vulnerability => {
          if (this.checkSameCategory(asset.secondaryCategory, vulnerability.data[0].affectedAssetsCategories)) {
            asset.vulnerabilities.push(vulnerability);
            text += vulnerability.data[0].name;
            text += '\n';
          }
        });
        foundFigure.children.data[1].figure.setText(text);
      }
    });
    this.changeColor();
    this.closeForm();
  }

  fetchVulnerabilitiesFromCache(): void {
    const paste = this.store.pipe(select(selectVulnerabilitiesFromCache)).pipe(take(1)).subscribe(vulnerabilities => {
      this.fetchVulnerabilities(vulnerabilities);
    });
    this.closeForm();
  }

  fetchVulnerabilities(vulnerabilities: any): void {
    const list = vulnerabilities.map(item => ({label: item.label, data: item.value}));
    this.showVulnerabilities(this.selectedAsset, list);
  }

  setPermission() {

    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.enableSaveButton = (this.permission.create.findIndex(element => element === PermissionType.VulnerabilityModel) >= 0);
  }

  showSuccess() {
    this.msgsVuln = [];
    this.msgsVuln.push({severity: 'success', summary: 'Save Successful!', detail: 'Vulnerability Model Saved'});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  clearMessage() {
    this.msgsVuln = [];
  }

  // create the space to draw the cards
  createGraph() {

    this.createTree();
    this.canvas = new draw2d.Canvas('canvas-div');

    const MyPolicy = draw2d.policy.canvas.CanvasPolicy.extend({
      NAME: 'MyPolicy',
      init: function () {
        this._super();
      },
      onClick: function (the, mouseX, mouseY, shiftKey, ctrlKey) {
        this._super(the, mouseX, mouseY, shiftKey, ctrlKey);
        window.angularComponentRef.componentFn(the);
      }
    });

    const policy = new MyPolicy();
    this.canvas.installEditPolicy(policy);
    this.setPermission();
  }

  // to create a widget
  createVuln(s: string, id: string) {

    const elementsNumber = this.mapPosition.size;
    const elementsKeysArray = Array.from(this.mapPosition.keys());
    let nextPosition = elementsNumber;
    for (let i = 0; i < elementsNumber; i++) {
      const found = elementsKeysArray.includes(i);
      if (!found) {
        nextPosition = i;
        break;
      }
    }

    const column = nextPosition % this.maxColumns;
    const row = Math.floor(nextPosition / this.maxColumns);
    const figure = new CollapsibleShape({x: 150 * column, y: 150 * row}, id);
    figure.attr({
      x: (230 * column) + 30,
      y: (150 * row) + 30
    });

    figure.attr({
      width: 100,
      height: 60
    });

    figure.children.data[0].figure.children.data[0].figure.setText(s);
    figure.setId(id);
    figure.deleteable = false;
    figure.on('removed', function (emitter, event) {
      window.angularComponentRef.removeComponent(emitter);
    });
    this.canvas.add(figure);
    this.mapPosition.set(nextPosition, id);
    this.checkedRiskModel(id, figure);
  }

  // returns list of all the vulnerabilities already associated
  checkedRiskModel(id, figure) {

    const vul = [];
    for (const scenario of this.riskModel.scenarios) {
      if (scenario.assetId === id) {
        const vulnIdentifier = scenario.vulnerabilityId;
        const index = this.vulnerabilityModel.vulnerabilities.findIndex((n) =>
          n.identifier === vulnIdentifier);
        if (!vul.find(item => item.identifier === vulnIdentifier)) {
          vul.push(this.vulnerabilityModel.vulnerabilities[index]);
        }
      }
    }

    this.buildLeaf(vul, figure);
  }

  buildLeaf(vul, figure) {

    const leaves = [];
    let cat = [];

    for (const i in vul) {
      cat = this.findaffectedAssetsCategories(vul[i].affectedAssetsCategories);
      for (const j in vul[i].affectedAssetsCategories) {
        const category = vul[i].affectedAssetsCategories[j];
        let indexOfLabel = -1;
        indexOfLabel = leaves.findIndex(index => index.label === category);

        if (indexOfLabel === -1) {
          const array = [];
          let obj = {};

          array.push({
            'label': vul[i].name, 'data': [{
              'nodeType': 'Vulnerability',
              'canBeSelected': vul[i].canBeSelected,
              'description': vul[i].description,
              'phase': vul[i].phase,
              'score': vul[i].score,
              'name': vul[i].name,
              'catalogue': vul[i].catalogue,
              'affectedAssetsCategories': cat,
              'identifier': vul[i].identifier,
              'associatedThreats': vul[i].associatedThreats,
              'assessmentVulnerability': vul[i].assessmentVulnerability,
              'catalogueId': vul[i].catalogueId,
              'causalNature': vul[i].causalNature,
              'children': vul[i].children,
              'elementType': vul[i].elementType,
              'introductoryPhases': vul[i].introductoryPhases,
              'referenceUrls': vul[i].referenceUrls,
              'relatedCatalogueIds': vul[i].relatedCatalogueIds,
              'relatedVulnerabilities': vul[i].relatedVulnerabilities,
              'detectionMethods': vul[i].detectionMethods,
              'mitigations': vul[i].mitigations,
              'relatedSecurityRequirements': vul[i].relatedSecurityRequirements,
              'objType': 'VulnerabilityModel'

            }]
          });
          obj = {'label': category, 'data': [{'nodeType': 'Category'}], 'children': array};

          leaves.push(obj);

        } else {

          leaves[indexOfLabel].children.push({
            'label': vul[i].name, 'data': [{
              'nodeType': 'Vulnerability',
              'canBeSelected': vul[i].canBeSelected,
              'description': vul[i].description,
              'phase': vul[i].phase,
              'score': vul[i].score,
              'name': vul[i].name,
              'catalogue': vul[i].catalogue,
              'affectedAssetsCategories': cat,
              'identifier': vul[i].identifier,
              'associatedThreats': vul[i].associatedThreats,
              'assessmentVulnerability': vul[i].assessmentVulnerability,
              'catalogueId': vul[i].catalogueId,
              'causalNature': vul[i].causalNature,
              'children': vul[i].children,
              'elementType': vul[i].elementType,
              'introductoryPhases': vul[i].introductoryPhases,
              'referenceUrls': vul[i].referenceUrls,
              'relatedCatalogueIds': vul[i].relatedCatalogueIds,
              'relatedVulnerabilities': vul[i].relatedVulnerabilities,
              'detectionMethods': vul[i].detectionMethods,
              'mitigations': vul[i].mitigations,
              'relatedSecurityRequirements': vul[i].relatedSecurityRequirements,
              'objType': 'VulnerabilityModel'
            }]
          });
        }
      }
    }

    this.selectedVulnArray = [];
    this.selectedAsset = figure;

    for (const leaf in leaves) {
      for (const k in leaves[leaf].children) {

        const indexOfVuln = this.selectedVulnArray.findIndex(i => i.label === leaves[leaf].children[k].label);

        if (indexOfVuln === -1) {
          this.selectedVulnArray.push(leaves[leaf].children[k]);
        }
      }
    }

    this.showVulnerabilities(this.selectedAsset, this.selectedVulnArray);
    this.selectedVulnArray = [];
    this.selectedAsset = undefined;
  }

  addAssets() {

    for (const selectedFile of this.selectedFiles) {
      if (selectedFile.data !== null && selectedFile.data.nodeType === 'Asset') {

        // The widget of the asset is not already in the canvas
        if (this.canvas.getFigures().data.findIndex(uni => uni.id === selectedFile.data.identifier) === -1) {

          let index = -1;

          const a = selectedFile.data.identifier;
          index = this.idAssets.findIndex(ix => ix.identifier === a);

          if (index === -1) {
            this.idAssets.push({
              'identifier': selectedFile.data.identifier,
              'secondaryCategory': selectedFile.data.category,
              'vulnerabilities': []
            });
          }
          this.createVuln(selectedFile.label, selectedFile.data.identifier);
        }
      }
    }

    let figureToRemove = [];
    for (const figure of this.canvas.getFigures().data) {
      const foundFigure = this.selectedFiles.find(selectedFile => selectedFile.data.identifier === figure.id);
      if (!foundFigure) {
        figureToRemove.push(figure);
      }
    }
    if (figureToRemove.length > 0) {
      figureToRemove.forEach(figure => {
        this.canvas.remove(figure);
        const foundPosition = Array.from(this.mapPosition.entries()).filter(({1: v}) => v === figure.id)
          .map(([k]) => k);
        if (foundPosition) {
          this.mapPosition.delete(foundPosition[0]);
        }
      });
    }

    this.thereAreChanges = true;
  }

  showVulnerabilities(selectedAsset: any, selectedVulnerabilitiesArray: any[]) {

    let vulnLabel = '';
    const assVuln = [];
    let isMatchingAssetCategory = true;
    // for each selected vulnerability
    for (const selectedVulnerability of selectedVulnerabilitiesArray) {
      // it checks if the leaf is a vulnerability
      if ((selectedVulnerability.data[0].nodeType === 'Vulnerability') && (selectedVulnerability.data[0].canBeSelected)) {

        // It checks if the vulnerability is already in the VulnerabilityModel
        // If it is, it is better to use the Vulnerability in the VulnerabilityModel
        // because it may be updated with respect to the selected vulnerability
        const vulnCatalogueId = selectedVulnerability.data[0].catalogueId;
        for (const vulnId in this.vulnerabilityModel.vulnerabilities) {
          if (this.vulnerabilityModel.vulnerabilities[vulnId].catalogueId === vulnCatalogueId) {

            selectedVulnerability.data[0].score = this.vulnerabilityModel.vulnerabilities[vulnId].score;
            selectedVulnerability.data[0].description = this.vulnerabilityModel.vulnerabilities[vulnId].description;
          }
        }

        // it checks if the current selected node belongs to the nodes array of the assets view
        const k = this.nodes.findIndex(nod => nod.identifier === selectedAsset.id);
        if (k !== -1) {
          // to avoid duplicates
          // it checks if the current array nodes/vulnerabilities contains the current selected node
          const indexIdAsset = this.idAssets.findIndex(id => id.identifier === this.nodes[k].identifier);
          // it checks if the array nodes/vulnerabilities already contains the selected vulnerability
          const index = this.idAssets[indexIdAsset].vulnerabilities.findIndex(vul =>
            vul.data[0].catalogueId === selectedVulnerability.data[0].catalogueId);

          if (index === -1) {

            const indexVul = assVuln.findIndex(indAssVul =>
              indAssVul.data[0].catalogueId === selectedVulnerability.data[0].catalogueId);

            // to avoid duplicates when we are loading "old" vulnerabilities of the risk model
            if (indexVul === -1) {

              if (selectedAsset.children.data[1].figure.getText().length > 0) {
                vulnLabel = selectedAsset.children.data[1].figure.getText();
              }

              const same = this.checkSameCategory(this.nodes[k].category, selectedVulnerability.data[0].affectedAssetsCategories);

              if (same) {
                const indexOldVuln = this.vulnerabilitiesList.findIndex(z =>
                  z.data[0].identifier === selectedVulnerability.data[0].identifier);

                if (indexOldVuln === -1) {
                  this.vulnerabilitiesList.push({
                    'name': selectedVulnerability.label,
                    'data': selectedVulnerability.data
                  });
                  assVuln.push({'name': selectedVulnerability.label, 'data': selectedVulnerability.data});
                } else {
                  assVuln.push({
                    'name': selectedVulnerability.label,
                    'data': this.vulnerabilitiesList[indexOldVuln].data
                  });
                }

                if (vulnLabel === '') {
                  vulnLabel = vulnLabel + selectedVulnerability.label;
                  selectedAsset.children.data[1].figure.setText(vulnLabel);
                } else {
                  vulnLabel = vulnLabel + '\n' + selectedVulnerability.label;
                  selectedAsset.children.data[1].figure.setText(vulnLabel);
                }

              } else {
                isMatchingAssetCategory = false;
              }
            }
          } else {
            for (const oldString of this.idAssets[indexIdAsset].vulnerabilities) {

              if (vulnLabel === '') {
                vulnLabel = vulnLabel + oldString.data[0].name;
              } else {
                if (vulnLabel.indexOf(oldString.data[0].name) === -1) {
                  vulnLabel = vulnLabel + '\n' + oldString.data[0].name;
                }
              }
            }
            selectedAsset.children.data[1].figure.setText(vulnLabel);
          }
        }
      }
    }

    for (const q in this.idAssets) {

      if (this.idAssets[q].identifier === selectedAsset.id) {
        for (const fg in assVuln) {
          this.idAssets[q].vulnerabilities.push(assVuln[fg]);
        }
      }
    }
    if (!isMatchingAssetCategory) {

      this.messageService.add({
        key: 'tc',
        severity: 'info',
        summary: 'Info Message',
        detail: VulnerabilitiesComponent.WARN_NO_ASSET_MATCH
      });
    }

    this.setColor(this.idAssets);
  }


  addVulnerabilities() {

    const idVul = [];

    let vulnLabel = '';
    const assVuln = [];
    // for each selected vulnerability
    for (const i in this.selectedVulnArray) {

      // it checks if the leaf is a vulnerability
      if ((this.selectedVulnArray[i].data[0].nodeType === 'Vulnerability') && (this.selectedVulnArray[i].data[0].canBeSelected)) {

        // It checks if the vulnerability is already in the VulnerabilityModel
        // If it is, it is better to use the Vulnerability in the VulnerabilityModel
        // because it may be updated with respect to the selected vulnerability
        const vulnCatalogueId = this.selectedVulnArray[i].data[0].catalogueId;

        for (const vulnId in this.vulnerabilityModel.vulnerabilities) {
          if (this.vulnerabilityModel.vulnerabilities[vulnId].catalogueId === vulnCatalogueId) {

            this.selectedVulnArray[i].data[0].score = this.vulnerabilityModel.vulnerabilities[vulnId].score;
            this.selectedVulnArray[i].data[0].description = this.vulnerabilityModel.vulnerabilities[vulnId].description;
          }
        }

        // it checks if the current selected node belongs to the nodes array of the assets view
        const k = this.nodes.findIndex(nod => nod.identifier === this.selectedAsset.id);
        if (k !== -1) {

          // to avoid duplicates
          // it checks if the current array nodes/vulnerabilities contains the current selected node
          const indexIdAsset = this.idAssets.findIndex(id => id.identifier === this.nodes[k].identifier);
          // it checks if the array nodes/vulnerabilities already contains the selected vulnerability
          const index = this.idAssets[indexIdAsset].vulnerabilities.findIndex(vul =>
            vul.data[0].catalogueId === this.selectedVulnArray[i].data[0].catalogueId);

          if (index === -1) {

            const indexVul = assVuln.findIndex(indAssVul =>
              indAssVul.data[0].catalogueId === this.selectedVulnArray[i].data[0].catalogueId);

            // to avoid duplicates when we are loading "old" vulnerabilities of the risk model
            if (indexVul === -1) {

              if (this.selectedAsset.children.data[1].figure.getText().length > 0) {
                vulnLabel = this.selectedAsset.children.data[1].figure.getText();
              }
              const same = this.checkSameCategory(this.nodes[k].category, this.selectedVulnArray[i].data[0].affectedAssetsCategories);
              // It is possible to add the vulnerability
              if (same) {
                const indexOldVuln = this.vulnerabilitiesList.findIndex(z =>
                  z.data[0].identifier === this.selectedVulnArray[i].data[0].identifier);

                if (indexOldVuln === -1) {
                  this.vulnerabilitiesList.push({
                    'name': this.selectedVulnArray[i].label,
                    'data': this.selectedVulnArray[i].data
                  });
                  assVuln.push({'name': this.selectedVulnArray[i].label, 'data': this.selectedVulnArray[i].data});
                } else {
                  assVuln.push({
                    'name': this.selectedVulnArray[i].label,
                    'data': this.vulnerabilitiesList[indexOldVuln].data
                  });
                }
                if (vulnLabel === '') {
                  vulnLabel = vulnLabel + this.selectedVulnArray[i].label;
                  this.selectedAsset.children.data[1].figure.setText(vulnLabel);
                } else {
                  vulnLabel = vulnLabel + '\n' + this.selectedVulnArray[i].label;
                  this.selectedAsset.children.data[1].figure.setText(vulnLabel);
                }
              }
            }
          } else {

            for (const oldString in this.idAssets[indexIdAsset].vulnerabilities) {
              if (vulnLabel === '') {
                vulnLabel = vulnLabel + this.idAssets[indexIdAsset].vulnerabilities[oldString].data[0].name;
              } else {
                if (vulnLabel.indexOf(this.idAssets[indexIdAsset].vulnerabilities[oldString].data[0].name) === -1) {
                  vulnLabel = vulnLabel + '\n' + this.idAssets[indexIdAsset].vulnerabilities[oldString].data[0].name;
                }
              }
            }
            this.selectedAsset.children.data[1].figure.setText(vulnLabel);
          }
        }
      }
    }

    for (const q in this.idAssets) {
      if (this.idAssets[q].identifier === this.selectedAsset.id) {
        for (const fg in assVuln) {
          this.idAssets[q].vulnerabilities.push(assVuln[fg]);
        }
      }
    }

    this.selectedVulnArray = [];
    this.setColor(this.idAssets);
    this.selectedAsset = undefined;
  }

  checkSameCategory(assetCat, vulCat): boolean {

    if ((assetCat === null) || (assetCat === undefined)) {
      return false;
    }

    for (const j in vulCat) {
      if (assetCat === vulCat[j]) {
        return true;
      }
    }
    return false;
  }

  compareLists(listOld, listNew) {
    const toDelete = [];
    const toAdd = [];
    const catalogue = listNew[0].data[0].catalogue;

    for (const y in listOld) {
      if (listOld[y].data[0].catalogue === catalogue) {

        const index = listNew.findIndex(i => i.data[0].identifier === listOld[y].data[0].identifier);
        if (index === -1) {
          toDelete.push(y);
        }
      }
    }

    for (const j in listNew) {

      if (listNew[j].data[0].catalogue === catalogue) {
        const index = listOld.findIndex(i => i.data[0].identifier === listNew[j].data[0].identifier);
        if (index === -1) {
          toAdd.push(j);
        }
      }
    }

    for (const del in toDelete) {
      listOld.splice(Number(toDelete[del]), 1);
    }

    for (const add in toAdd) {
      listOld.push(listNew[toAdd[add]]);
    }

    return listOld;
  }

  correspondingColor(s): string {

    if (s === 'CRITICAL') {

      return '#ff0000';
    } else if (s === 'HIGH') {

      return '#ffa500';
    } else if (s === 'MEDIUM') {

      return '#ffdb00';
    } else if (s === 'LOW') {

      return '#00ff00';
    } else if (s === '') {

      return '#129CE4';
    }
  }

  setColor(array) {

    // for each assets
    for (const i in array) {

      let impact = '';
      const totalImpact = [];
      const assetImpact = this.nodes[this.nodes.findIndex((n) => n.identifier === array[i].identifier)].securityImpacts;

      // for each associated vulnerabilities
      for (const y in array[i].vulnerabilities) {

        // for each consequence
        for (const j in array[i].vulnerabilities[y].data[0].score.consequences) {

          // for each impact
          for (const k in array[i].vulnerabilities[y].data[0].score.consequences[j].securityImpacts) {

            // impact = this.maxImpact(impact, array[i].vulnerabilities[y].data[0].score.consequences[j].securityImpacts[k].impact);
            for (const ai in assetImpact) {

              if (array[i].vulnerabilities[y].data[0].score.consequences[j].securityImpacts[k].scope === assetImpact[ai].scope) {
                impact = this.maxImpact(impact, assetImpact[ai].impact);
              }
            }
          }
        }
      }

      if (impact !== '') {

        if (array[i].identifier === this.selectedAsset.id) {

          this.selectedAsset.setColor(this.correspondingColor(impact));
          this.selectedAsset.children.data[0].figure.setBackgroundColor(this.correspondingColor(impact));
        }
      }
    }
  }

  vulSelect(event) {

    this.avaVul = null;
    this.conVul = null;
    this.effVul = null;
    this.intVul = null;

    const index = -1;
    if (event.node.data[0].nodeType === 'Vulnerability') {

      for (const i in event.node.data[0].score.consequences) {
        for (const j in event.node.data[0].score.consequences[i].securityImpacts) {
          if (event.node.data[0].score.consequences[i].securityImpacts[j].scope === 'Availability') {

            const a = this.maxImpact(this.avaVul, event.node.data[0].score.consequences[i].securityImpacts[j].impact);

            this.avaVul = a;
          } else if (event.node.data[0].score.consequences[i].securityImpacts[j].scope === 'Integrity') {

            const a = this.maxImpact(this.intVul, event.node.data[0].score.consequences[i].securityImpacts[j].impact);
            this.intVul = a;
          } else if (event.node.data[0].score.consequences[i].securityImpacts[j].scope === 'Confidentiality') {

            const a = this.maxImpact(this.conVul, event.node.data[0].score.consequences[i].securityImpacts[j].impact);
            this.conVul = a;
          } else if (event.node.data[0].score.consequences[i].securityImpacts[j].scope === 'Efficiency') {

            const a = this.maxImpact(this.effVul, event.node.data[0].score.consequences[i].securityImpacts[j].impact);
            this.effVul = a;
          }
        }

        // I always put the last description because I have always just a consequence otherwise I should take the worst
        this.descriptionScore = event.node.data[0].score.consequences[0].description;
      }

      this.scoreVul = event.node.data[0].score.score;
      this.scoringVul = event.node.data[0].score.scoringType;
      this.exploiVul = event.node.data[0].score.exploitability;
      this.associatedThreats = event.node.data[0].associatedThreats;
      this.affectedCategories = event.node.data[0].affectedAssetsCategories;
      this.descriptionVul = event.node.data[0].description;
      this.nameVul = event.node.data[0].name;
      this.catalogueVul = event.node.data[0].catalogue;

    } else {

      for (const i in event.node.children[event.node.children.length - 1].data[0].score.consequences) {
        for (const j in event.node.children[event.node.children.length - 1].data[0].score.consequences[i].securityImpacts) {
          const scope = event.node.children[event.node.children.length - 1].data[0].score.consequences[i].securityImpacts[j].scope;
          const impact = event.node.children[event.node.children.length - 1].data[0].score.consequences[i].securityImpacts[j].impact;

          if (scope === 'Availability') {

            this.avaVul = this.maxImpact(this.avaVul, impact);
          } else if (scope === 'Integrity') {

            this.intVul = this.maxImpact(this.intVul, impact);
          } else if (scope === 'Confidenciality') {

            this.conVul = this.maxImpact(this.conVul, impact);
          } else if (scope === 'Efficiency') {

            this.effVul = this.maxImpact(this.effVul, impact);
          }

          // I always put the last description because I have always just a consequence otherwise I should take the worst
          this.descriptionScore = event.node.children[event.node.children.length - 1].data[0].score.consequences[0].description;
        }
      }

      // this.natureVul=event.node.children[event.node.children.length-1].data[0].causalNature;
      // this.phaseVul=event.node.children[event.node.children.length-1].data[0].phase;
      this.scoreVul = event.node.children[event.node.children.length - 1].data[0].score.score;
      this.scoringVul = event.node.children[event.node.children.length - 1].data[0].score.scoringType;
      this.exploiVul = event.node.children[event.node.children.length - 1].data[0].score.exploitability;
      this.associatedThreats = event.node.children[event.node.children.length - 1].data[0].associatedThreats;
      this.affectedCategories = event.node.children[event.node.children.length - 1].data[0].affectedAssetsCategories;
      this.descriptionVul = event.node.children[event.node.children.length - 1].data[0].description;
      this.nameVul = event.node.children[event.node.children.length - 1].data[0].name;
      this.catalogueVul = event.node.children[event.node.children.length - 1].data[0].catalogue;
    }

    this.showDetails = true;
  }

  maxImpact(oldImp: string, newImp: string): string {

    if (newImp === 'CRITICAL') {
      oldImp = 'CRITICAL';
    } else if ((newImp === 'HIGH') && (oldImp !== 'CRITICAL')) {
      oldImp = 'HIGH';
    } else if ((newImp === 'MEDIUM') && (oldImp !== 'CRITICAL') && (oldImp !== 'HIGH')) {
      oldImp = 'MEDIUM';
    } else if ((newImp === 'LOW') && (oldImp !== 'CRITICAL') && (oldImp !== 'HIGH') && (oldImp !== 'MEDIUM')) {
      oldImp = 'LOW';
    }
    return oldImp;

  }

  changeColor() {
    const figure = this.canvas.getFigures().data;

    for (const i in this.idAssets) {

      let impact = '';
      const totalImpact = [];

      const assetImpact = this.nodes[this.nodes.findIndex((n) => n.identifier === this.idAssets[i].identifier)].securityImpacts;

      for (const y in this.idAssets[i].vulnerabilities) {
        for (const j in this.idAssets[i].vulnerabilities[y].data[0].score.consequences) {
          for (const k in this.idAssets[i].vulnerabilities[y].data[0].score.consequences[j].securityImpacts) {

            for (const ai in assetImpact) {

              if (this.idAssets[i].vulnerabilities[y].data[0].score.consequences[j].securityImpacts[k].scope ===
                assetImpact[ai].scope) {
                impact = this.maxImpact(impact, assetImpact[ai].impact);
              }
            }
          }
        }
      }
      for (const fig in figure) {

        if (this.idAssets[i].identifier === figure[fig].id) {
          figure[fig].setColor(this.correspondingColor(impact));
          figure[fig].children.data[0].figure.setBackgroundColor(this.correspondingColor(impact));
        }
      }
    }
  }


  vulUnselect(event) {

    if (this.selectedVulnArray.length === 0) {
      this.showDetails = false;
    }
  }

  // event when a tree node is selected
  nodeSelect(event) {

    this.availability = null;
    this.integrity = null;
    this.efficiency = null;
    this.confidentiality = null;

    if (event.node.data.nodeType === 'Asset') {
      this.nameAsset = event.node.data.name;
      this.secondCat = event.node.data.category;
      this.primaryAssetCategory = event.node.data.primaryCategories[0];

      const requirementsName = [];
      for (const req in event.node.data.relatedRequirementsIds) {

        const reqInd = this.requirementsList.findIndex(reqI => reqI.identifier === event.node.data.relatedRequirementsIds[req]);
        if (reqInd !== -1) {
          requirementsName.push(this.requirementsList[reqInd].id);
        }
      }

      this.showSelectedRequirements = requirementsName;

      for (const i in event.node.data.securityImpacts) {
        if (event.node.data.securityImpacts[i].scope === 'Availability') {
          this.availability = event.node.data.securityImpacts[i].impact;
        }

        if (event.node.data.securityImpacts[i].scope === 'Integrity') {
          this.integrity = event.node.data.securityImpacts[i].impact;
        }

        if (event.node.data.securityImpacts[i].scope === 'Efficiency') {
          this.efficiency = event.node.data.securityImpacts[i].impact;
        }

        if (event.node.data.securityImpacts[i].scope === 'Confidentiality') {
          this.confidentiality = event.node.data.securityImpacts[i].impact;
        }
      }

      this.showAssetDetails = true;
    }
  }

  // event when a tree node is deselected
  nodeUnselect(event) {

    if (this.selectedFiles.length === 0) {
      this.showAssetDetails = false;
    }
  }

  // event when an asset node is selected
  clickVuln(value) {
    this.selectedVulnArray = [];

    if (value === null) {

      this.showAssetDetails = false;
      this.showDetails = false;
    } else {

      this.selectedAsset = value.canvas.selection.all.data[0];
      const j = this.nodes.findIndex(i => i.identifier === value.id);

      this.integrity = null;
      this.confidentiality = null;
      this.efficiency = null;
      this.availability = null;
      this.nameAsset = this.nodes[j].name;

      this.primaryAssetCategory = this.nodes[j].primaryCategories[0];

      const requirementsName = [];
      for (const req in this.nodes[j].relatedRequirementsIds) {
        const reqInd = this.requirementsList.findIndex(reqI => reqI.identifier === this.nodes[j].relatedRequirementsIds[req]);
        if (reqInd !== -1) {
          requirementsName.push(this.requirementsList[reqInd].id);
        }
      }
      this.showSelectedRequirements = requirementsName;
      this.secondCat = this.nodes[j].category;

      for (const imp in this.nodes[j].securityImpacts) {

        if (this.nodes[j].securityImpacts[imp].scope === 'Integrity') {
          this.integrity = this.nodes[j].securityImpacts[imp].impact;
        }
        if (this.nodes[j].securityImpacts[imp].scope === 'Availability') {
          this.availability = this.nodes[j].securityImpacts[imp].impact;
        }
        if (this.nodes[j].securityImpacts[imp].scope === 'Efficiency') {
          this.efficiency = this.nodes[j].securityImpacts[imp].impact;
        }
        if (this.nodes[j].securityImpacts[imp].scope === 'Confidentiality') {
          this.confidentiality = this.nodes[j].securityImpacts[imp].impact;
        }
      }

      if (value.cssClass === 'draw2d_shape_basic_Label') {
        // it could be usefull to change the vulnerabilities list
        this.stringVul = value;

        this.associatedVulnerabilities = [];
        for (const k in this.idAssets) {
          if (value.id === this.idAssets[k].identifier) {
            this.currentId = this.idAssets[k].identifier;
            for (const vul in this.idAssets[k].vulnerabilities) {
              this.associatedVulnerabilities.push({
                'label': this.idAssets[k].vulnerabilities[vul].name
                , 'value': this.idAssets[k].vulnerabilities[vul].data
              });
            }
          }
        }
        this.modifiedVulnerabilitiesList = JSON.parse(JSON.stringify(this.vulnerabilitiesList));
        this.selectedVulnerability = undefined;
        this.displayEditVulnerability = true;
      }

      this.showAssetDetails = true;
    }
  }

  removeAsset(value) {

    const id = value.identifier;

    for (const i in this.idAssets) {
      if (id === this.idAssets[i].identifier) {
        this.idAssets.splice(parseInt(i), 1);
        break;
      }
    }

    if (this.canvas.getFigures().data.length === 0) {
      this.thereAreChanges = false;
    }
  }

  findaffectedAssetsCategories(array): any {
    const a = [];
    for (const i in array) {

      a.push(array[i]);
    }
    return a;
  }

  createMehariTree() {

    this.mehariVulns = [];
    let cat = [];
    for (const i in this.mehari.vulnerabilities) {

      cat = this.findaffectedAssetsCategories(this.mehari.vulnerabilities[i].affectedAssetsCategories);

      for (const j in this.mehari.vulnerabilities[i].affectedAssetsCategories) {
        const a = this.mehari.vulnerabilities[i].affectedAssetsCategories[j];
        let indexOfLabel = -1;
        indexOfLabel = this.mehariVulns.findIndex(index => index.label === a);

        if (indexOfLabel === -1) {
          const array = [];
          let obj = {};
          array.push({
            'label': this.mehari.vulnerabilities[i].name, 'data': [{
              'nodeType': 'Vulnerability',
              'canBeSelected': this.mehari.vulnerabilities[i].canBeSelected,
              'description': this.mehari.vulnerabilities[i].description,
              'phase': this.mehari.vulnerabilities[i].phase,
              'score': this.mehari.vulnerabilities[i].score,
              'name': this.mehari.vulnerabilities[i].name,
              'catalogue': this.mehari.vulnerabilities[i].catalogue,
              'affectedAssetsCategories': cat,
              'identifier': this.mehari.vulnerabilities[i].identifier,
              'associatedThreats': this.mehari.vulnerabilities[i].associatedThreats,
              'assessmentVulnerability': this.mehari.vulnerabilities[i].assessmentVulnerability,
              'catalogueId': this.mehari.vulnerabilities[i].catalogueId,
              'causalNature': this.mehari.vulnerabilities[i].causalNature,
              'children': this.mehari.vulnerabilities[i].children,
              'elementType': this.mehari.vulnerabilities[i].elementType,
              'introductoryPhases': this.mehari.vulnerabilities[i].introductoryPhases,
              'referenceUrls': this.mehari.vulnerabilities[i].referenceUrls,
              'relatedCatalogueIds': this.mehari.vulnerabilities[i].relatedCatalogueIds,
              'relatedVulnerabilities': this.mehari.vulnerabilities[i].relatedVulnerabilities,
              'detectionMethods': this.mehari.vulnerabilities[i].detectionMethods,
              'mitigations': this.mehari.vulnerabilities[i].mitigations,
              'relatedSecurityRequirements': this.mehari.vulnerabilities[i].relatedSecurityRequirements,
              'objType': 'VulnerabilityModel'

            }]
          });
          obj = {'label': a, 'data': [{'nodeType': 'Category'}], 'children': array};
          this.mehariVulns.push(obj);
        } else {

          this.mehariVulns[indexOfLabel].children.push({
            'label': this.mehari.vulnerabilities[i].name, 'data': [{
              'nodeType': 'Vulnerability',
              'canBeSelected': this.mehari.vulnerabilities[i].canBeSelected,
              'description': this.mehari.vulnerabilities[i].description,
              'phase': this.mehari.vulnerabilities[i].phase,
              'score': this.mehari.vulnerabilities[i].score,
              'name': this.mehari.vulnerabilities[i].name,
              'catalogue': this.mehari.vulnerabilities[i].catalogue,
              'affectedAssetsCategories': cat,
              'identifier': this.mehari.vulnerabilities[i].identifier,
              'associatedThreats': this.mehari.vulnerabilities[i].associatedThreats,
              'assessmentVulnerability': this.mehari.vulnerabilities[i].assessmentVulnerability,
              'catalogueId': this.mehari.vulnerabilities[i].catalogueId,
              'causalNature': this.mehari.vulnerabilities[i].causalNature,
              'children': this.mehari.vulnerabilities[i].children,
              'elementType': this.mehari.vulnerabilities[i].elementType,
              'introductoryPhases': this.mehari.vulnerabilities[i].introductoryPhases,
              'referenceUrls': this.mehari.vulnerabilities[i].referenceUrls,
              'relatedCatalogueIds': this.mehari.vulnerabilities[i].relatedCatalogueIds,
              'relatedVulnerabilities': this.mehari.vulnerabilities[i].relatedVulnerabilities,
              'detectionMethods': this.mehari.vulnerabilities[i].detectionMethods,
              'mitigations': this.mehari.vulnerabilities[i].mitigations,
              'relatedSecurityRequirements': this.mehari.vulnerabilities[i].relatedSecurityRequirements,
              'objType': 'VulnerabilityModel'
            }]
          });
        }
      }
    }
  }

  // algorith to create the assets tree
  createTree() {

    this.files = [];
    for (const i in this.nodes) {
      if (this.nodes[i].nodeType === 'Organization') {
        const org = {'label': null, 'data': null, 'children': []};
        if (this.nodes[i].name === null) {
          org.label = this.nodes[i].identifier;
        } else {
          org.label = this.nodes[i].name;
        }
        org.data = {'nodeType': 'Organization'};

        for (const j in this.nodes[i].children) {

          org.children.push(this.associatedProcess(this.nodes[i].children[j]));
        }
        this.files.push(org);
      }
    }
    this.blocked = false;
  }

  associatedProcess(id): Object {

    const a = {'label': null, 'data': null, 'children': []};
    for (const i in this.edges) {
      if (id === this.edges[i].identifier) {
        for (const j in this.nodes) {
          if (this.edges[i].target === this.nodes[j].identifier) {
            if (this.nodes[j].name !== null) {
              a.label = this.nodes[j].name;

            } else {
              a.label = this.nodes[j].identifier;
            }
            a.data = {'nodeType': 'Process'};
            for (const k in this.nodes[j].children) {
              a.children.push(this.associatedActivity(this.nodes[j].children[k]));
            }
          }
        }
      }
    }
    return a;
  }


  associatedActivity(id): Object {

    const a = {'label': null, 'data': null, 'children': []};
    for (const i in this.edges) {

      if (id === this.edges[i].identifier) {
        for (const j in this.nodes) {
          if (this.edges[i].target === this.nodes[j].identifier) {
            if (this.nodes[j].name !== null) {
              a.label = this.nodes[j].name;
            } else {
              a.label = this.nodes[j].identifier;
            }

            a.data = {'nodeType': 'Activity'};
            for (const k in this.nodes[j].children) {

              if (!this.isMalfunction(this.nodes[j].children[k])) {

                a.children.push(this.associatedAssets(this.nodes[j].children[k]));
              }
            }
          }
        }
      }
    }
    return a;

  }

  associatedAssets(id): Object {

    const a = {'label': null, 'data': null, 'children': []};
    for (const i in this.edges) {
      if (id === this.edges[i].identifier) {
        for (const j in this.nodes) {

          if ((this.edges[i].target === this.nodes[j].identifier) && (this.nodes[j].nodeType !== 'Malfunction')) {
            if (this.nodes[j].name !== null) {

              a.label = this.nodes[j].name;
            } else {

              a.label = this.nodes[j].identifier;
            }

            a.data = {
              'nodeType': 'Asset',
              'identifier': this.nodes[j].identifier,
              'primaryCategories': this.nodes[j].primaryCategories,
              'malfunctionIds': this.nodes[j].malfunctionsIds,
              'name': this.nodes[j].name,
              'relatedRequirementsIds': this.nodes[j].relatedRequirementsIds,
              'category': this.nodes[j].category,
              'securityImpacts': this.nodes[j].securityImpacts
            };
          }
        }
      }
    }
    return a;
  }

  isMalfunction(id): boolean {

    for (const i in this.edges) {
      if (id === this.edges[i].identifier) {
        for (const j in this.nodes) {

          if ((this.edges[i].target === this.nodes[j].identifier) && (this.nodes[j].nodeType === 'Malfunction')) {
            return true;
          }
        }
      }
    }
    return false;
  }

  deleteVulnerability(data) {

    this.confirmationService.confirm({
      message: 'This is a permanent operation.Do you really want  to remove this vulnerability?',
      header: 'Remove Confirmation',
      icon: 'fa fa-trash',
      accept: () => {

        // to delete the vulnerability in the other  data structures

        const index = this.associatedVulnerabilities.findIndex((n) =>
          ((n.value[0].identifier === data[0].identifier) && (n.value[0].catalogueId === data[0].catalogueId)));

        // I remove the deleted vulnerabilities only from the list associated to the Asset
        this.associatedVulnerabilities.splice(index, 1);

        const indexId = this.idAssets.findIndex((i) => i.identifier === this.currentId);

        const indexVuln = this.idAssets[indexId].vulnerabilities.findIndex((v) =>
          ((v.data[0].identifier === data[0].identifier) && (v.data[0].catalogueId === data[0].catalogueId)));
        this.idAssets[indexId].vulnerabilities.splice(indexVuln, 1);

        // to understand if we have to delete the vulnerabilities in vulnerabilitieslist(the other assets do not contain this vulnerability)
        let count = 0;

        for (const scen in this.riskModel.scenarios) {

          if ((this.riskModel.scenarios[scen].assetId !== this.currentId) &&
            (this.riskModel.scenarios[scen].vulnerabilityId === data[0].identifier)) {
            count = count + 1;
          }

          // There is at least a scenario with this vulnerability for another Asset
          if (count > 0) {
            break;
          }
        }

        if (count === 0) {
          for (const j in this.idAssets) {

            for (const k in this.idAssets[j].vulnerabilities) {

              if ((this.idAssets[j].vulnerabilities[k].data[0].identifier === data[0].identifier) &&
                (this.idAssets[j].vulnerabilities[k].data[0].catalogueId === data[0].catalogueId)) {

                count = count + 1;
              }
              if (count > 0) {
                break;
              }
            }
          }
        }

        if (count === 0) {

          // it deletes in the vulnerabilities List
          const indexVulList = this.vulnerabilitiesList.findIndex((m) =>
            ((m.data[0].identifier === data[0].identifier) && (m.data[0].catalogueId === data[0].catalogueId)));
          this.vulnerabilitiesList.splice(indexVulList, 1);

          const indexVulModList = this.modifiedVulnerabilitiesList.findIndex((m) =>
            ((m.data[0].identifier === data[0].identifier) && (m.data[0].catalogueId === data[0].catalogueId)));
          this.modifiedVulnerabilitiesList.splice(indexVulModList, 1);

          // it deletes in the vulnerabilities Model otherwise it'll add again this vulnerability
          // cheking the old scenarios before to save the model
          const indexVulMod = this.vulnerabilityModel.vulnerabilities.findIndex((b) =>
            ((b.identifier === data[0].identifier) && (b.catalogueId === data[0].catalogueId)));
          this.vulnerabilityModel.vulnerabilities.splice(indexVulMod, 1);
        }

        let string = '';
        for (const vul of this.idAssets[indexId].vulnerabilities) {

          if (string === '') {
            string = string + vul.name;
          } else {

            string = string + '\n' + vul.name;
          }
        }

        this.stringVul.setText(string);

        this.oldIdVul = undefined;
        this.selectedVulnerability = undefined;

        const scenariosToDelete = [];

        for (const del in this.riskModel.scenarios) {

          if ((this.riskModel.scenarios[del].assetId === this.currentId) &&
            (this.riskModel.scenarios[del].vulnerabilityId === data[0].identifier)) {
            scenariosToDelete.push(del);
          }
        }

        for (let toDelete = scenariosToDelete.length - 1; toDelete >= 0; toDelete--) {
          this.riskModel.scenarios.splice(Number(scenariosToDelete[toDelete]), 1);
        }
        this.changeColor();
      }
    });
  }


  // to show vulnerability information in the form
  changeInformation(data) {

    if (data !== null) {

      if (this.oldIdVul !== undefined) {

        for (const old in this.modifiedVulnerabilitiesList) {

          if (this.oldIdVul === this.modifiedVulnerabilitiesList[old].data[0].identifier) {

            this.modifiedVulnerabilitiesList[old].data[0].description = this.vulnerabilityForm.value.descriptionVuln;
            this.modifiedVulnerabilitiesList[old].data[0].score.exploitability = this.vulnerabilityForm.value.explVul;
            this.modifiedVulnerabilitiesList[old].data[0].score.score = this.vulnerabilityForm.value.scoVul;
            this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].description = this.vulnerabilityForm.value.scoredesVul;
            this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts = [];

            if ((this.vulnerabilityForm.value.confVul !== undefined) && (this.vulnerabilityForm.value.confVul !== '') &&
              (this.vulnerabilityForm.value.confVul !== null)) {

              this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts.push({
                'impact': this.vulnerabilityForm.value.confVul,
                'scope': 'Confidentiality',
                'technicalImpacts': []
              });
            }

            if ((this.vulnerabilityForm.value.intVul !== undefined) && (this.vulnerabilityForm.value.intVul !== '') &&
              (this.vulnerabilityForm.value.intVul !== null)) {

              this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts.push({
                'impact': this.vulnerabilityForm.value.intVul,
                'scope': 'Integrity',
                'technicalImpacts': []
              });
            }

            if ((this.vulnerabilityForm.value.avaVul !== undefined) && (this.vulnerabilityForm.value.avaVul !== '') &&
              (this.vulnerabilityForm.value.avaVul !== null)) {

              this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts.push({
                'impact': this.vulnerabilityForm.value.avaVul,
                'scope': 'Availability',
                'technicalImpacts': []
              });
            }

            if ((this.vulnerabilityForm.value.effVul !== undefined) && (this.vulnerabilityForm.value.effVul !== '') &&
              (this.vulnerabilityForm.value.effVul !== null)) {
              this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts.push({
                'impact': this.vulnerabilityForm.value.effVul,
                'scope': 'Efficiency',
                'technicalImpacts': []
              });
            }
          }
        }
      }

      this.oldIdVul = data[0].identifier;

      this.vulnerabilityForm.reset();
      this.associatedThreatsForm = [];
      this.affectedCategoriesForm = [];

      for (const mod in this.modifiedVulnerabilitiesList) {
        if (data[0].identifier === this.modifiedVulnerabilitiesList[mod].data[0].identifier) {

          this.nameVulForm = this.modifiedVulnerabilitiesList[mod].data[0].name;
          this.catalogueVulForm = this.modifiedVulnerabilitiesList[mod].data[0].catalogue;
          this.catalogueIDVulForm = this.modifiedVulnerabilitiesList[mod].data[0].catalogueId;
          this.scoringVulForm = this.modifiedVulnerabilitiesList[mod].data[0].score.scoringType;
          this.vulnerabilityForm.controls['descriptionVuln'].setValue(this.modifiedVulnerabilitiesList[mod].data[0].description);
          this.vulnerabilityForm.controls['scoVul'].setValue(this.modifiedVulnerabilitiesList[mod].data[0].score.score);
          this.vulnerabilityForm.controls['explVul'].setValue(this.modifiedVulnerabilitiesList[mod].data[0].score.exploitability);

          for (const i in this.modifiedVulnerabilitiesList[mod].data[0].affectedAssetsCategories) {

            if (this.affectedCategoriesForm.indexOf(this.modifiedVulnerabilitiesList[mod].data[0].affectedAssetsCategories[i]) === -1) {
              this.affectedCategoriesForm.push(this.modifiedVulnerabilitiesList[mod].data[0].affectedAssetsCategories[i]);
            }
          }

          for (const j in this.modifiedVulnerabilitiesList[mod].data[0].associatedThreats) {

            this.associatedThreatsForm.push(this.modifiedVulnerabilitiesList[mod].data[0].associatedThreats[j]);
          }


          for (const cons in this.modifiedVulnerabilitiesList[mod].data[0].score.consequences) {
            for (const sec in this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts) {
              if (this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts[sec].scope === 'Availability') {
                const d = this.maxImpact(this.selectedAvailabilityVul,
                  this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts[sec].impact);

                this.vulnerabilityForm.controls['avaVul'].setValue(d);
              }

              if (this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts[sec].scope === 'Integrity') {

                const a = this.maxImpact(this.selectedIntegrityVul,
                  this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts[sec].impact);
                this.vulnerabilityForm.controls['intVul'].setValue(a);
              }

              if (this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts[sec].scope === 'Efficiency') {

                const b = this.maxImpact(this.selectedEfficiencyVul,
                  this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts[sec].impact);

                this.vulnerabilityForm.controls['effVul'].setValue(b);
              }

              if (this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts[sec].scope === 'Confidentiality') {
                const c = this.maxImpact(this.selectedConfidentialityVul,
                  this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].securityImpacts[sec].impact);
                this.vulnerabilityForm.controls['confVul'].setValue(c);
              }
            }

            // I always put the last description because I have always just a consequence otherwise I should take the worst
            this.vulnerabilityForm.controls['scoredesVul'].setValue(
              this.modifiedVulnerabilitiesList[mod].data[0].score.consequences[cons].description);
          }
        }
      }
    }
  }


  // save in modifiedVulnerabilitiesList in order to not lose the last modification
  modifiedBeforeToSave(data) {

    for (const old in this.modifiedVulnerabilitiesList) {

      if (this.oldIdVul === this.modifiedVulnerabilitiesList[old].data[0].identifier) {

        this.modifiedVulnerabilitiesList[old].data[0].description = this.vulnerabilityForm.value.descriptionVuln;
        this.modifiedVulnerabilitiesList[old].data[0].score.exploitability = this.vulnerabilityForm.value.explVul;
        this.modifiedVulnerabilitiesList[old].data[0].score.score = this.vulnerabilityForm.value.scoVul;
        this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].description = this.vulnerabilityForm.value.scoredesVul;
        this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts = [];

        if ((this.vulnerabilityForm.value.confVul !== undefined) && (this.vulnerabilityForm.value.confVul !== '') &&
          (this.vulnerabilityForm.value.confVul !== null)) {

          this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts.push(
            {'impact': this.vulnerabilityForm.value.confVul, 'scope': 'Confidentiality', 'technicalImpacts': []});
        }

        if ((this.vulnerabilityForm.value.intVul !== undefined) && (this.vulnerabilityForm.value.intVul !== '') &&
          (this.vulnerabilityForm.value.intVul !== null)) {

          this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts.push(
            {'impact': this.vulnerabilityForm.value.intVul, 'scope': 'Integrity', 'technicalImpacts': []});
        }

        if ((this.vulnerabilityForm.value.avaVul !== undefined) && (this.vulnerabilityForm.value.avaVul !== '') &&
          (this.vulnerabilityForm.value.avaVul !== null)) {

          this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts.push(
            {'impact': this.vulnerabilityForm.value.avaVul, 'scope': 'Availability', 'technicalImpacts': []});
        }

        if ((this.vulnerabilityForm.value.effVul !== undefined) && (this.vulnerabilityForm.value.effVul !== '') &&
          (this.vulnerabilityForm.value.effVul !== null)) {
          this.modifiedVulnerabilitiesList[old].data[0].score.consequences[0].securityImpacts.push(
            {'impact': this.vulnerabilityForm.value.effVul, 'scope': 'Efficiency', 'technicalImpacts': []});
        }
      }
    }

    this.oldIdVul = undefined;
    this.selectedVulnerability = undefined;
    this.vulnerabilitiesList = JSON.parse(JSON.stringify(this.modifiedVulnerabilitiesList));
    this.replaceVulnerability();
    this.displayEditVulnerability = false;
    this.vulnerabilityForm.reset();
  }


  // replace old vulnerabilities with the new(modified by user) in the idAssets(combination
  // between assets and their associated vulnerabilities)
  replaceVulnerability() {

    for (const k in this.modifiedVulnerabilitiesList) {
      for (const j in this.idAssets) {

        let a = this.idAssets[j].vulnerabilities.findIndex(i =>
          i.data[0].identifier === this.modifiedVulnerabilitiesList[k].data[0].identifier);

        if (a !== -1) {
          this.idAssets[j].vulnerabilities[a] = this.modifiedVulnerabilitiesList[k];
          a = -1;
        }
      }
    }
    this.changeColor();
  }

  closeForm() {

    this.modifiedVulnerabilitiesList = JSON.parse(JSON.stringify(this.vulnerabilitiesList));
    this.oldIdVul = undefined;
    this.selectedVulnerability = undefined;
    this.displayEditVulnerability = false;
    this.vulnerabilityForm.reset();
  }

  editVulnerability() {
    if (this.modifiedVulnerabilitiesList.length > 0) {
      this.vulnerabilitiesList = JSON.parse(JSON.stringify(this.modifiedVulnerabilitiesList));
    }

    this.vulnerabilityModel.vulnerabilities = [];
    for (const i in this.vulnerabilitiesList) {

      this.vulnerabilityModel.vulnerabilities.push(this.vulnerabilitiesList[i].data[0]);
    }
    for (const i in this.vulnerabilityModel.vulnerabilities) {

      this.vulnerabilityModel.vulnerabilities[i].assessmentVulnerability = true;
    }
    this.sendVulnerabilityModel();
  }

  createRiskModel(idAssets) {

    const scenario = this.riskModel.scenarios;
    const oldScenario = this.riskModel.scenarios;
    for (const i in idAssets) {
      for (const j in idAssets[i].vulnerabilities) {
        for (const y in idAssets[i].vulnerabilities[j].data[0].score.consequences) {
          for (const k in idAssets[i].vulnerabilities[j].data[0].score.consequences[y].securityImpacts) {

            if (scenario.findIndex(check =>
              check.impactScope === idAssets[i].vulnerabilities[j].data[0].score.consequences[y].securityImpacts[k].scope &&
              check.assetId === idAssets[i].identifier &&
              check.vulnerabilityId === idAssets[i].vulnerabilities[j].data[0].identifier) === -1) {

              scenario.push({
                'impactScope': idAssets[i].vulnerabilities[j].data[0].score.consequences[y].securityImpacts[k].scope,
                'safeguardIds': [], 'assetId': idAssets[i].identifier,
                'threatId': null, 'vulnerabilityId': idAssets[i].vulnerabilities[j].data[0].identifier,
                'identifier': UUID.UUID(),
                'objType': 'RiskModel'
              });
            }
          }
        }
      }
    }
    this.riskModel.scenarios = scenario;

    // it adds old vulnerabilities of an old scenario in the current vulnerability Model
    for (const old in oldScenario) {
      for (const vul in this.vulnerabilityModel.vulnerabilities) {

        if (this.vulnerabilityModel.vulnerabilities[vul].identifier === oldScenario[old].vulnerabilityId) {
          const indexVul = this.vulnerabilitiesList.findIndex(b => b.data[0].identifier === oldScenario[old].vulnerabilityId);

          if (indexVul === -1) {
            const newVulnerability = [];
            newVulnerability.push(this.vulnerabilityModel.vulnerabilities[vul]);
            this.vulnerabilitiesList.push({
              'name': this.vulnerabilityModel.vulnerabilities[vul].name,
              'data': newVulnerability
            });
          }
        }
      }
    }
    this.editVulnerability();
  }

  checkedRepository() {
    if (this.selectedRepository !== undefined) {
      this.repository = this.selectedRepository;

      const filter = {
        'filterMap': {
          'METHODOLOGY': this.selectedRepository === 'ALL' ? null : this.selectedRepository,
          'FULL': false
        }
      };
      this.subscriptions.push(
        this.dataService.loadVulnerabilityRepository(JSON.stringify(filter)).subscribe(response => {

            this.mehari = response;
            this.createMehariTree();
          }, err => {
            throw err;
          }
        ));
    }
  }

  getRiskModel() {

    const idProcedure = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadRiskModel(JSON.stringify(idProcedure)).subscribe((response: ModelObject) => {
        this.riskModel = JSON.parse(response.jsonModel);
        const a = {
          'filterMap': {
            'IDENTIFIER': sessionStorage.getItem('sysprojectId'),
            'PROJECT': sessionStorage.getItem('idProject')
          }
        };
        this.createGraph();
        this.getRequirements(JSON.stringify(a));
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  getRiskModelAfterUpdate() {


    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadRiskModel(JSON.stringify(a)).subscribe((response: ModelObject) => {
        this.riskModel = JSON.parse(response.jsonModel);
        ;
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
        this.getRiskModel();
        this.lockService.addLock(response.objectIdentifier, response.lockedBy);
      }, err => {
        this.blocked = false;
        throw err;
      }));

  }

  showFailed(s: string) {
    this.msgsVuln = [];
    this.msgsVuln.push({severity: 'error', summary: 'Error!', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
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

        this.tot = JSON.parse(response.jsonModel);
        this.nodes = this.tot.nodes;
        this.edges = this.tot.edges;
        this.getVulnerabilityModel();
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }


  sendRiskModel() {

    let completeList = {};
    completeList = {
      'jsonModel': (JSON.stringify(this.riskModel, null, 2)),
      'objectIdentifier': this.riskModel.identifier
    };
    this.blocked = true;

    this.subscriptions.push(
      this.dataService.updateRiskModel(completeList).subscribe(response => {
        this.blocked = false;
        this.showSuccess();
        if (JSON.parse(response).otherModelsStatus === 'UPDATED') {
          this.idAssets = [];
          this.selectedFiles = [];
          this.canvas.clear();
          this.mapPosition.clear();
          this.getAsset();
        } else {
          this.getRiskModelAfterUpdate();
        }
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  sendVulnerabilityModel() {

    this.blocked = true;
    let completeList = {};
    completeList = {
      'jsonModel': (JSON.stringify(this.vulnerabilityModel, null, 2)),
      'objectIdentifier': this.vulnerabilityModel.identifier
    };

    this.messageService.add({
      key: 'tc',
      severity: 'info',
      summary: 'Info Message',
      detail: VulnerabilitiesComponent.INFO_SERVER_FILTER
    });
    this.subscriptions.push(
      this.dataService.updateVulnerability(JSON.stringify(completeList, null, 2)).subscribe(response => {
        this.sendRiskModel();
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  getRequirements(idsystemproject) {

    const a = {
      'filterMap': {
        'SYS_PROJECT': sessionStorage.getItem('idsystemproject'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };

    this.subscriptions.push(
      this.dataService.loadRequirementsById(JSON.stringify(a)).subscribe(response => {
        this.requirementsList = response;
      }));
  }

  isLockedByCurrentUser() {
    return this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername');
  }

  ngOnDestroy() {
    this.lockService.removeLock(this.vulnerabilityModel.identifier);
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
