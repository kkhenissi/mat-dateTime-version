import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { DataSetComponent } from './data-set.component';
import { DataSetDetailComponent } from './data-set-detail.component';
import { DataSetUpdateComponent } from './data-set-update.component';
import { DataSetDeletePopupComponent, DataSetDeleteDialogComponent } from './data-set-delete-dialog.component';
import { dataSetRoute, dataSetPopupRoute } from './data-set.route';

const ENTITY_STATES = [...dataSetRoute, ...dataSetPopupRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    DataSetComponent,
    DataSetDetailComponent,
    DataSetUpdateComponent,
    DataSetDeleteDialogComponent,
    DataSetDeletePopupComponent
  ],
  entryComponents: [DataSetDeleteDialogComponent]
})
export class ArchivemanagerDataSetModule {}
