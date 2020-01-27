import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Run } from 'app/shared/model/run.model';
import { RunService } from './run.service';
import { RunComponent } from './run.component';
import { RunDetailComponent } from './run-detail.component';
import { RunUpdateComponent } from './run-update.component';
import { RunDeletePopupComponent } from './run-delete-dialog.component';
import { IRun } from 'app/shared/model/run.model';

@Injectable({ providedIn: 'root' })
export class RunResolve implements Resolve<IRun> {
  constructor(private service: RunService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IRun> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Run>) => response.ok),
        map((run: HttpResponse<Run>) => run.body)
      );
    }
    return of(new Run());
  }
}

export const runRoute: Routes = [
  {
    path: '',
    component: RunComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Runs'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: RunDetailComponent,
    resolve: {
      run: RunResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Runs'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: RunUpdateComponent,
    resolve: {
      run: RunResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Runs'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: RunUpdateComponent,
    resolve: {
      run: RunResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Runs'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const runPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: RunDeletePopupComponent,
    resolve: {
      run: RunResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Runs'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
