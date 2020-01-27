import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { ArchivemanagerTestModule } from '../../../test.module';
import { DataSetComponent } from 'app/entities/data-set/data-set.component';
import { DataSetService } from 'app/shared/services/data-set.service';
import { DataSet } from 'app/shared/model/data-set.model';

describe('Component Tests', () => {
  describe('DataSet Management Component', () => {
    let comp: DataSetComponent;
    let fixture: ComponentFixture<DataSetComponent>;
    let service: DataSetService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [DataSetComponent],
        providers: []
      })
        .overrideTemplate(DataSetComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DataSetComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DataSetService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new DataSet(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.dataSets[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
