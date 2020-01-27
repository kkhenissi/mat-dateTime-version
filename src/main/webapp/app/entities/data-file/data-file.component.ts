import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { IDataFile } from 'app/shared/model/data-file.model';
import { AccountService } from 'app/core/auth/account.service';
import { DataFileService } from '../../shared/services/data-file.service';

@Component({
  selector: 'jhi-data-file',
  templateUrl: './data-file.component.html'
})
export class DataFileComponent implements OnInit, OnDestroy {
  dataFiles: IDataFile[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected dataFileService: DataFileService,
    protected jhiAlertService: JhiAlertService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.dataFileService
      .query()
      .pipe(
        filter((res: HttpResponse<IDataFile[]>) => res.ok),
        map((res: HttpResponse<IDataFile[]>) => res.body)
      )
      .subscribe(
        (res: IDataFile[]) => {
          this.dataFiles = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInDataFiles();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IDataFile) {
    return item.id;
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  registerChangeInDataFiles() {
    this.eventSubscriber = this.eventManager.subscribe('dataFileListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
