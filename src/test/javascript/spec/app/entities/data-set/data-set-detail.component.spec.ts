import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { DataSetDetailComponent } from 'app/entities/data-set/data-set-detail.component';
import { DataSet } from 'app/shared/model/data-set.model';

describe('Component Tests', () => {
  describe('DataSet Management Detail Component', () => {
    let comp: DataSetDetailComponent;
    let fixture: ComponentFixture<DataSetDetailComponent>;
    const route = ({ data: of({ dataSet: new DataSet(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [DataSetDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(DataSetDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(DataSetDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.dataSet).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
