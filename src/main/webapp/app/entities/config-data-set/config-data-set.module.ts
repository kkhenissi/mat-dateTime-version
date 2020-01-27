import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { ConfigDataSetComponent } from './config-data-set.component';
import { ConfigDataSetDetailComponent } from './config-data-set-detail.component';
import { ConfigDataSetUpdateComponent } from './config-data-set-update.component';
import { ConfigDataSetDeletePopupComponent, ConfigDataSetDeleteDialogComponent } from './config-data-set-delete-dialog.component';
import { configDataSetRoute, configDataSetPopupRoute } from './config-data-set.route';

const ENTITY_STATES = [...configDataSetRoute, ...configDataSetPopupRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ConfigDataSetComponent,
    ConfigDataSetDetailComponent,
    ConfigDataSetUpdateComponent,
    ConfigDataSetDeleteDialogComponent,
    ConfigDataSetDeletePopupComponent
  ],
  entryComponents: [ConfigDataSetDeleteDialogComponent]
})
export class ArchivemanagerConfigDataSetModule {}
