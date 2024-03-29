import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IDataFile } from 'app/shared/model/data-file.model';

type EntityResponseType = HttpResponse<IDataFile>;
type EntityArrayResponseType = HttpResponse<IDataFile[]>;

@Injectable({ providedIn: 'root' })
export class DataFileService {
  public resourceUrl = SERVER_API_URL + 'api/data-files';

  constructor(protected http: HttpClient) {}

  create(dataFile: IDataFile): Observable<EntityResponseType> {
    return this.http.post<IDataFile>(this.resourceUrl, dataFile, { observe: 'response' });
  }

  update(dataFile: IDataFile): Observable<EntityResponseType> {
    return this.http.put<IDataFile>(this.resourceUrl, dataFile, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDataFile>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDataFile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
