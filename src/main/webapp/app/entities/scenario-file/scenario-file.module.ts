import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { ScenarioFileComponent } from './scenario-file.component';
import { ScenarioFileDetailComponent } from './scenario-file-detail.component';
import { ScenarioFileUpdateComponent } from './scenario-file-update.component';
import { scenarioFileRoute } from './scenario-file.route';
const ENTITY_STATES = [...scenarioFileRoute];

@NgModule({
  imports: [ArchivemanagerSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [ScenarioFileComponent, ScenarioFileDetailComponent, ScenarioFileUpdateComponent]
})
export class ArchivemanagerScenarioFileModule {}
