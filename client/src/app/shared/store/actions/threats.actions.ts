/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="threats.actions.ts"
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
import {SelectItem} from 'primeng/primeng';

export const copyThreats = createAction('[Threats Component] Copy threats', props<{ cachedThreats: SelectItem[] }>());
export const pasteThreats = createAction('[Threats Component] Paste threats');
export const resetThreats = createAction('[Threats Component] Reset threats');
