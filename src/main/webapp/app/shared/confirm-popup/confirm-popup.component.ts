import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';

@Component({
  selector: 'jhi-confirm-popup',
  templateUrl: './confirm-popup.component.html',
  styleUrls: ['./confirm-popup.component.scss']
})
export class ConfirmPopupComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<ConfirmPopupComponent>,) { }

  ngOnInit() {
  }
   close() {
     //Can I close modal window manually?
     this.dialogRef.close(true)
   }

}
