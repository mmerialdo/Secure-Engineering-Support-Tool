/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="threats.component.ts"
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
import {Subscription} from 'rxjs/internal/Subscription';
import {PermissionType} from '../../permission-type.class';
import {Permission} from '../../permission.class';
import {FormBuilder, Validators} from '@angular/forms';
import {TreeNode, ConfirmationService, MessageService} from 'primeng/primeng';
import {DataService} from '../../dataservice';
import {SelectItem} from 'primeng/primeng';
import {UUID} from 'angular2-uuid';
import {Message} from 'primeng/components/common/api';
import {select, Store} from '@ngrx/store';
import {copyThreats, pasteThreats, resetThreats} from '../../shared/store/actions/threats.actions';
import {fetchCopiedThreatsStatus, selectThreatsFromCache} from '../../shared/store/reducers/threats.reducer';
import {Observable} from 'rxjs';
import {take} from 'rxjs/operators';
import {LockService} from '../../shared/service/lock-service';
import {ModelObject} from '../../model-object';


declare var draw2d: any;

declare var CollapsibleShapeThreat: any;

declare var window: any;

@Component({
  selector: 'app-threats',
  templateUrl: './threats.component.html',
  styleUrls: ['./threats.component.scss'],
  providers: [ConfirmationService]
})
export class ThreatsComponent implements OnInit, OnDestroy {

  private permission: Permission;
  public enableSaveButton = true;
  public thereAreChanges = false;

  canvas: any;
  public edges: any;
  public nodes: any;

  // keeps elements position
  private mapPosition = new Map<Number, String>();
  private maxColumns = 5;

  files: TreeNode[];
  public selectedFiles = [];


  public mehariVulns: TreeNode[];

  public threatTree: TreeNode[];
  public selectedThreatArray = [];
  public ajson = [];
  public tot: any;

  public mehari;

  public threatsList;

  // List threats of the selected asset
  public stringThreat;

  // id selected Asset
  public currentId;
  public riskModel;
  public threatModel;
  public vulnerabilityModel;

  // list of all associated vulnerabilities
  public vulnerabilitiesList = [];

  // it is used for temporary modifies
  public modifiedVulnerabilitiesList = [];

  // to show different repository div
  public repository = '';

  // list vulnerabilities repositories
  public repositoryList = [];

  // selected Repository
  public selectedRepository;

  public selectedAsset;

  // array that contains the association assets id and threats for the threats model
  public idAssets = [];

  // it is used for temporary modifies
  public oldIdVul;

  // they are used to show Threats' details

  public showDetails = false;
  public affectedCategories = [];
  public relatedVulnerabilities = [];

  public descriptionTh;
  public nameVul;
  public catalogueVul;
  public scoreTh;
  public catalogueIDVul;

  public likeTh;

  public actorName;
  public actorCat;
  public actorCatId;
  public actorDescription;

  public eventName;
  public eventCat;
  public eventCatId;
  public eventDescription;

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
  public associatedVulnerabilitiesForm = [];
  public associatedThreatsForm = [];
  // public descriptionVul;
  public nameThForm;
  public catalogueThForm;
  public catalogueIDThForm;
  public threatClass;
  // public scoreVul;
  public scoringThForm;
  // public exploiVul;
  // public avaVul;
  // public intVul;
  // public conVul;
  // public effVul;
  // public descriptionScore;
  public placeName;
  public placeCat;
  public placeCatId;
  public placeDescription;

  public timeName;
  public timeCat;
  public timeCatId;
  public timeDescription;

  public accessName;
  public accessCat;
  public accessCatId;
  public accessDescription;

  public processName;
  public processCat;
  public processCatId;
  public processDescription;


  public selectedScore;
  public scoreList = [];
  public selectedLikeli;
  public likeliList = [];

  // to show Edit Vulnerabilities form
  public displayEditVulnerability = false;

  // to show Threat Information
  public displayThreatInformation = false;

  // to show associated Vulnerability List in the form
  public associatedVulnerabilities: SelectItem[];

  // selected associated vulnerability
  public selectedVulnerability;

  // form to edit the vulnerabilities
  threatForm: any;

  // to block the interactions in the page
  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsThreats: Message[] = [];
  // to show the requirements names

  public requirementsList: any;
  public copied$: Observable<boolean>;

  public static WARN_NO_VULNERABILITIES = 'It is not possible to add all these Threats. Some of them don\'t have any associated Vulnerabilities!';
  public static WARN_NO_ASSET_MATCH = 'It is not possible to add all these Threats. Some of their Secondary Categories are different from the Asset\'s Category';
  public static WARN_NO_VULN_MATCH = 'It is not possible to add all these Threats. Some of them don\'t match with the Asset\'s Vulnerabilities';
  public static INFO_SERVER_FILTER = 'Threats will be filtered during saving considering the existing risk scenario taxonomy';

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
      removeComponent: (value) => this.removeAsset(value),
      component: this
    };

    this.threatForm = this.formBuilder.group({

      'descriptionTh': ['', Validators.required],
      'scoTh': ['', Validators.required],
      'likeliTh': ['', Validators.required]

    });
    this.repositoryList = ['ALL', 'MEHARI', 'CUSTOM'];

    this.scoreList = ['VERY_LOW', 'LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH'];
    this.likeliList = ['LOW', 'MEDIUM', 'HIGH', 'VERY_HIGH'];
  }

  ngOnInit() {
    this.copied$ = this.store.pipe(select(fetchCopiedThreatsStatus));

    // paste.unsubscribe();
    this.getAsset();
  }

  // function return true if find any threat in array which doesn't have second category in affected categories
  validatePasteThreats(threats: Array<any>, secCat: string): boolean {

    return threats.some(threat => !threat.value[0].affectedAssetsCategories.filter(cat => cat).some(cat => cat === secCat));
  }

  addThreatsToAll(): void {

    const figure = this.canvas.getFigures().data;
    const list = this.associatedVulnerabilities.map(item => ({label: item.label, data: item.value}));

    figure.forEach(asset => {
      const text = asset.children.data[2].figure.getText();
      if (!text || text !== '') {
        this.addThreatsWithCheck(asset, list, false);
      }
    });

    this.changeColor();
    this.closeForm();
  }

  addThreatsToCache(): void {
    const cachedThreats = JSON.parse(JSON.stringify(this.associatedVulnerabilities));
    this.store.dispatch(copyThreats({cachedThreats}));
    this.displayEditVulnerability = false;
    this.messageService.add({key: 'tc', severity: 'info', summary: 'Info Message', detail: 'Threats Copied'});
  }

  fetchThreatsFromCache(): void {
    const paste = this.store.pipe(select(selectThreatsFromCache)).pipe(take(1)).subscribe(threats => {
      this.fetchThreats(threats);
    });
    this.closeForm();
  }

  fetchThreats(threats: any): void {

    const list = threats.map(item => ({label: item.label, data: item.value}));
    for (const asset of this.selectedAsset) {
      this.addThreatsWithCheck(asset, list, true);
    }
  }

  setPermission() {

    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));
    this.enableSaveButton = (this.permission.create.findIndex(element => element === PermissionType.ThreatModel) >= 0);
  }


  // create the space to draw the cards
  createGraph() {

    this.canvas = new draw2d.Canvas('canvas-div');

    // this.canvas.installEditPolicy(new draw2d.policy.canvas.ReadOnlySelectionPolicy( ));

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
    this.createTree();
    this.setPermission();
    this.blocked = false;
  }


  // to create a widget
  createCard(s: string, id: string) {

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
    const row = Math.floor(nextPosition/this.maxColumns);
    const figure = new CollapsibleShapeThreat({x: 150 * column, y: 150 * row}, id);
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

    const string = this.checkedRiskModel(id);

    if (string != '') {
      figure.children.data[4].figure.setText(string);
    }

    this.canvas.add(figure);
    this.mapPosition.set(nextPosition, id);
    this.checkedRiskModelThreat(id, figure);
  }

  // returns list of all the vulnerabilities already associated
  checkedRiskModel(id): string {

    const vulnerabilityRisk = [];

    for (const i in this.riskModel.scenarios) {

      if (this.riskModel.scenarios[i].assetId === id) {

        // to avoid duplicates
        if (vulnerabilityRisk.indexOf(this.riskModel.scenarios[i].vulnerabilityId) === -1) {

          vulnerabilityRisk.push(this.riskModel.scenarios[i].vulnerabilityId);
        }
      }
    }

    return this.labelsFromRisk(vulnerabilityRisk, id);
  }


  // to find name and score of the vulnerabilities
  labelsFromRisk(riskVulnList, id): string {

    let string = '';
    const vulnList = [];

    for (const i in riskVulnList) {

      for (const j in this.vulnerabilityModel.vulnerabilities) {
        if (riskVulnList[i] === this.vulnerabilityModel.vulnerabilities[j].identifier) {

          if (string === '') {
            string = string + this.vulnerabilityModel.vulnerabilities[j].name;
          } else {

            string = string + '\n' + this.vulnerabilityModel.vulnerabilities[j].name;
          }
        }
      }
    }

    return string;
  }

  // returns list of all the threats already associated
  checkedRiskModelThreat(id, figure) {

    const threat = [];

    for (const i in this.riskModel.scenarios) {

      if (this.riskModel.scenarios[i].assetId === id) {
        // vulnerabilityRisk.push(this.riskModel.scenarios[i].vulnerabilityId)
        const index = this.threatModel.threats.findIndex((n) => n.identifier === this.riskModel.scenarios[i].threatId);

        if ((this.threatModel.threats[index] != '') && (this.threatModel.threats[index] != undefined) && (this.threatModel.threats[index] != null)) {
          threat.push(this.threatModel.threats[index]);
        }
      }
    }

    this.buildLeafThreat(threat, figure);
  }

  buildLeafThreat(threat, figure) {

    const leaves = [];
    let cat = [];
    for (const i in threat) {

      cat = this.findaffectedAssetsCategories(threat[i].affectedAssetsCategories);

      for (const j in threat[i].affectedAssetsCategories) {
        const a = threat[i].affectedAssetsCategories[j];
        let indexOfLabel = -1;
        indexOfLabel = leaves.findIndex(i => i.label === a);

        if (indexOfLabel === -1) {
          const array = [];
          let obj = {};

          array.push({
            'label': threat[i].name, 'data': [{
              'nodeType': 'Threat',
              'assessmentThreat': threat[i].assessmentThreat,
              'canBeSelected': threat[i].canBeSelected,
              'description': threat[i].description,
              'elementType': threat[i].elementType,
              'phase': threat[i].phase,
              'score': threat[i].score,
              'name': threat[i].name,
              'catalogue': threat[i].catalogue,
              'catalogueId': threat[i].catalogueId,
              'threatClass': threat[i].threatClass,
              'affectedAssetsCategories': cat,
              'identifier': threat[i].identifier,
              'actor': threat[i].actor,
              'event': threat[i].event,
              'place': threat[i].place,
              'time': threat[i].time,
              'access': threat[i].access,
              'process': threat[i].process,
              'lastUpdate': threat[i].lastUpdate,
              'rawText': threat[i].rawText,
              'associatedVulnerabilities': threat[i].associatedVulnerabilities,
              'applicablePlatform': threat[i].applicablePlatform,
              'mitigations': threat[i].mitigations,
              'objType': 'ThreatModel'
            }]
          });
          obj = {'label': a, 'data': [{'nodeType': 'Category'}], 'children': array};

          leaves.push(obj);
        } else {

          leaves[indexOfLabel].children.push({
            'label': threat[i].name, 'data': [{
              'nodeType': 'Threat',
              'canBeSelected': threat[i].canBeSelected,
              'assessmentThreat': threat[i].assessmentThreat,
              'elementType': threat[i].elementType,
              'description': threat[i].description,
              'phase': threat[i].phase,
              'score': threat[i].score,
              'name': threat[i].name,
              'catalogue': threat[i].catalogue,
              'catalogueId': threat[i].catalogueId,
              'threatClass': threat[i].threatClass,
              'affectedAssetsCategories': cat,
              'identifier': threat[i].identifier,
              'actor': threat[i].actor,
              'event': threat[i].event,
              'place': threat[i].place,
              'time': threat[i].time,
              'access': threat[i].access,
              'process': threat[i].process,
              'lastUpdate': threat[i].lastUpdate,
              'rawText': threat[i].rawText,
              'associatedVulnerabilities': threat[i].associatedVulnerabilities,
              'applicablePlatform': threat[i].applicablePlatform,
              'mitigations': threat[i].mitigations,

              'objType': 'ThreatModel'
            }]
          });
        }
      }
    }

    this.selectedThreatArray = [];
    const a = [];
    a.push(figure);
    this.selectedAsset = a;

    for (const leaf in leaves) {

      for (const k in leaves[leaf].children) {
        const indexOfThreat = this.selectedThreatArray.findIndex(i => i.label === leaves[leaf].children[k].label);
        if (indexOfThreat === -1) {
          this.selectedThreatArray.push(leaves[leaf].children[k]);
        }
      }
    }

    this.addThreats();
  }


  addAssets() {
    for (const i in this.selectedFiles) {

      if (this.selectedFiles[i].data != null && this.selectedFiles[i].data.nodeType === 'Asset') {

        if (this.canvas.getFigures().data.findIndex(uni => uni.id === this.selectedFiles[i].data.identifier) === -1) {

          let index = -1;
          const a = this.selectedFiles[i].data.identifier;
          index = this.idAssets.findIndex(i => i.identifier === a);

          if (index === -1) {
            this.idAssets.push({
              'identifier': this.selectedFiles[i].data.identifier,
              'secondaryCategory': this.selectedFiles[i].data.category,
              'vulnerabilities': []
            });
          }
          this.createCard(this.selectedFiles[i].label, this.selectedFiles[i].data.identifier);
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


  addThreats() {

    for (const asset of this.selectedAsset) {
      this.addThreatsWithCheck(asset, this.selectedThreatArray, true);
    }
  }

  private addThreatsWithCheck(asset: any, threatsList: any[], showMessage: boolean) {
    const idVul = [];
    let string = '';
    const assVuln = [];
    let isMatchingAssetCategory = true;
    let isMatchingVulnerability = true;
    let hasVulnerability = true;

    for (const threat of threatsList) {

      // it checks if it is a Threat and if it canBeSelected
      if ((threat.data[0].nodeType === 'Threat') && (threat.data[0].canBeSelected)) {

        // It checks if the threat is already in the ThreatModel
        // If it is, it is better to use the Threat in the ThreatModel because it may be updated with respect to the selected threat
        const threatCatalogueId = threat.data[0].catalogueId;

        for (const threatId in this.threatModel.threats) {
          if (this.threatModel.threats[threatId].catalogueId === threatCatalogueId) {

            threat.data[0].score = this.threatModel.threats[threatId].score;
            threat.data[0].likelihood = this.threatModel.threats[threatId].likelihood;
            threat.data[0].description = this.threatModel.threats[threatId].description;
          }
        }

        // to check if the asset contais vulnerabilities
        if (this.riskModel.scenarios.findIndex(scen => scen.assetId === asset.id && scen.vulnerabilityId != null) != -1) {
          const k = this.nodes.findIndex(nod => nod.identifier === asset.id);

          if (k != -1) {

            // let index = idVul.indexOf(threat.data[0].identifier);
            const indexIdAsset = this.idAssets.findIndex(id => id.identifier === this.nodes[k].identifier);
            const index = this.idAssets[indexIdAsset].vulnerabilities.findIndex(vul => vul.data[0].catalogueId === threat.data[0].catalogueId);

            // to avoid duplicates when we are adding new threats
            if (index === -1) {

              const indexAssVul = assVuln.findIndex(indAssVul => indAssVul.data[0].catalogueId === threat.data[0].catalogueId);

              // to avoid duplicates when we are loading "old" threats of the risk model
              if (indexAssVul === -1) {

                if (asset.children.data[2].figure.getText().length > 0) {
                  string = asset.children.data[2].figure.getText();
                }
                const same = this.checkSameCategory(this.nodes[k].category, threat.data[0].affectedAssetsCategories);
                // it checks if there are Threats that have the same Asset's categories
                if (same) {
                  if (threat.data[0].associatedVulnerabilities.length > 0) {
                    // Creating the array of vulnerabilities associated to the asset

                    // to be optimized
                    const vulnerabilityCataloguesIdsAsset = [];

                    for (const j in this.riskModel.scenarios) {
                      if (this.nodes[k].identifier === this.riskModel.scenarios[j].assetId) {

                        let vulnerabilityCatalogueId = '';

                        for (const t in this.vulnerabilityModel.vulnerabilities) {
                          if (this.riskModel.scenarios[j].vulnerabilityId === this.vulnerabilityModel.vulnerabilities[t].identifier) {
                            vulnerabilityCatalogueId = this.vulnerabilityModel.vulnerabilities[t].catalogueId;
                            break;
                          }
                        }
                        if (vulnerabilityCatalogueId != '') {

                          if (vulnerabilityCataloguesIdsAsset.indexOf(vulnerabilityCatalogueId) === -1) {
                            vulnerabilityCataloguesIdsAsset.push(vulnerabilityCatalogueId);
                          }
                        }
                      }
                    }
                    let sameVulnCat = false;

                    for (const t in vulnerabilityCataloguesIdsAsset) {
                      for (const f in threat.data[0].associatedVulnerabilities) {
                        if (threat.data[0].associatedVulnerabilities[f] === vulnerabilityCataloguesIdsAsset[t]) {
                          sameVulnCat = true;
                          break;
                        }
                      }
                      if (sameVulnCat) {
                        break;
                      }
                    }
                    // it checks if there are Vulnerabilities associated to the Threats that have the same Asset's categories
                    if (sameVulnCat) {

                      // add new id in the associated threats
                      idVul.push(threat.data[0].identifier);
                      if (this.vulnerabilitiesList.findIndex(z => z.data[0].identifier === threat.data[0].identifier) === -1) {
                        this.vulnerabilitiesList.push({
                          'name': threat.label,
                          'data': threat.data
                        });
                      }
                      assVuln.push({'name': threat.label, 'data': threat.data});

                      if (string === '') {
                        string = string + threat.label;
                        asset.children.data[2].figure.setText(string);
                      } else {

                        string = string + '\n' + threat.label;
                        asset.children.data[2].figure.setText(string);
                      }
                    } else {
                      isMatchingVulnerability = false;
                    }
                  } else {
                    hasVulnerability = false;
                  }
                } else {
                  isMatchingAssetCategory = false;
                }
              }
            } else {

              // let string="";
              for (const oldString in this.idAssets[indexIdAsset].vulnerabilities) {

                if (string === '') {
                  string = string + this.idAssets[indexIdAsset].vulnerabilities[oldString].data[0].name;
                } else {

                  if (string.indexOf(this.idAssets[indexIdAsset].vulnerabilities[oldString].data[0].name) === -1) {
                    string = string + '\n' + this.idAssets[indexIdAsset].vulnerabilities[oldString].data[0].name;
                  }
                }
              }
              asset.children.data[2].figure.setText(string);
            }
          }

        } else {
          hasVulnerability = false;
        }
      }
    }

    for (const q in this.idAssets) {

      if (this.idAssets[q].identifier === asset.id) {

        for (const fg in assVuln) {

          this.idAssets[q].vulnerabilities.push(assVuln[fg]);
        }
      }
    }
    if (showMessage) {
      if (!isMatchingVulnerability) {
        this.showInfo(ThreatsComponent.WARN_NO_VULN_MATCH);
      }
      if (!hasVulnerability) {
        this.showInfo(ThreatsComponent.WARN_NO_VULNERABILITIES);
      }
      if (!isMatchingAssetCategory) {
        this.showInfo(ThreatsComponent.WARN_NO_ASSET_MATCH);
      }
    }

    this.selectedThreatArray = [];
    this.setColor(this.idAssets);
  }

  findCategories(list) {

    const total = [];
    for (const i in list) {
      for (const j in this.vulnerabilityModel.vulnerabilities) {
        if (list[i] === this.vulnerabilityModel.vulnerabilities[j].catalogueId) {

          for (const k in this.vulnerabilityModel.vulnerabilities[j].affectedAssetsCategories) {
            total.push(this.vulnerabilityModel.vulnerabilities[j].affectedAssetsCategories[k]);
          }
        }
      }
    }

    return total;
  }

  checkSameCategory(assetCat, vulCat): boolean {

    if ((assetCat === null) || (assetCat === undefined) || (vulCat.length === 0)) {
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

      listOld.splice(parseInt(toDelete[del]), 1);
    }

    for (const add in toAdd) {

      listOld.push(listNew[toAdd[add]]);
    }

    return listOld;
  }

  correspondingColor(s): string {

    if (s === 'VERY_HIGH') {

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

      // for each associated vulnerabilities
      for (const y in array[i].vulnerabilities) {

        impact = this.maxImpact(impact, array[i].vulnerabilities[y].data[0].score.likelihood);
      }

      if (impact != '') {

        for (const h in this.selectedAsset) {

          if (array[i].identifier === this.selectedAsset[h].id) {

            this.selectedAsset[h].setColor(this.correspondingColor(impact));
            this.selectedAsset[h].children.data[0].figure.setBackgroundColor(this.correspondingColor(impact));
          }
        }
      }
    }
  }

  vulSelect(event) {

    const index = -1;

    if (event.node.data[0].nodeType === 'Threat') {

      // this.natureVul=event.node.data[0].causalNature;
      // this.phaseVul=event.node.data[0].phase;

      this.scoreTh = event.node.data[0].score.score;

      this.likeTh = event.node.data[0].score.likelihood;
      this.affectedCategories = event.node.data[0].affectedAssetsCategories;
      this.relatedVulnerabilities = event.node.data[0].associatedVulnerabilities;
      this.descriptionTh = event.node.data[0].description;
      this.nameVul = event.node.data[0].name;
      this.catalogueVul = event.node.data[0].catalogue;
      this.catalogueIDVul = event.node.data[0].catalogueId;
      this.threatClass = event.node.data[0].threatClass;


      this.actorName = event.node.data[0].actor.name;
      this.actorCat = event.node.data[0].actor.catalogue;
      this.actorCatId = event.node.data[0].actor.catalogueId;
      this.actorDescription = event.node.data[0].actor.description;

      this.eventName = event.node.data[0].event.name;
      this.eventCat = event.node.data[0].event.catalogue;
      this.eventCatId = event.node.data[0].event.catalogueId;
      this.eventDescription = event.node.data[0].event.description;

      this.placeName = event.node.data[0].place.name;
      this.placeCat = event.node.data[0].place.catalogue;
      this.placeCatId = event.node.data[0].place.catalogueId;
      this.placeDescription = event.node.data[0].place.description;

      this.timeName = event.node.data[0].time.name;
      this.timeCat = event.node.data[0].time.catalogue;
      this.timeCatId = event.node.data[0].time.catalogueId;
      this.timeDescription = event.node.data[0].time.description;

      this.accessName = event.node.data[0].access.name;
      this.accessCat = event.node.data[0].access.catalogue;
      this.accessCatId = event.node.data[0].access.catalogueId;
      this.accessDescription = event.node.data[0].access.description;

      this.processName = event.node.data[0].process.name;
      this.processCat = event.node.data[0].process.catalogue;
      this.processCatId = event.node.data[0].process.catalogueId;
      this.processDescription = event.node.data[0].process.description;

    } else {

      this.scoreTh = event.node.children[event.node.children.length - 1].data[0].score.score;
      this.likeTh = event.node.children[event.node.children.length - 1].data[0].score.likelihood;

      this.affectedCategories = event.node.children[event.node.children.length - 1].data[0].affectedAssetsCategories;
      this.relatedVulnerabilities = event.node.children[event.node.children.length - 1].data[0].associatedVulnerabilities;
      this.descriptionTh = event.node.children[event.node.children.length - 1].data[0].description;
      this.nameVul = event.node.children[event.node.children.length - 1].data[0].name;
      this.catalogueVul = event.node.children[event.node.children.length - 1].data[0].catalogue;
      this.catalogueIDVul = event.node.children[event.node.children.length - 1].data[0].catalogueId;
      this.threatClass = event.node.children[event.node.children.length - 1].data[0].threatClass;

      // this.scoringVul = event.node.children[event.node.children.length - 1].data[0].score.scoringType;

      this.actorName = event.node.children[event.node.children.length - 1].data[0].actor.name;
      this.actorCat = event.node.children[event.node.children.length - 1].data[0].actor.catalogue;
      this.actorCatId = event.node.children[event.node.children.length - 1].data[0].actor.catalogueId;
      this.actorDescription = event.node.children[event.node.children.length - 1].data[0].actor.description;

      this.eventName = event.node.children[event.node.children.length - 1].data[0].event.name;
      this.eventCat = event.node.children[event.node.children.length - 1].data[0].event.catalogue;
      this.eventCatId = event.node.children[event.node.children.length - 1].data[0].event.catalogueId;
      this.eventDescription = event.node.children[event.node.children.length - 1].data[0].event.description;

      this.placeName = event.node.children[event.node.children.length - 1].data[0].place.name;
      this.placeCat = event.node.children[event.node.children.length - 1].data[0].place.catalogue;
      this.placeCatId = event.node.children[event.node.children.length - 1].data[0].place.catalogueId;
      this.placeDescription = event.node.children[event.node.children.length - 1].data[0].place.description;

      this.timeName = event.node.children[event.node.children.length - 1].data[0].time.name;
      this.timeCat = event.node.children[event.node.children.length - 1].data[0].time.catalogue;
      this.timeCatId = event.node.children[event.node.children.length - 1].data[0].time.catalogueId;
      this.timeDescription = event.node.children[event.node.children.length - 1].data[0].time.description;

      this.accessName = event.node.children[event.node.children.length - 1].data[0].access.name;
      this.accessCat = event.node.children[event.node.children.length - 1].data[0].access.catalogue;
      this.accessCatId = event.node.children[event.node.children.length - 1].data[0].access.catalogueId;
      this.accessDescription = event.node.children[event.node.children.length - 1].data[0].access.description;

      this.processName = event.node.children[event.node.children.length - 1].data[0].process.name;
      this.processCat = event.node.children[event.node.children.length - 1].data[0].process.catalogue;
      this.processCatId = event.node.children[event.node.children.length - 1].data[0].process.catalogueId;
      this.processDescription = event.node.children[event.node.children.length - 1].data[0].process.description;

    }


    this.showDetails = true;

  }

  maxImpact(oldImp: string, newImp: string): string {


    if (newImp === 'VERY_HIGH') {
      oldImp = 'VERY_HIGH';
    } else if ((newImp === 'HIGH') && (oldImp != 'VERY_HIGH')) {
      oldImp = 'HIGH';
    } else if ((newImp === 'MEDIUM') && (oldImp != 'VERY_HIGH') && (oldImp != 'HIGH')) {
      oldImp = 'MEDIUM';
    } else if ((newImp === 'LOW') && (oldImp != 'VERY_HIGH') && (oldImp != 'HIGH') && (oldImp != 'MEDIUM')) {
      oldImp = 'LOW';
    }
    return oldImp;
  }


  changeColor() {

    const figure = this.canvas.getFigures().data;
    for (const i in this.idAssets) {

      let likelihood = '';
      for (const j in this.idAssets[i].vulnerabilities) {
        likelihood = this.maxImpact(likelihood, this.idAssets[i].vulnerabilities[j].data[0].score.likelihood);
      }

      for (const fig in figure) {
        if (this.idAssets[i].identifier === figure[fig].id) {
          figure[fig].setColor(this.correspondingColor(likelihood));
          figure[fig].children.data[0].figure.setBackgroundColor(this.correspondingColor(likelihood));
        }
      }
    }
  }


  vulUnselect(event) {

    if (this.selectedThreatArray.length === 0) {
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
      // this.showSelectedRequirements = event.node.data.relatedRequirementsIds;

      const requirementsName = [];
      for (const req in event.node.data.relatedRequirementsIds) {

        const reqInd = this.requirementsList.findIndex(reqI => reqI.identifier === event.node.data.relatedRequirementsIds[req]);

        if (reqInd != -1) {
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


  deleteVulnerability(data) {

    this.confirmationService.confirm({
      message: 'This is a permanent operation. Do you really want to remove this threat?',
      header: 'Remove Confirmation',
      icon: 'fa fa-trash',
      accept: () => {

        // to delete the threat in the other  data structures
        const index = this.associatedVulnerabilities.findIndex((n) => ((n.value[0].identifier === data[0].identifier) && (n.value[0].catalogueId === data[0].catalogueId)));

        this.associatedVulnerabilities.splice(index, 1);

        const indexId = this.idAssets.findIndex((i) => i.identifier === this.currentId);
        const indexVuln = this.idAssets[indexId].vulnerabilities.findIndex((v) => ((v.data[0].identifier === data[0].identifier) && (v.data[0].catalogueId === data[0].catalogueId)));
        this.idAssets[indexId].vulnerabilities.splice(indexVuln, 1);

        // to understand if we have to delete the threat in threat list (the other assets do not contain this threat)
        let count = 0;

        for (const scen in this.riskModel.scenarios) {

          if ((this.riskModel.scenarios[scen].assetId != this.currentId) && (this.riskModel.scenarios[scen].threatId === data[0].identifier)) {
            count = count + 1;
          }

          // There is at least a scenario with this threat for another Asset
          if (count > 0) {
            break;
          }
        }

        if (count === 0) {
          for (const j in this.idAssets) {

            for (const k in this.idAssets[j].vulnerabilities) {

              if ((this.idAssets[j].vulnerabilities[k].data[0].identifier === data[0].identifier) && (this.idAssets[j].vulnerabilities[k].data[0].catalogueId === data[0].catalogueId)) {

                count = count + 1;
              }
              if (count > 0) {
                break;
              }
            }
          }
        }

        if (count === 0) {

          const indexVulList = this.vulnerabilitiesList.findIndex((m) => ((m.data[0].identifier === data[0].identifier) && (m.data[0].catalogueId === data[0].catalogueId)));
          this.vulnerabilitiesList.splice(indexVulList, 1);

          const indexVulModList = this.modifiedVulnerabilitiesList.findIndex((m) => ((m.data[0].identifier === data[0].identifier) && (m.data[0].catalogueId === data[0].catalogueId)));
          this.modifiedVulnerabilitiesList.splice(indexVulModList, 1);

          const indexVulMod = this.threatModel.threats.findIndex((b) => ((b.identifier === data[0].identifier) && (b.catalogueId === data[0].catalogueId)));
          this.threatModel.threats.splice(indexVulMod, 1);

        }
        let string = '';
        for (const vul in this.idAssets[indexId].vulnerabilities) {

          if (string === '') {
            string = string + this.idAssets[indexId].vulnerabilities[vul].name;
          } else {

            string = string + '\n' + this.idAssets[indexId].vulnerabilities[vul].name;
          }
        }

        this.stringThreat.setText(string);

        this.oldIdVul = undefined;
        this.selectedVulnerability = undefined;

        this.changeColor();
      }
    });
  }


  // event when an asset node is selected
  clickVuln(value) {

    this.selectedThreatArray = [];
    if (value === null) {

      this.showAssetDetails = false;
      this.showDetails = false;
    } else {

      // group of selected assets
      this.selectedAsset = value.canvas.selection.all.data;

      let j = -1;

      if (value.cssClass === 'draw2d_shape_layout_HorizontalLayout') {

        j = this.nodes.findIndex(i => i.identifier === value.parent.id);
      } else {
        j = this.nodes.findIndex(i => i.identifier === value.id);
      }

      this.integrity = null;
      this.confidentiality = null;
      this.efficiency = null;
      this.availability = null;
      this.nameAsset = this.nodes[j].name;
      this.primaryAssetCategory = this.nodes[j].primaryCategories[0];
      this.secondCat = this.nodes[j].category;

      const requirementsName = [];
      for (const req in this.nodes[j].relatedRequirementsIds) {

        const reqInd = this.requirementsList.findIndex(reqI => reqI.identifier === this.nodes[j].relatedRequirementsIds[req]);
        if (reqInd != -1) {
          requirementsName.push(this.requirementsList[reqInd].id);
        }
      }

      this.showSelectedRequirements = requirementsName;


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


      if ((this.selectedAsset.length === 1) && (value.cssClass === 'draw2d_shape_basic_Label') && (value.text != '')) {

        this.stringThreat = this.selectedAsset[0].children.data[2].figure;

        this.associatedVulnerabilities = [];
        for (const k in this.idAssets) {

          if (value.id === this.idAssets[k].identifier) {

            this.currentId = this.idAssets[k].identifier;

            for (const vul in this.idAssets[k].vulnerabilities) {

              this.associatedVulnerabilities.push({
                'label': this.idAssets[k].vulnerabilities[vul].name,
                'value': this.idAssets[k].vulnerabilities[vul].data
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


  createThreatsTree() {

    this.threatTree = [];
    let cat = [];
    for (const i in this.threatsList.threats) {

      cat = this.findaffectedAssetsCategories(this.threatsList.threats[i].affectedAssetsCategories);
      for (const j in this.threatsList.threats[i].affectedAssetsCategories) {
        const a = this.threatsList.threats[i].affectedAssetsCategories[j];
        let indexOfLabel = -1;
        indexOfLabel = this.threatTree.findIndex(i => i.label === a);

        if (indexOfLabel === -1) {
          const array = [];
          let obj = {};

          array.push({
            'label': this.threatsList.threats[i].name, 'data': [{
              'nodeType': 'Threat',
              'assessmentThreat': this.threatsList.threats[i].assessmentThreat,
              'canBeSelected': this.threatsList.threats[i].canBeSelected,
              'description': this.threatsList.threats[i].description,
              'elementType': this.threatsList.threats[i].elementType,
              'phase': this.threatsList.threats[i].phase,
              'score': this.threatsList.threats[i].score,
              'name': this.threatsList.threats[i].name,
              'catalogue': this.threatsList.threats[i].catalogue,
              'catalogueId': this.threatsList.threats[i].catalogueId,
              'threatClass': this.threatsList.threats[i].threatClass,
              'affectedAssetsCategories': cat,
              'identifier': this.threatsList.threats[i].identifier,
              'actor': this.threatsList.threats[i].actor,
              'event': this.threatsList.threats[i].event,
              'place': this.threatsList.threats[i].place,
              'time': this.threatsList.threats[i].time,
              'access': this.threatsList.threats[i].access,
              'process': this.threatsList.threats[i].process,
              'lastUpdate': this.threatsList.threats[i].lastUpdate,
              'rawText': this.threatsList.threats[i].rawText,
              'associatedVulnerabilities': this.threatsList.threats[i].associatedVulnerabilities,
              'applicablePlatform': this.threatsList.threats[i].applicablePlatform,
              'mitigations': this.threatsList.threats[i].mitigations,
              'objType': 'ThreatModel'
            }]
          });
          obj = {'label': a, 'data': [{'nodeType': 'Category'}], 'children': array};

          this.threatTree.push(obj);
        } else {

          this.threatTree[indexOfLabel].children.push({
            'label': this.threatsList.threats[i].name, 'data': [{
              'nodeType': 'Threat',
              'canBeSelected': this.threatsList.threats[i].canBeSelected,
              'assessmentThreat': this.threatsList.threats[i].assessmentThreat,
              'elementType': this.threatsList.threats[i].elementType,
              'description': this.threatsList.threats[i].description,
              'phase': this.threatsList.threats[i].phase,
              'score': this.threatsList.threats[i].score,
              'name': this.threatsList.threats[i].name,
              'catalogue': this.threatsList.threats[i].catalogue,
              'catalogueId': this.threatsList.threats[i].catalogueId,
              'threatClass': this.threatsList.threats[i].threatClass,
              'affectedAssetsCategories': cat,
              'identifier': this.threatsList.threats[i].identifier,
              'actor': this.threatsList.threats[i].actor,
              'event': this.threatsList.threats[i].event,
              'place': this.threatsList.threats[i].place,
              'time': this.threatsList.threats[i].time,
              'access': this.threatsList.threats[i].access,
              'process': this.threatsList.threats[i].process,
              'lastUpdate': this.threatsList.threats[i].lastUpdate,
              'rawText': this.threatsList.threats[i].rawText,
              'associatedVulnerabilities': this.threatsList.threats[i].associatedVulnerabilities,
              'applicablePlatform': this.threatsList.threats[i].applicablePlatform,
              'mitigations': this.threatsList.threats[i].mitigations,
              'objType': 'ThreatModel'
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
  }

  associatedProcess(id): Object {

    const a = {'label': null, 'data': null, 'children': []};
    for (const i in this.edges) {

      if (id === this.edges[i].identifier) {
        for (const j in this.nodes) {
          if (this.edges[i].target === this.nodes[j].identifier) {
            if (this.nodes[j].name != null) {

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
            if (this.nodes[j].name != null) {
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
          if ((this.edges[i].target === this.nodes[j].identifier) && (this.nodes[j].nodeType != 'Malfunction')) {
            if (this.nodes[j].name != null) {
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


  // to show vulnerability information in the form
  changeInformation(data) {

    if (data != null) {

      if (this.oldIdVul != undefined) {
        for (const old in this.modifiedVulnerabilitiesList) {
          if (this.oldIdVul === this.modifiedVulnerabilitiesList[old].data[0].identifier) {
            // this.modifiedVulnerabilitiesList[old].data[0].score.exploitability = this.vulnerabilityForm.value.explVul;
            this.modifiedVulnerabilitiesList[old].data[0].score.score = this.threatForm.value.scoTh;
            this.modifiedVulnerabilitiesList[old].data[0].description = this.threatForm.value.descriptionTh;
            this.modifiedVulnerabilitiesList[old].data[0].score.likelihood = this.threatForm.value.likeliTh;
          }
        }
      }
      this.oldIdVul = data[0].identifier;

      this.threatForm.reset();
      this.affectedCategoriesForm = [];
      this.associatedVulnerabilitiesForm = [];
      for (const mod in this.modifiedVulnerabilitiesList) {
        if (data[0].identifier === this.modifiedVulnerabilitiesList[mod].data[0].identifier) {

          this.nameThForm = this.modifiedVulnerabilitiesList[mod].data[0].name;
          this.catalogueThForm = this.modifiedVulnerabilitiesList[mod].data[0].catalogue;
          this.catalogueIDThForm = this.modifiedVulnerabilitiesList[mod].data[0].catalogueId;
          this.threatClass = this.modifiedVulnerabilitiesList[mod].data[0].threatClass;

          this.placeName = this.modifiedVulnerabilitiesList[mod].data[0].place.name;
          this.placeCatId = this.modifiedVulnerabilitiesList[mod].data[0].place.catalogueId;
          this.placeDescription = this.modifiedVulnerabilitiesList[mod].data[0].place.description;


          this.timeName = this.modifiedVulnerabilitiesList[mod].data[0].time.name;
          this.timeCatId = this.modifiedVulnerabilitiesList[mod].data[0].time.catalogueId;
          this.timeDescription = this.modifiedVulnerabilitiesList[mod].data[0].time.description;

          this.accessName = this.modifiedVulnerabilitiesList[mod].data[0].access.name;
          this.accessCatId = this.modifiedVulnerabilitiesList[mod].data[0].access.catalogueId;
          this.accessDescription = this.modifiedVulnerabilitiesList[mod].data[0].access.description;

          this.processName = this.modifiedVulnerabilitiesList[mod].data[0].process.name;
          this.processCatId = this.modifiedVulnerabilitiesList[mod].data[0].process.catalogueId;
          this.processDescription = this.modifiedVulnerabilitiesList[mod].data[0].process.description;

          this.actorDescription = this.modifiedVulnerabilitiesList[mod].data[0].actor.description;
          this.actorName = this.modifiedVulnerabilitiesList[mod].data[0].actor.name;
          this.actorCatId = this.modifiedVulnerabilitiesList[mod].data[0].actor.catalogueId;

          this.eventName = this.modifiedVulnerabilitiesList[mod].data[0].event.name;
          this.eventCatId = this.modifiedVulnerabilitiesList[mod].data[0].event.catalogueId;
          this.eventDescription = this.modifiedVulnerabilitiesList[mod].data[0].event.description;


          this.threatForm.controls['descriptionTh'].setValue(this.modifiedVulnerabilitiesList[mod].data[0].description);
          this.threatForm.controls['scoTh'].setValue(this.modifiedVulnerabilitiesList[mod].data[0].score.score);
          this.threatForm.controls['likeliTh'].setValue(this.modifiedVulnerabilitiesList[mod].data[0].score.likelihood);


          for (const i in this.modifiedVulnerabilitiesList[mod].data[0].affectedAssetsCategories) {

            if (this.affectedCategoriesForm.indexOf(this.modifiedVulnerabilitiesList[mod].data[0].affectedAssetsCategories[i]) === -1) {
              this.affectedCategoriesForm.push(this.modifiedVulnerabilitiesList[mod].data[0].affectedAssetsCategories[i]);
            }
          }

          for (const j in this.modifiedVulnerabilitiesList[mod].data[0].associatedVulnerabilities) {

            if (this.associatedVulnerabilitiesForm.indexOf(this.modifiedVulnerabilitiesList[mod].data[0].associatedVulnerabilities[j]) === -1) {
              this.associatedVulnerabilitiesForm.push(this.modifiedVulnerabilitiesList[mod].data[0].associatedVulnerabilities[j]);
            }
          }
        }
      }
    }
  }


  // save in modifiedVulnerabilitiesList in order to not lose the last modification
  modifiedBeforeToSave(data) {

    for (const old in this.modifiedVulnerabilitiesList) {

      if (this.oldIdVul === this.modifiedVulnerabilitiesList[old].data[0].identifier) {

        this.modifiedVulnerabilitiesList[old].data[0].description = this.threatForm.value.descriptionTh;
        this.modifiedVulnerabilitiesList[old].data[0].score.likelihood = this.threatForm.value.likeliTh;
        this.modifiedVulnerabilitiesList[old].data[0].score.score = this.threatForm.value.scoTh;
      }
    }
    this.oldIdVul = undefined;
    this.selectedVulnerability = undefined;

    this.vulnerabilitiesList = JSON.parse(JSON.stringify(this.modifiedVulnerabilitiesList));

    this.replaceVulnerability();
    this.updateThreatModel();

    this.displayEditVulnerability = false;
    this.threatForm.reset();
  }

  // replace old vulnerabilities with the new(modified by user) in the idAssets(combination between assets and their associated vulnerabilities)
  replaceVulnerability() {
    for (const k in this.modifiedVulnerabilitiesList) {
      for (const j in this.idAssets) {
        let a = this.idAssets[j].vulnerabilities.findIndex(i => i.data[0].identifier === this.modifiedVulnerabilitiesList[k].data[0].identifier);
        if (a != -1) {
          this.idAssets[j].vulnerabilities[a] = this.modifiedVulnerabilitiesList[k];
          a = -1;
        }
      }
    }

    this.changeColor();
  }

  updateThreatModel() {
    const threatsToSave = [];
    for (const threatId in this.threatModel.threats) {

      let threatToSave = false;

      for (const graphThreat in this.vulnerabilitiesList) {
        if (this.threatModel.threats[threatId].catalogueId === this.vulnerabilitiesList[graphThreat].data[0].catalogueId) {
          threatToSave = true;
          break;
        }
      }

      if (!threatToSave) {
        threatsToSave.push(this.threatModel.threats[threatId]);
      }
    }

    this.threatModel.threats = [];
    for (const i in this.vulnerabilitiesList) {
      this.threatModel.threats.push(this.vulnerabilitiesList[i].data[0]);
    }

    for (const i in threatsToSave) {
      this.threatModel.threats.push(threatsToSave[i]);
    }

    for (const i in this.threatModel.threats) {
      this.threatModel.threats[i].assessmentThreat = true;
    }
  }

  closeForm() {
    this.modifiedVulnerabilitiesList = JSON.parse(JSON.stringify(this.vulnerabilitiesList));

    this.oldIdVul = undefined;
    this.selectedVulnerability = undefined;

    this.displayEditVulnerability = false;
    this.threatForm.reset();
  }


  editThreat() {

    if (this.modifiedVulnerabilitiesList.length > 0) {
      this.vulnerabilitiesList = JSON.parse(JSON.stringify(this.modifiedVulnerabilitiesList));
    }
    this.updateThreatModel();
    this.createRiskModel(this.idAssets);
  }

  createRiskModel(idAssets) {

    const toDelete = [];

    for (const i in idAssets) {

      for (const j in this.riskModel.scenarios) {

        if ((this.riskModel.scenarios[j].vulnerabilityId === null || this.riskModel.scenarios[j].vulnerabilityId === '')) {
          continue;
        }
        if ((idAssets[i].identifier === this.riskModel.scenarios[j].assetId)) {
          if ((this.riskModel.scenarios[j].threatId === null || this.riskModel.scenarios[j].threatId === '')) {
            // If I'm putting some threats for the first time with the corresponding vulnerabilites
            if (idAssets[i].vulnerabilities.length != 0) {
              // The asset has some assigned threats. I need to check if these threats are compatible with the vulnerabilities of the Asset

              let vulnerabilityCatalogueId = '';
              for (const t in this.vulnerabilityModel.vulnerabilities) {

                if (this.riskModel.scenarios[j].vulnerabilityId === this.vulnerabilityModel.vulnerabilities[t].identifier) {
                  vulnerabilityCatalogueId = this.vulnerabilityModel.vulnerabilities[t].catalogueId;
                  break;
                }
              }
              if (vulnerabilityCatalogueId === '') {
                continue;
              }

              for (const k in idAssets[i].vulnerabilities) {

                let applicableThreat = false;
                for (const f in idAssets[i].vulnerabilities[k].data[0].associatedVulnerabilities) {

                  if (idAssets[i].vulnerabilities[k].data[0].associatedVulnerabilities[f] === vulnerabilityCatalogueId) {
                    applicableThreat = true;
                    break;
                  }
                }

                if (applicableThreat) {
                  // The old Risk Scenario (with an empty threat is not useful anymore, since I'm creating a number of Scenarios with the same vulnerability but all the selected threats
                  if (toDelete.indexOf(this.riskModel.scenarios[j].identifier) === -1) {
                    toDelete.push(this.riskModel.scenarios[j].identifier);
                  }

                  // This threat can match with the vulnerability from the RiskScenario
                  // console.log("Adding Risk Scenario");
                  this.riskModel.scenarios.push({
                    'objType': 'RiskModel',
                    'identifier': UUID.UUID(),
                    'threatId': idAssets[i].vulnerabilities[k].data[0].identifier,
                    'assetId': this.riskModel.scenarios[j].assetId,
                    // "excluded": this.riskModel.scenarios[j].excluded,
                    'impactScope': this.riskModel.scenarios[j].impactScope,
                    // "safeguardIds": this.riskModel.scenarios[j].safeguardIds,
                    // "scenarioResult": this.riskModel.scenarios[j].scenarioResult,
                    'vulnerabilityId': this.riskModel.scenarios[j].vulnerabilityId
                  });
                }
              }
            }
          } else {
            let threatToKeep = false;
            for (const k in idAssets[i].vulnerabilities) {
              if (this.riskModel.scenarios[j].threatId === idAssets[i].vulnerabilities[k].data[0].identifier) {
                threatToKeep = true;
                break;
              }
            }

            if (!threatToKeep) {
              if (toDelete.indexOf(this.riskModel.scenarios[j].identifier) === -1) {
                toDelete.push(this.riskModel.scenarios[j].identifier);
              }
            }

          }
        }
      }
    }
    const toDeleteIndexes = [];
    for (const j in this.riskModel.scenarios) {
      for (const t in toDelete) {
        if (this.riskModel.scenarios[j].identifier === toDelete[t]) {
          toDeleteIndexes.push(j);
          break;
        }
      }
    }

    for (let del = toDeleteIndexes.length - 1; del >= 0; del--) {
      this.riskModel.scenarios.splice(parseInt(toDeleteIndexes[del]), 1);
    }
    this.sendThreatModel();
  }

  checkedRepository() {

    if (this.selectedRepository != undefined) {

      this.repository = this.selectedRepository;

      const filter = {
        'filterMap': {
          'METHODOLOGY': this.selectedRepository === 'ALL' ? null : this.selectedRepository,
          'FULL': true
        }
      };
      this.subscriptions.push(
        this.dataService.loadThreatsRepository(JSON.stringify(filter)).subscribe(response => {

          this.threatsList = response;

          this.createThreatsTree();

        }, err => {
          this.blocked = false;
          throw err;
        }));
    }
  }

  showSuccess() {
    this.msgsThreats = [];
    this.msgsThreats.push({severity: 'success', summary: 'Save Successful!', detail: 'Threats Model Saved'});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);

  }

  showFailed(s: string) {
    this.msgsThreats = [];
    this.msgsThreats.push({severity: 'error', summary: 'Error!', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  showInfo(s: string) {
    this.messageService.add({key: 'tc', severity: 'info', summary: 'Info Message', detail: s});
  }

  clearMessage() {
    this.msgsThreats = [];
    this.blocked = false;
    this.blockedMessage = false;
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
        this.getVulnerabilityModel();
        this.lockService.addLock(response.objectIdentifier, response.lockedBy);
      }, err => {
        this.blocked = false;
        throw err;
      }));

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

        this.getThreatModel();
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  sendRiskModel() {

    let completeList = {};

    this.blocked = true;
    completeList = {
      'jsonModel': (JSON.stringify(this.riskModel, null, 2)),
      'objectIdentifier': this.riskModel.identifier
    };

    this.subscriptions.push(
      this.dataService.updateRiskModel(completeList).subscribe(response => {

        this.blockedMessage = true;
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
      }));
  }

  sendThreatModel() {

    this.blocked = true;

    let completeList = {};
    completeList = {
      'jsonModel': (JSON.stringify(this.threatModel, null, 2)),
      'objectIdentifier': this.threatModel.identifier
    };

    this.messageService.add({
      key: 'tc',
      severity: 'info',
      summary: 'Info Message',
      detail: ThreatsComponent.INFO_SERVER_FILTER
    });
    this.subscriptions.push(
      this.dataService.updateThreatModel(JSON.stringify(completeList, null, 2)).subscribe(response => {
        this.sendRiskModel();
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  getRequirements(idsystemproject) {

    this.subscriptions.push(
      this.dataService.loadRequirementsById(idsystemproject).subscribe(response => {
        this.requirementsList = response;
        this.createGraph();
      }));
  }

  isLockedByCurrentUser() {
    return this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername');
  }

  ngOnDestroy() {
    this.lockService.removeLock(this.threatModel.identifier);
    this.subscriptions.forEach(s => s.unsubscribe());
    this.store.dispatch(resetThreats());
  }
}
