/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="configloader.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import { ConfigService } from './configservice';
import { environment } from '../environments/environment';

export function ConfigLoader(configService: ConfigService) {

  return () => configService.load(environment.configFile);
}
