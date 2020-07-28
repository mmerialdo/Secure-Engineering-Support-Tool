/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="safeguard-definition.component.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {Safeguard} from '../../../taxonomiesManagement.model';
import {AbstractControl, FormBuilder, FormGroup, ValidatorFn} from '@angular/forms';
import {Observable, Subject} from 'rxjs';
import {SafeguardsTaxonomyService} from './safeguards-taxonomy.service';
import {takeUntil} from 'rxjs/operators';

@Component({
  selector: 'app-safeguard-definition',
  templateUrl: './safeguard-definition.component.html',
  styleUrls: ['./safeguard-definition.component.css'],
  providers: [SafeguardsTaxonomyService]
})
export class SafeguardDefinitionComponent implements OnInit, OnDestroy {

  @Input() safeguardData: any;
  @Output() safeguardDetails: EventEmitter<any> = new EventEmitter();
  safeguards$: Observable<Safeguard[]>;
  selectedTaxonomy;
  form: FormGroup;

  public formulaInfo;
  public displayFormulaInfo;
  // taxonomies;
  private componentDestroyed: Subject<Component> = new Subject<Component>();

  constructor(
    private safeguardsService: SafeguardsTaxonomyService,
    private fb: FormBuilder
  ) {

    // this.safeguardsCode = this.taxonomies.map(taxonomy => ({ value:taxonomy.category, label:taxonomy.category}));
  }

  ngOnInit() {

    this.safeguardsService.fetch$().subscribe();
    this.safeguards$ = this.safeguardsService.safeguards;

    // this.taxonomies = [{category: '09', value: 'test'}];
    this.form = this.createForm();
    // it is needed for enable/disable the buttons in the parent view
    this.safeguardDetails.emit({check: this.form.valid, value: this.form.value});

    this.form.valueChanges.pipe(
      takeUntil(this.componentDestroyed)
    ).subscribe(value => {

      this.safeguardDetails.emit({check: this.form.valid, value: this.form.value});
    });

  }

  ngOnDestroy() {
    this.componentDestroyed.next();
    this.componentDestroyed.complete();
  }

  showDialog(value: string) {

    // this.currentOperation = value;
    // this.showPopup = true;

  }

  onChange() {
    this.safeguardDetails.emit({check: this.form.valid, value: this.form.value});
  }

  private createForm() {
    return this.fb.group({
      dissuasion: [this.safeguardData && this.safeguardData.dissuasion !== '' ? this.safeguardData.dissuasion : null,  {validators: this.checkFormulaValidator()}],
      prevention: [this.safeguardData && this.safeguardData.prevention !== '' ? this.safeguardData.prevention : null,  {validators: this.checkFormulaValidator()}],
      confining: [this.safeguardData && this.safeguardData.confining !== '' ? this.safeguardData.confining : null,  {validators: this.checkFormulaValidator()}],
      palliative: [this.safeguardData && this.safeguardData.palliative !== '' ? this.safeguardData.palliative : null,  {validators: this.checkFormulaValidator()}]
    });
  }

  private checkFormulaValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } => {
      if (control.value && !this.checkFormulaValid(control.value)) {
        return {'wrongFormula': true};
      }
      return null;
    };
  }

  private checkFormulaValid(formula: string): boolean {

    let formulaToCheck = formula;
    const splitParentheses = formulaToCheck.split(')');
    const splitParenthesesLength = splitParentheses.length - 1;
    formulaToCheck = splitParentheses.join('');
    const splitMaxMin = formulaToCheck.split(/max\(|min\(/);
    const splitMaxMinLenght = splitMaxMin.length - 1;
    formulaToCheck = splitMaxMin.join('');
    formulaToCheck = formulaToCheck.split(/\d{2}[A-Z]{1}\d{2}\;/).join('');
    formulaToCheck = formulaToCheck.split(/\d{2}[A-Z]{1}\d{2}\b/).join('');
    if (formulaToCheck.length > 0) {
      return false;
    }
    // check each max/min are closed by parentheses and match their number
    if (splitParenthesesLength !== splitMaxMinLenght) {
      return false;
    }
    return true;
  }

  showFormulaInfo() {
    this.formulaInfo = "SEST and MEHARI define an efficiency indicator for each risk scenario and each type of risk reduction measure. \n" +
      "The efficiency for each risk reduction measure is shown under the following notations: \n" +
      "Dissuasion for the efficiency of dissuasive measures  \n" +
      "Prevention for the efficiency of preventive measures  \n" +
      "Protection for the efficiency of protective measures  \n" +
      "Palliative for the efficiency of palliative measures  \n" +
      " \n" +
      "These indicators are calculated using formulae that make reference to the security services (or Safeguards) from the Audit forms. \n" +
      "While creating a new risk scenarios reference, it is important to include the risk reduction formulae in order to allow SEST properly compute the risk reduction based on the selected Safeguard in the Audit section. \n" +
      "Please note that SEST requires the user to write the formulae using MEHARI notation (below) and performs a validity check on the text to verify if the format is correct.\n" +
      "The formulae are computed based on the following rules: \n" +
      "•\tEither a Safeguard directly, by its identified (for example, 06B02), when the safeguard is the only one affecting the risk scenario\n" +
      "•\tFormulae containing functions: MIN(arg1;arg2;…) or MAX(arg1;arg2;…): parameters (arg1, arg2,…) are represented by the safeguards identifiers\n" +
      "\n" +
      "The formulae can therefore have the following formats, for example:  \n" +
      "•\tPALLIATIVE = 06B01 \n" +
      "•\tPREVENTIVE = MAX(04B04;MIN(04B01;04B02;04B03)) \n" +
      "\n" +
      "The first formula means that the (proposed – it can always be changed manually by the Risk Analyst) efficiency of the palliative measures is a direct function of the service 06B01 and takes as a value the quality level of that service (from 1 to 4).  \n" +
      "The second formula signifies that the (proposed – it can always be changed manually by the Risk Analyst) efficiency of the preventive measures equals the greater value between the service quality of 04B04 and the function representing the minimum of the services 04B01, 04B02, and 04B03. \n" +
      "NOTE: \n" +
      "The MIN function means that the services called as parameters are complementary. If the level of one is low, the level of the whole will be low. \n" +
      "An example of such a case is in the management of user access and authentication; if one of them is of a low level, the whole of access management control is of a low level. \n" +
      "The MAX function signifies that the services called as parameters are alternatives. If one of the services is of a high quality level, so the whole will be of a high quality level. " +
      "An example of such a case, depending on certain scenarios, is in data access control and the encryption of the data itself.  \n"
    this.displayFormulaInfo = true;
  }
}
