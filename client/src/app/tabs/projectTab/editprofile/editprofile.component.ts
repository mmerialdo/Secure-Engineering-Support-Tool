/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="editprofile.component.ts"
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
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MenuItem} from 'primeng/primeng';
import {DataService} from '../../../dataservice';
import {Message} from 'primeng/components/common/api';
import {Subscription} from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-editprofile',
  templateUrl: './editprofile.component.html',
  styleUrls: ['./editprofile.component.scss']
})
export class EditprofileComponent implements OnInit, OnDestroy {

  public profileMenu: MenuItem[];
  public profile: any;

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
      'organization': ['', [Validators.required, Validators.maxLength(20)]],
      'phase': ['', [Validators.required]],
      'riskMethodology': ['', [Validators.required]],
      'description': ['', [Validators.required, Validators.maxLength(1000)]]


    });


    this.profileMenu = [

      {
        label: 'Save Profile', icon: 'fa fa-fw fa-floppy-o ', disabled: true, command: (event) => {
          this.blocked = true;
          this.editProfile();

        }
      },
      {label: 'Cancel', icon: 'fa-times ', routerLink: ['/tool/projects']}
    ];


    this.subscriptions.push(
      this.profileForm.valueChanges.subscribe(value => {

        if ((this.profileForm.valid)) {

          this.profileMenu[0].disabled = false;
        } else {
          this.profileMenu[0].disabled = true;
        }
      }));
  }

  ngOnInit() {


    this.getProfileById();


  }

  editProfile() {
    const template = [];

    for (const i in this.selectedTemplate) {
      template.push({'identifier': this.selectedTemplate[i].identifier});
    }

    const profile = {

      'identifier': sessionStorage.getItem('idProfile'),
      'name': this.profileForm.value.name,
      'organization': this.profileForm.value.organization,
      'phase': this.profileForm.value.phase,
      'riskMethodology': this.profileForm.value.riskMethodology,
      'description': this.profileForm.value.description,
      'templates': template

    };

    this.subscriptions.push(
      this.dataService.updateProfile(JSON.stringify(profile)).subscribe(response => {
        this.blocked = false;
        this.router.navigate(['/tool/projects']);
      }, err => {
        this.blocked = false;
        throw err;
      }));
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

  getTemplateByProfile(): void {

    const a = {'filterMap': {'PROFILE': this.profile.identifier,
        'PROJECT': sessionStorage.getItem('idProject')}};
    this.subscriptions.push(
      this.dataService.loadTemplateByProfile(JSON.stringify(a)).subscribe(response => {
        this.converterTemplate(response);
      }));
  }

  converterTemplate(array) {

    for (const i in array) {

      const a = JSON.parse(JSON.stringify(array[i]));
      const b = JSON.parse(a);
      this.selectedTemplate.push(b);
      this.removeSelectedProfile(this.selectedTemplate);


    }


  }

  getTemplate(): void {

    this.subscriptions.push(
      this.dataService.getTemplate().subscribe(response => {
        this.templateList = response;
        this.editProfileFields();
        this.getTemplateByProfile();
      }));
  }

  getProfileById(): void {

    const a = {'filterMap': {'IDENTIFIER': sessionStorage.getItem('idProfile'),
        'PROJECT': sessionStorage.getItem('idProject')}};
    this.subscriptions.push(
      this.dataService.loadProfile(JSON.stringify(a)).subscribe(response => {
          this.profile = response;
          this.getTemplate();
        },
        err => {
          this.blocked = false;
          throw err;
        }));
  }

  editProfileFields() {

    this.profileForm.controls['name'].setValue(this.profile.name);
    this.profileForm.controls['organization'].setValue(this.profile.organization);
    this.profileForm.controls['phase'].setValue(this.profile.phase);
    this.profileForm.controls['riskMethodology'].setValue(this.profile.riskMethodology);
    this.profileForm.controls['description'].setValue(this.profile.description);
    //this.profileForm.controls['templates'].setValue(this.profile.templates);
  }

  removeSelectedProfile(array) {
    for (const i in array) {
      for (const j in this.templateList) {
        if (array[i].name === this.templateList[j].name) {

          this.templateList.splice(Number(j), 1);
        }
      }
    }
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
