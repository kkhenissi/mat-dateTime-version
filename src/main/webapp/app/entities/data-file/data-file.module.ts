import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { DataFileComponent } from './data-file.component';
import { DataFileDetailComponent } from './data-file-detail.component';
import { DataFileUpdateComponent } from './data-file-update.component';
import { DataFileDeletePopupComponent, DataFileDeleteDialogComponent } from './data-file-delete-dialog.component';
import { dataFileRoute, dataFilePopupRoute } from './data-file.route';

const ENTITY_STATES = [...dataFileRoute, ...dataFilePopupRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    DataFileComponent,
    DataFileDetailComponent,
    DataFileUpdateComponent,
    DataFileDeleteDialogComponent,
    DataFileDeletePopupComponent
  ],
  entryComponents: [DataFileDeleteDialogComponent]
})
export class ArchivemanagerDataFileModule {}
