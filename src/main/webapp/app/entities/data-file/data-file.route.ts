import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { DataFile } from 'app/shared/model/data-file.model';
import { DataFileService } from '../../shared/services/data-file.service';
import { DataFileComponent } from './data-file.component';
import { DataFileDetailComponent } from './data-file-detail.component';
import { DataFileUpdateComponent } from './data-file-update.component';
import { DataFileDeletePopupComponent } from './data-file-delete-dialog.component';
import { IDataFile } from 'app/shared/model/data-file.model';

@Injectable({ providedIn: 'root' })
export class DataFileResolve implements Resolve<IDataFile> {
  constructor(private service: DataFileService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IDataFile> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<DataFile>) => response.ok),
        map((dataFile: HttpResponse<DataFile>) => dataFile.body)
      );
    }
    return of(new DataFile());
  }
}

export const dataFileRoute: Routes = [
  {
    path: '',
    component: DataFileComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'DataFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: DataFileDetailComponent,
    resolve: {
      dataFile: DataFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'DataFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: DataFileUpdateComponent,
    resolve: {
      dataFile: DataFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'DataFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: DataFileUpdateComponent,
    resolve: {
      dataFile: DataFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'DataFiles'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const dataFilePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: DataFileDeletePopupComponent,
    resolve: {
      dataFile: DataFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'DataFiles'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
