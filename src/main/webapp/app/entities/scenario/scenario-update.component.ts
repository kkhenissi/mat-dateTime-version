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
import { IScenario, Scenario } from 'app/shared/model/scenario.model';
import { ScenarioService } from '../../shared/services/scenario.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IScenarioFile } from 'app/shared/model/scenario-file.model';
import { ScenarioFileService } from 'app/shared/services/scenario-file.service';

@Component({
  selector: 'jhi-scenario-update',
  templateUrl: './scenario-update.component.html'
})
export class ScenarioUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  scenariofiles: IScenarioFile[];
  creationDateDp: any;
  startSimulatedDateDp: any;
  simulationDp: any;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    creationDate: [],
    simulationMode: [],
    startSimulatedDate: [],
    simulation: [],
    description: [],
    ownerId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected scenarioService: ScenarioService,
    protected userService: UserService,
    protected scenarioFileService: ScenarioFileService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ scenario }) => {
      this.updateForm(scenario);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.scenarioFileService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IScenarioFile[]>) => mayBeOk.ok),
        map((response: HttpResponse<IScenarioFile[]>) => response.body)
      )
      .subscribe((res: IScenarioFile[]) => (this.scenariofiles = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(scenario: IScenario) {
    this.editForm.patchValue({
      id: scenario.id,
      name: scenario.name,
      creationDate: scenario.creationDate,
      simulationMode: scenario.simulationMode,
      startSimulatedDate: scenario.startSimulatedDate,
     // simulation: scenario.simulation,
      description: scenario.description,
      ownerId: scenario.ownerId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const scenario = this.createFromForm();
    if (scenario.id !== undefined) {
      this.subscribeToSaveResponse(this.scenarioService.update(scenario));
    } else {
      this.subscribeToSaveResponse(this.scenarioService.create(scenario));
    }
  }

  private createFromForm(): IScenario {
    return {
      ...new Scenario(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      creationDate: this.editForm.get(['creationDate']).value,
      simulationMode: this.editForm.get(['simulationMode']).value,
      startSimulatedDate: this.editForm.get(['startSimulatedDate']).value,
     // simulation: this.editForm.get(['simulation']).value,
      description: this.editForm.get(['description']).value,
      ownerId: this.editForm.get(['ownerId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IScenario>>) {
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

  trackScenarioFileById(index: number, item: IScenarioFile) {
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
