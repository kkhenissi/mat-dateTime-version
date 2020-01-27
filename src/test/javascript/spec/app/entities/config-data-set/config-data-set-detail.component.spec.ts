import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ConfigDataSetDetailComponent } from 'app/entities/config-data-set/config-data-set-detail.component';
import { ConfigDataSet } from 'app/shared/model/config-data-set.model';

describe('Component Tests', () => {
  describe('ConfigDataSet Management Detail Component', () => {
    let comp: ConfigDataSetDetailComponent;
    let fixture: ComponentFixture<ConfigDataSetDetailComponent>;
    const route = ({ data: of({ configDataSet: new ConfigDataSet(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ConfigDataSetDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ConfigDataSetDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ConfigDataSetDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.configDataSet).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
