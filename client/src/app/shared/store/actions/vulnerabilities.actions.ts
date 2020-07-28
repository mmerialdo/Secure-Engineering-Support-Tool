/* --------------------------------------------------------------------------------------------------------------------
  // Copyright file="vulnerabilities.action.ts"
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

export const copyVulnerabilities = createAction('[Vulnerabilities Component] Copy vulnerabilities', props<{ cachedVulnerabilities: SelectItem[] }>());
export const pasteVulnerabilities = createAction('[Vulnerabilities Component] Paste vulnerabilities');
export const resetVulnerabilities = createAction('[Vulnerabilities Component] Reset vulnerabilities');
