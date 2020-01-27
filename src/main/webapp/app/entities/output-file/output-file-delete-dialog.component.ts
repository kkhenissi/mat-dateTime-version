import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IOutputFile } from 'app/shared/model/output-file.model';
import { OutputFileService } from '../../shared/services/output-file.service';

@Component({
  selector: 'jhi-output-file-delete-dialog',
  templateUrl: './output-file-delete-dialog.component.html'
})
export class OutputFileDeleteDialogComponent {
  outputFile: IOutputFile;

  constructor(
    protected outputFileService: OutputFileService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.outputFileService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'outputFileListModification',
        content: 'Deleted an outputFile'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-output-file-delete-popup',
  template: ''
})
export class OutputFileDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ outputFile }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(OutputFileDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.outputFile = outputFile;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/output-file', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/output-file', { outlets: { popup: null } }]);
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
