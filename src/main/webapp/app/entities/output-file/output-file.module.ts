import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { OutputFileComponent } from './output-file.component';
import { OutputFileDetailComponent } from './output-file-detail.component';
import { OutputFileUpdateComponent } from './output-file-update.component';
import { OutputFileDeletePopupComponent, OutputFileDeleteDialogComponent } from './output-file-delete-dialog.component';
import { outputFileRoute, outputFilePopupRoute } from './output-file.route';

const ENTITY_STATES = [...outputFileRoute, ...outputFilePopupRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    OutputFileComponent,
    OutputFileDetailComponent,
    OutputFileUpdateComponent,
    OutputFileDeleteDialogComponent,
    OutputFileDeletePopupComponent
  ],
  entryComponents: [OutputFileDeleteDialogComponent]
})
export class ArchivemanagerOutputFileModule {}
