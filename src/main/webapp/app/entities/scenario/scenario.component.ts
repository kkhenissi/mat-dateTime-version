import { Component, OnInit, OnDestroy, AfterViewInit, OnChanges, ChangeDetectorRef, ViewChild, ElementRef } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, Subscription, Subject, fromEvent, merge, of } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map, takeUntil, debounceTime, distinctUntilChanged, tap } from 'rxjs/operators';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { IScenario, Scenario } from 'app/shared/model/scenario.model';
import { AccountService } from 'app/core/auth/account.service';
import { ScenarioFileDeleteDialogComponent } from './scenario-file-delete-dialog.component';
import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { ScenarioService } from '../../shared/services/scenario.service';
import { FormBuilder, Validators } from '@angular/forms';
import { IUser } from 'app/core/user/user.model';
// import moment = require('moment');
import { IScenarioFile, ScenarioFile } from 'app/shared/model/scenario-file.model';
import { ScenarioFileService } from 'app/shared/services/scenario-file.service';
// import { afficherErreur, getErrorMessage, errorMessage} from "app/shared/util/errorMessage";
import { group } from '@angular/animations';
import doc = Mocha.reporters.doc;
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { MatDatepickerInputEvent, MatDialogRef, MatDialogConfig, MatDialog, MatPaginator, MatSort } from '@angular/material';
import { SearchPopupComponent } from 'app/entities/scenario/search-popup/search-popup.component';
// import { AddPopupComponent } from 'app/shared/add-popup/add-popup.component';
import { ConfigDataSetService } from 'app/shared/services/config-data-set.service';
import { IConfigDataSet } from 'app/shared/model/config-data-set.model';
import { IAuthor } from 'app/shared/model/author.model';
import { FormControl } from '@angular/forms';
import { EnumSecurityLevel } from '../../shared/model/enumerations/security-level.model';
import { getLocaleTimeFormat } from '@angular/common';
import MethodeGenerique from 'app/shared/methodes-generique';
import { thisExpression, tsIndexSignature } from '@babel/types';
import { AddPopupComponent } from './add-popup/add-popup.component';
import { ScenariosFileDataSource} from '../../shared/services/scenariosFile.datasource';
import { NotificationService } from 'app/shared/services/notification.service';

@Component({
  selector: 'jhi-scenario',
  templateUrl: './scenario.component.html',
  styleUrls: ['scenario.component.scss']
})
export class ScenarioComponent implements OnInit, OnChanges, OnDestroy {


  // dataSource: ScenariosFileDataSource;
  // displayedColumns: string[] = [
  //   'fileType',
  //   'relativePathInST',
  //   'lTInsertionDate',
  //   'pathInLT',
  //   'format',
  //   'subSystemAtOriginOfData',
  //   'timeOfData',
  //   'securityLevel',
  //   'actions'
  // ];

  // @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;

  // @ViewChild(MatSort, { static: true }) sort: MatSort;

  // @ViewChild('input', { static: true }) input: ElementRef;

//   listData2: ScenariosFileDataSource;




  Createdbetween:any="Created Between"

  currentAccount: any;
  currentAutoritie: any;
  allAccount: any[];
  scenarios: IScenario[];
  scenariosObse$: Observable<Scenario[]>
  error: any;
  success: any;
  eventSubscriber: Subscription;
  routeData: any;
  links: any;
  totalItems: any;
  itemsPerPage: any;
  currentPage: any;
  inputType: any;
  predicate: any;
  previousPage: any;
  reverse: any;
  isSaving: boolean;
  req: any;
  idScenario: any;
  nameScenario: any;
  ownerId:any;
  users: IUser[];
  scenarioFiles: IScenarioFile[];
  createdDate: any;
  minStartSimulatedDtae: any;
  minEndSimulatedDate: any;
  filesDatasetConfig: any;
  minDateDebut: any;
  maxDateDebut: any;
  minDateFin: any;
  maxDateFin: any;
  minDateTimeDebut: any;
  maxDateTimeDebut: any;
  minDateTimeFin: any;
  maxDateTimeFin: any;
  private unsubscribe = new Subject<void>();

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    creationDate: [],
    simulation: [],
    startSimulatedDate: [],
    startSimulatedTime: [],
    endSimulatedDate: [],
    endSimulatedTime: [],
    description: [],
    ownerId:[]
  });

  searchForm = this.fb.group({
    typeConfigFile: [],
    formatConfigFile: [],
    // allUser: [],
    owner: [],
    configDataSet: [],
    startDate: [null, []],
    endDate: [null, []],
    startTime: [null,[]],
    endTime: [null, []],
    name: [],
    subSys: [],
    secureLevel: []
  });

  securitysLevel = [EnumSecurityLevel.NORMAL, EnumSecurityLevel.EIC];
  selectedOption = new FormControl(EnumSecurityLevel.NORMAL);

  constructor(
    public dialog: MatDialog,
    protected scenarioFileService: ScenarioFileService,
    protected scenarioService: ScenarioService,
    protected parseLinks: JhiParseLinks,
    protected jhiAlertService: JhiAlertService,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    protected eventManager: JhiEventManager,
    private modalService: NgbModal,
    private fb: FormBuilder,
    protected configDataSetService: ConfigDataSetService,
    private notificationService: NotificationService,
    private changeDetectorRefs: ChangeDetectorRef
  ) {
    this.itemsPerPage = 20;
    this.routeData = this.activatedRoute.data.subscribe(data => {
      this.currentPage = data.pagingParams.page;
      this.previousPage = data.pagingParams.page;
      this.reverse = data.pagingParams.ascending;
      this.predicate = data.pagingParams.predicate;
      this.currentAutoritie = data['authorities'];
      this.scenarioFiles = [

      ];
    });
  }

  // authorss: IAuthor[]=[];
  // filteredAuthors: Observable<IAuthor[]>;
  // readonly authorValidatorKey = "authorNotFound";
  // get authorControl() {
  //   return this.searchForm.get("authors");
  // }
  /**
   * Manages how a priorite should be displayed in the input
   * @param priorite a given NiveauPriorite to be formatted
   */
  displayAuthor(author: any): string | undefined {
    if (author) {
      return `${author.code}`;
    }
  }

  loadAll() {
    this.scenarioService
      .query({
         page: this.currentPage - 1,
         size: this.itemsPerPage
      })
      .subscribe((res: HttpResponse<IScenario[]>) =>
       (this.scenarios = res.body), (res: HttpErrorResponse) => this.onError(res.message));

  }
  loadAllConfigDataSet() {
    this.configDataSetService
      .query({
        page: this.currentPage - 1,
        size: this.itemsPerPage
      })
      .subscribe(
        (res: HttpResponse<IConfigDataSet[]>) => (this.filesDatasetConfig = res.body),
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }
  loadAllAccount() {
    this.accountService.allAccount().subscribe(
      data => {
        this.allAccount = data.body;
      },
      err => {
        console.log(err);
      }
    );
  }

  searchFiltredConfigFiles() {

    this.startFilter();
    const typeConfigFile = this.typeConfigFileControl.value;
    const formatConfigFile = this.formatConfigFileControl.value;
    const dateFormEnd = this.searchForm.get(['endDate']).value;
    const dateFormStart = this.searchForm.get(['startDate']).value;
    const subSys = this.searchForm.get(['subSys']).value;
    const secureLevel = this.secureLevelControl.value;
    if(dateFormEnd !== null && dateFormStart !== null) {
      const dateEnd = new Date(this.searchForm.get(['endDate']).value);
      const dateStart = new Date(this.searchForm.get(['startDate']).value);
      const dateS = dateStart.toLocaleDateString().split('/');
      const dateE = dateEnd.toLocaleDateString().split('/');

      this.req = {

        'inputType.equals': 'CONFIG',
        'scenariosId.in': this.idScenario,
        'lTInsertionDate.lessThan': dateE[2] + '-' + dateE[1] + '-' + dateE[0] + 'T' + '00:00:00',
        'lTInsertionDate.greaterThan': dateS[2] + '-' + dateS[1] + '-' + dateS[0] + 'T' + '00:00:00'
      };
    } else if (dateFormEnd === null && dateFormStart !== null) {
      const dateStart = new Date(this.searchForm.get(['startDate']).value);
      const dateS = dateStart.toLocaleDateString().split('/');
      this.req = {

        'inputType.equals': 'CONFIG',
        'scenariosId.in': this.idScenario,
        'lTInsertionDate.greaterThan': dateS[2] + '-' + dateS[1] + '-' + dateS[0] + 'T' + '00:00:00'
      };
    } else if (dateFormEnd !== null && dateFormStart === null) {
      const dateEnd = new Date(this.searchForm.get(['endDate']).value);
      const dateE = dateEnd.toLocaleDateString().split('/');
      this.req = {

        'inputType.equals': 'CONFIG',
        'scenariosId.in': this.idScenario,
        'lTInsertionDate.lessThan': dateE[2] + '-' + dateE[1] + '-' + dateE[0] + 'T' + '00:00:00'
      };
    } else {
      this.req = {

        'inputType.equals': 'CONFIG',
        'scenariosId.in': this.idScenario
      };
    }

    //   const type =  this.searchForm.get(['typeConfigFile']).value;
    //  const authors =  this.searchForm.get(['authors']).value;
    //   const owner = this.searchForm.get(['owner']).value;
    const configDataSet = this.searchForm.get(['configDataSet']).value;

    const startTime = this.searchForm.get(['startTime']).value;
    const endTime = this.searchForm.get(['endTime']).value;
    const name = this.searchForm.get(['name']).value;

    if (typeConfigFile !== null) {
      this.req = { ...this.req, 'fileType.equals': typeConfigFile };
    }
    //  if( authors != null) { this.req = {...this.req, 'authors': authors }}
    // // // if (owner !== '') {
    // // //   this.req = { ...this.req, 'ownerId.equals': this.currentAccount.id };
    // // // }
    if (configDataSet !== null) {
      this.req = { ...this.req, 'configDataSet.configDatasetId.contains': configDataSet };
    }

    /** time of Data in search url */
     if( startTime !== null && endTime !== null ) {
      const timeEnd = new Date(this.searchForm.get(['endTime']).value);
      const timeStart = new Date(this.searchForm.get(['startTime']).value);
      const timeS = timeStart.toLocaleDateString().split('/');
      const timeE = timeEnd.toLocaleDateString().split('/');


      this.req = {...this.req,
        'timeOfData.lessThan': timeE[2] + '-' + timeE[1] + '-' + timeE[0] + 'T' + '00:00:00',
        'timeOfData.greaterThan': timeS[2] + '-' + timeS[1] + '-' + timeS[0] + 'T' + '00:00:00'
      }

    } else if(startTime == null && endTime !== null) {
       const timeEnd = new Date(this.searchForm.get(['endTime']).value);
       const timeE = timeEnd.toLocaleDateString().split('/');

       this.req = {...this.req,
        'timeOfData.lessThan': timeE[2] + '-' + timeE[1] + '-' + timeE[0] + 'T' + '00:00:00',
      }
    } else if (startTime !== null && endTime == null) {
      const timeStart = new Date(this.searchForm.get(['startTime']).value);
       const timeS = timeStart.toLocaleDateString().split('/');

       this.req = {...this.req,
        'timeOfData.greaterThan': timeS[2] + '-' + timeS[1] + '-' + timeS[0] + 'T' + '00:00:00',
      }

    }

      // this.req = {...this.req, 'timeOfData.greaterThan': startTime, 'timeOfData.lessThan': endTime }




    //  if( startTime != null && endTime == null ) {this.req = {...this.req, 'timeOfData.greaterThan': startTime }}
    //  if( startTime == null && endTime != null ) {this.req = {...this.req, 'timeOfData.lessThan': endTime }}
    // //  if( endTime != '') { }
    if (name !== null) {
        this.req = { ...this.req, 'relativePathInST.contains': name };
    }
    if (formatConfigFile !== null && formatConfigFile !== '') {
      this.req = { ...this.req, 'format.equals': formatConfigFile };
    }
    if (subSys !== null && subSys !== '') {
      this.req = { ...this.req, 'subSystemAtOriginOfData.equals': subSys };
    }
    if (secureLevel !== null && secureLevel !== '') {
      this.req = { ...this.req, 'securityLevel.equals': secureLevel };
    }
//
    this.searchFromApi(this.req);
 //   console.log('????????????????????????????this.dataSource,>',this.dataSource)

  }
  changeItemsPerPage(pageSize) {

    // if(pageSize != undefined) {
    //   this.itemsPerPage = pageSize;
    //   console.log('**********value====>', pageSize)

    // }
  }
  changeIndexPage(indexPage){
  //   alert('hirepageIndex')
  //   if(indexPage != undefined) {
  //     this.currentPage = indexPage;
  // }

  }

  searchFromApi(req: any) {


    // console.log('whats in req ======**************befort*******************==========>', this.req);
    // this.req.pageSize = this.itemsPerPage;
    // this.req.pageIndex = this.currentPage;
    // console.log('whats in req ======***************after******************==========>', this.req);
    // this.scenarioFileService
    //   .query(req)
    //   .subscribe(
    //     (res: HttpResponse<IScenarioFile[]>) => {
    //       this.scenarioFiles = res.body;
    //       console.log('zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz>>>', this.scenarioFiles)
    //       this.scenarioFileService.scenariosFile.next(this.scenarioFiles);
    //     },
    //     (res: HttpErrorResponse) => this.onError(res.message)
    //   );
      this.scenarioFileService.findScenarioFiles(req, 'asc', 0, 100);

  }
  // ngOnChanges(changes: import("@angular/core").SimpleChanges): void {

  //    alert('cht')
  //   // this.req.pageSize = this.itemsPerPage;
  //   // this.req.pageIndex = this.currentPage;
  //    this.searchFromApi(this.req);
  // }
  // ngAfterViewInit(): void {

  //   this.listData2 = new ScenariosFileDataSource(this.scenarioFileService);
  //   alert('ok5')
  //   //  this.listData2.loadScenarioFiles(this.req);
  //   this.req.pageIndex=this.paginator.pageIndex;
  //   this.req.previousPage=this.paginator.pageSize;
  //   this.paginator.page.subscribe(()=> {
  //     this.listData2.loadScenarioFiles(this.req);
  //   })
  //  }
  // ngAfterViewInit(): void {

  //   this.dataSource.loadScenarioFiles(this.req);

  //   this.sort.sortChange.subscribe(() => this.paginator.pageIndex = 0);

  //   fromEvent(this.input.nativeElement,'keyup')
  //       .pipe(
  //           debounceTime(150),
  //           distinctUntilChanged(),
  //           tap(() => {
  //               this.paginator.pageIndex = 0;

  //               this.loadScenarioFilesPage();
  //           })
  //       )
  //       .subscribe();

  //   merge(this.sort.sortChange, this.paginator.page)
  //   .pipe(
  //       tap(() => this.loadScenarioFilesPage())
  //   )
  //   .subscribe();


  //     }

      // loadScenarioFilesPage() {

      //   this.dataSource.loadScenarioFiles(
      //       this.req
            // this.scenario.id,
            // this.input.nativeElement.value,
            // this.sort.direction,
            // this.paginator.pageIndex,
    //         // this.paginator.pageSize
    //         );
    // }




  save() {
    this.isSaving = true;
    const scenario = this.createFromForm();
    if (scenario.id !== undefined) {
      this.scenarioService.update(scenario)
             .subscribe(data => {
              this.notificationService.success(':: Updated successfully');
             }, err => {
              this.notificationService.warn(':: Failure request ');
              console.log(err)})







      this.scenarioService.update(scenario)
     // this.subscribeToSaveResponse(this.scenarioService.update(scenario));
    } else {
    //  this.subscribeToSaveResponse(this.scenarioService.create(scenario));
    }
  }

  initializeSearchForm() {

    this.searchForm.get('name').setValue(null);
    this.searchForm.get('name').markAsDirty;

    this.searchForm.get('startDate').setValue(null);
    this.minDateDebut = null;
    this.maxDateDebut = null;
    this.minDateFin = null;
    this.maxDateFin = null;
    this.searchForm.get('startDate').markAsDirty;
    this.searchForm.get('endDate').setValue(null);
    this.searchForm.get('endDate').markAsDirty;
    this.searchForm.get('startTime').setValue(null);
    this.minDateTimeDebut = null;
    this.maxDateTimeDebut = null;
    this.minDateTimeFin = null;
    this.maxDateTimeFin = null;
    this.searchForm.get('startTime').markAsDirty;
    this.searchForm.get('endTime').setValue(null);
    this.searchForm.get('endTime').markAsDirty;
    this.searchForm.get('subSys').setValue(null);
    this.searchForm.get('subSys').markAsDirty;
    this.searchForm.get('typeConfigFile').setValue(null);
    this.searchForm.get('typeConfigFile').markAsDirty;
    this.searchForm.get('formatConfigFile').setValue(null);
    this.searchForm.get('formatConfigFile').markAsDirty;
    this.searchForm.get('secureLevel').setValue(EnumSecurityLevel.NORMAL);

  }

  onClear() {
    this.initializeSearchForm();
  }

  updateForm(scenario: IScenario) {
    console.log(scenario.ownerId,+ '----'+ scenario.description + '----'+ scenario.creationDate+ '----'+ scenario.endSimulatedDate + '----'+ scenario.startSimulatedDate)

    console.log('value of scenario after modification ====>', scenario)
    this.idScenario = scenario.id;
    this.nameScenario = scenario.name;
    this.ownerId = scenario.ownerId;

    this.editForm.patchValue({
      id: scenario.id,
      name: scenario.name,
      creationDate: scenario.creationDate,
      simulation: scenario.simulation,
      startSimulatedDate: scenario.startSimulatedDate,
      //  simulation: scenario.simulation,
      description: scenario.description,
      ownerId: this.ownerId
    });

  }
  previousState() {
    window.history.back();
  }
  // protected subscribeToSaveResponse(result: Observable<HttpResponse<IScenario>>) {
  //   result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  // }

  // protected onSaveSuccess() {
  //   this.isSaving = false;
  //   window.location.reload();
  // }

  // protected onSaveError() {
  //   this.isSaving = false;
  // }
  private createFromForm(): IScenario {
    return {
      ...new Scenario(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      creationDate: this.editForm.get(['creationDate']).value,
    //  simulation: this.editForm.get(['simulation']).value,
      startSimulatedDate: this.editForm.get(['startSimulatedDate']).value,
    //  startSimulatedTime: this.editForm.get(['startSimulatedTime']).value,
      //   simulation: this.editForm.get(['simulation']).value,
      endSimulatedDate: this.editForm.get(['simulation']).value,
      description: this.editForm.get(['description']).value,
      ownerId: this.ownerId
        };
  }

  private startFilter(): any {
    return {
      type: this.searchForm.get(['typeConfigFile']).value,
      //  authors: this.searchForm.get(['authors']).value,
      owner: this.searchForm.get(['owner']).value,
      startDate: this.searchForm.get(['startDate']).value,
      endDate: this.searchForm.get(['endDate']).value,
      startTime: this.searchForm.get(['startTime']).value,
      endTime: this.searchForm.get(['endTime']).value,
      name: this.searchForm.get(['name']).value
    };
  }

  // loadPage(page: number) {

  //   if (page !== this.previousPage) {
  //     this.previousPage = page;
  //     this.transition();
  //   }
  // }

  // transition() {

  //   this.router.navigate(['/scenario'], {
  //     queryParams: {
  //     //  page: this.page,
  //       pageSize: this.itemsPerPage,
  //       sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
  //     }
  //   });
  //   this.loadAll();
  // }

//   clear() {
//  //   this.page = 0;
//     this.router.navigate([
//       '/scenario',
//       {
//  //       page: this.page,
//         sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
//       }
//     ]);
//     this.loadAll();
//   }

ngOnChanges(changes: import("@angular/core").SimpleChanges): void {
    this.req.scenariosId = this.idScenario;
}

  ngOnInit() {
    this.loadAll();


    this.loadAllConfigDataSet();
    this.loadAllAccount();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
  //  this.registerChangeInScenarios();
    // this.listData2 = new ScenariosFileDataSource(this.scenarioFileService);
    // this.dataSource = new ScenariosFileDataSource(this.scenarioFileService);
    // this.dataSource.loadScenarioFiles(this.req, '','asc');
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  trackId(index: number, item: IScenario) {
    return item.id;
  }

  // registerChangeInScenarios() {
  //   this.eventSubscriber = this.eventManager.subscribe('scenarioListModification', response => this.loadAll());
  // }

  // sort() {
  //   const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
  //   if (this.predicate !== 'id') {
  //     result.push('id');
  //   }
  //   return result;
  // }

  // protected paginateScenarios(data: IScenario[], headers: HttpHeaders) {

  //   this.links = this.parseLinks.parse(headers.get('link'));
  //   this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
  //   this.scenarios = data;
  // }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  // checkUncheckAll(event) {
  //   const checked = event.target.checked;
  //   document.getElementById('activateItem').click();
  // }

  deleteScenarioFile(scenarioFile: ScenarioFile) {
    const modalRef = this.modalService.open(ScenarioFileDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.scenarioFile = scenarioFile;
    modalRef.result.then(
      result => {
        this.searchFiltredConfigFiles();
      },
      reason => {
        this.searchFiltredConfigFiles();
      }
    );
  }
  inputEvent(event) {
    // Return date object
    console.log(event.value);
  }
  changeEvent(event) {

    // Return date object
    console.log(event.value);
  }
  searchScenarios() {



  //   matDialogRef.afterClosed().subscribe(res => {
  //     this.loadAll();
  //   if (res === false) {
  //    }
  // });






    const matDialogRef = this.openSearchScenariosDialog();

    matDialogRef.afterClosed().subscribe(res => {
      if (res === false) {

        this.scenarioFiles = matDialogRef.componentInstance.data;
        console.log('/***ddddddddddddddddddddaaaaaaaaaaaaaatttttttttttt=====>>>***/',  this.scenarioFiles);
        // Récupère le destinataire sélectionné
        // this.destinataire = matDialogRef.componentInstance.data.beneficiaire;
        // this.courrier.destinataire = this.destinataire;

        //    Ajoute la référence du destinataire (à pour effet d'appeler la fonction setControlDestinataire())
        // this.destinataireReference = this.destinataire.reference;
        // this.destinataireReferenceControl.setValue(this.destinataireReference);
      }
    });
  }
  addScenario() {
    const matDialogRef = this.openAddScenariosDialog();
      matDialogRef.afterClosed().subscribe(res => {
        this.loadAll();
      if (res === false) {
       }
    });
  }
  /**
   * Configuration de la popup de recherche de scenarios
   */
  openSearchScenariosDialog(): MatDialogRef<SearchPopupComponent> {
    const config = new MatDialogConfig();
    config.width = '60%';
    config.disableClose = true;
    config.autoFocus = false;
    config.data = { scenario: null, title: 'Recherche Scenarios' };
    return this.dialog.open(SearchPopupComponent, config);
  }

  /* Control sur la validié de la date
  * Véification sur la longuer de la date saisie :
  * exemple 010319999 et 01/03/19999
  * ne fonctionnent pas
  */
 setControlDate() {
 /**Controls between start created Date && end Created Date */
    if(this.startDateControl.value !== null) {
         this.minDateFin = this.startDateControl.value;
    }
     if (this.endDateControl.value !== null) {
        this.maxDateDebut = this.endDateControl.value;
    }
 /**Controls between start date of Data && end date of Data */
    if (this.startTimeControl.value !== null) {
         this.minDateTimeFin = this.startTimeControl.value;
    }
    if(this.endTimeControl.value != null) {
        this.maxDateTimeDebut = this.startTimeControl.value;
    }
 }
   onBlurFromDate() {
     alert('t')
    this.setControlDate();
  }


  /**
   * Configuration de la popup de recherche de scenarios
   */
  openAddScenariosDialog(): MatDialogRef<AddPopupComponent> {
    const config = new MatDialogConfig();
    config.width = '60%';
    config.disableClose = true;
    config.autoFocus = false;
    config.data = { scenario: null, title: 'Add Scenario' };
    return this.dialog.open(AddPopupComponent, config);
  }
  close() {
    //Can I close modal window manually?
    //this.dialogRef.close(true)
  }
  setToNullValue(control: FormControl) {
    /**initialize min and max of created Date */
    if(this.endDateControl.get('endDate') == null ) {
      this.maxDateDebut = null;
    }
    if (this.startDateControl.get('startDate') === null ) {
      this.minDateFin = null;
    }
    /**initialize min and max of time of Data*/

    if(this.endTimeControl.get('endTime') == null) {
      this.maxDateTimeDebut = null;
    }
    if(this.startTimeControl.get('startTime') == null) {
      this.minDateTimeFin = null;
    }
    MethodeGenerique.setToNullValue(control);

  }
  get nameControl() {
    return this.searchForm.get('name');
  }
  get configDataSetControl() {
    return this.searchForm.get('configDataSet');
  }
  get startDateControl() {
    return this.searchForm.get('startDate');
  }
  get endDateControl() {
    return this.searchForm.get('endDate');
  }
  get startTimeControl() {
    return this.searchForm.get('startTime');
  }
  get endTimeControl() {
    return this.searchForm.get('endTime');
  }
  get subSysControl() {
   return this.searchForm.get('subSys');
 }
  get typeConfigFileControl() {
    return this.searchForm.get('typeConfigFile');
  }
  get formatConfigFileControl() {
    return this.searchForm.get('formatConfigFile');
  }
  get secureLevelControl() {
    return this.searchForm.get('secureLevel');
  }


}
