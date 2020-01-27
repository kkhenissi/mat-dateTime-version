import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDataSet } from 'app/shared/model/data-set.model';

@Component({
  selector: 'jhi-data-set-detail',
  templateUrl: './data-set-detail.component.html'
})
export class DataSetDetailComponent implements OnInit {
  dataSet: IDataSet;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ dataSet }) => {
      this.dataSet = dataSet;
    });
  }

  previousState() {
    window.history.back();
  }
}
