/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="threats.reducer.ts"
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
import {copyThreats, pasteThreats, resetThreats} from '../actions/threats.actions';

export interface CopiedThreatsState {
  cachedThreats: SelectItem[];
  count: number;
  copied: boolean;
}

export const initialState: CopiedThreatsState = {
  cachedThreats: [],
  count: 0,
  copied: false
};

const threatsReducerConst = createReducer(
  initialState,
  on(copyThreats, (state, props) => {
    const threats = [...props.cachedThreats];
    return {...state,
      cachedThreats: threats,
      count: threats.length,
      copied: true
    };
  }),
  on(pasteThreats, state => ({...state, cachedThreats: [], count: 0, copied: false})),
  on(resetThreats, state => ({...state, cachedThreats: [], count: 0, copied: false })),
);

export function threatsReducer(state: CopiedThreatsState | undefined, props: any) {
  return threatsReducerConst(state, props);
}

// selectors
export const getState = (state: CopiedThreatsState) => state;
export const fetchThreats = (state: any): SelectItem[] => state.threats.cachedThreats;
export const fetchCopiedThreatsCount = (state: any): number => state.threats.count;
export const fetchCopiedThreatsStatus = (state: any): boolean => state.threats.copied;
export const selectThreatsFromCache = createSelector(getState, fetchThreats);
