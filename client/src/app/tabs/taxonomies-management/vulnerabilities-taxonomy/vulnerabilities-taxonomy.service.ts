/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="vulnerability-taxonomy.service.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {ThreatTaxonomy, VulnerabilityTaxonomy} from '../taxonomiesManagement.model';
import {DataService} from '../../../dataservice'

@Injectable()
export class VulnerabilitiesTaxonomyService {

  private _vulnerabilities = new BehaviorSubject<VulnerabilityTaxonomy[]>([]);

  vulnerabilities$ = this._vulnerabilities.asObservable();

  constructor(
    private dataService: DataService
  ) {
  }

  get vulnerabilities() {
    return this._vulnerabilities.asObservable();
  }

  getVulnerabilies(): VulnerabilityTaxonomy[] {
    return this._vulnerabilities.getValue();
  }

  fetch$(value?: string): Observable<VulnerabilityTaxonomy[]> {

    const filter = {'filterMap': {'FULL': true}};
    return this.dataService.loadVulnerabilityRepository(JSON.stringify(filter)).pipe(
      map((response: any) => {
        this._vulnerabilities.next(response.vulnerabilities);
        return response;
      })
    );
  }

  addVulnerability(vulnerability: VulnerabilityTaxonomy) {

    return this.dataService.createVulnerabilityReference(JSON.stringify(vulnerability)).pipe(
      tap((response) => {
        vulnerability.identifier = response.toString();
        this._vulnerabilities.next([...this._vulnerabilities.getValue(), vulnerability]);
      }),
      catchError((error) => {
        throw error;
      }));
  }

  removeVulnerability(vulnerabilities: any[]) {

    return this.dataService.deleteVulnerabilityReference(vulnerabilities).pipe(
      tap((response) => {
        this._vulnerabilities.next(
          this._vulnerabilities.getValue().filter(taxonomy => !vulnerabilities.includes(taxonomy.identifier)
          ));
      }),
      catchError((error) => {
        throw error;
      }));
  }

  editVulnerability(vulnerability: VulnerabilityTaxonomy) {
    return this.dataService.updateVulnerabilityReference(vulnerability).pipe(
      tap(response => {
        const updatedList = this._vulnerabilities.getValue();
        const index = updatedList.findIndex(x => x.identifier == vulnerability.identifier);
        updatedList[index] = vulnerability;
        this._vulnerabilities.next(
          updatedList
        );
      }),
      catchError(err => {
        throw err;
      })
    );
  }

  private getResponseTextRequestOptions() {
    const headers = new HttpHeaders().set('Content-Type', 'text/plain; charset=utf-8');
    return {headers, responseType: 'text' as 'json'};
  }
}
