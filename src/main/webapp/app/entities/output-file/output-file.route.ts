import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { OutputFile } from 'app/shared/model/output-file.model';
import { OutputFileService } from '../../shared/services/output-file.service';
import { OutputFileComponent } from './output-file.component';
import { OutputFileDetailComponent } from './output-file-detail.component';
import { OutputFileUpdateComponent } from './output-file-update.component';
import { OutputFileDeletePopupComponent } from './output-file-delete-dialog.component';
import { IOutputFile } from 'app/shared/model/output-file.model';

@Injectable({ providedIn: 'root' })
export class OutputFileResolve implements Resolve<IOutputFile> {
  constructor(private service: OutputFileService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IOutputFile> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<OutputFile>) => response.ok),
        map((outputFile: HttpResponse<OutputFile>) => outputFile.body)
      );
    }
    return of(new OutputFile());
  }
}

export const outputFileRoute: Routes = [
  {
    path: '',
    component: OutputFileComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'OutputFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: OutputFileDetailComponent,
    resolve: {
      outputFile: OutputFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'OutputFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: OutputFileUpdateComponent,
    resolve: {
      outputFile: OutputFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'OutputFiles'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: OutputFileUpdateComponent,
    resolve: {
      outputFile: OutputFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'OutputFiles'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const outputFilePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: OutputFileDeletePopupComponent,
    resolve: {
      outputFile: OutputFileResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'OutputFiles'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
