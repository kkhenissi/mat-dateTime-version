import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ScenarioFile } from 'app/shared/model/scenario-file.model';
import { ScenarioFileService } from '../../shared/services/scenario-file.service';
import { ScenarioFileComponent } from './scenario-file.component';
import { ScenarioFileDetailComponent } from './scenario-file-detail.component';
import { ScenarioFileUpdateComponent } from './scenario-file-update.component';
import { IScenarioFile } from 'app/shared/model/scenario-file.model';

@Injectable({ providedIn: 'root' })
export class ScenarioFileResolve implements Resolve<IScenarioFile> {
  constructor(private service: ScenarioFileService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IScenarioFile> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<ScenarioFile>) => response.ok),
        map((scenarioFile: HttpResponse<ScenarioFile>) => scenarioFile.body)
      );
    }
    return of(new ScenarioFile());
  }
}

export const scenarioFileRoute: Routes = [
  {
    path: '',
    component: ScenarioFileComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'ScenarioFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ScenarioFileDetailComponent,
    resolve: {
      scenarioFile: ScenarioFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ScenarioFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ScenarioFileUpdateComponent,
    resolve: {
      scenarioFile: ScenarioFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ScenarioFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ScenarioFileUpdateComponent,
    resolve: {
      scenarioFile: ScenarioFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ScenarioFiles'
    },
    canActivate: [UserRouteAccessService]
  }
];
