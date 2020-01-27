import { Component, OnInit } from '@angular/core';

import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IConfigDataSet, ConfigDataSet } from 'app/shared/model/config-data-set.model';
import { ConfigDataSetService } from '../../shared/services/config-data-set.service';

@Component({
  selector: 'jhi-config-data-set-update',
  templateUrl: './config-data-set-update.component.html'
})
export class ConfigDataSetUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]]
  });

  constructor(protected configDataSetService: ConfigDataSetService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ configDataSet }) => {
      this.updateForm(configDataSet);
    });
  }

  updateForm(configDataSet: IConfigDataSet) {
    this.editForm.patchValue({
      id: configDataSet.id,
      name: configDataSet.name
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const configDataSet = this.createFromForm();
    if (configDataSet.id !== undefined) {
      this.subscribeToSaveResponse(this.configDataSetService.update(configDataSet));
    } else {
      this.subscribeToSaveResponse(this.configDataSetService.create(configDataSet));
    }
  }

  private createFromForm(): IConfigDataSet {
    return {
      ...new ConfigDataSet(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IConfigDataSet>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
