import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { ArchivemanagerTestModule } from '../../../test.module';
import { DataSetUpdateComponent } from 'app/entities/data-set/data-set-update.component';
import { DataSetService } from 'app/shared/services/data-set.service';
import { DataSet } from 'app/shared/model/data-set.model';

describe('Component Tests', () => {
  describe('DataSet Management Update Component', () => {
    let comp: DataSetUpdateComponent;
    let fixture: ComponentFixture<DataSetUpdateComponent>;
    let service: DataSetService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [DataSetUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(DataSetUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(DataSetUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DataSetService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new DataSet(123);
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
        const entity = new DataSet();
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
