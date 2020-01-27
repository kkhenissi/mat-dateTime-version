import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IUserPreferences } from 'app/shared/model/user-preferences.model';
import { AccountService } from 'app/core/auth/account.service';
import { UserPreferencesService } from './user-preferences.service';

@Component({
  selector: 'jhi-user-preferences',
  templateUrl: './user-preferences.component.html'
})
export class UserPreferencesComponent implements OnInit, OnDestroy {
  userPreferences: IUserPreferences[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected userPreferencesService: UserPreferencesService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.userPreferencesService
      .query()
      .pipe(
        filter((res: HttpResponse<IUserPreferences[]>) => res.ok),
        map((res: HttpResponse<IUserPreferences[]>) => res.body)
      )
      .subscribe(
        (res: IUserPreferences[]) => {
          this.userPreferences = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInUserPreferences();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IUserPreferences) {
    return item.id;
  }

  registerChangeInUserPreferences() {
    this.eventSubscriber = this.eventManager.subscribe('userPreferencesListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
