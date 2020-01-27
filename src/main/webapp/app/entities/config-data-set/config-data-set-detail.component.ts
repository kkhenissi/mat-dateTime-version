import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IConfigDataSet } from 'app/shared/model/config-data-set.model';

@Component({
  selector: 'jhi-config-data-set-detail',
  templateUrl: './config-data-set-detail.component.html'
})
export class ConfigDataSetDetailComponent implements OnInit {
  configDataSet: IConfigDataSet;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ configDataSet }) => {
      this.configDataSet = configDataSet;
    });
  }

  previousState() {
    window.history.back();
  }
}
