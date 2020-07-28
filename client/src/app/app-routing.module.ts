/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="app-routing.module.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { TabsComponent } from './tabs/tabs.component';
import { ProfileComponent } from './profile/profile.component';
import { CreateprojectComponent } from './tabs/projectTab/createproject/createproject.component';

import { ProjectTabComponent } from './tabs/projectTab/project-tab/project-tab.component';

import { UsersTabComponent } from './tabs/users-tab/users-tab.component';
import { SecondarytabsComponent } from './secondaryTabs/secondarytabs.component';
import { ProceduresComponent } from './secondaryTabs/procedures/procedures.component';
import { ProjectComponent } from './secondaryTabs/project/project.component';
import { EditprojectComponent } from './tabs/projectTab/editproject/editproject.component';

import { AuditComponent } from './secondaryTabs/audit/audit.component';
import { BusinessComponent } from './secondaryTabs/business/business.component';
import { EditprofileComponent } from './tabs/projectTab/editprofile/editprofile.component';

import { VulnerabilitiesComponent } from './secondaryTabs/vulnerabilities/vulnerabilities.component';

import { ThreatsComponent } from './secondaryTabs/threats/threats.component';
import { ScenarioComponent } from './secondaryTabs/scenario/scenario.component';
import { SettingsComponent } from './tabs/settings/settings.component';
import { TreatmentComponent } from './secondaryTabs/treatment/treatment.component';
import { HomeComponent } from './tabs/home/home.component';
import {
  AuthGuard as AuthGuard
} from './auth-guard.service';
import {TaxonomiesManagementComponent} from './tabs/taxonomies-management/taxonomies-management.component';
const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },

  {
    path: 'projectabs', component: SecondarytabsComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'project', component: ProjectComponent },
      { path: 'procedures', component: ProceduresComponent },
      { path: 'audit', component: AuditComponent },
      { path: 'assets', component: BusinessComponent },
      { path: 'vulnerabilities', component: VulnerabilitiesComponent },
      { path: 'threats', component: ThreatsComponent },
      { path: 'scenario', component: ScenarioComponent },
      { path: 'treatment', component: TreatmentComponent }


    ]

  },
  {
    path: 'tool', component: TabsComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'projects', component: ProjectTabComponent },
      { path: 'users', component: UsersTabComponent },
      { path: 'createprofile', component: ProfileComponent },
      { path: 'editprofile', component: EditprofileComponent },
      { path: 'editproject', component: EditprojectComponent },
      { path: 'createproject', component: CreateprojectComponent },

      { path: 'settings', component: SettingsComponent },
      { path: 'taxonomiesManagement', component: TaxonomiesManagementComponent},

      { path: 'home', component: HomeComponent }

    ]

  }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
