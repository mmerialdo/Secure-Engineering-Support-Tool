/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="app.module.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, ErrorHandler, NgModule} from '@angular/core';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {AppComponent} from './app.component';
import {LoginComponent} from './login/login.component';
import {TabsComponent} from './tabs/tabs.component';
import {DataService} from './dataservice';
import {AppRoutingModule} from './app-routing.module';
// import {DataTableModule} from 'primeng/components/datatable/datatable';
import {
  MessagesModule,
  TooltipModule,
  TreeTableModule,
  SharedModule,
  DropdownModule,
  ConfirmDialogModule,
  MenuModule,
  TabViewModule,
  DataListModule,
  PickListModule,
  TabMenuModule,
  DialogModule,
  ListboxModule,
  TieredMenuModule,
  MultiSelectModule,
  TreeModule,
  RadioButtonModule,
  AccordionModule,
  PaginatorModule, ProgressSpinnerModule, FileUploadModule, CheckboxModule, StepsModule, CardModule, AutoCompleteModule
} from 'primeng/primeng';
import {RegisterComponent} from './register/register.component';
import {ValidationService} from './validationservice';
import {ControlMessagesComponent} from './control-messages.component';
import {ProfileComponent} from './profile/profile.component';
import {CreateprojectComponent} from './tabs/projectTab/createproject/createproject.component';
import {ProjectTabComponent} from './tabs/projectTab/project-tab/project-tab.component';
import {UsersTabComponent} from './tabs/users-tab/users-tab.component';
import {ProceduresComponent} from './secondaryTabs/procedures/procedures.component';
import {ProjectComponent} from './secondaryTabs/project/project.component';
import {EditprojectComponent} from './tabs/projectTab/editproject/editproject.component';
import {AuditComponent} from './secondaryTabs/audit/audit.component';
import {BusinessComponent} from './secondaryTabs/business/business.component';
import {EditprofileComponent} from './tabs/projectTab/editprofile/editprofile.component';
import {VulnerabilitiesComponent} from './secondaryTabs/vulnerabilities/vulnerabilities.component';
import {ThreatsComponent} from './secondaryTabs/threats/threats.component';
import {ScenarioComponent} from './secondaryTabs/scenario/scenario.component';
import {SettingsComponent} from './tabs/settings/settings.component';
import {HomeComponent} from './tabs/home/home.component';
import {TreatmentComponent} from './secondaryTabs/treatment/treatment.component';
import {ConfigService} from './configservice';
import {ConfigLoader} from './configloader';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {FontAwesomeModule} from '@fortawesome/angular-fontawesome';
import {SecondarytabsComponent} from './secondaryTabs/secondarytabs.component';
import {ErrorsService} from './errors/errors.service';
import {DataAccessService} from './data-access.service';
import {AuthGuard as AuthGuard} from './auth-guard.service';
import {AuthService} from './core/authn/auth.service';
import {ToastModule} from 'primeng/toast';
import {MessageService} from 'primeng/components/common/messageservice';
import {StoreModule} from '@ngrx/store';
import {StoreDevtoolsModule} from '@ngrx/store-devtools';
import {reducers} from './shared/store';
import {TableModule} from 'primeng/table';
import {ToolbarModule} from 'primeng/toolbar';


import {TabViewComponent} from './secondaryTabs/business/tabularization-view/tab-view/tab-view.component';
import {GraphViewComponent} from './secondaryTabs/business/graph-view/graph-view.component';
import {TabularizationViewComponent} from './secondaryTabs/business/tabularization-view/tabularization-view.component';
import {ActivitiesTabViewComponent} from './secondaryTabs/business/tabularization-view/tab-view/activities-tab-view/activities-tab-view.component';
import {AssetsTabViewComponent} from './secondaryTabs/business/tabularization-view/tab-view/assets-tab-view/assets-tab-view.component';
import {MalfunctionsTabViewComponent} from './secondaryTabs/business/tabularization-view/tab-view/malfunctions-tab-view/malfunctions-tab-view.component';
import {AddRowDirective} from './shared/add-row.directive';
import {EditMalfunctionComponent} from './secondaryTabs/business/tabularization-view/tab-view/malfunctions-tab-view/edit-malfunction/edit-malfunction.component';
import {EditAssetComponent} from './secondaryTabs/business/tabularization-view/tab-view/assets-tab-view/edit-asset/edit-asset.component';
import {EditActivityComponent} from './secondaryTabs/business/tabularization-view/tab-view/activities-tab-view/edit-activity/edit-activity.component';
import {ProcessTabViewComponent} from './secondaryTabs/business/tabularization-view/tab-view/process-tab-view/process-tab-view.component';
import {EditProcessComponent} from './secondaryTabs/business/tabularization-view/tab-view/process-tab-view/edit-process/edit-process.component';
import {TaxonomiesManagementComponent} from './tabs/taxonomies-management/taxonomies-management.component';
import {VulnerabilitiesTaxonomyComponent} from './tabs/taxonomies-management/vulnerabilities-taxonomy/vulnerabilities-taxonomy.component';
import {AddEditRowComponent} from './tabs/taxonomies-management/vulnerabilities-taxonomy/add-edit-row/add-edit-row.component';
import {ThreatsTaxonomyComponent} from './tabs/taxonomies-management/threats-taxonomy/threats-taxonomy.component';
import {AddEditThreatRowComponent} from './tabs/taxonomies-management/threats-taxonomy/add-edit-threat-row/add-edit-threat-row.component';
import {RiskScenarioTaxonomyComponent} from './tabs/taxonomies-management/risk-scenario-taxonomy/risk-scenario-taxonomy.component';
import {ScenarioCreationComponent} from './tabs/taxonomies-management/risk-scenario-taxonomy/scenario-creation/scenario-creation.component';
import {AssetDefinitionComponent} from './tabs/taxonomies-management/risk-scenario-taxonomy/scenario-creation/asset-definition/asset-definition.component';
import {VulnerabilityDefinitionComponent} from './tabs/taxonomies-management/risk-scenario-taxonomy/scenario-creation/vulnerability-definition/vulnerability-definition.component';
import {ThreatDefinitionComponent} from './tabs/taxonomies-management/risk-scenario-taxonomy/scenario-creation/threat-definition/threat-definition.component';
import {SafeguardDefinitionComponent} from './tabs/taxonomies-management/risk-scenario-taxonomy/scenario-creation/safeguard-definition/safeguard-definition.component';
import {OrganizationsTabViewComponent} from './secondaryTabs/business/tabularization-view/tab-view/organizations-tab-view/organizations-tab-view.component';
import {EditOrganizationComponent} from './secondaryTabs/business/tabularization-view/tab-view/organizations-tab-view/edit-organization/edit-organization.component';
import {LoginTrackerService} from './shared/service/login-tracker-service';
import {LockService} from './shared/service/lock-service';
import {UserPasswordComponent} from './tabs/users-tab/user-password/user-password.component';
import {HashLocationStrategy, LocationStrategy} from '@angular/common';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    TabsComponent,
    SecondarytabsComponent,
    RegisterComponent,
    ControlMessagesComponent,
    ProfileComponent,
    CreateprojectComponent,
    ProjectTabComponent,
    UsersTabComponent,
    ProceduresComponent,
    ProjectComponent,
    EditprojectComponent,
    AuditComponent,
    BusinessComponent,
    EditprofileComponent,
    VulnerabilitiesComponent,
    ThreatsComponent,
    ScenarioComponent,
    SettingsComponent,
    TreatmentComponent,
    HomeComponent,
    TabViewComponent,
    GraphViewComponent,
    TabularizationViewComponent,
    ActivitiesTabViewComponent,
    AssetsTabViewComponent,
    MalfunctionsTabViewComponent,
    AddRowDirective,
    EditMalfunctionComponent,
    EditAssetComponent,
    EditActivityComponent,
    ProcessTabViewComponent,
    TaxonomiesManagementComponent,
    VulnerabilitiesTaxonomyComponent,
    AddEditRowComponent,
    ThreatsTaxonomyComponent,
    AddEditThreatRowComponent,
    RiskScenarioTaxonomyComponent,
    ScenarioCreationComponent,
    AssetDefinitionComponent,
    VulnerabilityDefinitionComponent,
    ThreatDefinitionComponent,
    SafeguardDefinitionComponent,
    OrganizationsTabViewComponent,
    EditProcessComponent,
    EditOrganizationComponent,
    UserPasswordComponent
  ],
  imports: [
    AccordionModule,
    BrowserAnimationsModule,
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule,
    MenuModule,
    TabViewModule,
    DataListModule,
    PickListModule,
    FileUploadModule,
    ConfirmDialogModule,
    TabMenuModule,
    DialogModule,
    ListboxModule,
    TieredMenuModule,
    MultiSelectModule,
    TreeModule,
    DropdownModule,
    CheckboxModule,
    TreeTableModule,
    SharedModule,
    TooltipModule,
    MessagesModule,
    FontAwesomeModule,
    TableModule,
    PaginatorModule,
    ToastModule,
    RadioButtonModule,
    PaginatorModule,
    ProgressSpinnerModule,
    CheckboxModule,
    StepsModule,
    CardModule,
    AutoCompleteModule,
    ToastModule,
    ToolbarModule,
    StoreModule.forRoot(reducers),
    StoreDevtoolsModule.instrument({maxAge: 25})
  ],
  providers: [ValidationService,
    DataService,
    DataAccessService,
    ConfigService,
    MessageService,
    LoginTrackerService,
    LockService,
    {
      provide: APP_INITIALIZER,
      useFactory: ConfigLoader,
      deps: [ConfigService],
      multi: true
    },
    {
      provide: ErrorHandler,
      useClass: ErrorsService
    },
    {
      provide: LocationStrategy,
      useClass: HashLocationStrategy
    },
    AuthGuard, AuthService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
