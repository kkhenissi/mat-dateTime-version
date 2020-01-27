// import { Component, OnInit } from '@angular/core';
// import { ScenarioService } from 'app/shared/services/scenario.service';
// import { MatDialog, MatDialogRef, MatDialogConfig } from '@angular/material';

// @Component({
//   selector: 'jhi-scenario-add',
//   templateUrl: './scenario-add.component.html',
//   styleUrls: ['./scenario-add.component.scss']
// })
// export class ScenarioAddComponent implements OnInit {

//   constructor(
//     private scenarioService: ScenarioService,
//     private dialog: MatDialog
//   ) { }

//   ngOnInit() {
//   }
//   openAddScenariosDialog(): MatDialogRef<ScenarioAddComponent> {
//     const config = new MatDialogConfig();
//     config.width = '80%';
//     config.disableClose = true;
//     config.autoFocus = false;
//     config.data = { scenario: null, title: 'Add Scenario' };
//     return this.dialog.open(ScenarioAddComponent, config);
//   }
// }
