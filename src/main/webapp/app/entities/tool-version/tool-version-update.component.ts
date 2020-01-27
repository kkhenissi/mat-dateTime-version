import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IToolVersion, ToolVersion } from 'app/shared/model/tool-version.model';
import { ToolVersionService } from './tool-version.service';
import { IRun } from 'app/shared/model/run.model';
import { RunService } from 'app/entities/run/run.service';

@Component({
  selector: 'jhi-tool-version-update',
  templateUrl: './tool-version-update.component.html'
})
export class ToolVersionUpdateComponent implements OnInit {
  isSaving: boolean;

  runs: IRun[];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    version: [null, [Validators.required]],
    runId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected toolVersionService: ToolVersionService,
    protected runService: RunService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ toolVersion }) => {
      this.updateForm(toolVersion);
    });
    this.runService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IRun[]>) => mayBeOk.ok),
        map((response: HttpResponse<IRun[]>) => response.body)
      )
      .subscribe((res: IRun[]) => (this.runs = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(toolVersion: IToolVersion) {
    this.editForm.patchValue({
      id: toolVersion.id,
      name: toolVersion.name,
      version: toolVersion.version,
      runId: toolVersion.runId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const toolVersion = this.createFromForm();
    if (toolVersion.id != undefined) {
      this.subscribeToSaveResponse(this.toolVersionService.update(toolVersion));
    } else {
      this.subscribeToSaveResponse(this.toolVersionService.create(toolVersion));
    }
  }

  private createFromForm(): IToolVersion {
    return {
      ...new ToolVersion(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      version: this.editForm.get(['version']).value,
      runId: this.editForm.get(['runId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IToolVersion>>) {
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

  trackRunById(index: number, item: IRun) {
    return item.id;
  }
}
