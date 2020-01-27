import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IOutputFile } from 'app/shared/model/output-file.model';

type EntityResponseType = HttpResponse<IOutputFile>;
type EntityArrayResponseType = HttpResponse<IOutputFile[]>;

@Injectable({ providedIn: 'root' })
export class OutputFileService {
  public resourceUrl = SERVER_API_URL + 'api/output-files';

  constructor(protected http: HttpClient) {}

  create(outputFile: IOutputFile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(outputFile);
    return this.http
      .post<IOutputFile>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(outputFile: IOutputFile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(outputFile);
    return this.http
      .put<IOutputFile>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IOutputFile>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOutputFile[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(outputFile: IOutputFile): IOutputFile {
    const copy: IOutputFile = Object.assign({}, outputFile, {
      lTInsertionDate:
        outputFile.lTInsertionDate != null && outputFile.lTInsertionDate.isValid() ? outputFile.lTInsertionDate.format(DATE_FORMAT) : null,
      timeOfData: outputFile.timeOfData != null && outputFile.timeOfData.isValid() ? outputFile.timeOfData.format(DATE_FORMAT) : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.lTInsertionDate = res.body.lTInsertionDate != null ? moment(res.body.lTInsertionDate) : null;
      res.body.timeOfData = res.body.timeOfData != null ? moment(res.body.timeOfData) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((outputFile: IOutputFile) => {
        outputFile.lTInsertionDate = outputFile.lTInsertionDate != null ? moment(outputFile.lTInsertionDate) : null;
        outputFile.timeOfData = outputFile.timeOfData != null ? moment(outputFile.timeOfData) : null;
      });
    }
    return res;
  }
}
