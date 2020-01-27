import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IRun } from 'app/shared/model/run.model';
import { RunService } from './run.service';

@Component({
  selector: 'jhi-run-delete-dialog',
  templateUrl: './run-delete-dialog.component.html'
})
export class RunDeleteDialogComponent {
  run: IRun;

  constructor(protected runService: RunService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.runService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'runListModification',
        content: 'Deleted an run'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-run-delete-popup',
  template: ''
})
export class RunDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ run }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(RunDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.run = run;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/run', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/run', { outlets: { popup: null } }]);
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
