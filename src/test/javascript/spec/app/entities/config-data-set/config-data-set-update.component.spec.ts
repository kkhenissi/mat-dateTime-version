import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ConfigDataSetUpdateComponent } from 'app/entities/config-data-set/config-data-set-update.component';
import { ConfigDataSetService } from 'app/shared/services/config-data-set.service';
import { ConfigDataSet } from 'app/shared/model/config-data-set.model';

describe('Component Tests', () => {
  describe('ConfigDataSet Management Update Component', () => {
    let comp: ConfigDataSetUpdateComponent;
    let fixture: ComponentFixture<ConfigDataSetUpdateComponent>;
    let service: ConfigDataSetService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ConfigDataSetUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ConfigDataSetUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ConfigDataSetUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ConfigDataSetService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ConfigDataSet(123);
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
        const entity = new ConfigDataSet();
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
