import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT, DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IScenario } from 'app/shared/model/scenario.model';
import { IScenarioFile } from '../model/scenario-file.model';

type EntityResponseType = HttpResponse<IScenario>;
type EntityArrayResponseType = HttpResponse<IScenario[]>;

@Injectable({ providedIn: 'root' })
export class ScenarioService {
  public resourceUrl = SERVER_API_URL + 'api/scenarios';

  constructor(protected http: HttpClient) {}

  create(scenario: IScenario): Observable<EntityResponseType> {
    console.log('yes  from hirewhat is in ', this.resourceUrl);
    const result = this.http.post<IScenario>(this.resourceUrl, scenario, { observe: 'response' })
             .pipe(map((res: EntityResponseType) => res),
             );
             console.log('what is in result', result)
             return result;
  }






  update(scenario: IScenario): Observable<EntityResponseType> {
    //const copy = this.convertDateFromClient(scenario);
    return this.http.put<IScenario>(this.resourceUrl, scenario, { observe: 'response' }).pipe(map((res: EntityResponseType) => res));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IScenario>(`${this.resourceUrl}/${id}`, { observe: 'response' }).pipe(map((res: EntityResponseType) => res));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    console.log('what is in req from modalSearche ====>', req)
    const options = createRequestOption(req);
    console.log('what is in option from modalSearche ====>', options)
    return this.http
      .get<IScenario[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)))
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((scenarioFile: IScenarioFile) => {
        scenarioFile.lTInsertionDate = scenarioFile.lTInsertionDate != null ? moment(scenarioFile.lTInsertionDate) : null;
        scenarioFile.timeOfData = scenarioFile.timeOfData != null ? moment(scenarioFile.timeOfData) : null;
      });
    }
    console.log('what in res hire  de==>', res)
    return res;
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}


// return this.http
// .get<ScenarioFile[]>(this.resourceUrl, { params: options, observe: 'response' })
// .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)))
