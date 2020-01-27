import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SenarioListComponent } from './senario-list/senario-list.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'scenario-file',
        loadChildren: () => import('./scenario-file/scenario-file.module').then(m => m.ArchivemanagerScenarioFileModule)
      },
      {
        path: 'output-file',
        loadChildren: () => import('./output-file/output-file.module').then(m => m.ArchivemanagerOutputFileModule)
      },
      {
        path: 'data-file',
        loadChildren: () => import('./data-file/data-file.module').then(m => m.ArchivemanagerDataFileModule)
      },
      {
        // path: 'scenarioStructure',
        path: 'scenario',
        loadChildren: () => import('./scenario/scenario.module').then(m => m.ArchivemanagerScenarioModule)
      },
      {
        path: 'run',
        loadChildren: () => import('./run/run.module').then(m => m.ArchivemanagerRunModule)
      },
      {
        path: 'tool-version',
        loadChildren: () => import('./tool-version/tool-version.module').then(m => m.ArchivemanagerToolVersionModule)
      },
      {
        path: 'transfer',
        loadChildren: () => import('./transfer/transfer.module').then(m => m.ArchivemanagerTransferModule)
      },
      {
        path: 'data-set',
        loadChildren: () => import('./data-set/data-set.module').then(m => m.ArchivemanagerDataSetModule)
      },
      {
        path: 'config-data-set',
        loadChildren: () => import('./config-data-set/config-data-set.module').then(m => m.ArchivemanagerConfigDataSetModule)
      },
      {
        path: 'user-preferences',
        loadChildren: () => import('./user-preferences/user-preferences.module').then(m => m.ArchivemanagerUserPreferencesModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ],
  declarations: [SenarioListComponent]
})
export class ArchivemanagerEntityModule {}
