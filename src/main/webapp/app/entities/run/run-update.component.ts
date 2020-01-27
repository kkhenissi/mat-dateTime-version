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
import { IRun, Run } from 'app/shared/model/run.model';
import { RunService } from './run.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IScenario } from 'app/shared/model/scenario.model';
import { ScenarioService } from 'app/shared/services/scenario.service';

@Component({
  selector: 'jhi-run-update',
  templateUrl: './run-update.component.html'
})
export class RunUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  scenarios: IScenario[];
  startDateDp: any;
  endDateDp: any;

  editForm = this.fb.group({
    id: [],
    startDate: [null, [Validators.required]],
    endDate: [],
    status: [],
    platformHardwareInfo: [],
    description: [],
    ownerId: [],
    scenarioId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected runService: RunService,
    protected userService: UserService,
    protected scenarioService: ScenarioService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ run }) => {
      this.updateForm(run);
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
  }

  updateForm(run: IRun) {
    this.editForm.patchValue({
      id: run.id,
      startDate: run.startDate,
      endDate: run.endDate,
      status: run.status,
      platformHardwareInfo: run.platformHardwareInfo,
      description: run.description,
      ownerId: run.ownerId,
      scenarioId: run.scenarioId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const run = this.createFromForm();
    if (run.id != undefined) {
      this.subscribeToSaveResponse(this.runService.update(run));
    } else {
      this.subscribeToSaveResponse(this.runService.create(run));
    }
  }

  private createFromForm(): IRun {
    return {
      ...new Run(),
      id: this.editForm.get(['id']).value,
      startDate: this.editForm.get(['startDate']).value,
      endDate: this.editForm.get(['endDate']).value,
      status: this.editForm.get(['status']).value,
      platformHardwareInfo: this.editForm.get(['platformHardwareInfo']).value,
      description: this.editForm.get(['description']).value,
      ownerId: this.editForm.get(['ownerId']).value,
      scenarioId: this.editForm.get(['scenarioId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IRun>>) {
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
}
