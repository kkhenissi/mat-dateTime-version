import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { UserPreferences } from 'app/shared/model/user-preferences.model';
import { UserPreferencesService } from './user-preferences.service';
import { UserPreferencesComponent } from './user-preferences.component';
import { UserPreferencesDetailComponent } from './user-preferences-detail.component';
import { UserPreferencesUpdateComponent } from './user-preferences-update.component';
import { UserPreferencesDeletePopupComponent } from './user-preferences-delete-dialog.component';
import { IUserPreferences } from 'app/shared/model/user-preferences.model';

@Injectable({ providedIn: 'root' })
export class UserPreferencesResolve implements Resolve<IUserPreferences> {
  constructor(private service: UserPreferencesService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IUserPreferences> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<UserPreferences>) => response.ok),
        map((userPreferences: HttpResponse<UserPreferences>) => userPreferences.body)
      );
    }
    return of(new UserPreferences());
  }
}

export const userPreferencesRoute: Routes = [
  {
    path: '',
    component: UserPreferencesComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'UserPreferences'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: UserPreferencesDetailComponent,
    resolve: {
      userPreferences: UserPreferencesResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'UserPreferences'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: UserPreferencesUpdateComponent,
    resolve: {
      userPreferences: UserPreferencesResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'UserPreferences'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: UserPreferencesUpdateComponent,
    resolve: {
      userPreferences: UserPreferencesResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'UserPreferences'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const userPreferencesPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: UserPreferencesDeletePopupComponent,
    resolve: {
      userPreferences: UserPreferencesResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'UserPreferences'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
