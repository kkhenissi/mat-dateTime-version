import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IScenario } from 'app/shared/model/scenario.model';
import { ScenarioService } from '../../shared/services/scenario.service';

@Component({
  selector: 'jhi-scenario-delete-dialog',
  templateUrl: './scenario-delete-dialog.component.html'
})
export class ScenarioDeleteDialogComponent {
  scenario: IScenario;

  constructor(protected scenarioService: ScenarioService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.scenarioService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'scenarioListModification',
        content: 'Deleted an scenario'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-scenario-delete-popup',
  template: ''
})
export class ScenarioDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ scenario }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ScenarioDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.scenario = scenario;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/scenario', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/scenario', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
