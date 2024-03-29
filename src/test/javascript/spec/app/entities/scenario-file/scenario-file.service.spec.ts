import { TestBed, getTestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { take, map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { ScenarioFileService } from 'app/shared/services/scenario-file.service';
import { IScenarioFile, ScenarioFile } from 'app/shared/model/scenario-file.model';
import { EnumSecurityLevel } from 'app/shared/model/enumerations/security-level.model';

describe('Service Tests', () => {
  describe('ScenarioFile Service', () => {
    let injector: TestBed;
    let service: ScenarioFileService;
    let httpMock: HttpTestingController;
    let elemDefault: IScenarioFile;
    let expectedResult;
    let currentDate: moment.Moment;
    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule]
      });
      expectedResult = {};
      injector = getTestBed();
      service = injector.get(ScenarioFileService);
      httpMock = injector.get(HttpTestingController);
      currentDate = moment();

      elemDefault = new ScenarioFile(
        0,
        'AAAAAAA',
        'AAAAAAA',
        currentDate,
        'AAAAAAA',
        'AAAAAAA',
        'AAAAAAA',
        currentDate,
        EnumSecurityLevel.NORMAL,
        'AAAAAAA'
      );
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            lTInsertionDate: currentDate.format(DATE_FORMAT),
            timeOfData: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        service
          .find('123')
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: elemDefault });
      });

      it('should create a ScenarioFile', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            lTInsertionDate: currentDate.format(DATE_FORMAT),
            timeOfData: currentDate.format(DATE_FORMAT)
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            lTInsertionDate: currentDate,
            timeOfData: currentDate
          },
          returnedFromService
        );
        service
          .create(new ScenarioFile(null))
          .pipe(take(1))
          .subscribe(resp => (expectedResult = resp));
        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject({ body: expected });
      });

      it('should update a ScenarioFile', () => {
        const returnedFromService = Object.assign(
          {
            fileType: 'BBBBBB',
            relativePathInST: 'BBBBBB',
            lTInsertionDate: currentDate.format(DATE_FORMAT),
            pathInLT: 'BBBBBB',
            format: 'BBBBBB',
            subSystemAtOriginOfData: 'BBBBBB',
            timeOfData: currentDate.format(DATE_FORMAT),
            securityLevel: 'BBBBBB',
            crc: 'BBBBBB'
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            lTInsertionDate: currentDate,
            timeOfData: currentDate
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

      it('should return a list of ScenarioFile', () => {
        const returnedFromService = Object.assign(
          {
            fileType: 'BBBBBB',
            relativePathInST: 'BBBBBB',
            lTInsertionDate: currentDate.format(DATE_FORMAT),
            pathInLT: 'BBBBBB',
            format: 'BBBBBB',
            subSystemAtOriginOfData: 'BBBBBB',
            timeOfData: currentDate.format(DATE_FORMAT),
            securityLevel: 'BBBBBB',
            crc: 'BBBBBB'
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            lTInsertionDate: currentDate,
            timeOfData: currentDate
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

      it('should delete a ScenarioFile', () => {
        service.delete('123').subscribe(resp => (expectedResult = resp.ok));

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
