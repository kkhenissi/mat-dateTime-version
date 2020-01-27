import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { ScenarioComponent } from './scenario.component';
import { ScenarioDetailComponent } from './scenario-detail.component';
import { ScenarioUpdateComponent } from './scenario-update.component';
import { ScenarioFileDeleteDialogComponent } from './scenario-file-delete-dialog.component';
import { scenarioRoute } from './scenario.route';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatRadioModule } from '@angular/material/radio';
import { AmazingTimePickerModule } from 'amazing-time-picker'; // this line you need
import { ScrollingModule } from '@angular/cdk/scrolling';
import { ScenarioFileComponent } from '../scenario-file/scenario-file.component';
import { AddPopupComponent } from './add-popup/add-popup.component';
import { SearchPopupComponent } from './search-popup/search-popup.component';

const ENTITY_STATES = [...scenarioRoute];

@NgModule({
  exports: [MatListModule, MatSelectModule],
  imports: [
    MatListModule,
    MatSelectModule,
    ArchivemanagerSharedModule,
    RouterModule.forChild(ENTITY_STATES),
    MatRadioModule,
    AmazingTimePickerModule,
    ScrollingModule
  ],

  declarations: [
    ScenarioComponent,
    ScenarioDetailComponent,
    ScenarioUpdateComponent,
    ScenarioFileDeleteDialogComponent,
    ScenarioFileComponent,
    AddPopupComponent,
    SearchPopupComponent
  ],
  entryComponents: [ScenarioFileDeleteDialogComponent, ScenarioFileComponent,AddPopupComponent,SearchPopupComponent]
})
export class ArchivemanagerScenarioModule {}
