import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { RunUpdateComponent } from 'app/entities/run/run-update.component';
import { RunService } from 'app/entities/run/run.service';
import { Run } from 'app/shared/model/run.model';

describe('Component Tests', () => {
  describe('Run Management Update Component', () => {
    let comp: RunUpdateComponent;
    let fixture: ComponentFixture<RunUpdateComponent>;
    let service: RunService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [RunUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(RunUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(RunUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RunService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Run(123);
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
        const entity = new Run();
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
