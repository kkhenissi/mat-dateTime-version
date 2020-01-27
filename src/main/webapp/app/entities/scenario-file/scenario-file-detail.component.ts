import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IScenarioFile } from 'app/shared/model/scenario-file.model';

@Component({
  selector: 'jhi-scenario-file-detail',
  templateUrl: './scenario-file-detail.component.html'
})
export class ScenarioFileDetailComponent implements OnInit {
  scenarioFile: IScenarioFile;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ scenarioFile }) => {
      this.scenarioFile = scenarioFile;
    });
  }

  previousState() {
    window.history.back();
  }
}
