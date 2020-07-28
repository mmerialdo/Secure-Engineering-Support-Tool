/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="threat-definition.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ImpactValueEnum, ThreatTaxonomy, VulnerabilityTaxonomy} from "../../../taxonomiesManagement.model";
import {ThreatsTaxonomyService} from "../../../threats-taxonomy/threats-taxonomy.service";
import {BehaviorSubject, combineLatest, Observable} from "rxjs";
import {map, tap} from "rxjs/operators";

@Component({
  selector: 'app-threat-definition',
  templateUrl: './threat-definition.component.html',
  providers: [ThreatsTaxonomyService]
})
export class ThreatDefinitionComponent implements OnInit {

  @Input() threatData: ThreatTaxonomy;
  @Input() assetVulDetails: {secondary: {value:string}, vulnerability: string}
  @Output() threatDetails: EventEmitter<any> = new EventEmitter();
  threats$: Observable<ThreatTaxonomy[]>;
  selectedTaxonomy;
  showPopup = false;
  filteredThreats$;

  filterCategory$: BehaviorSubject<{value:string}[]> = new BehaviorSubject([]);
  filterVulnerabilities$: BehaviorSubject<string> = new BehaviorSubject("");

  impacts = Object.keys(ImpactValueEnum).map( key => ImpactValueEnum[key]).map((value) => ({
    label: value,
    value: value
  }));

  constructor(
    private threatsService: ThreatsTaxonomyService
  ) {

  }

  ngOnInit() {

    this.threatsService.fetch$().subscribe();
    this.threats$ = this.threatsService.threats;

    this.filterCategory$.next([this.assetVulDetails.secondary]);
    this.filterVulnerabilities$.next(this.assetVulDetails.vulnerability);

    this.filteredThreats$ = this.createFilterThreatas(
      this.filterCategory$,
      this.filterVulnerabilities$,
      this.threats$
    ) .pipe(
      tap((response) =>{
        if(this.threatData !== undefined) {
          this.selectedTaxonomy = response.filter(threat => threat.catalogueId === this.threatData.catalogueId)[0];
        }
        this.threatDetails.emit(this.selectedTaxonomy);
      }),
      map(response => response)
    );
  }

  showDialog(value) {
    this.showPopup = true;
  }

  onSelect() {
    this.threatDetails.emit(this.selectedTaxonomy)
  }

  onDeselect() {
    this.threatDetails.emit(null);
  }

  onClose(event) {
    this.showPopup = false;
  }

  createFilterThreatas(filterCategory$, filterVulnerability$, threats$) {

    return combineLatest(
      threats$,
      filterCategory$,
      filterVulnerability$,(threats: ThreatTaxonomy[], filterCategory: {value: string}[], filterVulnerability: string) => {
        if (filterCategory.length === 0) return threats;
        return threats.filter(threat => {
            return threat.affectedAssetsCategories.some(category =>
                filterCategory.map(f => f.value).includes(category)) &&
                threat.associatedVulnerabilities.includes(filterVulnerability);
        })
      });
  }


}
