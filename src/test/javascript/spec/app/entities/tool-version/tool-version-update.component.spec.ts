import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ToolVersionUpdateComponent } from 'app/entities/tool-version/tool-version-update.component';
import { ToolVersionService } from 'app/entities/tool-version/tool-version.service';
import { ToolVersion } from 'app/shared/model/tool-version.model';

describe('Component Tests', () => {
  describe('ToolVersion Management Update Component', () => {
    let comp: ToolVersionUpdateComponent;
    let fixture: ComponentFixture<ToolVersionUpdateComponent>;
    let service: ToolVersionService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ToolVersionUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ToolVersionUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ToolVersionUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ToolVersionService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ToolVersion(123);
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
        const entity = new ToolVersion();
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
