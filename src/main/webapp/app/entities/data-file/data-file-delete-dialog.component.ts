import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IDataFile } from 'app/shared/model/data-file.model';
import { DataFileService } from '../../shared/services/data-file.service';

@Component({
  selector: 'jhi-data-file-delete-dialog',
  templateUrl: './data-file-delete-dialog.component.html'
})
export class DataFileDeleteDialogComponent {
  dataFile: IDataFile;

  constructor(protected dataFileService: DataFileService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.dataFileService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'dataFileListModification',
        content: 'Deleted an dataFile'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-data-file-delete-popup',
  template: ''
})
export class DataFileDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ dataFile }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(DataFileDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.dataFile = dataFile;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/data-file', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/data-file', { outlets: { popup: null } }]);
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
