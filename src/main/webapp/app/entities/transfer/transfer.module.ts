import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { TransferComponent } from './transfer.component';
import { TransferDetailComponent } from './transfer-detail.component';
import { TransferUpdateComponent } from './transfer-update.component';
import { TransferDeletePopupComponent, TransferDeleteDialogComponent } from './transfer-delete-dialog.component';
import { transferRoute, transferPopupRoute } from './transfer.route';

const ENTITY_STATES = [...transferRoute, ...transferPopupRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    TransferComponent,
    TransferDetailComponent,
    TransferUpdateComponent,
    TransferDeleteDialogComponent,
    TransferDeletePopupComponent
  ],
  entryComponents: [TransferDeleteDialogComponent]
})
export class ArchivemanagerTransferModule {}
