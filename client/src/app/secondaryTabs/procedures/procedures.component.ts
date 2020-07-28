/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="procedure.component.ts"
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
import {MenuItem, ConfirmationService} from 'primeng/primeng';
import {TabStatusService} from '../../tabStatusService';
import {FormBuilder, Validators} from '@angular/forms';
import {DataService} from '../../dataservice';
import {Message} from 'primeng/api';
import {Subscription} from 'rxjs/internal/Subscription';
import {Permission} from '../../permission.class';
import {PermissionType} from '../../permission-type.class';
import {Subject} from 'rxjs';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-procedures',
  templateUrl: './procedures.component.html',
  styleUrls: ['./procedures.component.scss'],
  providers: [ConfirmationService]
})
export class ProceduresComponent implements OnInit, OnDestroy {


  private project: any;

  public procedureslist = [];
  public proceduresListCols: any;
  public proceduresitems: MenuItem[];

  // visibility create procedures
  public displayCreate = false;
  public procedureForm: any;
  public templateForm: any;
  public reportForm: any;
  public displaycreation = false;
  public displaycreationTemp = false;
  public displaycreationReport = false;
  public displaygenerateReport = false;
  private phase;

  private idAsset;
  private permission: Permission;
  private selectedProcedure;

  public reportTypes = ['COMPLETE', 'LIGHT', 'ISO'];
  public thresholdValues = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];
  public blocked = false;
  public blockedMessage = false;
  msgsProcedures: Message[] = [];

  private subscriptions: Subscription[] = [];

  duplicatedProceduresName = false;
  private componentDestroyed: Subject<Component> = new Subject<Component>();


  constructor(private confirmationService: ConfirmationService, private tabStatusService: TabStatusService, private formBuilder: FormBuilder, private dataService: DataService) {
  }

  ngOnInit() {

    this.getProceduresByProject();

    this.procedureForm = this.formBuilder.group({
      'name': ['', [Validators.required, Validators.maxLength(80)]],
      'phase': ['', Validators.required]

    });
    this.templateForm = this.formBuilder.group({
      'namet': ['', [Validators.required, Validators.maxLength(80)]],
      'description': ['', [Validators.required, Validators.maxLength(800)]]
    });

    this.reportForm = this.formBuilder.group({
      'type': ['COMPLETE', [Validators.required]],
      'threshold': ['LOW']
    });

    this.proceduresitems = [
      {
        label: 'Create Procedure', icon: 'fa fa-fw fa-user-plus', command: (event => {
          this.showCreationProcedure();
        })
      },
      {
        label: 'Load Procedure', icon: 'fa fa-fw fa-pencil-square-o ', disabled: true, command: (event) => {
          this.setStatus(false);
        }
      },
      {
        label: 'Close Procedure', icon: 'fa fa-fw fa-user-times ', disabled: true, command: (event) => {

          this.confirmationService.confirm({
            message: 'Are you sure you want to close this procedure?',
            header: 'Close Confirmation',
            icon: 'fa fa-trash',
            accept: () => {
              this.closeProcedure();
            }
          });
        }
      },

      {
        label: 'Save Template', icon: 'fa fa-fw  fa-user-plus', disabled: true, command: (event => {

          this.showCreationTemplate();
        })
      },

      {
        label: 'Generate Report', icon: 'fa fa-fw fa-user-plus', disabled: true, command: (event => {
          this.showCreationReport();
        })
      },

      {
        label: 'Download Report', icon: 'fa fa-fw fa-user-plus', disabled: true, command: (event => {
          this.showDownloadReport();
        })
      }
    ];
    this.proceduresListCols = [
      {field: 'name', header: 'Procedure Name'},
      {field: 'creationTime', header: 'Creation Time'},
      {field: 'updateTime', header: 'Update Time'},
      {field: 'lastUpdateUser', header: 'Last Update User'},
      {field: 'status', header: 'Methodology'},
      {field: 'creationTime', header: 'Status'}
    ];

    this.procedureForm.get('name').valueChanges.pipe(
      takeUntil(this.componentDestroyed)
    ).subscribe((value) => {
      this.duplicatedProceduresName = this.procedureslist.some(procedure => procedure.name === value);
    });
  }

  checkProceduresStatus(): boolean {

    for (const i in this.procedureslist) {


      if (this.procedureslist[i].status === 'OnGoing') {


        return true;
      }


    }
    return false;


  }

  getProceduresByProject(): void {
    this.blocked = true;
    const a = {'filterMap': {'PROJECT': sessionStorage.getItem('idProject')}};

    this.subscriptions.push(
      this.dataService.loadProcedureByProject(JSON.stringify(a)).subscribe(response => {
        this.blocked = false;
        this.blockedMessage = false;
        this.converterProcedure(response);

        this.displaycreation = false;
        this.procedureForm.reset();


      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  converterProcedure(array) {

    this.procedureslist = [];

    for (const i in array) {

      const a = JSON.parse(JSON.stringify(array[i]));
      const b = JSON.parse(a);
      this.procedureslist.push(b);

    }

    this.permission = new Permission();
    this.permission.fromJSON(JSON.parse(sessionStorage.getItem('authzToken')));

    this.proceduresitems[0].disabled = !this.canCreateProcedure() ||
      this.procedureslist.some(procedure => procedure.status === 'OnGoing');
  }

  setStatus(value) {
    this.tabStatusService.changeStatus(value);
  }

  onRowSelectProcedure(event) {

    this.selectedProcedure = event.data;
    this.phase = this.selectedProcedure.phase;

    if (this.canViewProcedure() && this.selectedProcedure.status === 'OnGoing') {

      sessionStorage.setItem('idProcedure', this.selectedProcedure.identifier);
      this.idAsset = this.selectedProcedure.assetModel.identifier;
      this.proceduresitems[1].disabled = false;
    } else {
      this.proceduresitems[1].disabled = true;
    }

    this.proceduresitems[2].disabled = !this.canCreateProcedure();
    this.proceduresitems[3].disabled = !this.canCreateProcedure();

    // Each User can generate and download the report
    this.proceduresitems[4].disabled = false;
    this.proceduresitems[5].disabled = false;

  }

  private canCreateProcedure() {
    return this.permission.create.findIndex(element => element === PermissionType.AssessmentProcedure) >= 0;
  }

  private canViewProcedure() {
    return this.permission.view.findIndex(element => element === PermissionType.AssessmentProcedure) >= 0;
  }

  onRowUnselectProcedure(event) {

    this.proceduresitems[1].disabled = true;
    this.proceduresitems[2].disabled = true;
    this.proceduresitems[3].disabled = true;
    this.proceduresitems[4].disabled = true;
    this.proceduresitems[5].disabled = true;

  }

  closeProcedure() {

    this.blocked = true;
    const procedure = {

      'identifier': this.selectedProcedure.identifier,
      'name': this.selectedProcedure.name,

      'phase': this.selectedProcedure.phase,

      'status': 'Closed'

    };

    this.subscriptions.push(
      this.dataService.updateProcedure(JSON.stringify(procedure)).subscribe(response => {

        this.blocked = false;

        this.getProceduresByProject();

        this.proceduresitems[1].disabled = true;
        this.proceduresitems[2].disabled = true;
        this.proceduresitems[3].disabled = true;
        this.proceduresitems[4].disabled = true;
        this.proceduresitems[5].disabled = true;

        this.setStatus(true);

      }, err => {
        this.blocked = false;
        throw err;
      }));

  }

  showCreationProcedure() {
    this.displaycreation = true;
  }

  showCreationTemplate() {
    this.displaycreationTemp = true;
  }

  showCreationReport() {
    this.displaycreationReport = true;
  }

  showDownloadReport() {
    this.displaygenerateReport = true;
  }

  cancelProcedure() {

    this.displaycreation = false;
    this.procedureForm.reset();

  }

  createProcedure() {

    this.blocked = true;
    const procedure = {
      'procedures': [{

        'name': this.procedureForm.value.name,

        'phase': this.procedureForm.value.phase,
        'status': 'OnGoing',
        'assetModel': {'identifier': '1'},
        'threatModel': {'identifier': '1'},
        'vulnerabilityModel': {'identifier': '1'},
        'safeguardModel': {'identifier': '1'},
        'riskModel': {'identifier': '1'},
        'riskTreatmentModel': {'identifier': '1'}

      }],

      'identifier': sessionStorage.getItem('idProject')
    };


    this.dataService.insertProcedure(JSON.stringify(procedure)).subscribe(response => {
      this.blocked = false;
      this.getProceduresByProject();

    }, err => {
      this.blocked = false;
      throw err;
    });
  }

  cancelTemplate() {

    this.displaycreationTemp = false;
    this.templateForm.reset();

  }

  createTemplate() {

    this.blocked = true;
    const template = {

      'identifier': sessionStorage.getItem('idProfileProj'),
      'templates': [{
        'name': this.templateForm.value.namet,
        'description': this.templateForm.value.description,
        'phase': this.phase,
        'riskMethodology': sessionStorage.getItem('methodologyProj'),
        'assetModel': {'identifier': '0'},
        'threatModel': {'identifier': '0'},
        'vulnerabilityModel': {'identifier': '0'},
        'safeguardModel': {'identifier': '0'},
        'riskTreatmentModel': {'identifier': '0'},
        'riskModel': {'identifier': '0'},
        'identifier': sessionStorage.getItem('idProcedure')
      }]


    };

    this.subscriptions.push(
      this.dataService.insertTemplate(JSON.stringify(template)).subscribe(response => {

        this.displaycreationTemp = false;
        this.templateForm.reset();

        this.blocked = true;
        this.blockedMessage = true;
        this.showSuccess('Risk Assessment Template created!', 'Risk Assessment Template available');

      }, err => {
        this.displaycreationTemp = false;
        this.templateForm.reset();

        this.blocked = false;
        throw err;
      }));
  }

  cancelReport() {

    this.displaycreationReport = false;
    this.displaygenerateReport = false;
    this.reportForm.reset();
  }

  createReport() {
    this.blocked = true;

    let impact = this.reportForm.get('threshold').value;
    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject'),
        'IMPACT': impact ? impact : 'LOW'
      }
    };

    this.subscriptions.push(
      this.dataService.generateReport(JSON.stringify(a), this.reportForm.get('type').value).subscribe(response => {

        this.blocked = true;
        this.blockedMessage = true;
        // console.log(response);
        this.showSuccess('Report generated!', 'Report available to download');
      }, err => {
        this.blocked = false;
        this.blockedMessage = false;
        throw err;
      }));
  }

  downloadReport() {

    this.blocked = true;
    // console.log("download");
    const a = {
      'filterMap': {
        'PROCEDURE': sessionStorage.getItem('idProcedure'),
        'PROJECT': sessionStorage.getItem('idProject'),
        'REPORT_TYPE': this.reportForm.get('type').value
      }
    };

    this.subscriptions.push(
      this.dataService.downloadReportXHR(JSON.stringify(a)).subscribe(data => {

          this.blocked = false;
        },
        error => {
          this.blocked = true;
          this.blockedMessage = true;
          this.showFailed('Unable to download.', 'Please generate report!');
        },
        () => console.info('OK')
      ));
  }

  showFailed(summaryMsg: string, detailMsg: string) {
    this.msgsProcedures = [];
    this.msgsProcedures.push({severity: 'error', summary: summaryMsg, detail: detailMsg});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  showSuccess(summaryMsg: string, detailMsg: string) {
    this.msgsProcedures = [];
    this.msgsProcedures.push({severity: 'success', summary: summaryMsg, detail: detailMsg});

    setTimeout(() => {
      this.clearMessage();
    }, 4000);
  }

  clearMessage() {
    this.msgsProcedures = [];
    this.blocked = false;
    this.blockedMessage = false;
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
