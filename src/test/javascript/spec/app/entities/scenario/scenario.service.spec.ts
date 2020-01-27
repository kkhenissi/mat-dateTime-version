import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { ScenarioService } from 'app/shared/services/scenario.service';
import { IScenario, Scenario } from 'app/shared/model/scenario.model';
import { SimulationModeType } from 'app/shared/model/enumerations/simulation-mode-type.model';

describe('Service Tests', () => {
  describe('Scenario Service', () => {
    let injector: TestBed;
    let service: ScenarioService;
    let httpMock: HttpTestingController;
    let elemDefault: IScenario;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(ScenarioService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

    //  elemDefault = new Scenario(0, 'AAAAAAA', currentDate, SimulationModeType.PLAY, currentDate, currentDate, 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            creationDate: currentDate.format(DATE_FORMAT),
            startSimulatedDate: currentDate.format(DATE_FORMAT),
            simulation: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        service
          .find(123)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a Scenario', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            creationDate: currentDate.format(DATE_FORMAT),
            startSimulatedDate: currentDate.format(DATE_FORMAT),
            simulation: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            creationDate: currentDate,
            startSimulatedDate: currentDate,
            simulation: currentDate
          },
          returnedFromService
        );
        service
          .create(new Scenario(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a Scenario', () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            creationDate: currentDate.format(DATE_FORMAT),
            simulationMode: 'BBBBBB',
            startSimulatedDate: currentDate.format(DATE_FORMAT),
            simulation: currentDate.format(DATE_FORMAT),
            description: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            creationDate: currentDate,
            startSimulatedDate: currentDate,
            simulation: currentDate
          },
          returnedFromService
        );
        service
          .update(expected)
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should return a list of Scenario', () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            creationDate: currentDate.format(DATE_FORMAT),
            simulationMode: 'BBBBBB',
            startSimulatedDate: currentDate.format(DATE_FORMAT),
            simulation: currentDate.format(DATE_FORMAT),
            description: 'BBBBBB'
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            creationDate: currentDate,
            startSimulatedDate: currentDate,
            simulation: currentDate
          },
          returnedFromService
        );
        service
          .query(expected)
          .pipe(
            take(1),
            map(resp => resp.body)
          )
          .subscribe(body => (expectedResult = body));
        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Scenario', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
