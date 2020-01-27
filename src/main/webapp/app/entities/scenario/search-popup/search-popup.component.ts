import { Component, OnInit, Inject, ChangeDetectorRef } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { FormBuilder } from '@angular/forms';
import { ScenarioService } from 'app/shared/services/scenario.service';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { IScenario } from 'app/shared/model/scenario.model';
import { JhiAlertService } from 'ng-jhipster';

@Component({
  selector: 'jhi-search-popup',
  templateUrl: './search-popup.component.html',
  styleUrls: ['./search-popup.component.scss']
})
export class SearchPopupComponent implements OnInit {
  
  maxDateTimeDebut: any;
  minDateTimeFin: any;
  maxSimDateTimeDebut: Date;
  minSimDateTimeFin: Date;

  //scenarios: IScenario[];

  req: any;
  searchForm = this.fb.group({
    name: [],
    startCreationDateTime: [],
    endCreationDateTime: [],
    simulationMode: [],
    startSimulatedDateTime: [],
    endSimulatedDateTime: [],
    description: []
  });
  constructor( @Inject(MAT_DIALOG_DATA) public data,
              public dialogRef: MatDialogRef<SearchPopupComponent>,
              private fb: FormBuilder,
              private _changeDetector: ChangeDetectorRef,
              protected jhiAlertService: JhiAlertService,
              private scenarioService: ScenarioService) { }

  ngOnInit() {
  }
  onSearchModalClose() {
     //Can I close modal window manually?
     this.dialogRef.close()
   }

  onsearchSubmit() {
  
    this.startFilter();
     
 

    const dateCreatEnd = this.searchForm.get(['endCreationDateTime']).value;
    const dateCreatStart = this.searchForm.get(['startCreationDateTime']).value 
    if(dateCreatEnd !== null && dateCreatStart !== null) {
      
      const dateStart = new Date(this.searchForm.get(['startCreationDateTime']).value);
      const dateEnd = new Date(this.searchForm.get(['endCreationDateTime']).value);
      const dateS = dateStart.toLocaleDateString().split('/');
      const dateE = dateEnd.toLocaleDateString().split('/');
      this.req = {
       
        'endCreationDate.lessThan': dateE[2] + '-' + dateE[1] + '-' + dateE[0] + 'T' + '00:00:00',
        'startCreationDate.greaterThan': dateS[2] + '-' + dateS[1] + '-' + dateS[0] + 'T' + '00:00:00'
      };

    } else if (dateCreatEnd === null && dateCreatStart !== null) {
      const dateStart = new Date(this.searchForm.get(['startCreationDateTime']).value);
      const dateS = dateStart.toLocaleDateString().split('/');
      this.req = {
 
        'startCreationDate.greaterThan': dateS[2] + '-' + dateS[1] + '-' + dateS[0] + 'T' + '00:00:00'
      };

    } else if (dateCreatEnd !== null && dateCreatStart === null) {
      const dateEnd = new Date(this.searchForm.get(['endCreationDateTime']).value);
      const dateS = dateEnd.toLocaleDateString().split('/');
      this.req = {
 
        'endCreationDate.greaterThan': dateS[2] + '-' + dateS[1] + '-' + dateS[0] + 'T' + '00:00:00'
      };

    };
  

     const dateFormEnd = this.searchForm.get(['endSimulatedDateTime']).value;
     const dateFormStart = this.searchForm.get(['startSimulatedDateTime']).value;
 
 
    if(dateFormStart !== null && dateFormEnd !== null) {
      const dateStart = new Date(this.searchForm.get(['startSimulatedDateTime']).value);
      const dateEnd = new Date(this.searchForm.get(['endSimulatedDateTime']).value);
      const dateS = dateStart.toLocaleDateString().split('/');
      const dateE = dateEnd.toLocaleDateString().split('/');
     

      this.req = {...this.req,
       
        'endSimulatedDate.lessThan': dateE[2] + '-' + dateE[1] + '-' + dateE[0] + 'T' + '00:00:00',
        'startSimulatedDate.greaterThan': dateS[2] + '-' + dateS[1] + '-' + dateS[0] + 'T' + '00:00:00'
      };
     
    } else if (dateFormEnd === null && dateFormStart !== null) {
      const dateStart = new Date(this.searchForm.get(['startSimulatedDateTime']).value);
      const dateS = dateStart.toLocaleDateString().split('/');
      this.req = {...this.req,
 
        'startSimulatedDate.greaterThan': dateS[2] + '-' + dateS[1] + '-' + dateS[0] + 'T' + '00:00:00'
      };
   
    } else if (dateFormEnd !== null && dateFormStart === null) {
      const dateEnd = new Date(this.searchForm.get(['endSimulatedDateTime']).value);
      const dateE = dateEnd.toLocaleDateString().split('/');
      this.req = {...this.req,
 
        'endSimulatedDate.lessThan': dateE[2] + '-' + dateE[1] + '-' + dateE[0] + 'T' + '00:00:00'
      };
      
    }  
    const name = this.searchForm.get(['name']).value;

    if (name !== null) {
      this.req = { ...this.req, 'name.contains': name };
    }
    const description = this.searchForm.get(['description']).value;  
    if (description !== null) {
      this.req = { ...this.req, 'description.contains': description };
    }

    const simulationMode =  this.searchForm.get(['simulationMode']).value;
 
    if (simulationMode !== null) {
      this.req = { ...this.req, 'simulationMode.equals': simulationMode };
    }


    console.log('===============================> what is now in req',this.req)
   
    this.searchFromApi(this.req);
    this.onSearchModalClose();
   
  }

  initializeSearchForm() {

  //   this.searchForm.get('name').setValue(null);
  //   this.searchForm.get('name').markAsDirty;
   
  //   this.searchForm.get('creationDate').setValue(null);
  //   // this.minDateDebut = null;
  //   // this.maxDateDebut = null;
  //   // this.minDateFin = null;
  //   // this.maxDateFin = null;
  //   this.searchForm.get('creationDate').markAsDirty;
  //   this.searchForm.get('endDate').setValue(null);
  //   this.searchForm.get('endDate').markAsDirty;
  //   this.searchForm.get('startTime').setValue(null);
  //   // this.minDateTimeDebut = null;
  //   // this.maxDateTimeDebut = null;
  //   // this.minDateTimeFin = null;
  //   // this.maxDateTimeFin = null;
  //   this.searchForm.get('startTime').markAsDirty;
  //   this.searchForm.get('endTime').setValue(null);
  //   this.searchForm.get('endTime').markAsDirty;
  //   this.searchForm.get('subSys').setValue(null);
  //   this.searchForm.get('subSys').markAsDirty;
  //   this.searchForm.get('typeConfigFile').setValue(null);
  //   this.searchForm.get('typeConfigFile').markAsDirty;
  //   this.searchForm.get('formatConfigFile').setValue(null);
  //   this.searchForm.get('formatConfigFile').markAsDirty;
  //  // this.searchForm.get('secureLevel').setValue(EnumSecurityLevel.NORMAL);
 
  }





  searchFromApi(req: any) {
    this.scenarioService.query(req)
      .subscribe(data => {
        
        this.data=data.body
        console.log('66666666=======================999999999999==>', this.data)
      })
    
 
    // this.scenarioService
    // .query(req)
    // .subscribe((res: HttpResponse<IScenario[]>) =>
    //  (this.data = res.body), (res: HttpErrorResponse) => this.onError(res.message));
    //  console.log('66666666=======================999999999999==>', this.scenarios)
  
       
  }



  private startFilter(): any {
    return {
       
      name: this.searchForm.get(['name']).value, 
      startCreationDateTime: this.searchForm.get(['startCreationDateTime']).value,
      endCreationDateTime: this.searchForm.get(['endCreationDateTime']).value,
      simulationMode: this.searchForm.get(['simulationMode']).value,
      startSimulatedDateTime: this.searchForm.get(['startSimulatedDateTime']).value,
      endSimulatedDateTime: this.searchForm.get(['endSimulatedDateTime']).value,
      description: this.searchForm.get(['description']).value, 
    };
  }

  inputEvent(event) {
    // Return date object
    console.log(event.value);
  }
  changeEvent(event) {
    
    // Return date object
    console.log(event.value);
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
  onBlurFromDate() {
    this.setControlDate();
  }
  setControlDate() {
    /**Controls between start created Date && end Created Date */
       if(this.startCreationDateTimeControl.value !== null) {
            this.minDateTimeFin = this.startCreationDateTimeControl.value;
       }
        if (this.endCreationDateTimeControl.value !== null) {
           this.maxDateTimeDebut = this.endCreationDateTimeControl.value;
       }
    /**Controls between startSimulatedDateTime of data && endSimulatedDateTime of Data */
       if (this.startSimulatedDateTimeControl.value !== null) {
       
             this.minSimDateTimeFin = this.startSimulatedDateTimeControl.value;
       }
      if(this.endSimulatedDateTimeControl.value != null) {
            this.maxSimDateTimeDebut = this.endSimulatedDateTimeControl.value;
      }
    }

  get nameControl() {
    return this.searchForm.get('name');
  }
  get startCreationDateTimeControl() {
    return this.searchForm.get('startCreationDateTime');
  }
  get endCreationDateTimeControl() {
    return this.searchForm.get('endCreationDateTime');
  }
  get simulationModeControl() {
    return this.searchForm.get('simulationMode');
  }  
  get startSimulatedDateTimeControl() {
    return this.searchForm.get('startSimulatedDateTime');
  }
  get endSimulatedDateTimeControl() {
    return this.searchForm.get('endSimulatedDateTime');
  }
  get descriptionControl() {
    return this.searchForm.get('description');
  }

 
}
