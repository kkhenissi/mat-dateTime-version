import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IDataFile, DataFile } from 'app/shared/model/data-file.model';
import { DataFileService } from '../../shared/services/data-file.service';

@Component({
  selector: 'jhi-data-file-update',
  templateUrl: './data-file-update.component.html'
})
export class DataFileUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    pathInLT: [],
    raw: [],
    rawContentType: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected dataFileService: DataFileService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ dataFile }) => {
      this.updateForm(dataFile);
    });
  }

  updateForm(dataFile: IDataFile) {
    this.editForm.patchValue({
      id: dataFile.id,
      pathInLT: dataFile.pathInLT,
      raw: dataFile.raw,
      rawContentType: dataFile.rawContentType
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file: File = event.target.files[0];
        if (isImage && !file.type.startsWith('image/')) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      // eslint-disable-next-line no-console
      () => console.log('blob added'), // success
      this.onError
    );
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const dataFile = this.createFromForm();
    if (dataFile.id != undefined) {
      this.subscribeToSaveResponse(this.dataFileService.update(dataFile));
    } else {
      this.subscribeToSaveResponse(this.dataFileService.create(dataFile));
    }
  }

  private createFromForm(): IDataFile {
    return {
      ...new DataFile(),
      id: this.editForm.get(['id']).value,
      pathInLT: this.editForm.get(['pathInLT']).value,
      rawContentType: this.editForm.get(['rawContentType']).value,
      raw: this.editForm.get(['raw']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDataFile>>) {
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
}
