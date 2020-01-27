import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOutputFile } from 'app/shared/model/output-file.model';

@Component({
  selector: 'jhi-output-file-detail',
  templateUrl: './output-file-detail.component.html'
})
export class OutputFileDetailComponent implements OnInit {
  outputFile: IOutputFile;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ outputFile }) => {
      this.outputFile = outputFile;
    });
  }

  previousState() {
    window.history.back();
  }
}
