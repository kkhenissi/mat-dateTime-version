import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { UserPreferencesComponent } from './user-preferences.component';
import { UserPreferencesDetailComponent } from './user-preferences-detail.component';
import { UserPreferencesUpdateComponent } from './user-preferences-update.component';
import { UserPreferencesDeletePopupComponent, UserPreferencesDeleteDialogComponent } from './user-preferences-delete-dialog.component';
import { userPreferencesRoute, userPreferencesPopupRoute } from './user-preferences.route';

const ENTITY_STATES = [...userPreferencesRoute, ...userPreferencesPopupRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    UserPreferencesComponent,
    UserPreferencesDetailComponent,
    UserPreferencesUpdateComponent,
    UserPreferencesDeleteDialogComponent,
    UserPreferencesDeletePopupComponent
  ],
  entryComponents: [UserPreferencesDeleteDialogComponent]
})
export class ArchivemanagerUserPreferencesModule {}
