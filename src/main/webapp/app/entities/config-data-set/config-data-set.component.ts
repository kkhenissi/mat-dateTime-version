import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IConfigDataSet } from 'app/shared/model/config-data-set.model';
import { AccountService } from 'app/core/auth/account.service';
import { ConfigDataSetService } from '../../shared/services/config-data-set.service';

@Component({
  selector: 'jhi-config-data-set',
  templateUrl: './config-data-set.component.html'
})
export class ConfigDataSetComponent implements OnInit, OnDestroy {
  configDataSets: IConfigDataSet[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected configDataSetService: ConfigDataSetService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.configDataSetService
      .query()
      .pipe(
        filter((res: HttpResponse<IConfigDataSet[]>) => res.ok),
        map((res: HttpResponse<IConfigDataSet[]>) => res.body)
      )
      .subscribe(
        (res: IConfigDataSet[]) => {
          this.configDataSets = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInConfigDataSets();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IConfigDataSet) {
    return item.id;
  }

  registerChangeInConfigDataSets() {
    this.eventSubscriber = this.eventManager.subscribe('configDataSetListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
