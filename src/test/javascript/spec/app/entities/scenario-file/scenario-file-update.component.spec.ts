import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ScenarioFileUpdateComponent } from 'app/entities/scenario-file/scenario-file-update.component';
import { ScenarioFileService } from 'app/shared/services/scenario-file.service';
import { ScenarioFile } from 'app/shared/model/scenario-file.model';

describe('Component Tests', () => {
  describe('ScenarioFile Management Update Component', () => {
    let comp: ScenarioFileUpdateComponent;
    let fixture: ComponentFixture<ScenarioFileUpdateComponent>;
    let service: ScenarioFileService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ScenarioFileUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ScenarioFileUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ScenarioFileUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ScenarioFileService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ScenarioFile(123);
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
        const entity = new ScenarioFile();
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
