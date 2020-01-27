import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITransfer } from 'app/shared/model/transfer.model';
import { TransferService } from './transfer.service';

@Component({
  selector: 'jhi-transfer-delete-dialog',
  templateUrl: './transfer-delete-dialog.component.html'
})
export class TransferDeleteDialogComponent {
  transfer: ITransfer;

  constructor(protected transferService: TransferService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.transferService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'transferListModification',
        content: 'Deleted an transfer'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-transfer-delete-popup',
  template: ''
})
export class TransferDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ transfer }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(TransferDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.transfer = transfer;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/transfer', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/transfer', { outlets: { popup: null } }]);
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
