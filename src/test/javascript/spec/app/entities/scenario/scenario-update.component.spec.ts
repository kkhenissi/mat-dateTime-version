import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ScenarioUpdateComponent } from 'app/entities/scenario/scenario-update.component';
import { ScenarioService } from 'app/shared/services/scenario.service';
import { Scenario } from 'app/shared/model/scenario.model';

describe('Component Tests', () => {
  describe('Scenario Management Update Component', () => {
    let comp: ScenarioUpdateComponent;
    let fixture: ComponentFixture<ScenarioUpdateComponent>;
    let service: ScenarioService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ScenarioUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ScenarioUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ScenarioUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ScenarioService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Scenario(123);
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
        const entity = new Scenario();
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
