/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="index.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import * as fromThreats from './reducers/threats.reducer';
import * as fromAssets from './reducers/assets.reducer';
import * as fromVulnerabilities from './reducers/vulnerabilities.reducer';
import {ActionReducerMap} from '@ngrx/store';

export interface AppState {
  threats: fromThreats.CopiedThreatsState;
  assets: fromAssets.AssetsState;
  vulnerabilities: fromVulnerabilities.CopiedVulnerabilitiesState
}

export const reducers: ActionReducerMap<AppState> = {
  threats: fromThreats.threatsReducer,
  assets: fromAssets.assetsReducer,
  vulnerabilities: fromVulnerabilities.vulnerabilitiesReducer,
};
