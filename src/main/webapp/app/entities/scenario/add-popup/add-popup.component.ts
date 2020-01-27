import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material';
import { FormBuilder, Validators } from '@angular/forms';
import { NotificationService } from 'app/shared/services/notification.service';
import { ScenarioService } from 'app/shared/services/scenario.service';

@Component({
  selector: 'jhi-add-popup',
  templateUrl: './add-popup.component.html',
  styleUrls: ['./add-popup.component.scss']
})
export class AddPopupComponent  implements OnInit {
  
  addForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    creationDate: [],
    simulation: [],
    startSimulatedDate: [],
    startSimulatedTime: [],
    endSimulatedDate: [],
    endSimulatedTime: [],
    description: []
  });
  constructor(public dialogRef: MatDialogRef<AddPopupComponent>,
              private fb: FormBuilder,
              private notificationService: NotificationService,
              private scenarioService: ScenarioService) { }

  ngOnInit() {
  }
  onClearAddForm() {
    this.addForm.reset();
   // this.initializeAddForm();
   // this.dialogRef.close()
    
  }
  initializeAddForm() {
     
    this.addForm.get('name').setValue(null);
    this.addForm.get('name').markAsDirty;
    this.addForm.get('creationDate').setValue(null);
    // this.minDateDebut = null;
    // this.maxDateDebut = null;
    // this.minDateFin = null;
    // this.maxDateFin = null;
    this.addForm.get('creationDate').markAsDirty;
    this.addForm.get('simulation').setValue(null);
    this.addForm.get('simulation').markAsDirty;
    this.addForm.get('startSimulatedDate').setValue(null);
    // this.minDateTimeDebut = null;
    // this.maxDateTimeDebut = null;
    // this.minDateTimeFin = null;
    // this.maxDateTimeFin = null;
    this.addForm.get('startSimulatedDate').markAsDirty;
    this.addForm.get('startSimulatedTime').setValue(null);
    this.addForm.get('startSimulatedTime').markAsDirty;
    this.addForm.get('endSimulatedDate').setValue(null);
    this.addForm.get('endSimulatedDate').markAsDirty;
    this.addForm.get('endSimulatedTime').setValue(null);
    this.addForm.get('endSimulatedTime').markAsDirty;
    this.addForm.get('description').setValue(null);
    this.addForm.get('description').markAsDirty;
 //   this.addForm.get('secureLevel').setValue(EnumSecurityLevel.NORMAL);
 
  }

 
  onAddModalClose() {
      
     this.addForm.reset();
     this.initializeAddForm();
     this.dialogRef.close()
   }
   onSubmit(){
   // this.dialogRef.close(true)



    if (this.addForm.valid) {
     // if (!this.addForm.get('id').value)
     console.log('this.addForm.value ===>',this.addForm.value)
        this.scenarioService.create(this.addForm.value)
             .subscribe(data => {
              this.notificationService.success(':: Submitted successfully');
             }, err => { 
              this.notificationService.warn(':: Failure request '); 
              console.log(err)})
    //  else
    //  this.scenarioService.updateEmployee(this.addForm.value);
    //  this.addForm.reset();
      // this.initializeFormGroup();
      
      this.onAddModalClose();
  //  }
     }
   }
}