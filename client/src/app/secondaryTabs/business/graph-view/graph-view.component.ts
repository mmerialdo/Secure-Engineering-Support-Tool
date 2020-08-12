/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="graph-view.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, Input, NgZone, OnDestroy, OnInit, ViewEncapsulation} from '@angular/core';
import {FormBuilder, Validators} from '@angular/forms';

import {DataService} from '../../../dataservice';
import * as FileSaver from 'file-saver';
import {ProgressSpinnerModule} from 'primeng/progressspinner';

import {SelectItem, ConfirmationService, Message, MessageService} from 'primeng/primeng';
import * as $ from 'jquery';
import {PermissionType} from '../../../permission-type.class';
import {Permission} from '../../../permission.class';
import {Subscription} from 'rxjs/internal/Subscription';
import {Store} from '@ngrx/store';
import {switchToTabView} from '../../../shared/store/actions/assets.actions';
import {tryCatch} from 'rxjs/internal-compatibility';
import {ServerAssetHelper} from '../server-asset.helper';
import {LockService} from '../../../shared/service/lock-service';
import {Model} from '@swimlane/ngx-datatable';
import {ModelObject} from '../../../model-object';

declare var window: any;
declare var draw2d: any;

declare var example: any;


@Component({
  selector: 'app-graph-view',
  templateUrl: './graph-view.component.html',
  styleUrls: ['./graph-view.component.scss'],
  providers: [ConfirmationService]
})
export class GraphViewComponent implements OnInit, OnDestroy {

  canvas: any;

  @Input() enableSaveButton: boolean;
  // old associated malfunctions list lenght
  public sizeMalf = 0;

  // new associated malfunctions list lenght
  public newSizeMalf = 0;

  // asset's set colored by Malfunctions
  public coloredByMal = [];

  // edges involved when a mulfunctions set is associated to an asset
  public involvedEdges = [];

  public idsystemproject = sessionStorage.getItem('sysprojectId');

  // to load a graph
  public jsonDocument;

  // to hide empty list
  // public isClassVisible=false;

  toBeRemoved: any;
  businessEditAssetForm: any;
  businessEditEdgeForm: any;
  businessEditElementForm: any;
  businessEditActivityForm: any;
  businessEditMalfunctioForm: any;
  businessEditOrgForm: any;

  propertyCheck = null;

  public width: Number;
  public height: Number;

  // selected figure after a click
  public selectedFigure;

  // selected edge after a click
  public selectedEdge;

  public displayRequirements = false;
  public displayEditProcess = false;
  public displayEditActivity = false;
  public displayEditOrganization = false;
  public displayEditMalfunction = false;
  public displayEditEdge = false;
  public displayEditAsset = false;

  public allChildren = {id: null, check: false};

  public assets = [];
  public edges = [];

  //This variable stores if, at AssetModel loading, there is a mismatch between AssetModel and graph model
  public mismatch = false;

  // REST variables
  public requirementsList: any;


  // to manage extra malfunctions parameters
  public malfuncOpt = [];


  // it is used to know if the selected edge has an asset with associated Malfunctions
  public thereIsMalf = false;


  // show the malfunctions in the info menu
  public showMalfunction;

  public ownersList;


  // form interaction

  public description;
  public goal;
  public name;
  public owner;
  public type;
  public id;

  public functionalConsequence;
  public functionalDescription;
  public functionalType;
  public technicalConsequence;
  public technicalDescription;
  public technicalTypes = [];
  public scales = [];
  public low;
  public medium;
  public high;
  public critical;
  public assetCategory;
  public secondaryAssetCategory;


  // malfunctions checkbox

  public malfunctionAvailability = false;
  public malfunctionIntegrity = false;
  public malfunctionEfficency = false;
  public malfunctionConfidentiality = false;

  // edges checkbox

  public edgeAvailability = false;
  public edgeIntegrity = false;
  public edgeEfficiency = false;
  public edgeConfidentiality = false;


  public map = [];

  // edge information
  public sourceNode;
  public targetNode;
  // public edgeBAS=[];
  public idEdge;
  public confindentialityEdge;
  public integrityEdge;
  public availabilityEdge;
  public efficiencyEdge;


  // asset information
  public associatedMalf: SelectItem[];
  public selectedMalfunction;
  public assetDescription;

  // SelectItem used in the p-listBox to show all the requirements
  public showRequirementsList: SelectItem[];


  // SelectItem used in the p-listBox to show all the temporanely selected requirements
  public selectedRequirementList: SelectItem[];

  // returns the selected Requirement in the Requirements List
  public selectedRequirement;

  // returns the chosen Requirement in the temporanely selected requirements list
  public chosenRequirement: SelectItem[];

  // it shows the selected Requirements
  public showSelectedRequirements = [];


  // variable to show requirement details

  public reqName;
  public reqCat;
  public reqSub;
  public reqPri;
  public reqDes;
  public reqTyp;


  // to filter by category
  public selectedCategory;
  public categoryList = [];

  // to filter by Subcategory
  public selectedSubcategory;
  public totalSubcategoryList = [];
  public subcategoryList = [];


  public primaryAssetConf = [];
  public primaryAssetInt = [];
  public primaryAssetAva = [];
  public primaryAssetCategory = [];
  public selectedCategoryData = '';
  public selectedCategoryService = '';
  public selectedCategoryCompli = '';
  public selectedSecondary = '';

  // map with edges and their corresponding color
  public edgeColor = [];

  // for the impact assets information

  public assetConfidentiality;
  public assetIntegrity;
  public assetEfficiency;
  public assetAvailability;

  public ownerListProcess = [];
  public ownerListOrganization = [];
  public ownerListActivity = [];
  public ownerListAsset = [];

  // to remove

  public finalJSON = {};

  public serverAsset: any;


  public blocked = false;
  public blockedMessage = false;

  // to show messages
  msgsAsset: Message[] = [];

  tryMode = true;

  // to show duplicate name message
  public duplicateName = false;

  // To show info forms
  public showMalfunctionTypeInfo = false;
  public showSeriousnessInfo = false;
  public showAssetPrimaryDataTypeInfo = false;
  public showAssetPrimaryServicesTypeInfo = false;
  public showAssetPrimaryManagementTypeInfo = false;
  public showAssetSecondaryTypeInfo = false;

  private subscriptions: Subscription[] = [];

  constructor(private confirmationService: ConfirmationService,
              private zone: NgZone,
              private formBuilder: FormBuilder,
              private dataService: DataService,
              private lockService: LockService,
              private store: Store<any>) {


    this.width = 1500;
    this.height = 2000;
    window.angularComponentRef = {
      zone: this.zone,
      componentFn: (value) => this.clickProcess(value),
      componentDBClick: (event) => this.dbClick(event),
      connectionPolicy: (value) => this.managementConnection(value),
      removeComponent: (value) => this.removeFigure(value),
      removeConnection: (value) => this.removeLink(value),
      componentEdge: (the) => this.clickOnEdge(the),
      component: this
    };


    this.showRequirementsList = [];
    this.selectedRequirementList = [];
    this.chosenRequirement = [];

    this.selectedMalfunction = [];

    this.associatedMalf = [];


    this.primaryAssetConf = [];
    this.primaryAssetConf.push('');
    this.primaryAssetConf.push('Data_DataFile_Database');
    this.primaryAssetConf.push('Data_Shared_Office_File');
    this.primaryAssetConf.push('Data_Personal_Office_File');
    this.primaryAssetConf.push('Data_Physical_File');
    this.primaryAssetConf.push('Data_Exchanged_Message');
    this.primaryAssetConf.push('Data_Digital_Mail');
    this.primaryAssetConf.push('Data_Physical_Mail');
    this.primaryAssetConf.push('Data_Physical_Archive');
    this.primaryAssetConf.push('Data_IT_Archive');
    this.primaryAssetConf.push('Data_Published_Data');


    this.primaryAssetInt = [];

    this.primaryAssetInt.push('');
    this.primaryAssetInt.push('Service_Extended_Network_Service');
    this.primaryAssetInt.push('Service_Local_Network_Service');
    this.primaryAssetInt.push('Service_Application_Service');
    this.primaryAssetInt.push('Service_Shared_Service');
    this.primaryAssetInt.push('Service_User_Hardware');
    this.primaryAssetInt.push('Service_Common_Service');
    this.primaryAssetInt.push('Service_User_Workspace');
    this.primaryAssetInt.push('Service_Telecommunication_Service');
    this.primaryAssetInt.push('Service_Web_editing_Service');

    this.primaryAssetAva = [];

    this.primaryAssetAva.push('');
    this.primaryAssetAva.push('Compliance_Policy_Personal_Information_Protection');
    this.primaryAssetAva.push('Compliance_Policy_Financial_Communication');
    this.primaryAssetAva.push('Compliance_Policy_Digital_Accounting_Control');
    this.primaryAssetAva.push('Compliance_Policy_Intellectual_Property');
    this.primaryAssetAva.push('Compliance_Policy_Protection_Of_Information_Systems');
    this.primaryAssetAva.push('Compliance_Policy_People_And_Environment_Safety');


    // to remove

    this.businessEditAssetForm = this.formBuilder.group({

      'assetName': ['', [Validators.required, Validators.maxLength(100)]],
      // 'secAsset':['', Validators.required],
      'desAsset': ['', [Validators.required, Validators.maxLength(800)]],
      'ownAsset': ['', null]

    });

    this.businessEditEdgeForm = this.formBuilder.group({

      'targetNode': ['', Validators.required],
      'sourceNode': ['', Validators.required],
      'impactLevel': ['', Validators.required],
      'impactIntLevel': ['', Validators.required],
      'impactAvaLevel': ['', Validators.required],
      'impactEffeLevel': ['', Validators.required]

    });

    this.businessEditElementForm = this.formBuilder.group({
      'nameEP': ['', [Validators.required, Validators.maxLength(100)]],
      'descriptionEP': ['', [Validators.required, Validators.maxLength(800)]],
      'ownerEP': ['', null],
      'goalEP': ['', [Validators.maxLength(100)]],
      'typeEP': ['', Validators.required]

    });

    this.businessEditOrgForm = this.formBuilder.group({
      'nameEO': ['', [Validators.required, Validators.maxLength(100)]],
      'descriptionEO': ['', [Validators.required, Validators.maxLength(800)]],
      'ownerEO': ['', null],
      'goalEO': ['', [Validators.maxLength(100)]]

    });


    this.businessEditActivityForm = this.formBuilder.group({
      'nameEA': ['', [Validators.required, Validators.maxLength(100)]],
      'descriptionEA': ['', [Validators.required, Validators.maxLength(800)]],
      'ownerEA': ['', null],
      'goalEA': ['', [Validators.maxLength(100)]]

    });


    this.businessEditMalfunctioForm = this.formBuilder.group({
      'nameEM': ['', [Validators.required, Validators.maxLength(100)]],
      // 'assCat': ['', null],

      'descriptionET': ['', [Validators.maxLength(500)]],
      'consE': ['', [Validators.maxLength(500)]],
      'descriptionEF': ['', [Validators.maxLength(500)]],
      'consEF': ['', [Validators.maxLength(500)]],
      'typeEF': ['', Validators.required],
      'lowE': ['', [Validators.maxLength(500)]],
      'mediumE': ['', [Validators.maxLength(500)]],
      'highE': ['', [Validators.maxLength(500)]],
      'criticalE': ['', [Validators.maxLength(500)]]


    });

    if (sessionStorage.getItem('sysproject') && JSON.parse(sessionStorage.getItem('sysproject')).participants) {

      this.ownersList = JSON.parse(sessionStorage.getItem('sysproject')).participants;

      this.ownerListProcess.push({'name': undefined, 'role': undefined, 'surname': undefined});
      for (const i in this.ownersList) {

        this.ownerListProcess.push({
          'name': this.ownersList[i].name,
          'role': this.ownersList[i].role,
          'surname': this.ownersList[i].surname
        });

      }


      this.ownerListOrganization.push({'name': undefined, 'role': undefined, 'surname': undefined});
      for (const i in this.ownersList) {

        this.ownerListOrganization.push({
          'name': this.ownersList[i].name,
          'role': this.ownersList[i].role,
          'surname': this.ownersList[i].surname
        });

      }

      this.ownerListActivity.push({'name': undefined, 'role': undefined, 'surname': undefined});
      for (const i in this.ownersList) {

        this.ownerListActivity.push({
          'name': this.ownersList[i].name,
          'role': this.ownersList[i].role,
          'surname': this.ownersList[i].surname
        });
      }

      this.ownerListAsset.push({'name': undefined, 'role': undefined, 'surname': undefined});
      for (const i in this.ownersList) {

        this.ownerListAsset.push({'name': this.ownersList[i].name, 'role': this.ownersList[i].role, 'surname': this.ownersList[i].surname});

      }
    }
  }

  changeView(): void {
    this.store.dispatch(switchToTabView());
  }

  ngOnInit() {
    this.getAsset();
  }

  createGraph() {

    this.canvas = new draw2d.Canvas('canvas-div');

    this.canvas.installEditPolicy(new draw2d.policy.connection.DragConnectionCreatePolicy());

    // to hide far ports
    this.canvas.installEditPolicy(new draw2d.policy.canvas.CoronaDecorationPolicy());

    this.canvas.installEditPolicy(new draw2d.policy.canvas.ShowGridEditPolicy());


    const MyPolicy = draw2d.policy.canvas.CanvasPolicy.extend({
      NAME: 'MyPolicy',
      init: function () {
        this._super();

      },
      onClick: function (the, mouseX, mouseY, shiftKey, ctrlKey) {
        this._super(the, mouseX, mouseY, shiftKey, ctrlKey);

        window.angularComponentRef.componentEdge(the);
      },
      onDoubleClick: function (event) {
        window.angularComponentRef.componentDBClick(event);
      }
    });


    const policy = new MyPolicy();
    this.canvas.installEditPolicy(policy);
  }

  exportPNG() {

    const xCoords = [];
    const yCoords = [];
    this.canvas.getFigures().each(function (i, f) {
      if (f.isVisible() === true) {
        const b = f.getBoundingBox();
        xCoords.push(b.x, b.x + b.w);
        yCoords.push(b.y, b.y + b.h);
      }
    });
    const minX = Math.min.apply(Math, xCoords);
    const minY = Math.min.apply(Math, yCoords);
    const width = Math.max.apply(Math, xCoords) - minX;
    const height = Math.max.apply(Math, yCoords) - minY;

    let a;
    const writer = new draw2d.io.png.Writer();
    writer.marshal(this.canvas, function (png) {

      a = png;
    }, new draw2d.geo.Rectangle(minX, minY, width, height));

    // Split the base64 string in data and contentType
    const block = a.split(';');
    // Get the content type
    const mimeType = block[0].split(':')[1]; // In this case "image/png"
    // get the real base64 content of the file
    const realData = block[1].split(',')[1]; // For example:  iVBORw0KGgouqw23....

    // Convert b64 to blob and store it into a variable (with real base64 as value)
    const canvasBlob = this.b64toBlob(realData, mimeType, 512);

    FileSaver.saveAs(canvasBlob, 'Risk Assessment Report.png');

  }

  b64toBlob(b64Data, contentType, sliceSize) {
    contentType = contentType || '';
    sliceSize = sliceSize || 512;

    const byteCharacters = atob(b64Data);
    const byteArrays = [];

    for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
      const slice = byteCharacters.slice(offset, offset + sliceSize);

      const byteNumbers = new Array(slice.length);
      for (let i = 0; i < slice.length; i++) {
        byteNumbers[i] = slice.charCodeAt(i);
      }

      const byteArray = new Uint8Array(byteNumbers);

      byteArrays.push(byteArray);
    }

    const blob = new Blob(byteArrays, {type: contentType});
    return blob;
  }

  hide() {

    for (const g in this.canvas.getSelection().all.data) {

      if (this.canvas.getSelection().all.data[g].cssClass === 'asset' ||
        this.canvas.getSelection().all.data[g].cssClass === 'organization' ||
        this.canvas.getSelection().all.data[g].cssClass === 'process' ||
        this.canvas.getSelection().all.data[g].cssClass === 'activity' ||
        this.canvas.getSelection().all.data[g].cssClass === 'malfunction') {
        this.canvas.getSelection().all.data[g].setVisible(false);
        this.canvas.getSelection().all.data[g].setSelectable(false);

        for (const label in this.canvas.getSelection().all.data[g].children.data) {

          this.canvas.getSelection().all.data[g].children.data[label].figure.setVisible(false);
        }

        for (const link in this.canvas.getSelection().all.data[g].getConnections().data) {

          this.canvas.getSelection().all.data[g].getConnections().data[link].setVisible(false);
          this.canvas.getSelection().all.data[g].getConnections().data[link].setSelectable(false);
        }
      }

    }

    // this.canvas.getCommandStack().execute(new draw2d.command.CommandGroup(this.canvas, this.canvas.getSelection()));
  }

  unhide() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      listFig.data[g].setVisible(true);
      listFig.data[g].setSelectable(true);

      for (const label in listFig.data[g].children.data) {

        listFig.data[g].children.data[label].figure.setVisible(true);
      }

      for (const link in listFig.data[g].getConnections().data) {

        listFig.data[g].getConnections().data[link].setVisible(true);
        listFig.data[g].getConnections().data[link].setSelectable(true);
      }
    }

    // this.canvas.getCommandStack().execute(new draw2d.command.CommandUngroup(this.canvas, this.canvas.getSelection()));

  }

  hideOrganizations() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'organization') {
        listFig.data[g].setVisible(false);
        listFig.data[g].setSelectable(false);

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(false);
        }

        for (const link in listFig.data[g].getConnections().data) {

          listFig.data[g].getConnections().data[link].setVisible(false);
          listFig.data[g].getConnections().data[link].setSelectable(false);
        }
      }

    }
  }

  unhideOrganizations() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'organization') {
        listFig.data[g].setVisible(true);
        listFig.data[g].setSelectable(true);

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(true);
        }

        for (const link in listFig.data[g].getConnections().data) {

          if (listFig.data[g].getConnections().data[link].sourcePort.parent.isVisible() && listFig.data[g].getConnections().data[link].targetPort.parent.isVisible()) {
            listFig.data[g].getConnections().data[link].setVisible(true);
            listFig.data[g].getConnections().data[link].setSelectable(true);
          }
        }
      }

    }
  }

  hideProcesses() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'process') {
        listFig.data[g].setVisible(false);
        listFig.data[g].setSelectable(false);

        // console.log(this.canvas.getSelection().all.data[g])

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(false);
        }

        for (const link in listFig.data[g].getConnections().data) {

          listFig.data[g].getConnections().data[link].setVisible(false);
          listFig.data[g].getConnections().data[link].setSelectable(false);
        }
      }

    }
  }

  unhideProcesses() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'process') {
        listFig.data[g].setVisible(true);
        listFig.data[g].setSelectable(true);

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(true);
        }

        for (const link in listFig.data[g].getConnections().data) {

          if (listFig.data[g].getConnections().data[link].sourcePort.parent.isVisible() && listFig.data[g].getConnections().data[link].targetPort.parent.isVisible()) {
            listFig.data[g].getConnections().data[link].setVisible(true);
            listFig.data[g].getConnections().data[link].setSelectable(true);
          }
        }
      }

    }
  }

  hideActivities() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'activity') {
        listFig.data[g].setVisible(false);
        listFig.data[g].setSelectable(false);

        // console.log(this.canvas.getSelection().all.data[g])

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(false);
        }

        for (const link in listFig.data[g].getConnections().data) {

          listFig.data[g].getConnections().data[link].setVisible(false);
          listFig.data[g].getConnections().data[link].setSelectable(false);
        }
      }

    }
  }

  unhideActivities() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'activity') {
        listFig.data[g].setVisible(true);
        listFig.data[g].setSelectable(true);

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(true);
        }

        for (const link in listFig.data[g].getConnections().data) {

          if (listFig.data[g].getConnections().data[link].sourcePort.parent.isVisible() && listFig.data[g].getConnections().data[link].targetPort.parent.isVisible()) {
            listFig.data[g].getConnections().data[link].setVisible(true);
            listFig.data[g].getConnections().data[link].setSelectable(true);
          }
        }
      }

    }
  }

  hideMalfunctions() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'malfunction') {
        listFig.data[g].setVisible(false);
        listFig.data[g].setSelectable(false);

        // console.log(this.canvas.getSelection().all.data[g])

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(false);
        }

        for (const link in listFig.data[g].getConnections().data) {

          listFig.data[g].getConnections().data[link].setVisible(false);
          listFig.data[g].getConnections().data[link].setSelectable(false);
        }
      }

    }
  }

  unhideMalfunctions() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'malfunction') {
        listFig.data[g].setVisible(true);
        listFig.data[g].setSelectable(true);

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(true);
        }

        for (const link in listFig.data[g].getConnections().data) {

          if (listFig.data[g].getConnections().data[link].sourcePort.parent.isVisible() && listFig.data[g].getConnections().data[link].targetPort.parent.isVisible()) {
            listFig.data[g].getConnections().data[link].setVisible(true);
            listFig.data[g].getConnections().data[link].setSelectable(true);
          }
        }
      }

    }
  }

  hideAssets() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'asset') {
        listFig.data[g].setVisible(false);
        listFig.data[g].setSelectable(false);

        // console.log(this.canvas.getSelection().all.data[g])

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(false);
        }

        for (const link in listFig.data[g].getConnections().data) {

          listFig.data[g].getConnections().data[link].setVisible(false);
          listFig.data[g].getConnections().data[link].setSelectable(false);
        }
      }

    }
  }

  unhideAssets() {

    const listFig = this.canvas.getFigures();

    for (const g in listFig.data) {

      if (listFig.data[g].cssClass === 'asset') {
        listFig.data[g].setVisible(true);
        listFig.data[g].setSelectable(true);

        for (const label in listFig.data[g].children.data) {

          listFig.data[g].children.data[label].figure.setVisible(true);
        }

        for (const link in listFig.data[g].getConnections().data) {

          if (listFig.data[g].getConnections().data[link].sourcePort.parent.isVisible() && listFig.data[g].getConnections().data[link].targetPort.parent.isVisible()) {
            listFig.data[g].getConnections().data[link].setVisible(true);
            listFig.data[g].getConnections().data[link].setSelectable(true);
          }
        }
      }

    }
  }

  createProcess() {

    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'BP'}).attr({
      'color': '#d3e1ff'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: ''}).attr({
      'color': '#d3e1ff'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#d3e1ff'
    });


    endNode.attr({
      'cssClass': 'process'
    });

    endNode.resetPorts();

    endNode.createPort('input', new draw2d.layout.locator.BottomLocator()).on('connect', function (emitterPort, connection) {

      window.angularComponentRef.connectionPolicy(connection);


    });
    endNode.createPort('output', new draw2d.layout.locator.TopLocator()).on('disconnect', function (emitterPort, connection) {
      window.angularComponentRef.removeConnection(connection);
    });

    endNode.on('click', function (emitter, event) {

      // console.log("click create process")
      window.angularComponentRef.componentFn(event);


    });
    /*endNode.on("dblclick", function(emitter, event){


        window.angularComponentRef.componentDBClick(emitter);


      });*/
    endNode.on('removed', function (emitter, event) {


      window.angularComponentRef.removeComponent(emitter);

    });


    this.canvas.add(endNode);

    this.assets.push({
      'type': null,
      'assessmentNode': true,
      'description': null,
      'goal': null,
      'name': null,
      'relatedRequirementsIds': [],
      'systemParticipantOwnerId': null,
      'children': [],
      'parents': [],
      'identifier': endNode.getId(),
      'objType': 'AssetModel',
      'nodeType': 'BusinessProcess'
    });

  }

  createActivity() {

    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'BA'}).attr({
      'color': '#E6F1F5'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: ''}).attr({
      'color': '#E6F1F5'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#E6F1F5'
    });
    endNode.attr({
      'cssClass': 'activity'
    });

    endNode.resetPorts();
    // endNode.createPort("hybrid",new draw2d.layout.locator.InputPortLocator());
    // endNode.createPort("hybrid",new draw2d.layout.locator.OutputPortLocator());
    endNode.createPort('input', new draw2d.layout.locator.BottomLocator()).on('connect', function (emitterPort, connection) {

      window.angularComponentRef.connectionPolicy(connection);
      // this.canvas.removeFigure(connection);


    });

    endNode.createPort('output', new draw2d.layout.locator.TopLocator()).on('disconnect', function (emitterPort, connection) {
      window.angularComponentRef.removeConnection(connection);
    });

    endNode.on('click', function (emitter, event) {


      window.angularComponentRef.componentFn(event);


    });

    /*endNode.on("dblclick", function(emitter, event){


        window.angularComponentRef.componentDBClick(emitter);


      });*/
    endNode.on('removed', function (emitter, event) {


      window.angularComponentRef.removeComponent(emitter);


    });


    this.canvas.add(endNode);

    this.assets.push({
      'assessmentNode': true,
      'description': null,
      'goal': null,
      'name': null,
      'relatedRequirementsIds': [],
      'systemParticipantOwnerId': null,
      'children': [],
      'parents': [],
      'identifier': endNode.getId(),
      'objType': 'AssetModel',
      'nodeType': 'BusinessActivity'
    });

  }

  createOrganization() {

    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'ORG'}).attr({
      'color': '#e0e0e0'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: ''}).attr({
      'color': '#e0e0e0'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#e0e0e0'
    });
    endNode.attr({
      'cssClass': 'organization'
    });

    endNode.resetPorts();
    // endNode.createPort("hybrid",new draw2d.layout.locator.InputPortLocator());
    // endNode.createPort("hybrid",new draw2d.layout.locator.OutputPortLocator());
    endNode.createPort('input', new draw2d.layout.locator.BottomLocator()).on('connect', function (emitterPort, connection) {

      window.angularComponentRef.connectionPolicy(connection);
      // this.canvas.removeFigure(connection);

    });
    // endNode.createPort("output",new draw2d.layout.locator.TopLocator()).on("disconnect", function(emitterPort, connection){
    //   window.angularComponentRef.removeConnection(connection);
    // });

    endNode.on('click', function (emitter, event) {

      window.angularComponentRef.componentFn(event);

    });

    /*endNode.on("dblclick", function(emitter, event){


        window.angularComponentRef.componentDBClick(emitter);


      });*/
    endNode.on('removed', function (emitter, event) {


      window.angularComponentRef.removeComponent(emitter);

    });

    this.canvas.add(endNode);

    this.assets.push({
      'assessmentNode': true,
      'description': null,
      'goal': null,
      'name': null,
      'relatedRequirementsIds': [],
      'systemParticipantOwnerId': null,
      'children': [],
      'parents': [],
      'identifier': endNode.getId(), //Unique identifier is provided by Draw2d
      'objType': 'AssetModel',
      'nodeType': 'Organization'
    });


  }

  createMalfunction() {

    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'MAL'}).attr({
      'color': '#ffffff'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: ''}).attr({
      'color': '#ffffff'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#ffffff'
    });
    endNode.attr({
      'cssClass': 'malfunction'
    });

    endNode.resetPorts();
    // endNode.createPort("hybrid",new draw2d.layout.locator.InputPortLocator());
    // endNode.createPort("hybrid",new draw2d.layout.locator.OutputPortLocator());
    // endNode.createPort("input",new draw2d.layout.locator.BottomLocator()).on("connect", function(emitterPort, connection){
    //
    //   window.angularComponentRef.connectionPolicy(connection);
    //   // this.canvas.removeFigure(connection);
    //
    // });
    endNode.createPort('output', new draw2d.layout.locator.TopLocator()).on('disconnect', function (emitterPort, connection) {
      window.angularComponentRef.removeConnection(connection);
    });

    endNode.on('click', function (emitter, event) {


      window.angularComponentRef.componentFn(event);


    });

    /*endNode.on("dblclick", function(emitter, event){


        window.angularComponentRef.componentDBClick(emitter);


      });*/

    endNode.on('removed', function (emitter, event) {


      window.angularComponentRef.removeComponent(emitter);

    });


    this.canvas.add(endNode);

    this.malfuncOpt.push({'id': endNode.getId(), 'name': null, 'low': null, 'medium': null, 'high': null, 'critical': null, 'scales': []});

    this.assets.push({
      'assetCategory': null,
      'functionalConsequence': null,
      'functionalDescription': null,
      'functionalType': null,
      'technicalConsequence': null,
      'technicalDescription': null,
      'weight': 0.0,
      'technicalTypes': [],
      'scales': [],
      'assessmentNode': true,
      'description': null,
      'goal': null,
      'name': null,
      'relatedRequirementsIds': [],
      'systemParticipantOwnerId': null,
      'children': [],
      'parents': [],
      'identifier': endNode.getId(),
      'objType': 'AssetModel',
      'nodeType': 'Malfunction'
    });

  }

  createAsset() {

    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'AS'}).attr({
      'color': '#fdf5e2'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: ''}).attr({
      'color': '#fdf5e2'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#fdf5e2'
    });
    endNode.attr({
      'cssClass': 'asset'
    });

    endNode.resetPorts();
    // endNode.createPort("hybrid",new draw2d.layout.locator.InputPortLocator());
    // endNode.createPort("hybrid",new draw2d.layout.locator.OutputPortLocator());
    // endNode.createPort("input",new draw2d.layout.locator.BottomLocator()).on("connect", function(emitterPort, connection){
    //
    //   window.angularComponentRef.connectionPolicy(connection);
    //   // this.canvas.removeFigure(connection);
    //
    // });

    endNode.createPort('output', new draw2d.layout.locator.TopLocator()).on('disconnect', function (emitterPort, connection) {
      window.angularComponentRef.removeConnection(connection);
    });

    endNode.on('click', function (emitter, event) {


      window.angularComponentRef.componentFn(event);


    });

    /*endNode.on("dblclick", function(emitter, event){


        window.angularComponentRef.componentDBClick(emitter);


      });*/
    endNode.on('removed', function (emitter, event) {


      window.angularComponentRef.removeComponent(emitter);


    });


    this.canvas.add(endNode);

    this.assets.push({
      'category': null,
      'cost': 0,
      'primaryCategories': [],
      'businessImpacts': null,
      'securityImpacts': [],
      'malfunctionsIds': [],
      'assessmentNode': true,
      'description': null,
      'goal': null,
      'name': null,
      'relatedRequirementsIds': [],
      'systemParticipantOwnerId': null,
      'children': [],
      'parents': [],
      'identifier': endNode.getId(),
      'objType': 'AssetModel',
      'nodeType': 'Asset'
    });


  }

  dbClick(value) {
    // to manage the double click also when the user clicks on a label
    if (value.cssClass === 'draw2d_shape_basic_Label') {
      this.dbClick(value.parent);
    } else if (value.cssClass === 'draw2d_Connection') {
      return;
    }

    const listCon = value.getConnections();
    const listFig = this.canvas.getFigures();

    if (listCon.data.length != 0) {

      if ((this.allChildren.id == value.id) && (this.allChildren.check)) {
        this.allChildren.check = false;
        for (const g in listFig.data) {


          if (listFig.data[g].cssClass === 'asset') {

            for (const j in this.assets) {

              if (this.assets[j].identifier === listFig.data[g].id) {

                if (this.assets[j].malfunctionsIds.length != 0) {

                  let maxImpact = null;
                  for (const secImpacIndex in this.assets[j].securityImpacts) {

                    const currentImpact = this.assets[j].securityImpacts[secImpacIndex].impact;

                    maxImpact = this.realImpact(currentImpact, maxImpact);
                  }

                  listFig.data[g].setColor(this.impactColor(maxImpact));

                } else {
                  listFig.data[g].setColor('#000000');
                }

              }

            }

            listFig.data[g].setStroke(1);
          } else {
            listFig.data[g].setColor('#000000');
            listFig.data[g].setStroke(1);
          }

        }

        for (const r in listFig.data) {

          const secCon = listFig.data[r].getConnections();


          for (const we in this.edgeColor) {

            for (const t in secCon.data) {

              if (this.edgeColor[we].id === secCon.data[t].id) {
                // secCon.data[t].setColor("#129CE4")
                secCon.data[t].setColor(this.edgeColor[we].color);

                secCon.data[t].setStroke(1);
              }
            }
          }
        }


      } else {

        this.allChildren.id = value.id;
        this.allChildren.check = true;

        for (const g in listFig.data) {

          if (listFig.data[g].cssClass === 'asset') {

            for (const j in this.assets) {

              if (this.assets[j].identifier === listFig.data[g].id) {

                if (this.assets[j].malfunctionsIds.length != 0) {

                  let maxImpact = null;
                  for (const secImpacIndex in this.assets[j].securityImpacts) {

                    const currentImpact = this.assets[j].securityImpacts[secImpacIndex].impact;

                    maxImpact = this.realImpact(currentImpact, maxImpact);
                  }

                  listFig.data[g].setColor(this.impactColor(maxImpact));

                } else {
                  listFig.data[g].setColor('#000000');
                }

              }

            }

            listFig.data[g].setStroke(1);
          } else {
            listFig.data[g].setColor('#000000');
            listFig.data[g].setStroke(1);
          }

        }

        for (const r in listFig.data) {

          const secCon = listFig.data[r].getConnections();


          for (const we in this.edgeColor) {

            for (const t in secCon.data) {

              if (this.edgeColor[we].id === secCon.data[t].id) {
                secCon.data[t].setColor(this.edgeColor[we].color);
                secCon.data[t].setStroke(1);
              }

            }
          }
        }

        this.edgeColor = [];

        for (const i in listCon.data) {

          for (const j in listFig.data) {

            const secCon = listFig.data[j].getConnections();


            for (const x in secCon.data) {


              if (listCon.data[i].id == secCon.data[x].id) {


                if (listCon.data[i].getColor().hashString != '#0000B3') {


                  this.edgeColor.push({'id': listCon.data[i].id, 'color': listCon.data[i].getColor().hashString});

                }
                // listCon.data[i].attr({
                //         color: "#0000b3"
                //       });

                listCon.data[i].setColor('#0000b3');
                listCon.data[i].setStroke(4);


                listFig.data[j].setColor('#0000b3');

                listFig.data[j].setStroke(3);


              }


            }


          }

        }
      }

    }

    //
    // listFig.data[0].attr({
    //       color: "#f3f3f3"
    //     });

    //
    // for(let i in listCon){
    // console.log(listCon[i][0])
    //   listCon[i][0].canvas.attr({
    //     color: "#f3f3f3"
    //   });
    //
    // }

  }

  clickOnEdge(the) {


    try {

      this.selectedEdge = the;

      if (the.cssClass === 'draw2d_Connection') {

        if ((the.sourcePort.parent.cssClass === 'asset') || (the.targetPort.parent.cssClass === 'asset')) {


          this.idEdge = the.id;

          if (the.sourcePort.parent.cssClass === 'asset') {


            for (const j in this.assets) {

              if (this.assets[j].identifier === the.sourcePort.parent.id) {

                if (this.assets[j].malfunctionsIds.length != 0) {

                  this.thereIsMalf = true;

                } else {

                  this.thereIsMalf = false;
                }


              }

            }


            if (the.sourcePort.parent.children.data[1].figure.text != '') {

              this.targetNode = the.sourcePort.parent.children.data[1].figure.text;

            } else {

              this.targetNode = the.sourcePort.parent.id;
            }

            if (the.targetPort.parent.children.data[1].figure.text != '') {

              this.sourceNode = the.targetPort.parent.children.data[1].figure.text;

            } else {

              this.sourceNode = the.targetPort.parent.id;
            }

          } else {
            for (const j in this.assets) {

              if (this.assets[j].identifier === the.targetPort.parent.id) {

                if (this.assets[j].malfunctionsIds.length != 0) {

                  this.thereIsMalf = true;

                } else {

                  this.thereIsMalf = false;
                }


              }

            }
            // let isCheck = this.findMalfuction(the.targetPort.parent.id);
            //
            // if (isCheck != 0) {
            //
            //   this.thereIsMalf = true;
            // }

            if (the.sourcePort.parent.children.data[1].figure.text != '') {

              this.sourceNode = the.sourcePort.parent.children.data[1].figure.text;

            } else {

              this.sourceNode = the.sourcePort.parent.id;
            }

            if (the.targetPort.parent.children.data[1].figure.text != '') {

              this.targetNode = the.targetPort.parent.children.data[1].figure.text;

            } else {

              this.targetNode = the.targetPort.parent.id;
            }


          }

          this.availabilityEdge = null;
          this.confindentialityEdge = null;
          this.integrityEdge = null;
          this.efficiencyEdge = null;

          for (const ed in this.edges) {

            if (this.edges[ed].identifier === this.idEdge) {

              for (const sec in this.edges[ed].securityImpacts) {


                if (this.edges[ed].securityImpacts[sec].scope === 'Availability') {

                  this.availabilityEdge = this.edges[ed].securityImpacts[sec].impact;
                }

                if (this.edges[ed].securityImpacts[sec].scope === 'Confidentiality') {

                  this.confindentialityEdge = this.edges[ed].securityImpacts[sec].impact;
                }

                if (this.edges[ed].securityImpacts[sec].scope === 'Integrity') {

                  this.integrityEdge = this.edges[ed].securityImpacts[sec].impact;
                }

                if (this.edges[ed].securityImpacts[sec].scope === 'Efficiency') {

                  this.efficiencyEdge = this.edges[ed].securityImpacts[sec].impact;
                }


              }


            }

          }


          this.propertyCheck = 'edge';

        }
      } else if (the.cssClass === 'draw2d_shape_basic_Label') {

        // console.log("click on label");
        const figure = {'figure': the.parent};
        this.clickProcess(figure);
      }
    } catch (err) {

      this.deselectionReachability();

      this.propertyCheck = null;
    }

  }

  //If reachability of a node was highlighted, this method removes it
  deselectionReachability() {

    if (this.allChildren.check) {
      const listFig = this.canvas.getFigures();

      this.allChildren.check = false;


      for (const g in listFig.data) {

        if (listFig.data[g].cssClass === 'asset') {

          for (const j in this.assets) {

            if (this.assets[j].identifier === listFig.data[g].id) {

              if (this.assets[j].malfunctionsIds.length != 0) {

                let maxImpact = null;
                for (const secImpacIndex in this.assets[j].securityImpacts) {

                  const currentImpact = this.assets[j].securityImpacts[secImpacIndex].impact;

                  maxImpact = this.realImpact(currentImpact, maxImpact);
                }

                listFig.data[g].setColor(this.impactColor(maxImpact));

              } else {
                listFig.data[g].setColor('#000000');
              }

            }

          }

          listFig.data[g].setStroke(1);
        } else {
          listFig.data[g].setColor('#000000');
          listFig.data[g].setStroke(1);
        }

      }

      // for (let r in listFig.data) {

      //  let secCon = listFig.data[r].getConnections();


      for (const r in listFig.data) {

        const secCon = listFig.data[r].getConnections();


        for (const we in this.edgeColor) {
          for (const t in secCon.data) {

            if (this.edgeColor[we].id === secCon.data[t].id) {
              // secCon.data[t].setColor("#129CE4")
              secCon.data[t].setColor(this.edgeColor[we].color);

              secCon.data[t].setStroke(1);
            }

          }
        }
      }
      // }
    }

  }

  clickProcess(value) {

    /*console.log("There are some requirements")
      console.log(this.requirementsList)*/

    // let a={"filterMap":{"IDENTIFIER":this.idsystemproject}}
    //
    // this.getRequirements(JSON.stringify(a),value);


    // Requirements part
    this.showRequirementsList = [];
    this.categoryList = [];
    this.totalSubcategoryList = [];
    this.categoryList.push('All');


    this.selectedFigure = value.figure;

    this.id = this.selectedFigure.id;

    for (const i in this.assets) {

      if (this.id === this.assets[i].identifier) {

        if (this.selectedFigure.cssClass === 'process') {

          this.description = this.assets[i].description;
          this.goal = this.assets[i].goal;
          this.name = this.assets[i].name;
          this.owner = this.assets[i].systemParticipantOwnerId;

          this.businessEditElementForm.controls['ownerEP'].setValue(this.assets[i].systemParticipantOwnerId);
          this.type = this.assets[i].type;

          this.businessEditElementForm.controls['typeEP'].setValue(this.assets[i].type);

          this.showSelectedRequirements = [];
          //  this.selectedRequirementList=[];

          for (const m in this.assets[i].relatedRequirementsIds) {

            for (const n in this.requirementsList) {

              if (this.assets[i].relatedRequirementsIds[m] === this.requirementsList[n].identifier) {

                if (this.requirementsList[n].status != 'Canceled') {


                  this.showSelectedRequirements.push(this.requirementsList[n].id);
                  //  this.selectedRequirementList.push(({label:this.requirementsList[n].id+" ----- "+this.requirementsList[n].category+" ----- "+this.requirementsList[n].subCategory,value:this.requirementsList[n].identifier}))
                } else {

                  alert('The requirement ' + this.requirementsList[n].id + ' was canceled!');


                }


              }


            }


          }

          this.propertyCheck = 'businessProcess';

        } else if (this.selectedFigure.cssClass === 'activity') {

          this.description = this.assets[i].description;
          this.goal = this.assets[i].goal;
          this.name = this.assets[i].name;
          this.owner = this.assets[i].systemParticipantOwnerId;

          this.businessEditActivityForm.controls['ownerEA'].setValue(this.assets[i].systemParticipantOwnerId);

          this.showSelectedRequirements = [];
          // this.selectedRequirementList=[];

          for (const m in this.assets[i].relatedRequirementsIds) {

            for (const n in this.requirementsList) {

              if (this.assets[i].relatedRequirementsIds[m] === this.requirementsList[n].identifier) {

                if (this.requirementsList[n].status != 'Canceled') {

                  this.showSelectedRequirements.push(this.requirementsList[n].id);
                  //    this.selectedRequirementList.push(({label:this.requirementsList[n].id+" ----- "+this.requirementsList[n].category+" ----- "+this.requirementsList[n].subCategory,value:this.requirementsList[n].identifier}))
                } else {

                  alert('The requirement ' + this.requirementsList[n].id + ' was cancelled!');


                }


              }


            }


          }

          this.propertyCheck = 'businessActivity';
        } else if (this.selectedFigure.cssClass === 'organization') {

          this.description = this.assets[i].description;
          this.goal = this.assets[i].goal;
          this.name = this.assets[i].name;
          this.owner = this.assets[i].systemParticipantOwnerId;

          this.businessEditOrgForm.controls['ownerEO'].setValue(this.assets[i].systemParticipantOwnerId);

          this.showSelectedRequirements = [];
          //   this.selectedRequirementList=[];

          for (const m in this.assets[i].relatedRequirementsIds) {

            for (const n in this.requirementsList) {

              if (this.assets[i].relatedRequirementsIds[m] === this.requirementsList[n].identifier) {

                if (this.requirementsList[n].status != 'Canceled') {

                  this.showSelectedRequirements.push(this.requirementsList[n].id);
                  //    this.selectedRequirementList.push(({label:this.requirementsList[n].id+" ----- "+this.requirementsList[n].category+" ----- "+this.requirementsList[n].subCategory,value:this.requirementsList[n].identifier}))
                } else {

                  alert('The requirement ' + this.requirementsList[n].id + ' was canceled!');


                }


              }


            }


          }

          this.propertyCheck = 'businessOrg';

        } else if (this.selectedFigure.cssClass === 'malfunction') {

          this.name = this.assets[i].name;
          this.functionalDescription = this.assets[i].functionalDescription;
          this.technicalConsequence = this.assets[i].technicalConsequence;
          this.functionalConsequence = this.assets[i].functionalConsequence;
          this.technicalDescription = this.assets[i].technicalDescription;
          this.functionalType = this.assets[i].functionalType;
          // this.assetCategory=this.assets[i].assetCategory;

          this.malfunctionIntegrity = false;
          this.malfunctionConfidentiality = false;
          this.malfunctionEfficency = false;
          this.malfunctionAvailability = false;

          for (const r in this.assets[i].technicalTypes) {

            if (this.assets[i].technicalTypes[r] === 'Integrity_Loss') {
              this.malfunctionIntegrity = true;

            } else if (this.assets[i].technicalTypes[r] === 'Availability_Loss') {

              this.malfunctionAvailability = true;
            } else if (this.assets[i].technicalTypes[r] === 'Efficiency_Loss') {

              this.malfunctionEfficency = true;
            } else if (this.assets[i].technicalTypes[r] === 'Confidentiality_Loss') {

              this.malfunctionConfidentiality = true;
            }
          }

          for (const x in this.malfuncOpt) {

            if (this.id === this.malfuncOpt[x].id) {

              this.low = this.malfuncOpt[x].low;
              this.medium = this.malfuncOpt[x].medium;
              this.high = this.malfuncOpt[x].high;
              this.critical = this.malfuncOpt[x].critical;

            }

          }
          this.businessEditMalfunctioForm.controls['typeEF'].setValue(this.assets[i].functionalType);

          this.showSelectedRequirements = [];
          //  this.selectedRequirementList=[];

          for (const m in this.assets[i].relatedRequirementsIds) {

            for (const n in this.requirementsList) {

              if (this.assets[i].relatedRequirementsIds[m] === this.requirementsList[n].identifier) {

                if (this.requirementsList[n].status != 'Canceled') {

                  this.showSelectedRequirements.push(this.requirementsList[n].id);
                  //    this.selectedRequirementList.push(({label:this.requirementsList[n].id+" ----- "+this.requirementsList[n].category+" ----- "+this.requirementsList[n].subCategory,value:this.requirementsList[n].identifier}))
                } else {

                  alert('The requirement ' + this.requirementsList[n].id + ' was canceled!');


                }


              }


            }


          }

          this.propertyCheck = 'businessMalfun';

        } else if (this.selectedFigure.cssClass === 'asset') {
          const con = this.selectedFigure.getConnections();

          this.findAssociatedMalfuncions(this.assets[i]);

          this.name = this.assets[i].name;
          this.secondaryAssetCategory = this.assets[i].category;
          this.primaryAssetCategory = this.assets[i].primaryCategories;

          this.selectedCategoryService = '';
          this.selectedCategoryCompli = '';
          this.selectedCategoryData = '';


          if (this.primaryAssetCategory.length > 0) {

            if (this.primaryAssetCategory[0].indexOf('Data') != -1) {


              this.selectedCategoryData = this.primaryAssetCategory[0];


            } else if (this.primaryAssetCategory[0].indexOf('Service') != -1) {

              this.selectedCategoryService = this.primaryAssetCategory[0];

            } else if (this.primaryAssetCategory[0].indexOf('Compliance') != -1) {

              this.selectedCategoryCompli = this.primaryAssetCategory[0];

            }
          }


          this.showMalfunction = [];
          this.selectedMalfunction = JSON.parse(JSON.stringify(this.assets[i].malfunctionsIds));


          // to change edge color when there is an asset with 0 associated malfunctions
          this.newSizeMalf = this.assets[i].malfunctionsIds.length;


          this.businessEditAssetForm.controls['ownAsset'].setValue(this.assets[i].systemParticipantOwnerId);
          this.businessEditAssetForm.controls['desAsset'].setValue(this.assets[i].description);
          // this.businessEditAssetForm.controls['secAsset'].setValue(this.assets[i].category);
          this.selectedSecondary = this.assets[i].category;

          // this.associatedImpactToAsset(this.assets[i].parents);

          this.assetAvailability = null;
          this.assetEfficiency = null;
          this.assetConfidentiality = null;
          this.assetIntegrity = null;

          for (const impact in this.assets[i].securityImpacts) {

            if (this.assets[i].securityImpacts[impact].scope === 'Availability') {

              this.assetAvailability = this.assets[i].securityImpacts[impact].impact;
            }

            if (this.assets[i].securityImpacts[impact].scope === 'Integrity') {

              this.assetIntegrity = this.assets[i].securityImpacts[impact].impact;
            }

            if (this.assets[i].securityImpacts[impact].scope === 'Confidentiality') {

              this.assetConfidentiality = this.assets[i].securityImpacts[impact].impact;
            }

            if (this.assets[i].securityImpacts[impact].scope === 'Efficiency') {

              this.assetEfficiency = this.assets[i].securityImpacts[impact].impact;
            }
          }

          for (const j in this.assets[i].malfunctionsIds) {

            for (const x in this.assets) {


              // if it is a malfunction
              if (this.assets[x].identifier === this.assets[i].malfunctionsIds[j]) {

                if ((this.assets[x].name === null) || (this.assets[x].name === ' ')) {

                  this.showMalfunction.push(this.assets[i].malfunctionsIds[j]);

                } else {


                  this.showMalfunction.push(this.assets[x].name);
                }

              }


            }


          }

          this.showSelectedRequirements = [];
          //   this.selectedRequirementList=[];

          for (const m in this.assets[i].relatedRequirementsIds) {

            for (const n in this.requirementsList) {

              if (this.assets[i].relatedRequirementsIds[m] === this.requirementsList[n].identifier) {

                if (this.requirementsList[n].status != 'Canceled') {

                  this.showSelectedRequirements.push(this.requirementsList[n].id);
                  //     this.selectedRequirementList.push(({label:this.requirementsList[n].id+" ----- "+this.requirementsList[n].category+" ----- "+this.requirementsList[n].subCategory,value:this.requirementsList[n].identifier}))
                } else {

                  alert('The requirement ' + this.requirementsList[n].id + ' was canceled!');


                }


              }


            }


          }

          this.propertyCheck = 'businessAsset';

        }
      }

    }

  }

  // create the list of all the Malfunctions that could be associated to an asset
  findAssociatedMalfuncions(asset: any) {
    this.associatedMalf = [];

    const activities = [];
    const children = new Set<any>();
    asset.parents.forEach(parent => {
      activities.push(ServerAssetHelper.traverseFromChildrenToParent(asset, parent, this.serverAsset));
    });

    activities.forEach(a => {
      a.children.forEach(activityId => children.add(
        ServerAssetHelper.traverseFromParentToChildren(a, activityId, this.serverAsset)));
    });
    [...children].filter(c => (c.nodeType === 'Malfunction' && c.scales.length > 0)).forEach(child => {
      this.associatedMalf.push({label: child.name, value: child.identifier});
    });


    /*const malf = [];

    this.associatedMalf = [];


    for (const i in array.data) {

      if (array.data[i].sourcePort.parent.cssClass === 'activity') {

        const actCon = array.data[i].sourcePort.parent.getConnections();

        for (const j in actCon.data) {

          if (actCon.data[j].sourcePort.parent.cssClass === 'malfunction') {


            malf.push(actCon.data[j].sourcePort.parent);

          }
          if (actCon.data[j].targetPort.parent.cssClass === 'malfunction') {

            malf.push(actCon.data[j].sourcePort.parent);

          }

        }

      }
      if (array.data[i].targetPort.parent.cssClass === 'activity') {

        const actCon = array.data[i].targetPort.parent.getConnections();

        for (const j in actCon.data) {

          if (actCon.data[j].sourcePort.parent.cssClass === 'malfunction') {

            if (malf.findIndex(m => m.id === actCon.data[j].sourcePort.parent.id) === -1) {
              malf.push(actCon.data[j].sourcePort.parent);
            }

          }
          if (actCon.data[j].targetPort.parent.cssClass === 'malfunction') {

            if (malf.findIndex(m => m.id === actCon.data[j].targetPort.parent.id) === -1) {

              malf.push(actCon.data[j].sourcePort.parent);

            }

          }

        }


      }

    }

    for (const z in malf) {

      for (const x in this.malfuncOpt) {

        if (malf[z].id === this.malfuncOpt[x].id) {

          if (this.malfuncOpt[x].name != null) {


            this.associatedMalf.push({label: this.malfuncOpt[x].name, value: this.malfuncOpt[x].id});

          } else {

            this.associatedMalf.push({label: this.malfuncOpt[x].id, value: this.malfuncOpt[x].id});

          }
        }
      }

    }*/


  }

  //This event is called everytime we add a graphical edge on the canvas
  managementConnection(value) {
    let notAllowedConnection = false;
    let alreadyExistingConnection = false;

    // Here we check if the connection is allowed
    if (((value.connection.sourcePort.parent.cssClass === 'process') && ((value.connection.targetPort.parent.cssClass === 'process') || (value.connection.targetPort.parent.cssClass === 'asset') || (value.connection.targetPort.parent.cssClass === 'malfunction'))) ||
      ((value.connection.sourcePort.parent.cssClass === 'organization') && ((value.connection.targetPort.parent.cssClass === 'activity') || (value.connection.targetPort.parent.cssClass === 'malfunction') || (value.connection.targetPort.parent.cssClass === 'asset') || (value.connection.targetPort.parent.cssClass === 'organization'))) ||
      ((value.connection.sourcePort.parent.cssClass === 'activity') && ((value.connection.targetPort.parent.cssClass === 'organization') || (value.connection.targetPort.parent.cssClass === 'activity'))) ||
      ((value.connection.sourcePort.parent.cssClass === 'malfunction') && ((value.connection.targetPort.parent.cssClass === 'organization') || (value.connection.targetPort.parent.cssClass === 'malfunction') || (value.connection.targetPort.parent.cssClass === 'asset') || (value.connection.targetPort.parent.cssClass === 'process'))) ||
      ((value.connection.sourcePort.parent.cssClass === 'asset') && ((value.connection.targetPort.parent.cssClass === 'organization') || (value.connection.targetPort.parent.cssClass === 'malfunction') || (value.connection.targetPort.parent.cssClass === 'asset') || (value.connection.targetPort.parent.cssClass === 'process')))) {

      this.toBeRemoved = value.connection;

      notAllowedConnection = true;

    } else {
      //here we check if the edge is already existing in the AssetModel
      for (const i in this.edges) {
        if (this.edges[i].source === value.connection.sourcePort.parent.id && this.edges[i].target === value.connection.targetPort.parent.id) {
          this.toBeRemoved = value.connection;
          alreadyExistingConnection = true;
          break;
        } else if (this.edges[i].target === value.connection.sourcePort.parent.id && this.edges[i].source === value.connection.targetPort.parent.id) {
          this.toBeRemoved = value.connection;
          alreadyExistingConnection = true;
          break;
        }
      }
    }
    // If we can add the edge in the AssetModel we need to check which type it is
    if (!notAllowedConnection && !alreadyExistingConnection) {

      if (((value.connection.sourcePort.parent.cssClass === 'process') && (value.connection.targetPort.parent.cssClass === 'organization'))

        || ((value.connection.sourcePort.parent.cssClass === 'activity') && (value.connection.targetPort.parent.cssClass === 'process')) || ((value.connection.sourcePort.parent.cssClass === 'malfunction') && ((value.connection.targetPort.parent.cssClass === 'activity') || (value.connection.targetPort.parent.cssClass === 'process'))) || ((value.connection.sourcePort.parent.cssClass === 'asset') && (value.connection.targetPort.parent.cssClass === 'activity'))
      ) {

        this.edges.push({
          'operationalWeight': 0,
          'securityImpacts': [],
          'source': value.connection.targetPort.parent.id,
          'target': value.connection.sourcePort.parent.id,
          'businessImpactWeights': null,
          'identifier': value.connection.id,
          'objType': 'AssetModel'
        });

        // if ((value.connection.sourcePort.parent.cssClass === "asset")) {
        //   this.edgeBAS.push({
        //     "id": value.connection.id,
        //     "source": value.connection.targetPort.parent.id,
        //     "target": value.connection.sourcePort.parent.id,
        //     "Confidentiality": "",
        //     "Integrity": "",
        //     "Availability": "",
        //     "Efficiency": ""
        //   })
        // }
        for (const i in this.assets) {
          if (this.assets[i].identifier == value.connection.targetPort.parent.id) {
            this.assets[i].children.push(value.connection.id);
          }
          if (this.assets[i].identifier == value.connection.sourcePort.parent.id) {
            this.assets[i].parents.push(value.connection.id);
          }
        }
      } else {
        this.edges.push({
          'operationalWeight': 0,
          'securityImpacts': [],
          'source': value.connection.sourcePort.parent.id,
          'target': value.connection.targetPort.parent.id,
          'businessImpactWeights': null,
          'identifier': value.connection.id,
          'objType': 'AssetModel'
        });
        // if ((value.connection.targetPort.parent.cssClass === "asset")) {
        //   this.edgeBAS.push({
        //     "id": value.connection.id,
        //     "source": value.connection.sourcePort.parent.id,
        //     "target": value.connection.targetPort.parent.id,
        //     "Confidentiality": "",
        //     "Integrity": "",
        //     "Availability": "",
        //     "Efficiency": ""
        //   })
        // }
        for (const i in this.assets) {
          if (this.assets[i].identifier == value.connection.targetPort.parent.id) {
            this.assets[i].parents.push(value.connection.id);
          }
          if (this.assets[i].identifier == value.connection.sourcePort.parent.id) {
            this.assets[i].children.push(value.connection.id);
          }
        }
      }

      value.connection.attr({
        color: '#787878',
        stroke: 1
      });
      if ((value.connection.sourcePort.parent.cssClass === 'asset') || (value.connection.targetPort.parent.cssClass === 'asset')) {
        value.connection.attr({
          color: '#129CE4',
          stroke: 1
        });
      }
    }

    if (notAllowedConnection) {
      this.confirmationService.confirm({
        message: 'This connection is not allowed!',
        header: 'Warning',
        icon: 'fa fa-exclamation-triangle',
        accept: () => {
          // this.msgs = [{severity:'info', summary:'Confirmed', detail:'You have accepted'}];

          this.remove();
        },
        reject: () => {
          // this.msgs = [{severity:'info', summary:'Rejected', detail:'You have rejected'}];
        }
      });
      notAllowedConnection = false;
    }

    if (alreadyExistingConnection) {
      this.confirmationService.confirm({
        message: 'This connection has already been added!',
        header: 'Warning',
        icon: 'fa fa-exclamation-triangle',
        accept: () => {
          // this.msgs = [{severity:'info', summary:'Confirmed', detail:'You have accepted'}];

          this.remove();
        },
        reject: () => {
          // this.msgs = [{severity:'info', summary:'Rejected', detail:'You have rejected'}];
        }
      });
      alreadyExistingConnection = false;
    }
  }

  remove() {
    this.canvas.remove(this.toBeRemoved);
  }

  removeLink(event) {

    if (this.toBeRemoved != null) {
      this.toBeRemoved = null;
      return;
    }

    // I remove the corresponding edge on the edge list
    for (const i in this.edges) {
      if (this.edges[i].identifier === event.connection.id) {

        this.edges.splice(parseInt(i), 1);

      }

    }


    for (const j in this.assets) {

      for (const x in this.assets[j].parents) {

        if (this.assets[j].parents[x] === event.connection.id) {

          this.assets[j].parents.splice(parseInt(x), 1);

        }
      }
      for (const y in this.assets[j].children) {

        if (this.assets[j].children[y] === event.connection.id) {

          this.assets[j].children.splice(parseInt(y), 1);

        }
      }

    }

    // console.log("Check involved nodes");
    if ((event.connection.sourcePort.parent.cssClass === 'malfunction') && (event.connection.targetPort.parent.cssClass === 'activity')) {

      const assId = [];

      let indAsset;

      for (const j in this.assets) {

        if (this.assets[j].nodeType === 'Asset') {

          for (const y in this.assets[j].malfunctionsIds) {

            if (this.assets[j].malfunctionsIds[y] === event.connection.sourcePort.parent.id) {


              this.assets[j].malfunctionsIds.splice(parseInt(y), 1);

              const indexAsset = this.canvas.getFigures().data.findIndex(ind => ind.id === this.assets[j].identifier);

              const links = [];
              for (const link in this.canvas.getFigures().data[indexAsset].getConnections().data) {


                if (this.canvas.getFigures().data[indexAsset].getConnections().data[link].id != event.connection.id) {

                  links.push(this.canvas.getFigures().data[indexAsset].getConnections().data[link].id);
                }
              }

              assId.push({'asset': this.assets[j].identifier, 'malfunctions': this.assets[j].malfunctionsIds, 'links': links});


            }

          }

        }


      }

      this.newEditEdgeColor(assId);
    }

    if ((event.connection.sourcePort.parent.cssClass === 'asset') && (event.connection.targetPort.parent.cssClass === 'activity')) {

      const indexAssetDel = this.assets.findIndex(as => as.identifier === event.connection.sourcePort.parent.id);
      const indexAssetLinks = this.canvas.getFigures().data.findIndex(ind => ind.id === this.assets[indexAssetDel].identifier);

      const arrayForColor = [];

      const linksDel = [];

      for (const linkDel in this.canvas.getFigures().data[indexAssetLinks].getConnections().data) {

        linksDel.push(this.canvas.getFigures().data[indexAssetLinks].getConnections().data[linkDel].id);
      }

      arrayForColor.push({
        'asset': this.assets[indexAssetDel].identifier,
        'malfunctions': this.assets[indexAssetDel].malfunctionsIds,
        'links': linksDel
      });

      // to remove the asset's associated Malfunctions
      this.removeMalfunctionsToAsset(event.connection.targetPort.parent.id, this.assets[indexAssetDel].malfunctionsIds);

      this.newEditEdgeColor(arrayForColor);

    }


  }

  removeMalfunctionsToAsset(activityId, malfToAsset) {

    const malfToRemove = [];

    const indexActivity = this.assets.findIndex(ind => ind.identifier === activityId);

    for (const i in this.assets[indexActivity].children) {

      for (const j in this.assets) {

        if ((this.assets[j].nodeType === 'Malfunction') && (this.assets[j].parents.indexOf(this.assets[indexActivity].children[i]) != -1)) {

          malfToRemove.push(this.assets[j].identifier);

        }

      }


    }

    for (const k in malfToRemove) {

      const ind = malfToAsset.indexOf(malfToRemove[k]);

      if (ind != -1) {

        malfToAsset.splice(ind, 1);

      }
    }

  }

  removeFigure(event) {

    /*console.log("Remove figure");
      console.log(event);*/

    for (const j in this.assets) {

      if (this.assets[j].identifier === event.id) {

        this.assets.splice(parseInt(j), 1);
      }
    }

    for (const i in this.edges) {

      if ((this.edges[i].source === event.id) || (this.edges[i].target === event.id)) {

        this.edges.splice(parseInt(i), 1);
      }

    }


    if (event.cssClass === 'malfunction') {

      for (const x in this.malfuncOpt) {

        if (this.malfuncOpt[x].id === event.id) {

          this.malfuncOpt.splice(parseInt(x), 1);
        }

      }

      const assId = [];

      for (const j in this.assets) {

        if (this.assets[j].nodeType === 'Asset') {

          for (const y in this.assets[j].malfunctionsIds) {

            if (this.assets[j].malfunctionsIds[y] === event.id) {

              this.assets[j].malfunctionsIds.splice(parseInt(y), 1);

              assId.push({'asset': this.assets[j].identifier, 'malfunctions': this.assets[j].malfunctionsIds});


            }

          }

        }


      }

      // this.deleteEdgeColor(assId);


      // this.newEditEdgeColor(assId)

    }

    this.propertyCheck = null;

  }

  //This methods converts the Draw2d canvas into a JSON representation and saves all the model (including the SEST AssetModel) in the repository
  saveAssetModel() {

    let message;
    let check = 0;
    for (const i in this.assets) {

      //Checking if each element has a Name
      if ((this.assets[i].name === null) || (this.assets[i].name === '') || (this.assets[i].name === ' ')) {

        check = 1;
        message = 'Fill Name fields of all the elements of the Asset Model!';
        this.blocked = true;
        this.blockedMessage = true;
        this.showFailed(message);
        break;
      }

      //if (((this.assets[i].nodeType === 'Organization') && (this.assets[i].children.length === 0)) || ((this.assets[i].nodeType === 'BusinessProcess') && ((this.assets[i].children.length === 0) || (this.assets[i].parents.length === 0))) || ((this.assets[i].nodeType === 'BusinessActivity') && ((this.assets[i].children.length === 0) && (this.assets[i].parents.length === 0))) || ((this.assets[i].nodeType === 'Malfunction') && (this.assets[i].parents.length === 0)) || ((this.assets[i].nodeType === 'Asset') && (this.assets[i].parents.length === 0))) {
      if (
        /*((this.assets[i].nodeType === 'Organization') && (this.assets[i].children.length === 0))
        ||*/
        ((this.assets[i].nodeType === 'BusinessProcess') && (this.assets[i].parents.length === 0))
        ||
        ((this.assets[i].nodeType === 'BusinessActivity') && (this.assets[i].parents.length === 0))
        ||
        ((this.assets[i].nodeType === 'Malfunction') && (this.assets[i].parents.length === 0))
        ||
        ((this.assets[i].nodeType === 'Asset') && (this.assets[i].parents.length === 0))
      ) {
        check = 1;
        message = 'Connect all the components: ' + this.assets[i].name + ' missing connections';
        this.blocked = true;
        this.blockedMessage = true;
        this.showFailed(message);
        break;

      }

    }

    if (check === 0) {

      this.deselectionReachability();
      let a;
      const writer = new draw2d.io.json.Writer();
      writer.marshal(this.canvas, function (json) {
        $('#json').text(JSON.stringify(json, null, 2));

        a = json;

      });

      //this.jsonDocument = JSON.stringify(a, null, 2);
      this.finalJSON = ({
        'creationTime': this.serverAsset.creationTime,
        'updateTime': this.serverAsset.updateTime,
        'identifier': this.serverAsset.identifier,
        'edges': this.edges,
        'nodes': this.assets,
        'graphJson': a,
        'objType': 'AssetModel'
      });

      // console.log(JSON.stringify(this.finalJSON, null, 2))

      let completeList = {};
      completeList = {
        'jsonModel': (JSON.stringify(this.finalJSON, null, 2)),
        'objectIdentifier': this.serverAsset.identifier
      };

      this.sendAsset(JSON.stringify(completeList, null, 2));
    }

  }

  loadJSON() {
    const reader = new draw2d.io.json.Reader();

    reader.unmarshal(this.canvas, JSON.stringify(this.serverAsset.graphJson, null, 2));

    this.assets = [];
    this.assets = this.serverAsset.nodes;
    this.edges = [];
    this.edges = this.serverAsset.edges;

    const figures = this.canvas.getFigures().data;

    for (const i in figures) {

      if (figures[i].cssClass === 'process') {

        figures[i].add(new draw2d.shape.basic.Label({text: 'BP'}).attr({
          'color': '#d3e1ff'
        }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

        figures[i].add(new draw2d.shape.basic.Label({text: ''}).attr({
          'color': '#d3e1ff'
        }), new draw2d.layout.locator.CenterLocator());

        if (figures[i].isVisible() === false) {
          for (const label in figures[i].children.data) {

            figures[i].children.data[label].figure.setVisible(false);
          }
        }

        figures[i].inputPorts.data[0].on('connect', function (emitterPort, connection) {

          window.angularComponentRef.connectionPolicy(connection);

        });

        figures[i].outputPorts.data[0].on('disconnect', function (emitterPort, connection) {
          window.angularComponentRef.removeConnection(connection);
        });

        figures[i].on('click', function (emitter, event) {

          // console.log("click load json")
          window.angularComponentRef.componentFn(event);

        });
        /*figures[i].on("dblclick", function(emitter, event){

            window.angularComponentRef.componentDBClick(emitter);


          });*/
        figures[i].on('removed', function (emitter, event) {

          window.angularComponentRef.removeComponent(emitter);

        });


      } else if (figures[i].cssClass === 'activity') {

        figures[i].add(new draw2d.shape.basic.Label({text: 'BA'}).attr({
          'color': '#E6F1F5'
        }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

        figures[i].add(new draw2d.shape.basic.Label({text: ''}).attr({
          'color': '#E6F1F5'
        }), new draw2d.layout.locator.CenterLocator());

        if (figures[i].isVisible() === false) {
          for (const label in figures[i].children.data) {

            figures[i].children.data[label].figure.setVisible(false);
          }
        }

        figures[i].inputPorts.data[0].on('connect', function (emitterPort, connection) {

          window.angularComponentRef.connectionPolicy(connection);

        });

        figures[i].outputPorts.data[0].on('disconnect', function (emitterPort, connection) {
          window.angularComponentRef.removeConnection(connection);
        });

        figures[i].on('click', function (emitter, event) {

          window.angularComponentRef.componentFn(event);

        });

        /*figures[i].on("dblclick", function(emitter, event){

            window.angularComponentRef.componentDBClick(emitter);

          });*/
        figures[i].on('removed', function (emitter, event) {

          window.angularComponentRef.removeComponent(emitter);

        });

      } else if (figures[i].cssClass === 'organization') {

        figures[i].add(new draw2d.shape.basic.Label({text: 'ORG'}).attr({
          'color': '#e0e0e0'
        }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

        figures[i].add(new draw2d.shape.basic.Label({text: ''}).attr({
          'color': '#e0e0e0'
        }), new draw2d.layout.locator.CenterLocator());

        if (figures[i].isVisible() === false) {
          for (const label in figures[i].children.data) {

            figures[i].children.data[label].figure.setVisible(false);
          }
        }

        figures[i].inputPorts.data[0].on('connect', function (emitterPort, connection) {

          window.angularComponentRef.connectionPolicy(connection);

        });


        figures[i].on('click', function (emitter, event) {

          window.angularComponentRef.componentFn(event);

        });

        /*figures[i].on("dblclick", function(emitter, event){

            window.angularComponentRef.componentDBClick(emitter);

          });*/
        figures[i].on('removed', function (emitter, event) {

          window.angularComponentRef.removeComponent(emitter);

        });

      } else if (figures[i].cssClass === 'asset') {

        figures[i].add(new draw2d.shape.basic.Label({text: 'AS'}).attr({
          'color': '#fdf5e2'
        }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

        figures[i].add(new draw2d.shape.basic.Label({text: ''}).attr({
          'color': '#fdf5e2'
        }), new draw2d.layout.locator.CenterLocator());

        if (figures[i].isVisible() === false) {
          for (const label in figures[i].children.data) {

            figures[i].children.data[label].figure.setVisible(false);
          }
        }

        figures[i].outputPorts.data[0].on('disconnect', function (emitterPort, connection) {
          window.angularComponentRef.removeConnection(connection);
        });

        figures[i].on('click', function (emitter, event) {

          window.angularComponentRef.componentFn(event);

        });

        /*figures[i].on("dblclick", function(emitter, event){

            window.angularComponentRef.componentDBClick(emitter);

          });*/
        figures[i].on('removed', function (emitter, event) {

          window.angularComponentRef.removeComponent(emitter);

        });

      } else if (figures[i].cssClass === 'malfunction') {

        figures[i].add(new draw2d.shape.basic.Label({text: 'MAL'}).attr({
          'color': '#ffffff'
        }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

        figures[i].add(new draw2d.shape.basic.Label({text: ''}).attr({
          'color': '#ffffff'
        }), new draw2d.layout.locator.CenterLocator());

        if (figures[i].isVisible() === false) {
          for (const label in figures[i].children.data) {

            figures[i].children.data[label].figure.setVisible(false);
          }
        }

        figures[i].outputPorts.data[0].on('disconnect', function (emitterPort, connection) {
          window.angularComponentRef.removeConnection(connection);
        });

        figures[i].on('click', function (emitter, event) {

          window.angularComponentRef.componentFn(event);

        });

        /*figures[i].on("dblclick", function(emitter, event){

            window.angularComponentRef.componentDBClick(emitter);

          });*/

        figures[i].on('removed', function (emitter, event) {

          window.angularComponentRef.removeComponent(emitter);
        });

        figures[i].children.data[0].figure.attr({
          'color': figures[i].getBackgroundColor().hashString
        });
        figures[i].children.data[1].figure.attr({
          'color': figures[i].getBackgroundColor().hashString
        });

      }

    }

    this.associateData();
  }

  associateData() {

    const figures = this.canvas.getFigures().data;

    let low;
    let medium;
    let high;
    let critical;

    for (const i in this.assets) {

      if (this.assets[i].nodeType === 'Malfunction') {

        for (const k in this.assets[i].scales) {

          if (this.assets[i].scales[k].seriousness === 'LOW') {

            low = this.assets[i].scales[k].description;

          }
          if (this.assets[i].scales[k].seriousness === 'MEDIUM') {

            medium = this.assets[i].scales[k].description;

          }
          if (this.assets[i].scales[k].seriousness === 'HIGH') {

            high = this.assets[i].scales[k].description;

          }
          if (this.assets[i].scales[k].seriousness === 'CRITICAL') {

            critical = this.assets[i].scales[k].description;

          }

        }

        this.malfuncOpt.push({
          'name': this.assets[i].name,
          'id': this.assets[i].identifier,
          'scale': [],
          'low': low,
          'medium': medium,
          'high': high,
          'critical': critical
        });

        low = medium = high = critical = null;

      }

      for (const j in figures) {

        if (this.assets[i].identifier === figures[j].id) {

          if ((this.assets[i].name != null) && (this.assets[i].name != '')) {

            figures[j].children.data[1].figure.setText(this.modifyString(this.assets[i].name));
          }

        }

      }

    }

  }

  changeEdgeMode() {

    this.displayEditEdge = true;
  }

  changeMode() {

    if (this.selectedFigure.cssClass === 'process') {

      this.displayEditProcess = true;

    }
    if (this.selectedFigure.cssClass === 'activity') {

      this.displayEditActivity = true;

    }
    if (this.selectedFigure.cssClass === 'organization') {

      this.displayEditOrganization = true;

    }
    if (this.selectedFigure.cssClass === 'malfunction') {

      this.displayEditMalfunction = true;
    }
    if (this.selectedFigure.cssClass === 'asset') {

      this.displayEditAsset = true;

      /*console.log(this.selectedCategoryData)
        console.log(this.selectedCategory)
        console.log(this.selectedCategoryCompli)*/

    }

  }

  editProcess() {
    for (const i in this.assets) {


      if (this.assets[i].identifier === this.id) {

        this.assets[i].name = this.businessEditElementForm.value.nameEP;
        this.assets[i].description = this.businessEditElementForm.value.descriptionEP;
        this.assets[i].goal = this.businessEditElementForm.value.goalEP;
        this.assets[i].type = this.businessEditElementForm.value.typeEP;
        this.assets[i].systemParticipantOwnerId = this.businessEditElementForm.value.ownerEP;

        this.name = this.businessEditElementForm.value.nameEP;
        this.description = this.businessEditElementForm.value.descriptionEP;
        this.goal = this.businessEditElementForm.value.goalEP;
        this.type = this.businessEditElementForm.value.typeEP;
        this.owner = this.businessEditElementForm.value.ownerEP;

      }

    }
    this.displayEditProcess = false;
    if (this.businessEditElementForm.value.nameEP != null) {
      this.selectedFigure.children.data[1].figure.setText(this.modifyString(this.businessEditElementForm.value.nameEP));
    }

    this.businessEditElementForm.reset();
    this.propertyCheck = null;
  }

  closeEditProForm() {
    this.businessEditElementForm.reset();
    this.duplicateName = false;
    this.displayEditProcess = false;

  }

  editOrg() {

    for (const i in this.assets) {


      if (this.assets[i].identifier === this.id) {

        this.assets[i].name = this.businessEditOrgForm.value.nameEO;
        this.assets[i].description = this.businessEditOrgForm.value.descriptionEO;
        this.assets[i].goal = this.businessEditOrgForm.value.goalEO;

        this.assets[i].systemParticipantOwnerId = this.businessEditOrgForm.value.ownerEO;

        this.name = this.businessEditOrgForm.value.nameEO;
        this.description = this.businessEditOrgForm.value.descriptionEO;
        this.goal = this.businessEditOrgForm.value.goalEO;

        this.owner = this.businessEditOrgForm.value.ownerEO;

      }

    }
    this.displayEditOrganization = false;
    if (this.businessEditOrgForm.value.nameEO != null) {
      this.selectedFigure.children.data[1].figure.setText(this.modifyString(this.businessEditOrgForm.value.nameEO));
    }

    this.businessEditOrgForm.reset();
    this.propertyCheck = null;
  }

  closeEditOrgForm() {
    this.businessEditOrgForm.reset();
    this.duplicateName = false;
    this.displayEditOrganization = false;

  }

  editEdge() {

    if (!this.thereIsMalf) {

      if ((this.businessEditEdgeForm.value.impactAvaLevel === 'critical') || (this.businessEditEdgeForm.value.impactEffeLevel === 'critical') || (this.businessEditEdgeForm.value.impactLevel === 'critical') || (this.businessEditEdgeForm.value.impactIntLevel === 'critical')) {

        this.selectedEdge.attr({
          color: '#ff0000',
          stroke: 1
        });
      } else if ((this.businessEditEdgeForm.value.impactAvaLevel === 'high') || (this.businessEditEdgeForm.value.impactEffeLevel === 'high') || (this.businessEditEdgeForm.value.impactLevel === 'high') || (this.businessEditEdgeForm.value.impactIntLevel === 'high')) {

        this.selectedEdge.attr({
          color: '#ffa500',
          stroke: 1
        });
      } else if ((this.businessEditEdgeForm.value.impactAvaLevel === 'medium') || (this.businessEditEdgeForm.value.impactEffeLevel === 'medium') || (this.businessEditEdgeForm.value.impactLevel === 'medium') || (this.businessEditEdgeForm.value.impactIntLevel === 'medium')) {

        this.selectedEdge.attr({
          color: '#ffdb00',
          stroke: 1
        });
      } else if ((this.businessEditEdgeForm.value.impactAvaLevel === 'low') || (this.businessEditEdgeForm.value.impactEffeLevel === 'low') || (this.businessEditEdgeForm.value.impactLevel === 'low') || (this.businessEditEdgeForm.value.impactIntLevel === 'low')) {

        this.selectedEdge.attr({
          color: '#00ff00',
          stroke: 1
        });
      } else {

        this.selectedEdge.attr({
          color: '#129CE4',
          stroke: 1
        });

      }

    }

    this.displayEditEdge = false;
    this.propertyCheck = null;
  }

  editActivity() {

    for (const i in this.assets) {


      if (this.assets[i].identifier === this.id) {

        this.assets[i].name = this.businessEditActivityForm.value.nameEA;
        this.assets[i].description = this.businessEditActivityForm.value.descriptionEA;
        this.assets[i].goal = this.businessEditActivityForm.value.goalEA;

        this.assets[i].systemParticipantOwnerId = this.businessEditActivityForm.value.ownerEA;

        this.name = this.businessEditActivityForm.value.nameEA;
        this.description = this.businessEditActivityForm.value.descriptionEA;
        this.goal = this.businessEditActivityForm.value.goalEA;

        this.owner = this.businessEditActivityForm.value.ownerEA;

      }

    }

    this.displayEditActivity = false;
    if (this.businessEditActivityForm.value.nameEA != null) {
      this.selectedFigure.children.data[1].figure.setText(this.modifyString(this.businessEditActivityForm.value.nameEA));
    }

    this.businessEditActivityForm.reset();
    this.propertyCheck = null;
  }

  closeEditActivityForm() {
    this.businessEditActivityForm.reset();
    this.duplicateName = false;
    this.displayEditActivity = false;

  }

  // to associate the impact value to an asset
  associatedImpactToAsset(parent) {

    this.assetAvailability = null;
    this.assetEfficiency = null;
    this.assetConfidentiality = null;
    this.assetIntegrity = null;

    const borderImpact = [];

    for (const i in parent) {
      let finalColor = null;
      for (const j in this.edges) {
        if (parent[i] === this.edges[j].identifier) {

          for (const k in this.edges[j].securityImpacts) {

            if (this.edges[j].securityImpacts[k].scope === 'Availability') {

              this.assetAvailability = this.realImpact(this.assetAvailability, this.edges[j].securityImpacts[k].impact);

              finalColor = this.realImpact(finalColor, this.assetAvailability);

            }
            if (this.edges[j].securityImpacts[k].scope === 'Integrity') {

              this.assetIntegrity = this.realImpact(this.assetIntegrity, this.edges[j].securityImpacts[k].impact);
              finalColor = this.realImpact(finalColor, this.assetIntegrity);

            }
            if (this.edges[j].securityImpacts[k].scope === 'Confidentiality') {

              this.assetConfidentiality = this.realImpact(this.assetConfidentiality, this.edges[j].securityImpacts[k].impact);
              finalColor = this.realImpact(finalColor, this.assetConfidentiality);

            }
            if (this.edges[j].securityImpacts[k].scope === 'Efficiency') {

              this.assetEfficiency = this.realImpact(this.assetEfficiency, this.edges[j].securityImpacts[k].impact);
              finalColor = this.realImpact(finalColor, this.assetEfficiency);

            }

          }

          for (const k in this.assets) {

            if (this.assets[k].identifier === this.edges[j].target) {

              this.assets[k].securityImpacts = [];

              if (this.assetEfficiency != null) {

                this.assets[k].securityImpacts.push({
                  'impact': this.assetEfficiency,
                  'scope': 'Efficiency',
                  'tecnicalImpacts': []
                });

              }

              if (this.assetConfidentiality != null) {

                this.assets[k].securityImpacts.push({
                  'impact': this.assetConfidentiality,
                  'scope': 'Confidentiality',
                  'tecnicalImpacts': []
                });

              }

              if (this.assetIntegrity != null) {

                this.assets[k].securityImpacts.push({
                  'impact': this.assetIntegrity,
                  'scope': 'Integrity',
                  'tecnicalImpacts': []
                });

              }

              if (this.assetAvailability != null) {

                this.assets[k].securityImpacts.push({
                  'impact': this.assetAvailability,
                  'scope': 'Availability',
                  'tecnicalImpacts': []
                });

              }

              //  }

              const index = this.canvas.getFigures().data.findIndex((n) => n.id === this.assets[k].identifier);

              const indexColor = borderImpact.findIndex((x) => x.identifier === this.edges[j].target);

              if (indexColor != -1) {

                finalColor = this.realImpact(borderImpact[indexColor].impact, finalColor);
                borderImpact[indexColor].impact = finalColor;

              } else {
                borderImpact.push({'identifier': this.edges[j].target, 'impact': finalColor});
              }

              // for the border color
              if (finalColor === 'CRITICAL') {

                this.canvas.getFigures().data[index].setColor('#ff0000');
              } else if (finalColor === 'HIGH') {

                this.canvas.getFigures().data[index].setColor('#ffa500');
              } else if (finalColor === 'MEDIUM') {

                this.canvas.getFigures().data[index].setColor('#ffdb00');
              } else if (finalColor === 'LOW') {

                this.canvas.getFigures().data[index].setColor('#00ff00');
              } else {

                this.canvas.getFigures().data[index].setColor('#000000');

              }

            }

          }


        }

      }

    }

  }

  editAsset() {

    // let tempIndex=-1;

    this.primaryAssetCategory = [];
    if (this.selectedCategoryData != '') {

      // this.primaryAssetCategory.push({"name":this.selectedCategoryData[i],"category":"Data"})
      this.primaryAssetCategory.push(this.selectedCategoryData);
      this.selectedCategoryData = '';
    }
    if (this.selectedCategoryService != '') {

      // this.primaryAssetCategory.push({"name":this.selectedCategoryService[j],"category":"Service"})
      this.primaryAssetCategory.push(this.selectedCategoryService);

      this.selectedCategoryService = '';
    }
    if (this.selectedCategoryCompli != '') {

      // this.primaryAssetCategory.push({"name":this.selectedCategoryCompli[x],"category":"Regolations"})
      this.primaryAssetCategory.push(this.selectedCategoryCompli);
      this.selectedCategoryCompli = '';

    }

    this.name = this.businessEditAssetForm.value.assetName;
    this.owner = this.businessEditAssetForm.value.ownAsset;
    // this.secondaryAssetCategory=this.businessEditAssetForm.value.secAsset;
    this.secondaryAssetCategory = this.selectedSecondary;


    if (this.businessEditAssetForm.value.assetName != null) {
      this.selectedFigure.children.data[1].figure.setText(this.modifyString(this.businessEditAssetForm.value.assetName));
    }

    this.showMalfunction = [];


    for (const y in this.assets) {

      if (this.assets[y].identifier === this.id) {

        // tempIndex=parseInt(y);


        // this.assets[y].category=this.businessEditAssetForm.value.secAsset;
        this.assets[y].category = this.selectedSecondary;


        if (this.businessEditAssetForm.value.desAsset != undefined) {
          this.assets[y].description = this.businessEditAssetForm.value.desAsset;
        }

        if (this.businessEditAssetForm.value.assetName != undefined) {
          this.assets[y].name = this.businessEditAssetForm.value.assetName;
        }

        if (this.businessEditAssetForm.value.ownAsset != undefined) {
          this.assets[y].systemParticipantOwnerId = this.businessEditAssetForm.value.ownAsset;
        }
        // this.sizeMalf= this.assets[y].malfunctionsIds.length;


        this.assets[y].malfunctionsIds = [];
        // for(let w in this.associatedMalf){
        //   this.assets[y].malfunctionsIds.push(this.associatedMalf[w].value)
        //
        // }


        this.assets[y].malfunctionsIds = JSON.parse(JSON.stringify(this.selectedMalfunction));


        this.sizeMalf = this.newSizeMalf;

        this.newSizeMalf = this.assets[y].malfunctionsIds.length;


        this.assets[y].primaryCategories = [];
        for (const k in this.primaryAssetCategory) {

          // this.assets[y].primaryCategories.push(this.primaryAssetCategory[k].name)
          this.assets[y].primaryCategories.push(this.primaryAssetCategory[k]);

        }

      }

      for (const g in this.selectedMalfunction) {

        if (this.assets[y].identifier === this.selectedMalfunction[g]) {

          if ((this.assets[y].name === null) || this.assets[y].name === ' ') {

            this.showMalfunction.push(this.selectedMalfunction[g], this.selectedMalfunction[g]);

          } else {

            this.showMalfunction.push(this.assets[y].name, this.selectedMalfunction[g]);

          }

        }

        // for color of the edge

      }

    }
    this.map = [];

    const indexAsset = this.canvas.getFigures().data.findIndex(ind => ind.id === this.id);

    const assId = [];
    const links = [];
    for (const link in this.canvas.getFigures().data[indexAsset].getConnections().data) {

      links.push(this.canvas.getFigures().data[indexAsset].getConnections().data[link].id);
    }

    assId.push({'asset': this.id, 'malfunctions': this.selectedMalfunction, 'links': links});

    // this.findColorEdgeWithMalfunction(this.selectedMalfunction);

    this.newEditEdgeColor(assId);


    // this.selectedMalfunction=[];
    this.propertyCheck = null;
    this.displayEditAsset = false;

    this.businessEditAssetForm.reset();
    // to show selected Malfunction also when an user clicks on "edit asset" button without to deselect the asset before
    // this.selectedMalfunction=this.assets[tempIndex].malfunctionsIds;

  }

  closeEditAssetForm() {
    this.businessEditAssetForm.reset();
    this.duplicateName = false;
    this.displayEditAsset = false;
  }

  //Not used. Consider to remove
  findColorEdgeWithMalfunction(arrayMal) {

    for (const i in this.selectedFigure.getConnections().data) {

      if (this.selectedFigure.getConnections().data[i].sourcePort.parent.cssClass === 'activity') {

        for (const j in arrayMal) {

          const ser = [];
          let tech = [];

          for (const z in this.edges) {

            if ((this.edges[z].source === this.selectedFigure.getConnections().data[i].sourcePort.parent.id) && (this.edges[z].target === arrayMal[j])) {

              for (const q in this.assets) {
                if (this.assets[q].identifier === arrayMal[j]) {

                  for (const t in this.assets[q].scales) {

                    ser.push(this.assets[q].scales[t].seriousness.split('_')[0]);

                  }
                  const appTech = [];
                  // console.log("First error")
                  appTech.push(this.assets[q].technicalTypes[0].split('_')[0]);
                  tech = appTech;

                }

              }

              this.map.push({
                'edge': this.selectedFigure.getConnections().data[i],
                'malfunction': arrayMal[j],
                'seriousness': ser,
                'technical': tech
              });

            }

          }


        }


      }
      if (this.selectedFigure.getConnections().data[i].targetPort.parent.cssClass === 'activity') {

        for (const j in arrayMal) {
          const ser = [];
          let tech = [];
          for (const z in this.edges) {

            if ((this.edges[z].source === this.selectedFigure.getConnections().data[i].targetPort.parent.id) && (this.edges[z].target === arrayMal[j])) {

              for (const q in this.assets) {
                if (this.assets[q].identifier === arrayMal[j]) {

                  for (const t in this.assets[q].scales) {

                    ser.push(this.assets[q].scales[t].seriousness);

                  }

                  const appTech = [];

                  appTech.push(this.assets[q].technicalTypes[0].split('_')[0]);
                  tech = appTech;

                }


              }

              this.map.push({
                'edge': this.selectedFigure.getConnections().data[i],
                'malfunction': arrayMal[j],
                'seriousness': ser,
                'technical': tech
              });

            }

          }

        }

      }

    }


    this.changeEdgeColor(this.map);

    /*console.log("map to be seen")
      console.log(this.map)*/

  }

  //Not used. Consider to remove
  changeEdgeColor(map) {

    let selectedColor = '';

    this.involvedEdges = [];
    this.involvedEdges.push({'asset': this.id, 'edge': map});

    for (const i in this.selectedFigure.getConnections().data) {

      for (const j in map) {

        if (map[j].edge.id === this.selectedFigure.getConnections().data[i].id) {

          // to set the edge securityImpacts

          for (const ed in this.edges) {

            if (this.edges[ed].identifier === map[j].edge.id) {

              // it gets the current securityImpacts of the edge
              const techEdge = this.edges[ed].securityImpacts;


              let index = -1;

              // for each kind of technical
              for (const tec in map[j].technical) {

                // it checks if already the edge has this kind of technical
                index = techEdge.findIndex((i) => i.scope === map[j].technical[tec].split('_')[0]);


                // if the edge contains this type of technical
                if (index != -1) {

                  const currentImpact = techEdge[index].impact;


                  // it update the value of the technical
                  this.edges[ed].securityImpacts[index].impact = this.realImpact(this.impactArray(map[j].seriousness), currentImpact);

                } else {

                  // it adds the new technical
                  this.edges[ed].securityImpacts.push({
                    'impact': this.impactArray(map[j].seriousness),
                    'scope': map[j].technical[tec].split('_')[0],
                    'tecnicalImpacts': []
                  });

                }

              }

            }

            /*console.log("edge with values")
              console.log(this.edges[ed])*/


          }

          for (const x in map[j].seriousness) {

            if (map[j].seriousness[x] === 'CRITICAL') {

              selectedColor = 'CRITICAL';

            } else if ((map[j].seriousness[x] === 'HIGH') && (selectedColor != 'CRITICAL')) {

              selectedColor = 'HIGH';


            } else if ((map[j].seriousness[x] === 'MEDIUM') && (selectedColor != 'CRITICAL') && (selectedColor != 'HIGH')) {

              selectedColor = 'MEDIUM';


            } else if ((map[j].seriousness[x] === 'LOW') && (selectedColor === '')) {

              selectedColor = 'LOW';


            }


          }


        }

      }
      if (selectedColor === 'CRITICAL') {

        this.selectedFigure.getConnections().data[i].attr({
          color: '#ff0000',
          stroke: 1
        });

        if (this.coloredByMal.findIndex(ind => ind.edge === this.id) === -1) {

          this.coloredByMal.push({'asset': this.id, 'edge': this.selectedFigure.getConnections().data[i].id});
        }

      } else {

        if (selectedColor === 'HIGH') {

          this.selectedFigure.getConnections().data[i].attr({
            color: '#ffa500',
            stroke: 1
          });

          if (this.coloredByMal.findIndex(ind => ind.edge === this.id) === -1) {

            this.coloredByMal.push({'asset': this.id, 'edge': this.selectedFigure.getConnections().data[i].id});
          }

        } else if (selectedColor === 'MEDIUM') {

          this.selectedFigure.getConnections().data[i].attr({
            color: '#ffdb00',
            stroke: 1
          });
          if (this.coloredByMal.findIndex(ind => ind.edge === this.id) === -1) {

            this.coloredByMal.push({'asset': this.id, 'edge': this.selectedFigure.getConnections().data[i].id});
          }

        } else if (selectedColor === 'LOW') {

          this.selectedFigure.getConnections().data[i].attr({
            color: '#00ff00',
            stroke: 1
          });

          if (this.coloredByMal.findIndex(ind => ind.edge === this.id) === -1) {

            this.coloredByMal.push({'asset': this.id, 'edge': this.selectedFigure.getConnections().data[i].id});
          }

        } else {

          for (const as in this.edges) {


            if (this.selectedFigure.getConnections().data[i].id === this.edges[as].identifier) {

              this.edges[as].securityImpacts = [];

            }

          }


        }


      }
      selectedColor = '';

    }

    // to associate the new impact data in the edges array for visualization

    for (const as in this.assets) {
      if (this.assets[as].identifier === this.id) {

        this.associatedImpactToAsset(this.assets[as].parents);
      }
    }

    /*console.log("new")
      console.log(this.newSizeMalf)
      console.log("old")
      console.log(this.sizeMalf)*/
    if (this.newSizeMalf < this.sizeMalf) {

      this.checkedColor(this.involvedEdges, this.coloredByMal);
    }

  }

  // to discolour an edge without malfunctions
  checkedColor(involved, colored) {

    /*console.log("+++++++entering checkColor++++++")

      console.log(involved)
      console.log(colored)*/

    const asset = involved[0].asset;
    const discoloured = [];

    for (const i in colored) {

      if (colored[i].asset === asset) {

        let a = 0;
        for (const j in involved) {

          if (involved[j].edge.length === 0) {

            discoloured.push({'index': i, 'edge': colored[i].edge});
          } else {


            if (colored[i].edge === involved[j].edge[0].edge.id) {

              a = a + 1;

            }


            if (a === 0) {

              discoloured.push({'index': i, 'edge': colored[i].edge});

            }

          }
        }

      }

    }


    const connections = this.selectedFigure.getConnections().data;

    for (const dis in discoloured) {

      for (const k in connections) {

        if (discoloured[dis].edge === connections[k].id) {

          // console.log("-----BLUE---")

          connections[k].attr({
            color: '#129CE4',
            stroke: 1
          });

          this.coloredByMal.splice(discoloured[dis].index, 1);

        }

      }

    }

  }

  // it gets a seriousness array and return the worst seriousness
  impactArray(array): string {

    let actual = ' ';

    if (array.length === 0) {

      return actual;
    }

    for (const i in array) {

      actual = this.realImpact(array[i], actual);

    }

    return actual;

  }

  // it gets 2 seriousness and returns the worst
  realImpact(newI, oldI): string {

    if (newI === 'CRITICAL') {

      return 'CRITICAL';

    } else if ((newI === 'HIGH') && (oldI != 'CRITICAL')) {

      return 'HIGH';

    } else if ((newI === 'MEDIUM') && (oldI != 'CRITICAL') && (oldI != 'HIGH')) {

      return 'MEDIUM';

    } else if ((newI === 'LOW') && (oldI != 'CRITICAL') && (oldI != 'HIGH') && (oldI != 'MEDIUM')) {

      return 'LOW';

    } else {

      return oldI;
    }

  }

  editMal() {
    for (const i in this.assets) {

      if (this.assets[i].identifier === this.id) {

        this.assets[i].name = this.businessEditMalfunctioForm.value.nameEM;
        this.assets[i].technicalDescription = this.businessEditMalfunctioForm.value.descriptionET;

        // this.assets[i].assetCategory=this.businessEditMalfunctioForm.value.assCat;
        this.assets[i].technicalConsequence = this.businessEditMalfunctioForm.value.consE;
        this.assets[i].functionalConsequence = this.businessEditMalfunctioForm.value.consEF;
        this.assets[i].functionalDescription = this.businessEditMalfunctioForm.value.descriptionEF;
        this.assets[i].functionalType = this.businessEditMalfunctioForm.value.typeEF;
        this.assets[i].technicalTypes = [];

        if (this.malfunctionAvailability) {

          this.assets[i].technicalTypes.push('Availability_Loss');

        }
        if (this.malfunctionConfidentiality) {

          this.assets[i].technicalTypes.push('Confidentiality_Loss');

        }
        if (this.malfunctionEfficency) {


          this.assets[i].technicalTypes.push('Efficiency_Loss');

        }
        if (this.malfunctionIntegrity) {

          this.assets[i].technicalTypes.push('Integrity_Loss');

        }

        this.assets[i].scales = [];
        if ((this.businessEditMalfunctioForm.value.criticalE != null) && (this.businessEditMalfunctioForm.value.criticalE != '')) {

          this.assets[i].scales.push({'seriousness': 'CRITICAL', 'description': this.businessEditMalfunctioForm.value.criticalE});

        }
        if ((this.businessEditMalfunctioForm.value.highE != null) && (this.businessEditMalfunctioForm.value.highE != '')) {

          this.assets[i].scales.push({'seriousness': 'HIGH', 'description': this.businessEditMalfunctioForm.value.highE});

        }
        if ((this.businessEditMalfunctioForm.value.mediumE != null) && (this.businessEditMalfunctioForm.value.mediumE != '')) {

          this.assets[i].scales.push({'seriousness': 'MEDIUM', 'description': this.businessEditMalfunctioForm.value.mediumE});

        }
        if ((this.businessEditMalfunctioForm.value.lowE != null) && (this.businessEditMalfunctioForm.value.lowE != '')) {

          this.assets[i].scales.push({'seriousness': 'LOW', 'description': this.businessEditMalfunctioForm.value.lowE});

        }

        this.name = this.businessEditMalfunctioForm.value.nameEM;
        this.technicalDescription = this.businessEditMalfunctioForm.value.descriptionET;
        this.technicalConsequence = this.businessEditMalfunctioForm.value.consE;


        for (const x in this.malfuncOpt) {

          if (this.id === this.malfuncOpt[x].id) {

            this.malfuncOpt[x].low = this.businessEditMalfunctioForm.value.lowE;
            this.malfuncOpt[x].medium = this.businessEditMalfunctioForm.value.mediumE;
            this.malfuncOpt[x].high = this.businessEditMalfunctioForm.value.highE;
            this.malfuncOpt[x].critical = this.businessEditMalfunctioForm.value.criticalE;
            this.malfuncOpt[x].name = this.businessEditMalfunctioForm.value.nameEM;

          }

        }

      }

    }


    if ((this.businessEditMalfunctioForm.value.criticalE != null) && (this.businessEditMalfunctioForm.value.criticalE != '')) {

      this.selectedFigure.attr({
        'bgColor': '#ff0000'
      });
      this.selectedFigure.children.data[0].figure.attr({
        'color': '#ff0000'
      });
      this.selectedFigure.children.data[1].figure.attr({
        'color': '#ff0000'
      });

    } else {

      if ((this.businessEditMalfunctioForm.value.highE != null) && (this.businessEditMalfunctioForm.value.highE != '')) {

        this.selectedFigure.attr({
          'bgColor': '#ffa500'
        });
        this.selectedFigure.children.data[0].figure.attr({
          'color': '#ffa500'
        });
        this.selectedFigure.children.data[1].figure.attr({
          'color': '#ffa500'
        });


      } else if ((this.businessEditMalfunctioForm.value.mediumE != null) && (this.businessEditMalfunctioForm.value.mediumE != '')) {

        this.selectedFigure.attr({
          'bgColor': '#ffdb00'
        });
        this.selectedFigure.children.data[0].figure.attr({
          'color': '#ffdb00'
        });
        this.selectedFigure.children.data[1].figure.attr({
          'color': '#ffdb00'
        });
      } else if ((this.businessEditMalfunctioForm.value.lowE != null) && (this.businessEditMalfunctioForm.value.lowE != '')) {

        this.selectedFigure.attr({
          'bgColor': '#00ff00'
        });
        this.selectedFigure.children.data[0].figure.attr({
          'color': '#00ff00'
        });
        this.selectedFigure.children.data[1].figure.attr({
          'color': '#00ff00'
        });

      } else {
        this.selectedFigure.attr({
          'bgColor': '#ffffff'
        });

        this.selectedFigure.children.data[0].figure.attr({
          'color': '#ffffff'
        });
        this.selectedFigure.children.data[1].figure.attr({
          'color': '#ffffff'
        });
      }

    }

    if (this.businessEditMalfunctioForm.value.nameEM != null) {
      // this.selectedFigure.children.data[1].figure.setText(this.businessEditMalfunctioForm.value.nameEM);
      this.selectedFigure.children.data[1].figure.setText(this.modifyString(this.businessEditMalfunctioForm.value.nameEM));
    }

    this.displayEditMalfunction = false;

    this.propertyCheck = null;
    this.businessEditMalfunctioForm.reset();


    // list of the assets that contain this malfunction like associated malfunction
    const assId = [];

    for (const j in this.assets) {

      if (this.assets[j].nodeType === 'Asset') {

        for (const y in this.assets[j].malfunctionsIds) {

          if (this.assets[j].malfunctionsIds[y] === this.selectedFigure.id) {

            assId.push({
              'asset': this.assets[j].identifier,
              'malfunctions': this.assets[j].malfunctionsIds,
              'links': this.assets[j].parents
            });


          }

        }

      }


    }

    // console.log("edit Mal")
    this.newEditEdgeColor(assId);
  }

  closeEditMalForm() {
    this.businessEditMalfunctioForm.reset();
    this.duplicateName = false;
    this.displayEditMalfunction = false;
  }

  //assId is an array
  //This method changes the color of an asset depending on the related Malfunctions
  newEditEdgeColor(assId) {
    console.log('newEditEdgeColor');
    console.log(assId);

    const graphicAssets = [];
    const graphicMalfunctions = [];
    const allFigures = this.canvas.getFigures().data;

    // to create a structure for graphical Assets and a structure for graphical Malfunctions
    for (const i in assId) {

      for (const j in allFigures) {

        if ((allFigures[j].cssClass === 'asset') && (allFigures[j].id === assId[i].asset)) {

          graphicAssets.push({graphic: allFigures[j], links: assId[i].links});

        } else if (allFigures[j].cssClass === 'malfunction') {

          const malIndex = assId[i].malfunctions.indexOf(allFigures[j].id);
          if ((malIndex != -1) && (graphicMalfunctions.findIndex(mal => mal.graphic.id === allFigures[j].id) === -1)) {

            graphicMalfunctions.push({graphic: allFigures[j], tType: [], scale: [], links: [], activities: []});

          }

        }


      }

    }

    // to add to graphical Malfunctions  their impacts and their edges
    for (const k in graphicMalfunctions) {
      const indexMalfunction = this.assets.findIndex(as => as.identifier === graphicMalfunctions[k].graphic.id);
      graphicMalfunctions[k].tType = this.assets[indexMalfunction].technicalTypes;
      graphicMalfunctions[k].scale = this.assets[indexMalfunction].scales;
      graphicMalfunctions[k].links = this.assets[indexMalfunction].parents;
    }

    // to add to graphical Malfunctions their associated activities
    for (const z in graphicMalfunctions) {

      for (const x in graphicMalfunctions[z].links) {

        const activityIndex = this.edges.findIndex(ed => ed.identifier === graphicMalfunctions[z].links[x]);

        if ((activityIndex != -1) && (graphicMalfunctions[z].activities.indexOf(this.edges[activityIndex].source) === -1)) {

          graphicMalfunctions[z].activities.push(this.edges[activityIndex].source);

        }

      }

    }

    /*console.log("figures")
      console.log(graphicMalfunctions)
      console.log(graphicAssets)*/

    this.calculateValueActivity(graphicMalfunctions, graphicAssets);

  }

  calculateValueActivity(graphicMalfunctions, graphicAssets) {

    const activitiesValue = [];
    let integrity = null;
    let confidentiality = null;
    let availability = null;
    let efficiency = null;

    for (const i in graphicMalfunctions) {

      integrity = null;
      confidentiality = null;
      availability = null;
      efficiency = null;

      for (const j in graphicMalfunctions[i].activities) {

        let value = null;

        for (const t in graphicMalfunctions[i].scale) {

          value = this.maxImpactValue(value, graphicMalfunctions[i].scale[t].seriousness);

        }

        for (const y in graphicMalfunctions[i].tType) {

          if (graphicMalfunctions[i].tType[y].split('_')[0] === 'Confidentiality') {

            confidentiality = this.maxImpactValue(confidentiality, value);

          } else if (graphicMalfunctions[i].tType[y].split('_')[0] === 'Availability') {

            availability = this.maxImpactValue(availability, value);

          } else if (graphicMalfunctions[i].tType[y].split('_')[0] === 'Efficiency') {

            efficiency = this.maxImpactValue(efficiency, value);
          } else if (graphicMalfunctions[i].tType[y].split('_')[0] === 'Integrity') {

            integrity = this.maxImpactValue(integrity, value);
          }
        }

        const activityIndex = activitiesValue.findIndex(act => act.id === graphicMalfunctions[i].activities[j]);

        if (activityIndex === -1) {

          activitiesValue.push({
            id: graphicMalfunctions[i].activities[j],
            confidentiality: confidentiality,
            integrity: integrity,
            efficiency: efficiency,
            availability: availability
          });

        } else {

          activitiesValue[activityIndex].confidentiality = this.maxImpactValue(activitiesValue[activityIndex].confidentiality, confidentiality);
          activitiesValue[activityIndex].integrity = this.maxImpactValue(activitiesValue[activityIndex].integrity, integrity);
          activitiesValue[activityIndex].efficiency = this.maxImpactValue(activitiesValue[activityIndex].efficiency, efficiency);
          activitiesValue[activityIndex].availability = this.maxImpactValue(activitiesValue[activityIndex].availability, availability);

        }

      }

    }

    /*console.log("Activities Value")
      console.log(activitiesValue);*/

    this.assignValueAndColorToEdgesByMalfunctions(activitiesValue, graphicAssets);

  }

  // it assigns color and values to the edge
  assignValueAndColorToEdgesByMalfunctions(activitiesValue, graphicAssets) {
    console.log('assignValueAndColorToEdgesByMalfunctions');

    // for each involved asset
    for (const i in graphicAssets) {

      const finalBorder = [];

      // it checks if the involved asset has links
      if (graphicAssets[i].links.length > 0) {

        // for each link
        for (const j in graphicAssets[i].links) {

          for (const k in this.assets) {

            if (this.assets[k].nodeType === 'BusinessActivity') {

              const index = this.assets[k].children.indexOf(graphicAssets[i].links[j]);

              // if there is an Activity that contains an asset edge as child
              if (index != -1) {

                const indexActivity = activitiesValue.findIndex(act => act.id === this.assets[k].identifier);

                /*console.log(indexActivity)
                  console.log(activitiesValue)*/

                const indexGraphicalEdge = this.canvas.getLines().data.findIndex(ed => ed.id === graphicAssets[i].links[j]);

                // if this activity has impact
                if (indexActivity != -1) {

                  let maximunValue = null;

                  // it finds the corrisponding graphical edge of the asset
                  const indexDataEdge = this.edges.findIndex(ed => ed.identifier === graphicAssets[i].links[j]);

                  //This directly modifies the edge (not only the graphical representation)
                  this.edges[indexDataEdge].securityImpacts = [];

                  // it assigns  new data to the edge and calculates the edge max impact
                  if (activitiesValue[indexActivity].confidentiality != null) {

                    this.edges[indexDataEdge].securityImpacts.push({
                      impact: activitiesValue[indexActivity].confidentiality,
                      scope: 'Confidentiality',
                      tecnicalImpacts: []
                    });

                    maximunValue = this.maxImpactValue(maximunValue, activitiesValue[indexActivity].confidentiality);

                  }
                  if (activitiesValue[indexActivity].availability != null) {

                    this.edges[indexDataEdge].securityImpacts.push({
                      impact: activitiesValue[indexActivity].availability,
                      scope: 'Availability',
                      tecnicalImpacts: []
                    });

                    maximunValue = this.maxImpactValue(maximunValue, activitiesValue[indexActivity].availability);
                  }
                  if (activitiesValue[indexActivity].integrity != null) {

                    this.edges[indexDataEdge].securityImpacts.push({
                      impact: activitiesValue[indexActivity].integrity,
                      scope: 'Integrity',
                      tecnicalImpacts: []
                    });

                    maximunValue = this.maxImpactValue(maximunValue, activitiesValue[indexActivity].integrity);
                  }
                  if (activitiesValue[indexActivity].efficiency != null) {

                    this.edges[indexDataEdge].securityImpacts.push({
                      impact: activitiesValue[indexActivity].efficiency,
                      scope: 'Efficiency',
                      tecnicalImpacts: []
                    });

                    maximunValue = this.maxImpactValue(maximunValue, activitiesValue[indexActivity].efficiency);


                  }
                  /*console.log("filled edge")
                    console.log(this.edges[indexDataEdge])*/

                  finalBorder.push(this.edges[indexDataEdge].securityImpacts);

                  // to assign color to the edge
                  this.canvas.getLines().data[indexGraphicalEdge].setColor(this.impactColor(maximunValue));


                  // this.assignValueAndColorToAssetByMalfunctions(graphicAssets[i], finalBorder);
                } else {

                  // console.log("the activity doesn't have impact ..... ")

                  this.canvas.getLines().data[indexGraphicalEdge].setColor(this.impactColor(null));
                  let countCol = 0;

                  // to clear old impact information on the right side menu
                  const indexDataEdge = this.edges.findIndex(ed => ed.identifier === graphicAssets[i].links[j]);

                  this.edges[indexDataEdge].securityImpacts = [];

                  for (const col in graphicAssets[i].graphic.getConnections().data) {

                    if (graphicAssets[i].graphic.getConnections().data[col].getColor().hashString === '#129CE4') {

                      countCol++;
                    }
                  }
                  if (graphicAssets[i].graphic.getConnections().data.length === countCol) {
                    // console.log("...but the asset can receive impact by other activities ")
                    this.assignValueAndColorToAssetByMalfunctions(graphicAssets[i], []);
                  }

                }

              }

            }

          }

          this.assignValueAndColorToAssetByMalfunctions(graphicAssets[i], finalBorder);

        }

      } else {
        // console.log("The involved asset doesn't have links")
        this.assignValueAndColorToAssetByMalfunctions(graphicAssets[i], []);
      }
    }
  }

  assignValueAndColorToAssetByMalfunctions(asset, finalBorder) {

    let assetC = null;
    let assetA = null;
    let assetI = null;
    let assetE = null;
    let assetColor = null;

    /*console.log("final border")
      console.log(finalBorder)*/


    for (const j in finalBorder) {
      for (const i in finalBorder[j]) {

        if (finalBorder[j][i].scope === 'Confidentiality') {

          assetC = this.maxImpactValue(assetC, finalBorder[j][i].impact);

          assetColor = this.maxImpactValue(assetColor, assetC);

        }
        if (finalBorder[j][i].scope === 'Availability') {

          assetA = this.maxImpactValue(assetA, finalBorder[j][i].impact);

          assetColor = this.maxImpactValue(assetColor, assetA);

        }
        if (finalBorder[j][i].scope === 'Integrity') {

          assetI = this.maxImpactValue(assetI, finalBorder[j][i].impact);

          assetColor = this.maxImpactValue(assetColor, assetI);

        }
        if (finalBorder[j][i].scope === 'Efficiency') {

          assetE = this.maxImpactValue(assetE, finalBorder[j][i].impact);

          assetColor = this.maxImpactValue(assetColor, assetE);

        }

      }

    }

    /*console.log(assetC);
      console.log(assetA);
      console.log(assetI);
      console.log(assetE);*/

    const assetIndex = this.assets.findIndex(as => as.identifier === asset.graphic.id);


    this.assets[assetIndex].securityImpacts = [];

    if (assetC != null) {

      this.assets[assetIndex].securityImpacts.push({
        impact: assetC,
        scope: 'Confidentiality',
        tecnicalImpacts: []
      });

    }
    if (assetA != null) {

      this.assets[assetIndex].securityImpacts.push({
        impact: assetA,
        scope: 'Availability',
        tecnicalImpacts: []
      });

    }
    if (assetI != null) {

      this.assets[assetIndex].securityImpacts.push({
        impact: assetI,
        scope: 'Integrity',
        tecnicalImpacts: []
      });

    }
    if (assetE != null) {

      this.assets[assetIndex].securityImpacts.push({
        impact: assetE,
        scope: 'Efficiency',
        tecnicalImpacts: []
      });

    }

    /*console.log("Filled Asset")
      console.log(this.assets[assetIndex].securityImpacts)*/

    /*console.log("colore finale")
      console.log(assetColor)*/
    let color = this.impactColor(assetColor);
    /*console.log("colore bordo")
      console.log(color)*/

    if (color === '#129CE4') {
      color = '#000000';

    }

    asset.graphic.setColor(color);

    /* console.log("nodes and edges")
       console.log(this.assets)
       console.log(this.edges)*/

  }

  // given an impact it returns the corresponding color
  impactColor(s: string): string {

    if (s === 'CRITICAL') {

      return '#ff0000';

    } else if (s === 'HIGH') {

      return '#ffa500';
    } else if (s === 'MEDIUM') {

      return '#ffdb00';
    } else if (s === 'LOW') {

      return '#00ff00';

    } else if (s === null) {

      return '#129CE4';

    }

  }

  // it returns the maximun impact
  maxImpactValue(oldValue, newValue): string {

    if (newValue === null) {

      return oldValue;
    }

    if ((newValue === 'CRITICAL') || ((newValue === 'HIGH') && (oldValue != 'CRITICAL')) || ((newValue === 'MEDIUM') && (oldValue != 'CRITICAL') && (oldValue != 'HIGH')) || (oldValue === null)) {

      return newValue;
    } else {

      return oldValue;
    }

  }

  // it calculates all the activity impact kinds
  findTotalImpactKinds(oldValues, newValues): Array<String> {

    for (const i in newValues) {

      if (oldValues.indexOf(newValues[i]) === -1) {

        oldValues.push(newValues[i]);
      }

    }

    return oldValues;

  }

  listActivityMal(activityId): Object {

    const a = [];

    for (const i in this.assets) {

      if (this.assets[i].identifier === activityId) {

        const children = this.assets[i].children;

        for (const j in children) {

          for (const k in this.edges) {

            if (children[j] === this.edges[k].identifier) {


              for (const y in this.assets) {


                if ((this.edges[k].target === this.assets[y].identifier) && (this.assets[y].nodeType === 'Malfunction')) {


                  a.push(this.assets[y].identifier);

                }

              }

            }

          }

        }

        return a;

      }

    }

    return a;

  }

  listAssetMal(assetId): Object {

    let a = [];

    for (const i in this.assets) {

      if (this.assets[i].identifier === assetId) {

        a = this.assets[i].malfunctionsIds;

      }

    }

    return a;

  }

  sameMal(list1, list2): number {

    for (const i in list1) {

      for (const j in list2) {

        if (list1[i] === list2[j]) {

          return 1;

        }

      }

    }

    return 0;
  }

  showRequirements() {

    this.displayRequirements = true;

  }

  cancelRequirements() {

    this.selectedRequirementList = [];
    this.chosenRequirement = [];
    this.selectedRequirement = undefined;
    this.chosenRequirement = undefined;


    this.displayRequirements = false;

  }

  addRequirements() {

    // this.showSelectedRequirements=[];

    for (const j in this.assets) {

      if (this.assets[j].identifier === this.id) {
        this.assets[j].relatedRequirementsIds = [];
        for (const i in this.selectedRequirementList) {

          // this.showSelectedRequirements.push(this.selectedRequirementList[i].label.split(" ----- ")[0]);
          this.assets[j].relatedRequirementsIds.push(this.selectedRequirementList[i].value);
        }
      }
    }
    // to hide empty list
    // this.isClassVisible= true;

    this.selectedRequirement = undefined;
    this.chosenRequirement = undefined;
    this.propertyCheck = null;
    this.displayRequirements = false;
  }

  modifyString(s: string): string {

    const a_split = s.split(' ');
    let res = '';

    for (const i in a_split) {
      res += a_split[i] + ' ';
      if ((parseInt(i) + 1) % 2 === 0) {
        res += '\n';
      }
    }
    return res;

  }

  requirementsMode() {
    // this.requirementsList=[];
    // let a={"filterMap":{"IDENTIFIER":this.idsystemproject}}

    // this.getRequirements(JSON.stringify(a));

    // this.displayRequirements=true;

    const indexArray = this.assets.findIndex(i => i.identifier === this.id);

    this.showSelectedRequirements = [];
    this.selectedRequirementList = [];

    for (const m in this.assets[indexArray].relatedRequirementsIds) {

      for (const n in this.requirementsList) {

        if (this.assets[indexArray].relatedRequirementsIds[m] === this.requirementsList[n].identifier) {

          if (this.requirementsList[n].status != 'Canceled') {

            this.showSelectedRequirements.push(this.requirementsList[n].id);
            this.selectedRequirementList.push(({
              label: this.requirementsList[n].id + ' ----- ' + this.requirementsList[n].category + ' ----- ' + this.requirementsList[n].subCategory,
              value: this.requirementsList[n].identifier
            }));
          } else {

            alert('The requirement ' + this.requirementsList[n].id + ' was canceled!');

          }

        }

      }

    }

    this.createShowRequirements(this.requirementsList);

  }

  // get Requirements By System Project ID
  getRequirements(idsystemproject) {
    this.dataService.loadRequirementsById(idsystemproject).subscribe(response => {
      this.requirementsList = response;
    });
  }

  getAsset() {
    this.blocked = true;
    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject')
      }
    };
    console.log(a);

    this.subscriptions.push(
      this.dataService.loadAsset(JSON.stringify(a)).subscribe((response: ModelObject) => {
        console.log(response);
        this.serverAsset = JSON.parse(response.jsonModel);

        // to load requirements
        const a = {
          'filterMap': {
            'IDENTIFIER': this.idsystemproject,
            'PROJECT': sessionStorage.getItem('idProject')
          }
        };

        this.getRequirements(JSON.stringify(a));

        //Draw2d canvas is created
        this.createGraph();

        //This method is called to align the AssetModel with its graphical representation, since the AssetModel can be modified with 2 different views
        this.alignAssetModelToGraph();

        this.loadJSON();

        this.updateAssetsColors();
        /*//if there was a mismatch between AssetModel and graphical model, SEST cleans and rectifies the JSON. In order to not risk to lose it, it is automatically saved
        //in the repository
        if(this.mismatch){

          this.finalJSON = ({
            'creationTime': this.serverAsset.creationTime,
            'updateTime': this.serverAsset.updateTime,
            'identifier': this.serverAsset.identifier,
            'edges': this.edges,
            'nodes': this.assets,
            'graphJson': this.serverAsset.graphJson,
            'objType': 'AssetModel'
          });

          let completeList = {};

          completeList = {
            'jsonModel': (JSON.stringify(this.finalJSON, null, 2)),
            'objectIdentifier': this.serverAsset.identifier
          };

          //this.sendAsset(JSON.stringify(completeList, null, 2));

          this.mismatch = false;

        }*/
        //console.log(this.serverAsset);
        this.lockService.addLock(response.objectIdentifier, response.lockedBy);
        this.blocked = false;

      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  //In case graphical representations of assets have been added, we need to properly colour them
  private updateAssetsColors() {
    for (const i in this.assets) {
      if (this.assets[i].nodeType === 'Asset') {
        let arrayForColor = [];
        arrayForColor.push({
          'asset': this.assets[i].identifier,
          'malfunctions': this.assets[i].malfunctionsIds,
          'links': this.assets[i].parents
        });

        this.newEditEdgeColor(arrayForColor);
      }

    }
  }

  //Since the AssetModel can be modified also from the TabularView, we need to be sure it is aligned with its graphical representation
  private alignAssetModelToGraph() {
    console.log('alignAssetModelToGraph');

    /*if (this.serverAsset.graphJson.length > (this.serverAsset.nodes.length + this.serverAsset.edges.length)) {
      console.log('there is a positive mismatch')
      this.mismatch = true;*/

    //There are mismatches between AssetModel and graphical representation. The AssetModel always helds the truth
    //At first, we remove any graphical node or edge without a correspondence in the AssetModel
    const reallyNodes = [];

    for (const graph in this.serverAsset.graphJson) {
      if ((this.serverAsset.nodes.findIndex(nod => nod.identifier === this.serverAsset.graphJson[graph].id) != -1)) {
        reallyNodes.push(this.serverAsset.graphJson[graph]);
      }
      if ((this.serverAsset.edges.findIndex(nod => nod.identifier === this.serverAsset.graphJson[graph].id) != -1)) {
        reallyNodes.push(this.serverAsset.graphJson[graph]);
      }
    }
    this.serverAsset.graphJson = reallyNodes;

    /*}*/
    if (this.serverAsset.graphJson.length < (this.serverAsset.nodes.length + this.serverAsset.edges.length)) {
      console.log('there is a negative mismatch');
      //this.mismatch = true;
      //There are mismatches between AssetModel and graphical representation. The AssetModel always helds the truth
      //Second, we add a graphical representation for each node/edge in the AssetModel without a graphical representation

      const assetsToAdd = [];
      const edgesToAdd = [];

      for (const node in this.serverAsset.nodes) {

        if ((this.serverAsset.graphJson.findIndex(nod => nod.id === this.serverAsset.nodes[node].identifier) === -1)) {
          assetsToAdd.push(this.serverAsset.nodes[node]);
        }
      }

      for (const edge in this.serverAsset.edges) {

        if ((this.serverAsset.graphJson.findIndex(nod => nod.id === this.serverAsset.edges[edge].identifier) === -1)) {
          edgesToAdd.push(this.serverAsset.edges[edge]);
        }
      }

      for (const node in assetsToAdd) {
        if (assetsToAdd[node].nodeType === 'Organization') {
          this.forceCreateOrganization(assetsToAdd[node]);
        } else if (assetsToAdd[node].nodeType === 'BusinessProcess') {
          this.forceCreateProcess(assetsToAdd[node]);
        } else if (assetsToAdd[node].nodeType === 'BusinessActivity') {
          this.forceCreateActivity(assetsToAdd[node]);
        } else if (assetsToAdd[node].nodeType === 'Malfunction') {
          this.forceCreateMalfunction(assetsToAdd[node]);
        } else if (assetsToAdd[node].nodeType === 'Asset') {
          this.forceCreateAsset(assetsToAdd[node]);
        }
      }

      //We use a 'trick' to easily convert to the JSON format of the Graph nodes
      let graphElementsToAdd;
      const writer = new draw2d.io.json.Writer();
      writer.marshal(this.canvas, function (json) {
        $('#json').text(JSON.stringify(json, null, 2));
        graphElementsToAdd = json;
      });

      //Figures temporary added to the canvas are removed
      this.canvas.clear();

      for (const graphNode in graphElementsToAdd) {
        this.serverAsset.graphJson.push(graphElementsToAdd[graphNode]);
      }

      for (const edge in edgesToAdd) {
        this.forceCreateEdge(edgesToAdd[edge]);
      }
    }
    /*else{
      this.mismatch = false;
    }*/

  }

  private forceCreateEdge(edge) {
    let edgeGraph = {};
    let source = {};
    let target = {};
    let vertex = [];
    let vertex1 = {};
    let vertex2 = {};

    source = {
      'node': edge.source,
      'port': 'input0'
    };

    target = {
      'node': edge.target,
      'port': 'output0'
    };

    vertex1 = {
      'x': 0,
      'y': 0
    };

    vertex2 = {
      'x': 0,
      'y': 0
    };

    vertex.push(vertex1);
    vertex.push(vertex2);

    edgeGraph = {
      'type': 'draw2d.Connection',
      'id': edge.identifier,
      'alpha': 1,
      'angle': 0,
      'userData': {},
      'cssClass': 'draw2d_Connection',
      'stroke': '2',
      'color': '#787878',
      'outlineStroke': 0,
      'outlineColor': 'none',
      'policy': 'draw2d.policy.line.LineSelectionFeedbackPolicy',
      'vertex': vertex,
      'router': 'draw2d.layout.connection.DirectRouter',
      'radius': 3,
      'source': source,
      'target': target,
      'visible': true
    };

    this.serverAsset.graphJson.push(edgeGraph);

  }

  private forceCreateOrganization(node) {

    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setId(node.identifier); //This is fundamental: a new graph node must have the same unique identifier as the Asset Model node

    endNode.setX(Math.random() * (150 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'ORG'}).attr({
      'color': '#e0e0e0'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
      'color': '#e0e0e0'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#e0e0e0'
    });
    endNode.attr({
      'cssClass': 'organization'
    });

    endNode.resetPorts();
    endNode.createPort('input', new draw2d.layout.locator.BottomLocator()).on('connect', function (emitterPort, connection) {
      window.angularComponentRef.connectionPolicy(connection);
    });

    endNode.on('click', function (emitter, event) {
      window.angularComponentRef.componentFn(event);
    });

    endNode.on('removed', function (emitter, event) {
      window.angularComponentRef.removeComponent(emitter);
    });

    this.canvas.add(endNode);
  }

  private forceCreateProcess(node) {
    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setId(node.identifier); //This is fundamental: a new graph node must have the same unique identifier as the Asset Model node

    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'BP'}).attr({
      'color': '#d3e1ff'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
      'color': '#d3e1ff'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#d3e1ff'
    });

    endNode.attr({
      'cssClass': 'process'
    });

    endNode.resetPorts();

    endNode.createPort('input', new draw2d.layout.locator.BottomLocator()).on('connect', function (emitterPort, connection) {
      window.angularComponentRef.connectionPolicy(connection);
    });
    endNode.createPort('output', new draw2d.layout.locator.TopLocator()).on('disconnect', function (emitterPort, connection) {
      window.angularComponentRef.removeConnection(connection);
    });

    endNode.on('click', function (emitter, event) {
      window.angularComponentRef.componentFn(event);
    });
    endNode.on('removed', function (emitter, event) {
      window.angularComponentRef.removeComponent(emitter);
    });

    this.canvas.add(endNode);
  }

  private forceCreateActivity(node) {
    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setId(node.identifier); //This is fundamental: a new graph node must have the same unique identifier as the Asset Model node
    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'BA'}).attr({
      'color': '#E6F1F5'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
      'color': '#E6F1F5'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#E6F1F5'
    });
    endNode.attr({
      'cssClass': 'activity'
    });

    endNode.resetPorts();

    endNode.createPort('input', new draw2d.layout.locator.BottomLocator()).on('connect', function (emitterPort, connection) {
      window.angularComponentRef.connectionPolicy(connection);
    });

    endNode.createPort('output', new draw2d.layout.locator.TopLocator()).on('disconnect', function (emitterPort, connection) {
      window.angularComponentRef.removeConnection(connection);
    });

    endNode.on('click', function (emitter, event) {
      window.angularComponentRef.componentFn(event);
    });

    endNode.on('removed', function (emitter, event) {
      window.angularComponentRef.removeComponent(emitter);
    });

    this.canvas.add(endNode);
  }


  private findMaxSeriousnessMalfunction(malfunction: any): string {
    if (malfunction.scales.length === 0) {
      return null;
    }

    let isCritical = false;
    let isHigh = false;
    let isMedium = false;
    let isLow = false;

    for (const scale in malfunction.scales) {

      if (malfunction.scales[scale].seriousness === 'CRITICAL') {
        isCritical = true;
      }
      if (malfunction.scales[scale].seriousness === 'HIGH') {
        isHigh = true;
      }
      if (malfunction.scales[scale].seriousness === 'MEDIUM') {
        isMedium = true;
      }
      if (malfunction.scales[scale].seriousness === 'LOW') {
        isLow = true;
      }
    }
    if (isCritical) {
      return 'CRITICAL';
    }
    if (isHigh) {
      return 'HIGH';
    }
    if (isMedium) {
      return 'MEDIUM';
    }
    if (isLow) {
      return 'LOW';
    }
  }

  private forceCreateMalfunction(node) {
    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setId(node.identifier); //This is fundamental: a new graph node must have the same unique identifier as the Asset Model node
    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');

    const seriousness = this.findMaxSeriousnessMalfunction(node);

    if (seriousness === null) {
      endNode.add(new draw2d.shape.basic.Label({text: 'MAL'}).attr({
        'color': '#ffffff'
      }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

      endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
        'color': '#ffffff'
      }), new draw2d.layout.locator.CenterLocator());

      endNode.attr({
        'bgColor': '#ffffff'
      });
    }

    if (seriousness === 'CRITICAL') {
      endNode.add(new draw2d.shape.basic.Label({text: 'MAL'}).attr({
        'color': '#ff0000'
      }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

      endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
        'color': '#ff0000'
      }), new draw2d.layout.locator.CenterLocator());

      endNode.attr({
        'bgColor': '#ff0000'
      });
    }
    if (seriousness === 'HIGH') {
      endNode.add(new draw2d.shape.basic.Label({text: 'MAL'}).attr({
        'color': '#ffa500'
      }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

      endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
        'color': '#ffa500'
      }), new draw2d.layout.locator.CenterLocator());

      endNode.attr({
        'bgColor': '#ffa500'
      });
    }
    if (seriousness === 'MEDIUM') {
      endNode.add(new draw2d.shape.basic.Label({text: 'MAL'}).attr({
        'color': '#ffdb00'
      }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

      endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
        'color': '#ffdb00'
      }), new draw2d.layout.locator.CenterLocator());

      endNode.attr({
        'bgColor': '#ffdb00'
      });
    }
    if (seriousness === 'LOW') {
      endNode.add(new draw2d.shape.basic.Label({text: 'MAL'}).attr({
        'color': '#00ff00'
      }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

      endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
        'color': '#00ff00'
      }), new draw2d.layout.locator.CenterLocator());

      endNode.attr({
        'bgColor': '#00ff00'
      });
    }


    endNode.attr({
      'cssClass': 'malfunction'
    });

    endNode.resetPorts();

    endNode.createPort('output', new draw2d.layout.locator.TopLocator()).on('disconnect', function (emitterPort, connection) {
      window.angularComponentRef.removeConnection(connection);
    });

    endNode.on('click', function (emitter, event) {
      window.angularComponentRef.componentFn(event);
    });

    endNode.on('removed', function (emitter, event) {
      window.angularComponentRef.removeComponent(emitter);
    });

    this.canvas.add(endNode);
  }

  private forceCreateAsset(node) {
    const endNode = new example();
    endNode.attr({
      width: 100,
      height: 60
    });
    endNode.setId(node.identifier); //This is fundamental: a new graph node must have the same unique identifier as the Asset Model node
    endNode.setX(Math.random() * (100 - 20) + 20);
    endNode.setY('70');
    endNode.add(new draw2d.shape.basic.Label({text: 'AS'}).attr({
      'color': '#fdf5e2'
    }), new draw2d.layout.locator.XYAbsPortLocator(1, 1));

    endNode.add(new draw2d.shape.basic.Label({text: node.name}).attr({
      'color': '#fdf5e2'
    }), new draw2d.layout.locator.CenterLocator());

    endNode.attr({
      'bgColor': '#fdf5e2'
    });
    endNode.attr({
      'cssClass': 'asset'
    });

    endNode.resetPorts();

    endNode.createPort('output', new draw2d.layout.locator.TopLocator()).on('disconnect', function (emitterPort, connection) {
      window.angularComponentRef.removeConnection(connection);
    });
    endNode.on('click', function (emitter, event) {
      window.angularComponentRef.componentFn(event);
    });
    endNode.on('removed', function (emitter, event) {
      window.angularComponentRef.removeComponent(emitter);
    });

    this.canvas.add(endNode);
  }


  showSuccess() {
    this.blockedMessage = true;
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'success', summary: 'Save Successful!', detail: 'Asset Model Saved'});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  showFailed(s: string) {
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'error', summary: 'Error!', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  clearMessage() {
    this.msgsAsset = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  // to avoid duplicates name
  checkName(event) {

    if (this.assets.findIndex(i => i.name != null && i.name === event && i.identifier != this.id) != -1) {

      this.duplicateName = true;
    } else {

      this.duplicateName = false;
    }

  }

  //This method saves the asset model on the repository
  sendAsset(s: Object) {

    console.log('Send Asset');
    this.blocked = true;
    this.subscriptions.push(
      this.dataService.updateAsset(s).subscribe(response => {
        this.showSuccess();
      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  // it creates an array with the requirements(without cancelled requirements) to show
  createShowRequirements(array) {

    /*console.log("createShowRequirements")
      console.log(array)*/
    this.showRequirementsList = [];
    this.categoryList = [];
    this.totalSubcategoryList = [];
    this.categoryList.push('All');


    for (const i in array) {

      if (this.selectedRequirementList.findIndex(ind => ind.value === array[i].identifier) != -1) {

        if (array[i].status === 'Canceled') {
          alert('The requirement ' + array[i].id + ' was canceled!');
          this.selectedRequirementList.splice(parseInt(i), 1);
        }

      } else {
        if (array[i].status != 'Canceled') {

          if (this.showRequirementsList.findIndex(req => req.value === array[i].identifier) === -1) {
            this.showRequirementsList.push({
              label: array[i].id + ' ----- ' + array[i].category + ' ----- ' + array[i].subCategory,
              value: array[i].identifier
            });
          }
          this.categoryList.push(array[i].category);
          this.totalSubcategoryList.push({'category': array[i].category, 'subcategory': 'All'});
          this.totalSubcategoryList.push({'category': array[i].category, 'subcategory': array[i].subCategory});
        }

      }

    }

    this.categoryList = this.categoryList.filter(function (elem, index, self) {
      return index == self.indexOf(elem);
    });
    // this.totalSubcategoryList = this.totalSubcategoryList.filter(function(elem, index, self) {
    //
    //   return index == self.indexOf(elem);
    // });

    this.totalSubcategoryList = this.totalSubcategoryList.filter((thing, index, self) => self.findIndex((t) => {
      return t.category === thing.category && t.subcategory === thing.subcategory;
    }) === index);

    this.displayRequirements = true;

    /*console.log("size")
      console.log(this.showRequirementsList.length)*/

  }

  checkedCategory() {

    const b = this.selectedCategory;
    this.showRequirementsList = [];


    if (b === 'All') {
      for (const i in this.requirementsList) {

        if (this.requirementsList[i].status != 'Canceled') {

          this.showRequirementsList.push({
            label: this.requirementsList[i].id + ' ----- ' + this.requirementsList[i].category + ' ----- ' + this.requirementsList[i].subCategory,
            value: this.requirementsList[i].identifier
          });
        }


      }
      for (const j in this.selectedRequirementList) {

        for (const k in this.showRequirementsList) {

          if (this.selectedRequirementList[j].value === this.showRequirementsList[k].value) {


            this.showRequirementsList.splice(parseInt(k), 1);

          }


        }

      }
    } else {
      for (const i in this.requirementsList) {

        if ((this.requirementsList[i].category === b) && (this.requirementsList[i].status != 'Canceled')) {

          this.showRequirementsList.push({
            label: this.requirementsList[i].id + ' ----- ' + this.requirementsList[i].category + ' ----- ' + this.requirementsList[i].subCategory,
            value: this.requirementsList[i].identifier
          });
        }


      }
      for (const j in this.selectedRequirementList) {

        for (const k in this.showRequirementsList) {

          if (this.selectedRequirementList[j].value === this.showRequirementsList[k].value) {


            this.showRequirementsList.splice(parseInt(k), 1);

          }


        }

      }


    }

    this.subcategoryList = this.totalSubcategoryList.filter(function (e) {
      return e.category === b;
    });

    this.subcategoryList = this.subcategoryList.filter(function (elem, index, self) {
      return index == self.indexOf(elem);
    });


  }

  checkedSubCategory() {

    const a = this.selectedCategory;
    const b = this.selectedSubcategory;
    this.showRequirementsList = [];


    if (b != 'All') {
      for (const i in this.requirementsList) {

        if ((this.requirementsList[i].category === a) && (this.requirementsList[i].subCategory === b) && (this.requirementsList[i].status != 'Canceled')) {

          this.showRequirementsList.push({
            label: this.requirementsList[i].id + ' ----- ' + this.requirementsList[i].category + ' ----- ' + this.requirementsList[i].subCategory,
            value: this.requirementsList[i].identifier
          });
        }


      }
      for (const j in this.selectedRequirementList) {

        for (const k in this.showRequirementsList) {

          if (this.selectedRequirementList[j].value === this.showRequirementsList[k].value) {


            this.showRequirementsList.splice(parseInt(k), 1);

          }


        }

      }
    } else {

      for (const i in this.requirementsList) {

        if ((this.requirementsList[i].category === a) && (this.requirementsList[i].status != 'Canceled')) {

          this.showRequirementsList.push({
            label: this.requirementsList[i].id + ' ----- ' + this.requirementsList[i].category + ' ----- ' + this.requirementsList[i].subCategory,
            value: this.requirementsList[i].identifier
          });
        }


      }
      for (const j in this.selectedRequirementList) {

        for (const k in this.showRequirementsList) {

          if (this.selectedRequirementList[j].value === this.showRequirementsList[k].value) {


            this.showRequirementsList.splice(parseInt(k), 1);

          }


        }

      }
    }

  }

  addRequirement() {

    for (const i in this.showRequirementsList) {

      if (this.showRequirementsList[i].value === this.selectedRequirement) {

        this.selectedRequirementList.push({label: this.showRequirementsList[i].label, value: this.showRequirementsList[i].value});

        this.showRequirementsList.splice(parseInt(i), 1);


      }
    }
    this.selectedRequirement = undefined;
    this.chosenRequirement = undefined;
  }

  removeRequirement() {

    for (const i in this.selectedRequirementList) {

      if (this.selectedRequirementList[i].value === this.chosenRequirement) {

        this.showRequirementsList.push({label: this.selectedRequirementList[i].label, value: this.selectedRequirementList[i].value});

        this.selectedRequirementList.splice(parseInt(i), 1);


      }
    }
    this.selectedRequirement = undefined;
    this.chosenRequirement = undefined;

  }

  requirementDescription(requirement, bool) {

    if (bool) {

      this.selectedRequirement = undefined;
    } else {

      this.chosenRequirement = undefined;
    }

    for (const i in this.requirementsList) {

      if (this.requirementsList[i].identifier === requirement) {

        this.reqName = this.requirementsList[i].id;
        this.reqCat = this.requirementsList[i].category;
        this.reqSub = this.requirementsList[i].subCategory;
        this.reqPri = this.requirementsList[i].priority;
        this.reqDes = this.requirementsList[i].description;
        this.reqTyp = this.requirementsList[i].type;


      }

    }
  }


  infoMalfunctionType() {

    this.showMalfunctionTypeInfo = true;
  }

  infoSeriousness() {

    this.showSeriousnessInfo = true;

  }

  infoDataAsset() {

    this.showAssetPrimaryDataTypeInfo = true;

  }

  infoServicesAsset() {

    this.showAssetPrimaryServicesTypeInfo = true;
  }

  infoManagementAsset() {

    this.showAssetPrimaryManagementTypeInfo = true;
  }

  infoSecondaryAsset() {

    this.showAssetSecondaryTypeInfo = true;
  }

  isLockedByCurrentUser() {
    return this.lockService.lockedBy.getValue() === sessionStorage.getItem('loggedUsername');
  }

  ngOnDestroy() {
    if (this.serverAsset) {
      this.lockService.removeLock(this.serverAsset.identifier);
    }
    this.subscriptions.forEach(s => s.unsubscribe());
  }

}
