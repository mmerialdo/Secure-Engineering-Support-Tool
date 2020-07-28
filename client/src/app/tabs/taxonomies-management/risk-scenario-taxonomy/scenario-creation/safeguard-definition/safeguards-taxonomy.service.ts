/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="safeguard-definition.component.spec.ts"
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
import {DataService} from "../../../../../dataservice";
import {RiskScenario, Safeguard} from "../../../taxonomiesManagement.model";
import {map} from "rxjs/operators";

@Injectable()
export class SafeguardsTaxonomyService {

  private _safeguards = new BehaviorSubject<any[]>([]);

  safeguards$ = this._safeguards.asObservable();

  constructor(
    private dataService: DataService,
  ) {
  }

  get safeguards() {
    return this._safeguards.asObservable();
  }

  fetch$(value?: string): Observable<Safeguard[]> {

    return this.dataService.loadSafeguard(value).pipe(
      map((response: any) => {
        this._safeguards.next(response);
        return response;
      })
    );
  }

  getFormulaFormat() {
    return "Safeguards formula can be a combined of max/min, divided by semicolon." +
      "es. max(09B05;min(07A01;07A02;08F01);min(09A01;09A02))\n"
  }
}
