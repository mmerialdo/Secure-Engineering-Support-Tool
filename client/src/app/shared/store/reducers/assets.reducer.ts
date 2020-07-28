/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="assets.reducer.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {createReducer, createSelector, on} from '@ngrx/store';
import {
  editActivity,
  editActivityClose,
  editActivityOpen,
  editAsset,
  editAssetClose,
  editAssetOpen,
  editMalfunction,
  editMalfunctionClose,
  editMalfunctionOpen,
  editOrganization,
  editOrganizationClose,
  editOrganizationOpen,
  editProcess,
  editProcessClose,
  editProcessOpen,
  newActivityOpen,
  newAssetOpen,
  newMalfunctionOpen,
  newOrganizationOpen,
  newProcessOpen,
  readMalfunction,
  refreshTablesStart,
  refreshTablesStop,
  clearView,
  storeActivity,
  storeAsset,
  storeMalfunction,
  storeOrganization,
  storeProcess,
  storeServerAsset,
  switchToGraphView,
  switchToTabView,
  validateFalse,
  validateTrue
} from '../actions/assets.actions';

export interface AssetsState {
  tabView: boolean;
  serverAsset: any;
  malfunction: any;
  asset: any;
  activity: any;
  process: any;
  organization: any;
  malfunctionToEdit: any;
  assetToEdit: any;
  activityToEdit: any;
  processToEdit: any;
  organizationToEdit: any;
  editMalfunction: boolean;
  editAsset: boolean;
  editActivity: boolean;
  editProcess: boolean;
  editOrganization: boolean;
  valid: boolean;
  refresh: boolean;
}

export const initialState: AssetsState = {
  tabView: true,
  serverAsset: {},
  malfunction: null,
  activity: null,
  asset: null,
  process: null,
  organization: null,
  malfunctionToEdit: null,
  assetToEdit: null,
  activityToEdit: null,
  processToEdit: null,
  organizationToEdit: null,
  editMalfunction: false,
  editActivity: false,
  editAsset: false,
  editProcess: false,
  editOrganization: false,
  valid: true,
  refresh: false
};

const assetsReducerConst = createReducer(
  initialState,
  on(clearView, (state) => {
    const tabViewValue = state.tabView;
    state = initialState;
    state.tabView = tabViewValue;
    return state;
  }),
  on(refreshTablesStart, (state) => {
    return {
      ...state,
      refresh: true
    };
  }),
  on(refreshTablesStop, (state) => {
    return {
      ...state,
      refresh: false
    };
  }),
  on(validateFalse, (state) => {
    return {
      ...state,
      valid: false
    };
  }),
  on(validateTrue, (state) => {
    return {
      ...state,
      valid: true
    };
  }),
  on(switchToGraphView, (state) => {
    return {
      ...state,
      tabView: false
    };
  }),
  on(switchToTabView, (state) => {
    return {
      ...state,
      tabView: true
    };
  }),
  on(newProcessOpen, (state) => {
    return {
      ...state,
      editProcess: true
    };
  }),
  on(editProcessOpen, (state, props) => {
    return {
      ...state,
      editProcess: true,
      processToEdit: props
    };
  }),
  on(editProcessClose, (state) => {
    return {
      ...state,
      editProcess: false,
      processToEdit: null
    };
  }),
  on(storeProcess, (state, props) => {
    return {
      ...state,
      process: props
    };
  }),
  on(editProcess, (state, props) => {
    return {
      ...state,
      process: props
    };
  }),
  on(newOrganizationOpen, (state) => {
    return {
      ...state,
      editOrganization: true
    };
  }),
  on(editOrganizationOpen, (state, props) => {
    return {
      ...state,
      editOrganization: true,
      organizationToEdit: props
    };
  }),
  on(editOrganizationClose, (state) => {
    return {
      ...state,
      editOrganization: false,
      organizationToEdit: null
    };
  }),
  on(storeOrganization, (state, props) => {
    return {
      ...state,
      organization: props
    };
  }),
  on(editOrganization, (state, props) => {
    return {
      ...state,
      organization: props
    };
  }),
  on(newMalfunctionOpen, (state) => {
    return {
      ...state,
      editMalfunction: true
    };
  }),
  on(editMalfunctionOpen, (state, props) => {
    return {
      ...state,
      editMalfunction: true,
      malfunctionToEdit: props
    };
  }),
  on(editMalfunctionClose, (state) => {
    return {
      ...state,
      editMalfunction: false,
      malfunctionToEdit: null
    };
  }),
  on(editAssetOpen, (state, props) => {
    return {
      ...state,
      editAsset: true,
      assetToEdit: props
    };
  }),
  on(editActivityOpen, (state, props) => {
    return {
      ...state,
      editActivity: true,
      activityToEdit: props
    };
  }),
  on(editAssetClose, (state) => {
    return {
      ...state,
      editAsset: false,
      assetToEdit: null
    };
  }),
  on(editActivityClose, (state) => {
    return {
      ...state,
      editActivity: false,
      activityToEdit: null
    };
  }),
  on(storeServerAsset, (state, props) => {
    return {
      ...state,
      serverAsset: props
    };
  }),
  on(storeMalfunction, (state, props) => {
    return {
      ...state,
      malfunction: props
    };
  }),
  on(readMalfunction, (state) => {
    return {
      ...state,
      malfunction: {}
    };
  }),
  on(editMalfunction, (state, props) => {
    return {
      ...state,
      malfunction: props
    };
  }),
  on(editAsset, (state, props) => {
    return {
      ...state,
      asset: props
    };
  }),
  on(editActivity, (state, props) => {
    return {
      ...state,
      activity: props
    };
  }),
  on(newActivityOpen, (state) => {
    return {
      ...state,
      editActivity: true
    };
  }),
  on(newAssetOpen, (state) => {
    return {
      ...state,
      editAsset: true
    };
  }),
  on(storeAsset, (state, props) => {
    return {
      ...state,
      asset: props
    };
  }),
  on(storeActivity, (state, props) => {
    return {
      ...state,
      activity: props
    };
  })
);

export function assetsReducer(state: AssetsState | undefined, props: any) {
  return assetsReducerConst(state, props);
}

// selectors
// export const getState = (state: AssetsState) => state.assets;
export const selectView = (state: any): boolean => state.assets.tabView;
export const selectValid = (state: any): boolean => state.assets.valid;
export const selectRefresh = (state: any): boolean => state.assets.refresh;
export const selectEditMalfunction = (state: any): boolean => state.assets.editMalfunction;
export const fetchServerAsset = (state: any): any => state.assets.serverAsset;
export const fetchMalfunction = (state: any): any => state.assets.malfunction;
export const fetchAsset = (state: any): any => state.assets.asset;
export const fetchMalfunctionForEdit = (state: any): any => state.assets.malfunctionToEdit;
export const fetchAssetForEdit = (state: any): any => state.assets.assetToEdit;
export const selectEditAsset = (state: any): boolean => state.assets.editAsset;
export const selectEditActivity = (state: any): boolean => state.assets.editActivity;
export const fetchActivityForEdit = (state: any): any => state.assets.activityToEdit;
export const fetchActivity = (state: any): any => state.assets.activity;
export const fetchProcess = (state: any): any => state.assets.process;
export const fetchProcessForEdit = (state: any): any => state.assets.processToEdit;
export const fetchOrganization = (state: any): any => state.assets.organization;
export const fetchOrganizationForEdit = (state: any): any => state.assets.organizationToEdit;
export const selectEditProcess = (state: any): boolean => state.assets.editProcess;
export const selectEditOrganization = (state: any): boolean => state.assets.editOrganization;

