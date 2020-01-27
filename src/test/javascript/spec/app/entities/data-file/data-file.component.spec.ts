import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { ArchivemanagerTestModule } from '../../../test.module';
import { DataFileComponent } from 'app/entities/data-file/data-file.component';
import { DataFileService } from 'app/shared/services/data-file.service';
import { DataFile } from 'app/shared/model/data-file.model';

describe('Component Tests', () => {
  describe('DataFile Management Component', () => {
    let comp: DataFileComponent;
    let fixture: ComponentFixture<DataFileComponent>;
    let service: DataFileService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [DataFileComponent],
        providers: []
      })
        .overrideTemplate(DataFileComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DataFileComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DataFileService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new DataFile(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.dataFiles[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
