import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ConfigDataSetComponent } from 'app/entities/config-data-set/config-data-set.component';
import { ConfigDataSetService } from 'app/shared/services/config-data-set.service';
import { ConfigDataSet } from 'app/shared/model/config-data-set.model';

describe('Component Tests', () => {
  describe('ConfigDataSet Management Component', () => {
    let comp: ConfigDataSetComponent;
    let fixture: ComponentFixture<ConfigDataSetComponent>;
    let service: ConfigDataSetService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ConfigDataSetComponent],
        providers: []
      })
        .overrideTemplate(ConfigDataSetComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ConfigDataSetComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ConfigDataSetService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new ConfigDataSet(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.configDataSets[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
