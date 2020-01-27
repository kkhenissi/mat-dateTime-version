import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {Observable, BehaviorSubject, of} from "rxjs";
import { ScenarioFileService } from '../../shared/services/scenario-file.service';
import {catchError, finalize} from "rxjs/operators";
import { ScenarioFile } from '../model/scenario-file.model';

export class ScenariosFileDataSource implements DataSource<ScenarioFile> {

    private scenariosFileSubject = new BehaviorSubject<ScenarioFile[]>([]);

    private loadingSubject = new BehaviorSubject<boolean>(false);

    public loading$ = this.loadingSubject.asObservable();
    public scenarioFilesFormatTable: ScenarioFile[]

    constructor(public scenarioFileService: ScenarioFileService) {

    }
 
    loadScenarioFiles(req: any, sort:string, pageIndex: number, pageSize: number) {
          
         this.loadingSubject.next(true);
         this.scenarioFileService
             .findScenarioFiles(req, 'asc', pageIndex, pageSize)
             .pipe(
             catchError(()=> of([])),
             finalize(()=> this.loadingSubject.next(false)))
             .subscribe(res =>  this.scenariosFileSubject.next(res.body))
 
     }

    connect(collectionViewer: CollectionViewer): Observable<ScenarioFile[]> {
        
        console.log("Connecting data source");
        return this.scenariosFileSubject.asObservable();
    }

    disconnect(collectionViewer: CollectionViewer): void {
        this.scenariosFileSubject.complete();
        this.loadingSubject.complete();
    }
    

}

