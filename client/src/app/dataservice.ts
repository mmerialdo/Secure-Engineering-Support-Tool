/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="dataservice.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders, HttpParams, HttpResponse} from '@angular/common/http';
import {Observable, Subject} from 'rxjs';
import * as FileSaver from 'file-saver';

import {Users} from './users';

import {ConfigService} from './configservice';
import {DataAccessService} from './data-access.service';
import {Permission} from './notificationservice';
import {map, take} from 'rxjs/operators';
import {Safeguard} from './tabs/taxonomies-management/taxonomiesManagement.model';
import {LoginTrackerService} from './shared/service/login-tracker-service';
import {Subscription} from 'rxjs/internal/Subscription';
import {ModelObject} from './model-object';

@Injectable()
export class DataService {

  sysProjId;

  private update = new Subject<boolean>();
  // permissionsUpdate$ = this.update.asObservable();
  private ipServer = 'localhost';
  private protocol = 'http';
  private port = '8081';

  private headers = new HttpHeaders();

  constructor(private http: HttpClient,
              private configService: ConfigService,
              private loginTrackerService: LoginTrackerService,
              private dataServiceGeneric: DataAccessService) {

    this.ipServer = this.configService.getConfiguration().ipServer;
    this.port = this.configService.getConfiguration().portServer;
    this.protocol = this.configService.getConfiguration().protocol;
  }

  /**
   * Gets the option headers.
   * @returns {{headers: HttpHeaders}} The object with all request options.
   */
  private getRequestOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      })
    };
  }

  private getResponseTextAsJsonRequestOptions() {
    const headers = new HttpHeaders();
    return {headers, responseType: 'text' as 'json'};
  }

  private getResponseTextRequestOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }), responseType: 'text' as any
    };
  }

  private getMultipartRequestOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'multipart/form-data'
      })
    };
  }

  private getDownloadRequestOptions(): {
    headers?: HttpHeaders | {
      [header: string]: string | string[];
    };
    observe: 'response';
    params?: HttpParams | {
      [param: string]: string | string[];
    };
    reportProgress?: boolean;
    responseType: 'blob';
    withCredentials?: boolean;
  } {
    return {headers: this.headers, responseType: 'blob', observe: 'response'};
  }

  private getDocumentRequestOptions(): {
    headers?: HttpHeaders | {
      [header: string]: string | string[];
    },
    observe: 'events',
    params?: HttpParams | {
      [param: string]: string | string[];
    },
    reportProgress?: boolean,
    responseType: 'text',
    withCredentials?: boolean
  } {
    return {
      headers: this.headers,
      observe: 'events',
      reportProgress: true,
      responseType: 'text'
    };
  }

  setSystemProject(s: string) {

    this.sysProjId = s;
  }

  refreshPermissionList<T>(): Observable<Object> {

    return this.http.get<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/permission/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken')).pipe(map(response => {
      const tokenLogged: any = response;

      if (tokenLogged.userId === sessionStorage.getItem('loggedUserId')) {
        const tokenPermission = new Permission(tokenLogged.userProfile, tokenLogged.read, tokenLogged.update, tokenLogged.create, tokenLogged.view);
        this.setAuthzToken(JSON.stringify(tokenPermission));
        return tokenPermission;
      }
    }));
  }

  getUsers<T>() {
    return this.http.get<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/user/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), this.getRequestOptions());
  }

  insertUser<T>(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/user/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateUser<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/user/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  changeUserPassword<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/user/editPassword?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  deleteUser<T>(s: string): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/user/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  deleteProject<T>(s: string): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/project/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }


  getProjects(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/project/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), this.getRequestOptions());
  }

  loadProject(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/project/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }


  insertProject(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/project/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateProject<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/project/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  deleteProfile<T>(s: string): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/profile/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  getProfiles(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/profile/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), this.getRequestOptions());
  }

  insertProfile(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/profile/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadProfile(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/profile/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());

  }

  updateProfile<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/profile/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  getProcedures(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/procedure/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), this.getRequestOptions());
  }

  loadProcedureByProject(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/procedure/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'),
      s, this.getRequestOptions());
  }


  insertProcedure<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/procedure/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateProcedure<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/procedure/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  getTemplate(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/template/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), this.getRequestOptions());
  }

  loadTemplateByProfile(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/template/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());

  }

  insertTemplate(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/template/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadRequirementsById(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/sysrequirement/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());

  }

  uploadRequirement(s: any): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    const fileList: FileList = s;

    if (fileList.length > 0) {
      const file: File = fileList[0];
      const formData: FormData = new FormData();
      formData.append('file', file);
      formData.append('sysprojectIdentifier', this.sysProjId);
      formData.append('filename', file.name);

      return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/sysrequirement/upload?SHIRO_SECURITY_TOKEN=' +
        sessionStorage.getItem('authnToken'), formData, this.getDocumentRequestOptions());
    }
  }

  listUploadedFilename(s: any): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/sysrequirement/filename/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadAudit(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/audit/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());

  }

  loadSafeguard(s?: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/audit/questionnaireSafeguard/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadQuestionnaireForTree(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/audit/questionnairejson/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  editAudit(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/audit/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadAsset(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/assetModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateAsset(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/assetModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadPrimaryAssetCategory(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/assetModel/loadPrimaryAssetCategory?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadSecondaryAssetCategory(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/assetModel/loadSecondaryAssetCategory?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  login(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/login?SHIRO_SECURITY_TOKEN=' + s, s, this.getRequestOptions());
  }

  logout(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/logout?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), '', this.getRequestOptions());
  }

  loadVulnerabilityRepository(s?: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/vulnerability/vulnerabilityRepository/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadThreatsRepository(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/threat/threatRepository/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadVulnerabilityModel(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/vulnerability/vulnerabilityModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());

  }

  updateVulnerability(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/vulnerability/vulnerabilityModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadRiskModel(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/scenario/riskModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());

  }

  updateRiskModel(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/scenario/riskModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'),
      s, this.getResponseTextAsJsonRequestOptions());
  }

  updateRiskScenario(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/scenario/riskScenario/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'),
      s, this.getRequestOptions());
  }

  loadThreatModel(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/threat/threatModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());

  }

  updateThreatModel(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/threat/threatModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadTreatementModel(s: Object): any {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/riskTreatmentModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadTreatementModelDetails(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/riskTreatmentModel/loadDetail?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateTreatment(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/riskTreatmentModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'),
      s, this.getRequestOptions());
  }

  calculateTreatment(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/riskTreatmentModel/calculate?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateTreatmentDetail(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/riskTreatmentModel/editDetail?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'),
      s, this.getRequestOptions());
  }

  calculateTreatmentDetail(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/riskTreatmentModel/calculateDetail?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s, this.getRequestOptions());

  }

  updateGasfRepository(): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/secrequirement/import?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), '', this.getRequestOptions());
  }

  generateReport(s: Object, type: string): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    let editPath = 'edit';
    switch (type) {
      case 'LIGHT': {
        editPath = 'editLight';
        break;
      }
      case 'ISO': {
        editPath = 'editISO';
        break;
      }
    }
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/report/' + editPath +
      '?SHIRO_SECURITY_TOKEN=' + sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateVulnerabilityReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/vulnerability/vulnerabilityReference/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  createVulnerabilityReference(s: Object): any {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/vulnerability/vulnerabilityReference/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  deleteVulnerabilityReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/vulnerability/vulnerabilityReference/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateThreatReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/threat/threatReference/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  createThreatReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/threat/threatReference/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  deleteThreatReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/threat/threatReference/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  updateRiskScenarioReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/scenario/riskScenarioReference/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  createRiskScenarioReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/scenario/riskScenarioReference/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  deleteRiskScenarioReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/scenario/riskScenarioReference/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  loadRiskScenarioReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/scenario/riskScenarioReference/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getRequestOptions());
  }

  clientError(s: Object): Observable<Object> {

    return this.dataServiceGeneric.postGeneric(s, this.protocol + '://' + (this.ipServer ? this.ipServer : 'localhost')
      + ':' + this.port + '/api/client_error?SHIRO_SECURITY_TOKEN=');
  }

  downloadReportXHR(s: any): Observable<HttpResponse<Blob>> {
    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/report/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getDownloadRequestOptions());
  }

  exportVulnerabilityReference(): Observable<HttpResponse<Blob>> {
    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/vulnerability/export?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), this.getDownloadRequestOptions());
  }

  exportThreatReference(): Observable<HttpResponse<Blob>> {
    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/threat/export?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), this.getDownloadRequestOptions());
  }

  exportRiskScenarioReference(): Observable<HttpResponse<Blob>> {
    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/riskScenario/export?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), this.getDownloadRequestOptions());
  }

  importVulnerabilityReference(s: any): any {
    this.loginTrackerService.restartSessionTimer();
    return this.importFile(s, this.protocol + '://' + this.ipServer + ':' + this.port + '/api/vulnerability/import');
  }

  importThreatReference(s: any): any {
    this.loginTrackerService.restartSessionTimer();
    return this.importFile(s, this.protocol + '://' + this.ipServer + ':' + this.port + '/api/threat/import');
  }

  importRiskScenarioReference(s: any): any {
    this.loginTrackerService.restartSessionTimer();
    return this.importFile(s, this.protocol + '://' + this.ipServer + ':' + this.port + '/api/riskScenario/import');
  }

  public importFile(files: any, url: string) {
    this.loginTrackerService.restartSessionTimer();

    const fileList: FileList = files;
    if (fileList.length > 0) {
      const file: File = fileList[0];
      const formData = new FormData();
      formData.append('file', file);
      formData.append('filename', file.name);

      return this.http.post(url + '?SHIRO_SECURITY_TOKEN=' + sessionStorage.getItem('authnToken'), formData, this.getDocumentRequestOptions());
    }
  }

  lock(s: any): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/lock?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextAsJsonRequestOptions());
  }

  unlock(s: any): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/unlock?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextAsJsonRequestOptions());
  }

  getlock(s: any): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/getlock?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextAsJsonRequestOptions());
  }

  getConfiguration(): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/configuration', null);
  }

  ping(): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/api/ping?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), null);
  }


  setAuthnToken(s: string) {

    sessionStorage.setItem('authnToken', s);
  }

  setAuthzToken(s: string) {

    sessionStorage.setItem('authzToken', s);
  }

  setLoggedUserId(identifier: string, username: string) {

    sessionStorage.setItem('loggedUserId', identifier);
    sessionStorage.setItem('loggedUsername', username);
  }

  setProjectId(s: string) {

    sessionStorage.setItem('idProject', s);
    if (s) {
      const tokenDecrypted = atob(sessionStorage.getItem('authnToken'));
      const token: any = JSON.parse(tokenDecrypted);
      token.project = s;
      sessionStorage.setItem('authnToken', btoa(JSON.stringify(token)));
    }
  }
}



