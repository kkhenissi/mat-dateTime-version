import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IScenario } from 'app/shared/model/scenario.model';

@Component({
  selector: 'jhi-scenario-detail',
  templateUrl: './scenario-detail.component.html'
})
export class ScenarioDetailComponent implements OnInit {
  scenario: IScenario;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ scenario }) => {
      this.scenario = scenario;
    });
  }

  previousState() {
    window.history.back();
  }
}
