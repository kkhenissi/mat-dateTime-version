import { Component, OnInit } from '@angular/core';

import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';
import { IOutputFile, OutputFile } from 'app/shared/model/output-file.model';
import { OutputFileService } from '../../shared/services/output-file.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IRun } from 'app/shared/model/run.model';
import { RunService } from 'app/entities/run/run.service';
import { ITransfer } from 'app/shared/model/transfer.model';
import { TransferService } from 'app/entities/transfer/transfer.service';

@Component({
  selector: 'jhi-output-file-update',
  templateUrl: './output-file-update.component.html'
})
export class OutputFileUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  runs: IRun[];

  transfers: ITransfer[];
  lTInsertionDateDp: any;
  timeOfDataDp: any;

  editForm = this.fb.group({
    id: [],
    relativePathInST: [null, [Validators.required]],
    lTInsertionDate: [],
    pathInLT: [],
    fileType: [],
    format: [],
    subSystemAtOriginOfData: [],
    timeOfData: [],
    securityLevel: [],
    crc: [],
    ownerId: [],
    runId: [],
    jobId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected outputFileService: OutputFileService,
    protected userService: UserService,
    protected runService: RunService,
    protected transferService: TransferService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ outputFile }) => {
      this.updateForm(outputFile);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.runService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IRun[]>) => mayBeOk.ok),
        map((response: HttpResponse<IRun[]>) => response.body)
      )
      .subscribe((res: IRun[]) => (this.runs = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.transferService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<ITransfer[]>) => mayBeOk.ok),
        map((response: HttpResponse<ITransfer[]>) => response.body)
      )
      .subscribe((res: ITransfer[]) => (this.transfers = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(outputFile: IOutputFile) {
    this.editForm.patchValue({
      id: outputFile.id,
      relativePathInST: outputFile.relativePathInST,
      lTInsertionDate: outputFile.lTInsertionDate,
      pathInLT: outputFile.pathInLT,
      fileType: outputFile.fileType,
      format: outputFile.format,
      subSystemAtOriginOfData: outputFile.subSystemAtOriginOfData,
      timeOfData: outputFile.timeOfData,
      securityLevel: outputFile.securityLevel,
      crc: outputFile.crc,
      ownerId: outputFile.ownerId,
      runId: outputFile.runId,
      jobId: outputFile.jobId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const outputFile = this.createFromForm();
    if (outputFile.id !== undefined) {
      this.subscribeToSaveResponse(this.outputFileService.update(outputFile));
    } else {
      this.subscribeToSaveResponse(this.outputFileService.create(outputFile));
    }
  }

  private createFromForm(): IOutputFile {
    return {
      ...new OutputFile(),
      id: this.editForm.get(['id']).value,
      relativePathInST: this.editForm.get(['relativePathInST']).value,
      lTInsertionDate: this.editForm.get(['lTInsertionDate']).value,
      pathInLT: this.editForm.get(['pathInLT']).value,
      fileType: this.editForm.get(['fileType']).value,
      format: this.editForm.get(['format']).value,
      subSystemAtOriginOfData: this.editForm.get(['subSystemAtOriginOfData']).value,
      timeOfData: this.editForm.get(['timeOfData']).value,
      securityLevel: this.editForm.get(['securityLevel']).value,
      crc: this.editForm.get(['crc']).value,
      ownerId: this.editForm.get(['ownerId']).value,
      runId: this.editForm.get(['runId']).value,
      jobId: this.editForm.get(['jobId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOutputFile>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  trackRunById(index: number, item: IRun) {
    return item.id;
  }

  trackTransferById(index: number, item: ITransfer) {
    return item.id;
  }
}
