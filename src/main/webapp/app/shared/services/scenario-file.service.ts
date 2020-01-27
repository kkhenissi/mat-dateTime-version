import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpParams } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IScenarioFile, ScenarioFile } from 'app/shared/model/scenario-file.model';

type EntityResponseType = HttpResponse<IScenarioFile>;
type EntityArrayResponseType = HttpResponse<ScenarioFile[]>;


@Injectable({ providedIn: 'root' })
export class ScenarioFileService {
  public resourceUrl = SERVER_API_URL + 'api/scenario-files';
  public scenariosFile = new BehaviorSubject<ScenarioFile[]>([]);

  constructor(protected http: HttpClient) {}

  create(scenarioFile: IScenarioFile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(scenarioFile);
    return this.http
      .post<IScenarioFile>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(scenarioFile: IScenarioFile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(scenarioFile);
    return this.http
      .put<IScenarioFile>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: string): Observable<EntityResponseType> {
    return this.http
      .get<IScenarioFile>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    
    
    const options = createRequestOption(req);
     
    return this.http
      .get<IScenarioFile[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }
 
  totalItems(req): Observable<HttpResponse<any>> {
    const options = createRequestOption(req);
    return this.http.get (this.resourceUrl + '/count', {params: options, observe: 'response'
      }) 
  }


    
  findScenarioFiles(req?: any,sort= "asc",page=0, size=5): Observable<any> {
 
       req = {...req, sort}
       req = {...req,page}
       req=  {...req, size}
       console.log('what is in req from modalSearche ====>', req)
       const options = createRequestOption(req);
       console.log('what is in option from search scenarioComponent ====>', options)
         return this.http
      .get<ScenarioFile[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
      }


  delete(id: string): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(scenarioFile: IScenarioFile): IScenarioFile {
    const copy: IScenarioFile = Object.assign({}, scenarioFile, {
      lTInsertionDate:
        scenarioFile.lTInsertionDate != null && scenarioFile.lTInsertionDate.isValid()
          ? scenarioFile.lTInsertionDate.format(DATE_FORMAT)
          : null,
      timeOfData: scenarioFile.timeOfData != null && scenarioFile.timeOfData.isValid() ? scenarioFile.timeOfData.format(DATE_FORMAT) : null
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
      res.body.forEach((scenarioFile: IScenarioFile) => {
        scenarioFile.lTInsertionDate = scenarioFile.lTInsertionDate != null ? moment(scenarioFile.lTInsertionDate) : null;
        scenarioFile.timeOfData = scenarioFile.timeOfData != null ? moment(scenarioFile.timeOfData) : null;
      });
    }
    console.log('what in res hire  de==>', res)
    return res;
  }
}
