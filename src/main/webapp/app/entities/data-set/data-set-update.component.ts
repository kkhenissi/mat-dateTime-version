import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IDataSet, DataSet } from 'app/shared/model/data-set.model';
import { DataSetService } from '../../shared/services/data-set.service';

@Component({
  selector: 'jhi-data-set-update',
  templateUrl: './data-set-update.component.html'
})
export class DataSetUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]]
  });

  constructor(protected dataSetService: DataSetService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ dataSet }) => {
      this.updateForm(dataSet);
    });
  }

  updateForm(dataSet: IDataSet) {
    this.editForm.patchValue({
      id: dataSet.id,
      name: dataSet.name
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const dataSet = this.createFromForm();
    if (dataSet.id != undefined) {
      this.subscribeToSaveResponse(this.dataSetService.update(dataSet));
    } else {
      this.subscribeToSaveResponse(this.dataSetService.create(dataSet));
    }
  }

  private createFromForm(): IDataSet {
    return {
      ...new DataSet(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDataSet>>) {
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
