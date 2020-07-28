/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="risk-scenario-taxonomies.service.ts"
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
import {RiskScenario, ThreatTaxonomy} from "../taxonomiesManagement.model";
import {DataService} from "../../../dataservice";
import {catchError, map, tap} from "rxjs/operators";

@Injectable()
export class RiskScenarioTaxonomiesService {

  private _scenarios = new BehaviorSubject<any[]>([]);

  scenarios$ = this._scenarios.asObservable();

  constructor(
    private dataService: DataService,
  ) {
  }

  get scenarios() {
    return this._scenarios.asObservable();
  }

  fetch$(value?: string): Observable<RiskScenario[]> {

    return this.dataService.loadRiskScenarioReference(value).pipe(
      map((response: any) => {
        this._scenarios.next(JSON.parse(response));
        return response;
      })
    );
  }

  addRiskScenario(riskScenario: RiskScenario) {

    return this.dataService.createRiskScenarioReference(JSON.stringify(riskScenario)).pipe(
      tap((response) => {
        this._scenarios.next([...this._scenarios.getValue(), { ...riskScenario, identifier: response.toString()}]);
      }),
      catchError((error) => {
        throw error;
      }));
  }

  removeRiskScenarios(riskScenarios: string[]) {

    return this.dataService.deleteRiskScenarioReference(riskScenarios).pipe(
      tap((response) => {
        this._scenarios.next(
          this._scenarios.getValue().filter(taxonomy => !riskScenarios.includes(taxonomy.identifier)
          ));
      }),
      catchError((error) => {
        throw error;
      }));
  }

  editRiskScenario(riskScenario: RiskScenario) {

    return this.dataService.updateRiskScenarioReference(riskScenario).pipe(
      tap( response => {
        const updatedList = this._scenarios.getValue();
        const index = updatedList.findIndex(x => x.identifier == riskScenario.identifier);
        updatedList[index] = riskScenario;
        this._scenarios.next(
          updatedList
        );
      }),
      catchError(err => {
        throw err;
      })
    )
  }
}
