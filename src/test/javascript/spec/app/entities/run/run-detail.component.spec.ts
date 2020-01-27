import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { RunDetailComponent } from 'app/entities/run/run-detail.component';
import { Run } from 'app/shared/model/run.model';

describe('Component Tests', () => {
  describe('Run Management Detail Component', () => {
    let comp: RunDetailComponent;
    let fixture: ComponentFixture<RunDetailComponent>;
    const route = ({ data: of({ run: new Run(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [RunDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(RunDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RunDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.run).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
