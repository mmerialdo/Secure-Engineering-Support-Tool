/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="configservice.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class ConfigService {
  private config: Configuration;

  constructor(private http: HttpClient) {
  }

  extractData(res: Response) {

    return res.json();
  }

  load(url: string) {
    return new Promise((resolve) => {
      this.http.get<Configuration>(url).subscribe(config => {
          this.config = config;
          resolve();
        });
    });
  }

  getConfiguration(): Configuration {

    return this.config;
  }
}

export class Configuration {
  constructor(public ipServer: string,
              public timeout: string,
              public timeoutWarning: string,
              public other: string) {

  }

}

