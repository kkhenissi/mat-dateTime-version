import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ScenarioFileDetailComponent } from 'app/entities/scenario-file/scenario-file-detail.component';
import { ScenarioFile } from 'app/shared/model/scenario-file.model';

describe('Component Tests', () => {
  describe('ScenarioFile Management Detail Component', () => {
    let comp: ScenarioFileDetailComponent;
    let fixture: ComponentFixture<ScenarioFileDetailComponent>;
    const route = ({ data: of({ scenarioFile: new ScenarioFile(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ScenarioFileDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ScenarioFileDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ScenarioFileDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.scenarioFile).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
