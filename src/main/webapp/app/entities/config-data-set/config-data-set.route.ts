import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ConfigDataSet } from 'app/shared/model/config-data-set.model';
import { ConfigDataSetService } from '../../shared/services/config-data-set.service';
import { ConfigDataSetComponent } from './config-data-set.component';
import { ConfigDataSetDetailComponent } from './config-data-set-detail.component';
import { ConfigDataSetUpdateComponent } from './config-data-set-update.component';
import { ConfigDataSetDeletePopupComponent } from './config-data-set-delete-dialog.component';
import { IConfigDataSet } from 'app/shared/model/config-data-set.model';

@Injectable({ providedIn: 'root' })
export class ConfigDataSetResolve implements Resolve<IConfigDataSet> {
  constructor(private service: ConfigDataSetService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IConfigDataSet> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<ConfigDataSet>) => response.ok),
        map((configDataSet: HttpResponse<ConfigDataSet>) => configDataSet.body)
      );
    }
    return of(new ConfigDataSet());
  }
}

export const configDataSetRoute: Routes = [
  {
    path: '',
    component: ConfigDataSetComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ConfigDataSets'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ConfigDataSetDetailComponent,
    resolve: {
      configDataSet: ConfigDataSetResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ConfigDataSets'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ConfigDataSetUpdateComponent,
    resolve: {
      configDataSet: ConfigDataSetResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ConfigDataSets'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ConfigDataSetUpdateComponent,
    resolve: {
      configDataSet: ConfigDataSetResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ConfigDataSets'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const configDataSetPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ConfigDataSetDeletePopupComponent,
    resolve: {
      configDataSet: ConfigDataSetResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ConfigDataSets'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
