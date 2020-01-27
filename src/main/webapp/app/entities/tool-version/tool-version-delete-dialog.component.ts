import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IToolVersion } from 'app/shared/model/tool-version.model';
import { ToolVersionService } from './tool-version.service';

@Component({
  selector: 'jhi-tool-version-delete-dialog',
  templateUrl: './tool-version-delete-dialog.component.html'
})
export class ToolVersionDeleteDialogComponent {
  toolVersion: IToolVersion;

  constructor(
    protected toolVersionService: ToolVersionService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.toolVersionService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'toolVersionListModification',
        content: 'Deleted an toolVersion'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-tool-version-delete-popup',
  template: ''
})
export class ToolVersionDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ toolVersion }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ToolVersionDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.toolVersion = toolVersion;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/tool-version', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/tool-version', { outlets: { popup: null } }]);
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
