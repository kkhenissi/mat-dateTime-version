import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IToolVersion } from 'app/shared/model/tool-version.model';

@Component({
  selector: 'jhi-tool-version-detail',
  templateUrl: './tool-version-detail.component.html'
})
export class ToolVersionDetailComponent implements OnInit {
  toolVersion: IToolVersion;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ toolVersion }) => {
      this.toolVersion = toolVersion;
    });
  }

  previousState() {
    window.history.back();
  }
}
