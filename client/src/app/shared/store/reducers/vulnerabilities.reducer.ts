/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="vulnerabilities.reducer.ts"
  //  © Copyright European Space Agency, 2018-2020
  //
  //  Author: Software developed by RHEA System S.A.
  //
  //  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
  //  No part of the package, including this file, may be copied, modified, propagated, or distributed
  //  except according to the terms contained in the file ‘LICENSE.txt’.
  // --------------------------------------------------------------------------------------------------------------------
  */

import {SelectItem} from 'primeng/primeng';
import {createReducer, createSelector, on} from '@ngrx/store';
import {copyVulnerabilities, pasteVulnerabilities, resetVulnerabilities} from '../actions/vulnerabilities.actions';

export interface CopiedVulnerabilitiesState {
  cachedVulnerabilities: SelectItem[];
  count: number;
  copied: boolean;
}

export const initialState: CopiedVulnerabilitiesState = {
  cachedVulnerabilities: [],
  count: 0,
  copied: false
};

const vulnerabilitiesReducerConst = createReducer(
  initialState,
  on(copyVulnerabilities, (state, props) => {
    const vulnerabilities = [...props.cachedVulnerabilities];
    return {...state,
      cachedVulnerabilities: vulnerabilities,
      count: vulnerabilities.length,
      copied: true
    };
  }),
  on(pasteVulnerabilities, state => ({...state, cachedVulnerabilities: [], count: 0, copied: false})),
  on(resetVulnerabilities, state => ({...state, cachedVulnerabilities: [], count: 0, copied: false })),
);

export function vulnerabilitiesReducer(state: CopiedVulnerabilitiesState | undefined, props: any) {
  return vulnerabilitiesReducerConst(state, props);
}

// selectors
export const getState = (state: CopiedVulnerabilitiesState) => state;
export const fetchVulnerabilities = (state: any): SelectItem[] => state.vulnerabilities.cachedVulnerabilities;
export const fetchCopiedVulnerabilitiesCount = (state: any): number => state.vulnerabilities.count;
export const fetchCopiedVulnerabilitiesStatus = (state: any): boolean => state.vulnerabilities.copied;
export const selectVulnerabilitiesFromCache = createSelector(getState, fetchVulnerabilities);
