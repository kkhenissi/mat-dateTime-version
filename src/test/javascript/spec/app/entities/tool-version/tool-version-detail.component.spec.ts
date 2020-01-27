import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ToolVersionDetailComponent } from 'app/entities/tool-version/tool-version-detail.component';
import { ToolVersion } from 'app/shared/model/tool-version.model';

describe('Component Tests', () => {
  describe('ToolVersion Management Detail Component', () => {
    let comp: ToolVersionDetailComponent;
    let fixture: ComponentFixture<ToolVersionDetailComponent>;
    const route = ({ data: of({ toolVersion: new ToolVersion(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ToolVersionDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ToolVersionDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ToolVersionDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.toolVersion).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
