import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IRun } from 'app/shared/model/run.model';

@Component({
  selector: 'jhi-run-detail',
  templateUrl: './run-detail.component.html'
})
export class RunDetailComponent implements OnInit {
  run: IRun;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ run }) => {
      this.run = run;
    });
  }

  previousState() {
    window.history.back();
  }
}
