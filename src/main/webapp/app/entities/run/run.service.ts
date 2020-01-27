import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IRun } from 'app/shared/model/run.model';

type EntityResponseType = HttpResponse<IRun>;
type EntityArrayResponseType = HttpResponse<IRun[]>;

@Injectable({ providedIn: 'root' })
export class RunService {
  public resourceUrl = SERVER_API_URL + 'api/runs';

  constructor(protected http: HttpClient) {}

  create(run: IRun): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(run);
    return this.http
      .post<IRun>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(run: IRun): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(run);
    return this.http
      .put<IRun>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IRun>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IRun[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(run: IRun): IRun {
    const copy: IRun = Object.assign({}, run, {
      startDate: run.startDate != null && run.startDate.isValid() ? run.startDate.format(DATE_FORMAT) : null,
      endDate: run.endDate != null && run.endDate.isValid() ? run.endDate.format(DATE_FORMAT) : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startDate = res.body.startDate != null ? moment(res.body.startDate) : null;
      res.body.endDate = res.body.endDate != null ? moment(res.body.endDate) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((run: IRun) => {
        run.startDate = run.startDate != null ? moment(run.startDate) : null;
        run.endDate = run.endDate != null ? moment(run.endDate) : null;
      });
    }
    return res;
  }
}
