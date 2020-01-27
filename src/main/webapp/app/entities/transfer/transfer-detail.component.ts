import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITransfer } from 'app/shared/model/transfer.model';

@Component({
  selector: 'jhi-transfer-detail',
  templateUrl: './transfer-detail.component.html'
})
export class TransferDetailComponent implements OnInit {
  transfer: ITransfer;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ transfer }) => {
      this.transfer = transfer;
    });
  }

  previousState() {
    window.history.back();
  }
}
