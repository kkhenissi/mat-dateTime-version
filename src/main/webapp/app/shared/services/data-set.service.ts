import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IDataSet } from 'app/shared/model/data-set.model';

type EntityResponseType = HttpResponse<IDataSet>;
type EntityArrayResponseType = HttpResponse<IDataSet[]>;

@Injectable({ providedIn: 'root' })
export class DataSetService {
  public resourceUrl = SERVER_API_URL + 'api/data-sets';

  constructor(protected http: HttpClient) {}

  create(dataSet: IDataSet): Observable<EntityResponseType> {
    return this.http.post<IDataSet>(this.resourceUrl, dataSet, { observe: 'response' });
  }

  update(dataSet: IDataSet): Observable<EntityResponseType> {
    return this.http.put<IDataSet>(this.resourceUrl, dataSet, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDataSet>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDataSet[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
