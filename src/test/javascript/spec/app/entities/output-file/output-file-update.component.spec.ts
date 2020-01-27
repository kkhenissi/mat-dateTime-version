import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { OutputFileUpdateComponent } from 'app/entities/output-file/output-file-update.component';
import { OutputFileService } from 'app/shared/services/output-file.service';
import { OutputFile } from 'app/shared/model/output-file.model';

describe('Component Tests', () => {
  describe('OutputFile Management Update Component', () => {
    let comp: OutputFileUpdateComponent;
    let fixture: ComponentFixture<OutputFileUpdateComponent>;
    let service: OutputFileService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [OutputFileUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(OutputFileUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OutputFileUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(OutputFileService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new OutputFile(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new OutputFile();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
