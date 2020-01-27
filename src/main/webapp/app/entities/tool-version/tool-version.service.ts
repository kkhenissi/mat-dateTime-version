import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IToolVersion } from 'app/shared/model/tool-version.model';

type EntityResponseType = HttpResponse<IToolVersion>;
type EntityArrayResponseType = HttpResponse<IToolVersion[]>;

@Injectable({ providedIn: 'root' })
export class ToolVersionService {
  public resourceUrl = SERVER_API_URL + 'api/tool-versions';

  constructor(protected http: HttpClient) {}

  create(toolVersion: IToolVersion): Observable<EntityResponseType> {
    return this.http.post<IToolVersion>(this.resourceUrl, toolVersion, { observe: 'response' });
  }

  update(toolVersion: IToolVersion): Observable<EntityResponseType> {
    return this.http.put<IToolVersion>(this.resourceUrl, toolVersion, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IToolVersion>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IToolVersion[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
