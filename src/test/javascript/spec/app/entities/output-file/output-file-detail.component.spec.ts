import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { OutputFileDetailComponent } from 'app/entities/output-file/output-file-detail.component';
import { OutputFile } from 'app/shared/model/output-file.model';

describe('Component Tests', () => {
  describe('OutputFile Management Detail Component', () => {
    let comp: OutputFileDetailComponent;
    let fixture: ComponentFixture<OutputFileDetailComponent>;
    const route = ({ data: of({ outputFile: new OutputFile(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [OutputFileDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(OutputFileDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(OutputFileDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.outputFile).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
