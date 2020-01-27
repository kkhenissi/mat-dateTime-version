import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IScenarioFile } from 'app/shared/model/scenario-file.model';
import { ScenarioFileService } from '../../shared/services/scenario-file.service';

@Component({
  selector: 'jhi-scenario-file-delete-dialog',
  templateUrl: './scenario-file-delete-dialog.component.html'
})
export class ScenarioFileDeleteDialogComponent {
  scenarioFile: IScenarioFile;

  constructor(
    protected scenarioFileService: ScenarioFileService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: string) {
    this.scenarioFileService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'scenarioFileListModification',
        content: 'Deleted an scenarioFile'
      });
      this.activeModal.dismiss(true);
    });
  }
}
