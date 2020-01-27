import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IDataSet } from 'app/shared/model/data-set.model';
import { AccountService } from 'app/core/auth/account.service';
import { DataSetService } from '../../shared/services/data-set.service';

@Component({
  selector: 'jhi-data-set',
  templateUrl: './data-set.component.html'
})
export class DataSetComponent implements OnInit, OnDestroy {
  dataSets: IDataSet[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected dataSetService: DataSetService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.dataSetService
      .query()
      .pipe(
        filter((res: HttpResponse<IDataSet[]>) => res.ok),
        map((res: HttpResponse<IDataSet[]>) => res.body)
      )
      .subscribe(
        (res: IDataSet[]) => {
          this.dataSets = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInDataSets();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IDataSet) {
    return item.id;
  }

  registerChangeInDataSets() {
    this.eventSubscriber = this.eventManager.subscribe('dataSetListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
