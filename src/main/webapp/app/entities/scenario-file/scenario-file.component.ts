import { Component, OnInit, OnDestroy, Input, ViewChild, OnChanges, AfterViewInit, Output, EventEmitter, ChangeDetectionStrategy, ElementRef } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription, fromEvent, merge } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { map, startWith, tap, debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { IScenarioFile, ScenarioFile } from 'app/shared/model/scenario-file.model';
import { AccountService } from 'app/core/auth/account.service';
import { MatPaginator } from '@angular/material/paginator';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { ScenarioFileService } from '../../shared/services/scenario-file.service';
import { ScenariosFileDataSource} from '../../shared/services/scenariosFile.datasource';

import { RunService } from '../run/run.service';
import { IRun } from 'app/shared/model/run.model';
import { ChangeDetectorRef } from '@angular/core';
 
import { start } from 'repl';
import { Scenario } from 'app/shared/model/scenario.model';
import { MatSort } from '@angular/material';

@Component({
  selector: 'jhi-scenario-file',
  templateUrl: './scenario-file.component.html',
  styleUrls: ['scenario-file.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ScenarioFileComponent implements OnInit, OnDestroy, AfterViewInit, OnChanges {

   @Input() currentAccount: any;
   @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
   @ViewChild(MatSort,{ static: true}) sort: MatSort;
   @ViewChild('input', {static: true}) input: ElementRef;
 // listData: MatTableDataSource<any>;
  listData2: ScenariosFileDataSource;
  scenarioFiles:Scenario[];
  @Input() req: any;
  displayedColumns: string[] = [
    'fileType',
    'relativePathInST',
    'lTInsertionDate',
    'pathInLT',
    'format',
    'subSystemAtOriginOfData',
    'timeOfData',
    'securityLevel',
    'actions'
  ];

  error: any;
  success: any;
  eventSubscriber: Subscription;
  routeData: any;
  links: any;
  totalScenarioFiles: any;
  itemsPerPage: any;
  page: any;
  predicate: any;
  previousPage: any;
  reverse: any;

  constructor(
    public scenarioFileService: ScenarioFileService,
    protected runService: RunService,
    protected parseLinks: JhiParseLinks,
    protected jhiAlertService: JhiAlertService,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    protected router: Router,
    private ref: ChangeDetectorRef,
    protected eventManager: JhiEventManager
  ) {
   
    this.routeData = this.activatedRoute.data.subscribe(data => {
       this.page = data.pagingParams.page;
       this.previousPage = data.pagingParams.page;
       this.reverse = data.pagingParams.ascending;
       this.predicate = data.pagingParams.predicate;
    });
  }

   
  ngAfterViewInit(): void {
     
     

  }

  ngOnChanges(changes: import("@angular/core").SimpleChanges): void {
 
  
    if(this.listData2 === undefined) {
      this.listData2 = new ScenariosFileDataSource(this.scenarioFileService);
    }
    this.scenarioFiles = [];
    this.sort.sortChange.subscribe(()=> this.paginator.pageIndex = 0);
    // fromEvent(this.input.nativeElement,'keyup')
    //      .pipe(
    //        debounceTime(150),
    //        distinctUntilChanged(),
    //        tap(() => {
    //          this.paginator.pageIndex= 0;
    //          this.loadScenarioFilesPage();
    //        })  
    //      )
    //      .subscribe();
      merge(this.sort.sortChange, this.paginator.page)
          .pipe(
            tap(()=> this.loadScenarioFilesPage())
          )   
          .subscribe();
          this.listData2.loadScenarioFiles(this.req, 'asc', this.paginator.pageIndex, this.paginator.pageSize);    
          this.scenarioFileService.totalItems(this.req)
          .subscribe((data) => {this.totalScenarioFiles = data.body;
                                console.log('totalScenarioFiles =====}}==>', this.totalScenarioFiles.body)}
          , err => console.log(err))
 
  }

  ngOnInit() {
 
       this.scenarioFileService.totalItems(this.req)
            .subscribe((data) => {this.totalScenarioFiles = data.body;
                                  console.log('totalScenarioFiles =====}}==>', this.totalScenarioFiles.body)}
            , err => console.log(err))
       
        this.listData2 = new ScenariosFileDataSource(this.scenarioFileService);
        this.listData2.loadScenarioFiles(this.req,'asc', this.paginator.pageIndex, this.paginator.pageSize);
      //  this.listData2.loadScenarioFiles(this.scenarioFileService.scenariosFile.value);
        // this.listData2.scenarioFileService.scenariosFile.subscribe(data =>  {
        //   this.scenarioFiles = data},
        //   err => {console.log('one error is happend ==>'), err}  );
        console.log('--------------------listData2------------->', this.scenarioFiles)
        //  this.loadAll();
  
    this.itemsPerPage = this.paginator.pageSize;
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
 
  
  }
 

  loadScenarioFilesPage() {
      
      this.listData2.loadScenarioFiles(
      this.req,
   //   this.input.nativeElement.value,
      this.sort.direction,
      this.paginator.pageIndex,
      this.paginator.pageSize
       )
       console.log('------------loadScenarioFilesPage--------listData2------------->', this.scenarioFiles)
       this.ref.detectChanges();
   }
 
  ngOnDestroy() {
    if(this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
    
  }

  trackId(index: number, item: IScenarioFile) {}

 
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
