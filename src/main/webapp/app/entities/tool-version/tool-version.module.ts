import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { ToolVersionComponent } from './tool-version.component';
import { ToolVersionDetailComponent } from './tool-version-detail.component';
import { ToolVersionUpdateComponent } from './tool-version-update.component';
import { ToolVersionDeletePopupComponent, ToolVersionDeleteDialogComponent } from './tool-version-delete-dialog.component';
import { toolVersionRoute, toolVersionPopupRoute } from './tool-version.route';

const ENTITY_STATES = [...toolVersionRoute, ...toolVersionPopupRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ToolVersionComponent,
    ToolVersionDetailComponent,
    ToolVersionUpdateComponent,
    ToolVersionDeleteDialogComponent,
    ToolVersionDeletePopupComponent
  ],
  entryComponents: [ToolVersionDeleteDialogComponent]
})
export class ArchivemanagerToolVersionModule {}
