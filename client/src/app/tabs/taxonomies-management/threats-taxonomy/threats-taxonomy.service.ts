/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="threat-taxonomy.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Injectable} from "@angular/core";
import {BehaviorSubject, Observable} from "rxjs";
import {ThreatTaxonomy} from "../taxonomiesManagement.model";
import {DataService} from "../../../dataservice";
import {catchError, map, tap} from "rxjs/operators";

@Injectable()
export class ThreatsTaxonomyService {
  private _threats = new BehaviorSubject<ThreatTaxonomy[]>([]);

  threats$ = this._threats.asObservable();

  constructor(
    private dataService: DataService,
  ) {
  }

  get threats() {
    return this._threats.asObservable();
  }

  fetch$(value?: string): Observable<ThreatTaxonomy[]> {

    const filter = {'filterMap': {'FULL': true}};
    return this.dataService.loadThreatsRepository(JSON.stringify(filter)).pipe(
      map((response: any) => {
        this._threats.next(response.threats);
        return response;
      })
    );
  }

  addThreat(threat: ThreatTaxonomy) {

    return this.dataService.createThreatReference(JSON.stringify(threat)).pipe(
      tap((response: any) => {
        threat.identifier = response.response;
        this._threats.next([...this._threats.getValue(), threat]);
      }),
      catchError((error) => {
        throw error;
      }));
  }

  removeThreats(threats: string[]) {

    return this.dataService.deleteThreatReference(threats).pipe(
      tap((response) => {
        this._threats.next(
          this._threats.getValue().filter(taxonomy => !threats.includes(taxonomy.identifier)
          ));
      }),
      catchError((error) => {
        throw error;
      }));
  }

  editThreat(threat: ThreatTaxonomy) {
    return this.dataService.updateThreatReference(threat).pipe(
      tap( response => {
        const updatedList = this._threats.getValue();
        const index = updatedList.findIndex(x => x.identifier == threat.identifier);
        updatedList[index] = threat;
        this._threats.next(
           updatedList
          );
      }),
      catchError(err => {
        throw err;
      })
    )
  }
}
