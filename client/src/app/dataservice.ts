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
import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
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
  private protocol = 'https';
  private port = '9090';
  private portCxf = '8443';

  constructor(private http: HttpClient,
              private configService: ConfigService,
              private loginTrackerService: LoginTrackerService,
              private dataServiceGeneric: DataAccessService) {

    this.ipServer = this.configService.getConfiguration().ipServer;
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

  /**
   * Gets the option headers.
   * @returns {{headers: HttpHeaders}} The object with all request options.
   */
  private getResponseTextRequestOptions() {
    const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
    return {headers, responseType: 'text' as 'json'};
  }

  private getMultipartRequestOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'multipart/form-data'
      })
    };
  }

  setSystemProject(s: string) {

    this.sysProjId = s;
  }

  refreshPermissionList<T>(): Observable<Object> {
    let url = this.protocol + '://' + this.ipServer + ':' + this.port + '/permission/list';
    let params = new HttpParams()
      .set('SHIRO_SECURITY_TOKEN', sessionStorage.getItem('authnToken'));
    if (sessionStorage.getItem('idProject')) {
      params = params.set('projectIdentifier', sessionStorage.getItem('idProject'));
    }

    return this.http.get(url, {params}).pipe(map(response => {
      const tokenLogged: any = response;

      if (tokenLogged.userId === sessionStorage.getItem('loggedUserId')) {
        const tokenPermission = new Permission(tokenLogged.userProfile, tokenLogged.read, tokenLogged.update, tokenLogged.create, tokenLogged.view);
        this.setAuthzToken(JSON.stringify(tokenPermission));
        return tokenPermission;
      }
    }));
  }

  getUsers<T>(): Observable<T> {
    console.log('getUsers');
    return this.http.get<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/user/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'));
  }

  insertUser<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/user/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  updateUser<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/user/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  changeUserPassword<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/user/editPassword?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  deleteUser<T>(s: string): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/user/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  deleteProject<T>(s: string): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/project/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }


  getProjects(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/project/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'));
  }

  loadProject(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/project/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }


  insertProject(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/project/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  updateProject<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/project/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  deleteProfile<T>(s: string): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/profile/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  getProfiles(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/profile/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'));
  }

  insertProfile(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/profile/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  loadProfile(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/profile/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);

  }

  updateProfile<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/profile/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  getProcedures(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/procedure/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'));
  }

  loadProcedureByProject(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/procedure/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }


  insertProcedure<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/procedure/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s,
      this.getResponseTextRequestOptions());
  }

  updateProcedure<T>(s: Object): Observable<T> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post<T>(this.protocol + '://' + this.ipServer + ':' + this.port + '/procedure/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  getTemplate(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.get(this.protocol + '://' + this.ipServer + ':' + this.port + '/template/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'));
  }

  loadTemplateByProfile(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/template/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);

  }

  insertTemplate(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/template/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s,
      this.getResponseTextRequestOptions());
  }

  loadRequirementsById(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/sysrequirement/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);

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
      const options = this.getMultipartRequestOptions();

      return this.http.post('https://' + this.ipServer + ':' + this.portCxf + '/cxf/sysrequirement/upload?SHIRO_SECURITY_TOKEN=' +
        sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), formData, options);
    }
  }

  listUploadedFilename(s: any): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/sysrequirement/filename/list?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  loadAudit(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/audit/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);

  }

  loadSafeguard(s?: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/questionnaireSafeguard/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  loadQuestionnaireForTree(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/questionnairejson/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  editAudit(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/audit/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  loadAsset(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/assetModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  updateAsset(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/assetModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  loadPrimaryAssetCategory(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/assetModel/loadPrimaryAssetCategory?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  loadSecondaryAssetCategory(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/assetModel/loadSecondaryAssetCategory?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  login(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/login?SHIRO_SECURITY_TOKEN=' + s, s);
  }

  logout(): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/logout?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), '');
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

    sessionStorage.setItem('projectIdentifier', s);
  }

  loadVulnerabilityRepository(s?: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/vulnerabilityRepository/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  loadThreatsRepository(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/threatRepository/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  loadVulnerabilityModel(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/vulnerabilityModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);

  }

  updateVulnerability(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/vulnerabilityModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  loadRiskModel(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);

  }

  updateRiskModel(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'),
      s, this.getResponseTextRequestOptions());
  }

  updateRiskScenario(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskScenario/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'),
      s, this.getResponseTextRequestOptions());
  }

  loadThreatModel(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/threatModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);

  }

  updateThreatModel(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/threatModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  loadTreatementModel(s: Object): any {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskTreatmentModel/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  loadTreatementModelDetails(s: Object): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskTreatmentModel/loadDetail?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s);
  }

  updateTreatment(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskTreatmentModel/edit?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'),
      s, this.getResponseTextRequestOptions());
  }

  calculateTreatment(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskTreatmentModel/calculate?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);
  }

  updateTreatmentDetail(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskTreatmentModel/editDetail?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'),
      s, this.getResponseTextRequestOptions());
  }

  calculateTreatmentDetail(s: Object): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskTreatmentModel/calculateDetail?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken') + '&PROJECT=' + sessionStorage.getItem('idProject'), s);

  }

  updateThreatRepository(): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/threatRepository/update?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), 'MEHARI', this.getResponseTextRequestOptions());
  }

  updateVulnerabilityRepository() {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/vulnerabilityRepository/update?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), 'MEHARI', this.getResponseTextRequestOptions());
  }

  updateScenarioRepository(): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/scenarioRepository/update?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), 'MEHARI', this.getResponseTextRequestOptions());
  }

  updateGasfRepository(): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/secrequirement/import?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), '', this.getResponseTextRequestOptions());
  }

  updateAuditRepository(): Observable<any> {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/questionnairejson/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), '', this.getResponseTextRequestOptions());
  }

  generateReport(s: Object, type: string): Observable<Object> {

    this.loginTrackerService.restartSessionTimer();
    let editPath = 'edit';
    switch(type) {
      case 'LIGHT': {
        editPath = 'editLight';
        break;
      }
      case 'ISO': {
        editPath = 'editISO';
        break;
      }
    }
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/report/' + editPath +
      '?SHIRO_SECURITY_TOKEN=' + sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  updateVulnerabilityReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/vulnerabilityReference/update?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  createVulnerabilityReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/vulnerabilityReference/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  deleteVulnerabilityReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/vulnerabilityReference/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  updateThreatReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/threatReference/update?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  createThreatReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/threatReference/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  deleteThreatReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/threatReference/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  updateRiskScenarioReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskScenarioReference/update?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  createRiskScenarioReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskScenarioReference/create?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  deleteRiskScenarioReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskScenarioReference/delete?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  loadRiskScenarioReference(s: Object) {

    this.loginTrackerService.restartSessionTimer();
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/riskScenarioReference/load?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  clientError(s: Object): Observable<Object> {

    return this.dataServiceGeneric.postGeneric(s, this.protocol + '://' + (this.ipServer ? this.ipServer : 'localhost')
      + ':' + this.port + '/client_error?SHIRO_SECURITY_TOKEN=');
  }

  downloadReportXHR(s: any): Observable<Object[]> {
    this.loginTrackerService.restartSessionTimer();
    return Observable.create(observer => {

      const xhr = new XMLHttpRequest();

      xhr.open('POST', 'https://' + this.ipServer + ':' + this.portCxf + '/cxf/report/load?SHIRO_SECURITY_TOKEN=' +
        sessionStorage.getItem('authnToken'), true);
      xhr.setRequestHeader('Content-type', 'application/json');
      xhr.responseType = 'blob';

      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {

            const contentType = 'application/vnd.openxmlformats-officedocument.wordprocessingml.document';
            const blob = new Blob([xhr.response], {type: contentType});
            observer.next(blob);
            FileSaver.saveAs(blob, 'Risk Assessment Report.docx');
            observer.complete();
          } else {
            observer.error(xhr.response);
          }
        }
      };
      xhr.send(s);
    });
  }

  exportVulnerabilityReference(): Observable<Object[]> {
    this.loginTrackerService.restartSessionTimer();
    return this.exportFile('https://' + this.ipServer + ':' + this.portCxf + '/cxf/vulnerability/export', 'application/json');
  }

  exportThreatReference(): Observable<Object[]> {
    this.loginTrackerService.restartSessionTimer();
    return this.exportFile('https://' + this.ipServer + ':' + this.portCxf + '/cxf/threat/export', 'application/json');
  }

  exportRiskScenarioReference(): Observable<Object[]> {
    this.loginTrackerService.restartSessionTimer();
    return this.exportFile('https://' + this.ipServer + ':' + this.portCxf + '/cxf/riskScenario/export', 'application/json');
  }

  importVulnerabilityReference(s: any): any {
    this.loginTrackerService.restartSessionTimer();
    return this.importFile(s, 'https://' + this.ipServer + ':' + this.portCxf + '/cxf/vulnerability/import');
  }

  importThreatReference(s: any): any {
    this.loginTrackerService.restartSessionTimer();
    return this.importFile(s, 'https://' + this.ipServer + ':' + this.portCxf + '/cxf/threat/import');
  }

  importRiskScenarioReference(s: any): any {
    this.loginTrackerService.restartSessionTimer();
    return this.importFile(s, 'https://' + this.ipServer + ':' + this.portCxf + '/cxf/riskScenario/import');
  }

  exportFile(url: string, contentType: string): Observable<Object[]> {
    this.loginTrackerService.restartSessionTimer();
    return Observable.create(observer => {

      const xhr = new XMLHttpRequest();
      xhr.open('POST', url + '?SHIRO_SECURITY_TOKEN=' +
        sessionStorage.getItem('authnToken'), true);
      xhr.setRequestHeader('Content-type', contentType);
      xhr.responseType = 'blob';

      xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
          if (xhr.status === 200) {

            const contentType = 'application/json';
            const blob = new Blob([xhr.response], {type: contentType});
            observer.next(blob);
            FileSaver.saveAs(blob, 'Export.json');
            observer.complete();
          } else {
            observer.error(xhr.response);
          }
        }
      };
      xhr.send();
    });
  }

  importFile(s: any, url: string): any {

    this.loginTrackerService.restartSessionTimer();
    const fileList: FileList = s;

    if (fileList.length > 0) {
      const file: File = fileList[0];
      const formData: FormData = new FormData();
      formData.append('file', file);
      formData.append('filename', file.name);
      const options = this.getMultipartRequestOptions();

      return this.http.post(url + '?SHIRO_SECURITY_TOKEN=' + sessionStorage.getItem('authnToken'), formData, options);
    }
    return null;
  }

  lock(s: any): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/lock?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  unlock(s: any): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/unlock?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  getlock(s: any): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/getlock?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), s, this.getResponseTextRequestOptions());
  }

  getConfiguration(): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/configuration', null);
  }

  ping(): any {
    return this.http.post(this.protocol + '://' + this.ipServer + ':' + this.port + '/ping?SHIRO_SECURITY_TOKEN=' +
      sessionStorage.getItem('authnToken'), null);
  }
}



