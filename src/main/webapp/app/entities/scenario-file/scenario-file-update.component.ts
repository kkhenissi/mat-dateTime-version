import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';
import { IScenarioFile, ScenarioFile } from 'app/shared/model/scenario-file.model';
import { ScenarioFileService } from '../../shared/services/scenario-file.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IScenario } from 'app/shared/model/scenario.model';
import { ScenarioService } from 'app/shared/services/scenario.service';
import { ITransfer } from 'app/shared/model/transfer.model';
import { TransferService } from 'app/entities/transfer/transfer.service';
import { IDataSet } from 'app/shared/model/data-set.model';
import { DataSetService } from 'app/shared/services/data-set.service';
import { IConfigDataSet } from 'app/shared/model/config-data-set.model';
import { ConfigDataSetService } from 'app/shared/services/config-data-set.service';

@Component({
  selector: 'jhi-scenario-file-update',
  templateUrl: './scenario-file-update.component.html'
})
export class ScenarioFileUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  scenarios: IScenario[];

  transfers: ITransfer[];

  datasets: IDataSet[];

  configdatasets: IConfigDataSet[];
  lTInsertionDateDp: any;
  timeOfDataDp: any;

  editForm = this.fb.group({
    id: [],
    fileType: [],
    relativePathInST: [null, [Validators.required]],
    lTInsertionDate: [],
    pathInLT: [],
    format: [],
    subSystemAtOriginOfData: [],
    timeOfData: [],
    securityLevel: [],
    crc: [],
    ownerId: [],
    scenarios: [],
    jobId: [],
    datasetId: [],
    configDatasetId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected scenarioFileService: ScenarioFileService,
    protected userService: UserService,
    protected scenarioService: ScenarioService,
    protected transferService: TransferService,
    protected dataSetService: DataSetService,
    protected configDataSetService: ConfigDataSetService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ scenarioFile }) => {
      this.updateForm(scenarioFile);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.scenarioService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IScenario[]>) => mayBeOk.ok),
        map((response: HttpResponse<IScenario[]>) => response.body)
      )
      .subscribe((res: IScenario[]) => (this.scenarios = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.transferService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<ITransfer[]>) => mayBeOk.ok),
        map((response: HttpResponse<ITransfer[]>) => response.body)
      )
      .subscribe((res: ITransfer[]) => (this.transfers = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.dataSetService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IDataSet[]>) => mayBeOk.ok),
        map((response: HttpResponse<IDataSet[]>) => response.body)
      )
      .subscribe((res: IDataSet[]) => (this.datasets = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.configDataSetService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IConfigDataSet[]>) => mayBeOk.ok),
        map((response: HttpResponse<IConfigDataSet[]>) => response.body)
      )
      .subscribe((res: IConfigDataSet[]) => (this.configdatasets = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(scenarioFile: IScenarioFile) {
    this.editForm.patchValue({
      fileType: scenarioFile.fileType,
      relativePathInST: scenarioFile.relativePathInST,
      lTInsertionDate: scenarioFile.lTInsertionDate,
      pathInLT: scenarioFile.pathInLT,
      format: scenarioFile.format,
      subSystemAtOriginOfData: scenarioFile.subSystemAtOriginOfData,
      timeOfData: scenarioFile.timeOfData,
      securityLevel: scenarioFile.securityLevel,
      crc: scenarioFile.crc,
      ownerId: scenarioFile.ownerId,
      scenarios: scenarioFile.scenarios,
      jobId: scenarioFile.jobId,
      datasetId: scenarioFile.datasetId,
      configDatasetId: scenarioFile.configDatasetId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const scenarioFile = this.createFromForm();
    if (scenarioFile.relativePathInST != undefined) {
      this.subscribeToSaveResponse(this.scenarioFileService.update(scenarioFile));
    } else {
      this.subscribeToSaveResponse(this.scenarioFileService.create(scenarioFile));
    }
  }

  private createFromForm(): IScenarioFile {
    return {
      ...new ScenarioFile(),
      fileType: this.editForm.get(['fileType']).value,
      relativePathInST: this.editForm.get(['relativePathInST']).value,
      lTInsertionDate: this.editForm.get(['lTInsertionDate']).value,
      pathInLT: this.editForm.get(['pathInLT']).value,
      format: this.editForm.get(['format']).value,
      subSystemAtOriginOfData: this.editForm.get(['subSystemAtOriginOfData']).value,
      timeOfData: this.editForm.get(['timeOfData']).value,
      securityLevel: this.editForm.get(['securityLevel']).value,
      crc: this.editForm.get(['crc']).value,
      ownerId: this.editForm.get(['ownerId']).value,
      scenarios: this.editForm.get(['scenarios']).value,
      jobId: this.editForm.get(['jobId']).value,
      datasetId: this.editForm.get(['datasetId']).value,
      configDatasetId: this.editForm.get(['configDatasetId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IScenarioFile>>) {
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

  trackScenarioById(index: number, item: IScenario) {
    return item.id;
  }

  trackTransferById(index: number, item: ITransfer) {
    return item.id;
  }

  trackDataSetById(index: number, item: IDataSet) {
    return item.id;
  }

  trackConfigDataSetById(index: number, item: IConfigDataSet) {
    return item.id;
  }

  getSelected(selectedVals: any[], option: any) {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
