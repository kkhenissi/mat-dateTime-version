import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IConfigDataSet } from 'app/shared/model/config-data-set.model';
import { ConfigDataSetService } from '../../shared/services/config-data-set.service';

@Component({
  selector: 'jhi-config-data-set-delete-dialog',
  templateUrl: './config-data-set-delete-dialog.component.html'
})
export class ConfigDataSetDeleteDialogComponent {
  configDataSet: IConfigDataSet;

  constructor(
    protected configDataSetService: ConfigDataSetService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.configDataSetService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'configDataSetListModification',
        content: 'Deleted an configDataSet'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-config-data-set-delete-popup',
  template: ''
})
export class ConfigDataSetDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ configDataSet }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ConfigDataSetDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.configDataSet = configDataSet;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/config-data-set', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/config-data-set', { outlets: { popup: null } }]);
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
