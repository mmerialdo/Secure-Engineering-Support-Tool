/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="taxonomies.service.ts"
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
import {VulnerabilitiesTaxonomyService} from "./vulnerabilities-taxonomy/vulnerabilities-taxonomy.service";
import {DataService} from "../../dataservice";
import {tap} from "rxjs/operators";
import {ThreatsTaxonomyService} from "./threats-taxonomy/threats-taxonomy.service";
import {RiskScenarioTaxonomiesService} from "./risk-scenario-taxonomy/risk-scenario-taxonomies.service";

@Injectable()
export class TaxonomiesService {

  constructor(
    private dataService: DataService,
    private vulnerabilitiesService: VulnerabilitiesTaxonomyService,
    private threatsService: ThreatsTaxonomyService,
    private scenarioService: RiskScenarioTaxonomiesService
  ) {
  }

  importVulnerabilitiesTaxonomiesByFile(file) {
    return this.dataService.importVulnerabilityReference(file).pipe(
      tap(response => {
        this.vulnerabilitiesService.fetch$().subscribe();
      })
    );
  }

  importThreatsTaxonomiesByFile(file) {
    return this.dataService.importThreatReference(file).pipe(
      tap(response => {
        this.threatsService.fetch$().subscribe();
      })
    );
  }

  importScenariosTaxonomiesByFile(file) {
    return this.dataService.importRiskScenarioReference(file).pipe(
      tap(response => {
        this.scenarioService.fetch$().subscribe();
      })
    );
  }
}
