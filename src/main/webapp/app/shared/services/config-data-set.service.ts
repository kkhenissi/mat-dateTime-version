import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IConfigDataSet } from 'app/shared/model/config-data-set.model';
import { map } from 'rxjs/operators';

type EntityResponseType = HttpResponse<IConfigDataSet>;
type EntityArrayResponseType = HttpResponse<IConfigDataSet[]>;

@Injectable({ providedIn: 'root' })
export class ConfigDataSetService {
  public resourceUrl = SERVER_API_URL + 'api/config-data-sets';

  constructor(protected http: HttpClient) {}

  create(configDataSet: IConfigDataSet): Observable<EntityResponseType> {
    return this.http.post<IConfigDataSet>(this.resourceUrl, configDataSet, { observe: 'response' });
  }

  update(configDataSet: IConfigDataSet): Observable<EntityResponseType> {
    return this.http.put<IConfigDataSet>(this.resourceUrl, configDataSet, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IConfigDataSet>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IConfigDataSet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => res));
    }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

 
}
