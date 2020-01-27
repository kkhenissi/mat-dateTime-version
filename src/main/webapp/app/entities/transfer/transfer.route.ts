import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Transfer } from 'app/shared/model/transfer.model';
import { TransferService } from './transfer.service';
import { TransferComponent } from './transfer.component';
import { TransferDetailComponent } from './transfer-detail.component';
import { TransferUpdateComponent } from './transfer-update.component';
import { TransferDeletePopupComponent } from './transfer-delete-dialog.component';
import { ITransfer } from 'app/shared/model/transfer.model';

@Injectable({ providedIn: 'root' })
export class TransferResolve implements Resolve<ITransfer> {
  constructor(private service: TransferService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ITransfer> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Transfer>) => response.ok),
        map((transfer: HttpResponse<Transfer>) => transfer.body)
      );
    }
    return of(new Transfer());
  }
}

export const transferRoute: Routes = [
  {
    path: '',
    component: TransferComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'Transfers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: TransferDetailComponent,
    resolve: {
      transfer: TransferResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Transfers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: TransferUpdateComponent,
    resolve: {
      transfer: TransferResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Transfers'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: TransferUpdateComponent,
    resolve: {
      transfer: TransferResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Transfers'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const transferPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: TransferDeletePopupComponent,
    resolve: {
      transfer: TransferResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'Transfers'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
