import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IDataSet } from 'app/shared/model/data-set.model';
import { DataSetService } from '../../shared/services/data-set.service';

@Component({
  selector: 'jhi-data-set-delete-dialog',
  templateUrl: './data-set-delete-dialog.component.html'
})
export class DataSetDeleteDialogComponent {
  dataSet: IDataSet;

  constructor(protected dataSetService: DataSetService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.dataSetService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'dataSetListModification',
        content: 'Deleted an dataSet'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-data-set-delete-popup',
  template: ''
})
export class DataSetDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ dataSet }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(DataSetDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.dataSet = dataSet;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/data-set', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/data-set', { outlets: { popup: null } }]);
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
