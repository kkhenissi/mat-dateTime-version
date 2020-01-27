import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ScenarioDetailComponent } from 'app/entities/scenario/scenario-detail.component';
import { Scenario } from 'app/shared/model/scenario.model';

describe('Component Tests', () => {
  describe('Scenario Management Detail Component', () => {
    let comp: ScenarioDetailComponent;
    let fixture: ComponentFixture<ScenarioDetailComponent>;
    const route = ({ data: of({ scenario: new Scenario(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ScenarioDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ScenarioDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ScenarioDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.scenario).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
