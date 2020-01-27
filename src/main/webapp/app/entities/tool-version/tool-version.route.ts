import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ToolVersion } from 'app/shared/model/tool-version.model';
import { ToolVersionService } from './tool-version.service';
import { ToolVersionComponent } from './tool-version.component';
import { ToolVersionDetailComponent } from './tool-version-detail.component';
import { ToolVersionUpdateComponent } from './tool-version-update.component';
import { ToolVersionDeletePopupComponent } from './tool-version-delete-dialog.component';
import { IToolVersion } from 'app/shared/model/tool-version.model';

@Injectable({ providedIn: 'root' })
export class ToolVersionResolve implements Resolve<IToolVersion> {
  constructor(private service: ToolVersionService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IToolVersion> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<ToolVersion>) => response.ok),
        map((toolVersion: HttpResponse<ToolVersion>) => toolVersion.body)
      );
    }
    return of(new ToolVersion());
  }
}

export const toolVersionRoute: Routes = [
  {
    path: '',
    component: ToolVersionComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'ToolVersions'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ToolVersionDetailComponent,
    resolve: {
      toolVersion: ToolVersionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ToolVersions'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ToolVersionUpdateComponent,
    resolve: {
      toolVersion: ToolVersionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ToolVersions'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ToolVersionUpdateComponent,
    resolve: {
      toolVersion: ToolVersionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ToolVersions'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const toolVersionPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ToolVersionDeletePopupComponent,
    resolve: {
      toolVersion: ToolVersionResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'ToolVersions'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
