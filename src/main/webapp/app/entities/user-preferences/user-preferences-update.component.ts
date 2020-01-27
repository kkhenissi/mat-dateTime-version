import { Component, OnInit } from '@angular/core';

import { HttpResponse, HttpErrorResponse } from '@angular/common/http';

import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IUserPreferences, UserPreferences } from 'app/shared/model/user-preferences.model';
import { UserPreferencesService } from './user-preferences.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';

@Component({
  selector: 'jhi-user-preferences-update',
  templateUrl: './user-preferences-update.component.html'
})
export class UserPreferencesUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  editForm = this.fb.group({
    id: [],
    userId: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected userPreferencesService: UserPreferencesService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ userPreferences }) => {
      this.updateForm(userPreferences);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(userPreferences: IUserPreferences) {
    this.editForm.patchValue({
      id: userPreferences.id,
      userId: userPreferences.userId
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const userPreferences = this.createFromForm();
    if (userPreferences.id !== undefined) {
      this.subscribeToSaveResponse(this.userPreferencesService.update(userPreferences));
    } else {
      this.subscribeToSaveResponse(this.userPreferencesService.create(userPreferences));
    }
  }

  private createFromForm(): IUserPreferences {
    return {
      ...new UserPreferences(),
      id: this.editForm.get(['id']).value,
      userId: this.editForm.get(['userId']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserPreferences>>) {
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
}
