/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="data-access.service.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {DataService} from './dataservice';

/**
 * The data access service allows for operations on the API.
 */
@Injectable()
export class DataAccessService {

  /**
   * request headers
   */
  private headers: HttpHeaders;

  /**
   * The API base route
   * @type {string}
   */
  private readonly apiBase: string = 'api';

  constructor(private http: HttpClient) {
  }

  public getString(url: string) {
    return this.http.get(url, {responseType: 'text'});
  }

  public getGeneric<T>(url: string): Observable<T> {
    // exclude config and user/me used at startup and restart timer
    if (url !== 'config' && url !== 'user/me') {
    }
    return this.http.get<T>(url, this.getRequestOptions());
  }

  public getFileGeneric(url: string) {
    return this.http.get(url, this.getDownloadRequestOptions());
  }

  /**
   * Puts one instance of generic type
   * @param {T} object
   * @param {string} fromPath
   */
  public putGeneric<T>(object: T, typeUrl: string): Observable<T> {
    return this.http.put<T>(typeUrl, JSON.stringify(object), this.getRequestOptions());
  }

  /**
   * Posts one instance of generic type
   * @param {T} object
   * @param {string} fromPath
   */
  public postGeneric<T>(object: any, typeUrl: string): Observable<T> {
    return this.http.post<T>(typeUrl, JSON.stringify(object), this.getRequestOptions());
  }


  public postDocument(documentRoute: string, file: File, name: string) {
    const formData = new FormData();
    formData.append('document', file);
    return this.http.post(this.constructUri(documentRoute), formData, this.getDocumentRequestOptions(name));
  }

  /**
   * Constructs a uri with a custom endpoint.
   * @param {string} uri The custom endpoint.
   */
  public constructUri(uri: string): string {
    return `${this.apiBase}/${uri}`;
  }

  private attachmentFormData<T>(file: File, object: T) {
    const formData = new FormData();

    formData.append('file', file);
    formData.append('object', JSON.stringify(object));

    return formData;
  }

  private setHeaders() {
    this.headers = new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    });
  }

  private setDocumentHeaders(name: string) {
    this.headers = new HttpHeaders({
      'document-name': name
    });
  }

  /**
   * Gets the option headers.
   * @returns {{headers: HttpHeaders}} The object with all request options.
   */
  private getRequestOptions() {
    this.setHeaders();
    return {headers: this.headers};
  }

  private getDownloadRequestOptions(): {
    headers?: HttpHeaders | {
      [header: string]: string | string[];
    };
    observe: 'response';
    params?: HttpParams | {
      [param: string]: string | string[];
    };
    reportProgress?: boolean;
    responseType: 'blob';
    withCredentials?: boolean;
  } {
    this.setHeaders();
    return {headers: this.headers, responseType: 'blob', observe: 'response'};
  }

  private getDocumentRequestOptions(name: string): {
    headers?: HttpHeaders | {
      [header: string]: string | string[];
    },
    observe: 'events',
    params?: HttpParams | {
      [param: string]: string | string[];
    },
    reportProgress?: boolean,
    responseType: 'text',
    withCredentials?: boolean
  } {
    this.setDocumentHeaders(name);

    return {
      headers: this.headers,
      observe: 'events',
      reportProgress: true,
      responseType: 'text'
    };
  }
}
