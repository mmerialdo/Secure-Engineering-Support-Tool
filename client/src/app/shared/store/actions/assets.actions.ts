/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="asstes.actions.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {createAction, props} from '@ngrx/store';

export const switchToGraphView = createAction('[Assets Component] Switch to graph view');
export const switchToTabView = createAction('[Assets Component] Switch to tab view');
export const newMalfunctionOpen = createAction('[Assets Component] New Malfunction open');
export const editMalfunctionOpen = createAction('[Assets Component] Edit Malfunction open', props<{ malfunctionToEdit: any }>());
export const editMalfunctionClose = createAction('[Assets Component] Edit Malfunction close');
export const storeServerAsset = createAction('[Assets Component] Store Server Asset', props<{ serverAsset: any }>());

export const storeMalfunction = createAction('[Assets Component] Store Malfunction', props<{ malfunction: any }>());
export const editMalfunction = createAction('[Assets Component] Edit Malfunction', props<{ malfunction: any }>());
export const readMalfunction = createAction('[Assets Component] Fetch Malfunction');
export const editAssetOpen = createAction('[Assets Component] Edit Asset open', props<{ assetToEdit: any }>());
export const editAssetClose = createAction('[Assets Component] Edit Malfunction close');

export const newAssetOpen = createAction('[Assets Component] New Asset open');
export const storeAsset = createAction('[Assets Component] Store Asset', props<{ asset: any }>());
export const editAsset = createAction('[Assets Component] Edit Asset', props<{ asset: any }>());

export const newOrganizationOpen = createAction('[Assets Component] New Organization open');
export const editOrganizationOpen = createAction('[Assets Component] Edit Organization open', props<{ organizationToEdit: any }>());
export const editOrganizationClose = createAction('[Assets Component] Edit Organization close');
export const storeOrganization = createAction('[Assets Component] Store Organization', props<{ organization: any }>());
export const editOrganization = createAction('[Assets Component] Edit Organization', props<{ organization: any }>());

export const newActivityOpen = createAction('[Assets Component] New Activity open');
export const editActivityOpen = createAction('[Assets Component] Edit Activity open', props<{ activityToEdit: any }>());
export const editActivityClose = createAction('[Assets Component] Edit Activity close');
export const storeActivity = createAction('[Assets Component] Store Activity', props<{ activity: any }>());
export const editActivity = createAction('[Assets Component] Edit Activity', props<{ activity: any }>());

export const newProcessOpen = createAction('[Assets Component] New Process open');
export const editProcessOpen = createAction('[Assets Component] Edit Process open', props<{ processToEdit: any }>());
export const editProcessClose = createAction('[Assets Component] Edit Process close');
export const storeProcess = createAction('[Assets Component] Store Process', props<{ process: any }>());
export const editProcess = createAction('[Assets Component] Edit Process', props<{ process: any }>());

export const refreshTablesStart = createAction('[Assets Component] Refresh tables start');
export const refreshTablesStop = createAction('[Assets Component] Refresh tables stop');

export const clearView = createAction('[Assets Component] Clear View');

export const validateTrue = createAction('[Assets Component] Validation succeed');
export const validateFalse = createAction('[Assets Component] Validation failed');
