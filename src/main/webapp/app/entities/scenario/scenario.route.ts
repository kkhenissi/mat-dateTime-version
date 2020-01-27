import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Scenario } from 'app/shared/model/scenario.model';
import { ScenarioService } from '../../shared/services/scenario.service';
import { ScenarioComponent } from './scenario.component';
import { ScenarioDetailComponent } from './scenario-detail.component';
import { ScenarioUpdateComponent } from './scenario-update.component';
import { ScenarioDeletePopupComponent } from './scenario-delete-dialog.component';
import { IScenario } from 'app/shared/model/scenario.model';
@Injectable({ providedIn: 'root' })
export class ScenarioResolve implements Resolve<IScenario> {
  constructor(private service: ScenarioService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IScenario> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Scenario>) => response.ok),
        map((scenario: HttpResponse<Scenario>) => scenario.body)
      );
    }
    return of(new Scenario());
  }
}

export const scenarioRoute: Routes = [
  {
    path: '',
    component: ScenarioComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Scenarios'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ScenarioDetailComponent,
    resolve: {
      scenario: ScenarioResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Scenarios'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ScenarioUpdateComponent,
    resolve: {
      scenario: ScenarioResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Scenarios'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ScenarioUpdateComponent,
    resolve: {
      scenario: ScenarioResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Scenarios'
    },
    canActivate: [UserRouteAccessService]
  }
];
