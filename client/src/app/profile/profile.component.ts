/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="profile.component.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {AbstractControl, FormBuilder, FormGroup, ValidatorFn, Validators} from '@angular/forms';
import {MenuItem} from 'primeng/primeng';
import {DataService} from '../dataservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';


@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit, OnDestroy {

  public profileMenu: MenuItem[];

  profileForm: any;

  selectedTemplate = [];
  templateList: any;


  public blocked = false;
  public blockedMessage = false;

  public showPhaseInfo = false;
  public showTemplateInfo = false;

  // to show messages
  msgsAsset: Message[] = [];

  private subscriptions: Subscription[] = [];

  constructor(private formBuilder: FormBuilder, private dataService: DataService, private router: Router) {

    this.profileForm = this.formBuilder.group({
      'name': ['', [Validators.required, Validators.maxLength(50)]],
      'organization': ['', [Validators.required, Validators.maxLength(40)]],
      'phase': ['', [Validators.required]],
      'riskMethodology': ['', [Validators.required]],
      'description': ['', [Validators.required, Validators.maxLength(800)]]
   //   'templates': ['', this.validateTemplateRequired()]
    });

    this.profileMenu = [

      {
        label: 'Save Profile', icon: 'fa fa-fw fa-floppy-o ', disabled: true, command: (event) => {

          this.blocked = true;
          this.createProfile();
        }
      },
      {label: 'Cancel', icon: 'fa fa-fw fa-times ', routerLink: ['/tool/projects']}
    ];

    this.subscriptions.push(
      this.profileForm.valueChanges.subscribe(value => {

        if (this.profileForm.valid) {

          this.profileMenu[0].disabled = false;
        } else {

          this.profileMenu[0].disabled = true;
        }
      }));
  }

  ngOnInit() {

    this.getTemplate();
  }

  createProfile() {
    const template = [];
    for (const i in this.selectedTemplate) {

      template.push({'identifier': this.selectedTemplate[i].identifier});
    }

    const profile = {

      'name': this.profileForm.value.name,
      'organization': this.profileForm.value.organization,
      'phase': this.profileForm.value.phase,
      'riskMethodology': this.profileForm.value.riskMethodology,
      'description': this.profileForm.value.description,
      'templates': template

    };
    this.subscriptions.push(
      this.dataService.insertProfile(JSON.stringify(profile)).subscribe(response => {
        this.blocked = true;

        this.router.navigate(['/tool/projects']);

      }, err => {
        this.blocked = false;
        throw err;
      }));
  }

  private validateTemplateRequired(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: any } | null => {
      if (this.selectedTemplate) {
        let temp = {};
        temp['templates'] = true;
        return temp;
      }
      return null;
    };
  }

  showFailed(s: string) {
    this.msgsAsset = [];
    this.msgsAsset.push({severity: 'error', summary: 'Error', detail: s});

    setTimeout(() => {
      this.clearMessage();
    }, 6000);
  }

  clearMessage() {
    this.msgsAsset = [];
    this.blocked = false;
    this.blockedMessage = false;
    this.router.navigate(['/tool/projects']);
  }

  getTemplate(): void {
    this.subscriptions.push(
      this.dataService.getTemplate().subscribe(response => {
        this.templateList = response;
      }));
  }

  infoPhase() {
    this.showPhaseInfo = true;
  }

  infoTemplate() {
    this.showTemplateInfo = true;
  }

  ngOnDestroy() {
    this.subscriptions.forEach(s => s.unsubscribe());
  }
}
