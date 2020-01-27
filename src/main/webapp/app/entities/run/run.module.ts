import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { RunComponent } from './run.component';
import { RunDetailComponent } from './run-detail.component';
import { RunUpdateComponent } from './run-update.component';
import { RunDeletePopupComponent, RunDeleteDialogComponent } from './run-delete-dialog.component';
import { runRoute, runPopupRoute } from './run.route';

const ENTITY_STATES = [...runRoute, ...runPopupRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [RunComponent, RunDetailComponent, RunUpdateComponent, RunDeleteDialogComponent, RunDeletePopupComponent],
  entryComponents: [RunDeleteDialogComponent]
})
export class ArchivemanagerRunModule {}
