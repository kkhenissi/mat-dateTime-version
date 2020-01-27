import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ConfigDataSetDeleteDialogComponent } from 'app/entities/config-data-set/config-data-set-delete-dialog.component';
import { ConfigDataSetService } from 'app/shared/services/config-data-set.service';

describe('Component Tests', () => {
  describe('ConfigDataSet Management Delete Component', () => {
    let comp: ConfigDataSetDeleteDialogComponent;
    let fixture: ComponentFixture<ConfigDataSetDeleteDialogComponent>;
    let service: ConfigDataSetService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ConfigDataSetDeleteDialogComponent]
      })
        .overrideTemplate(ConfigDataSetDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ConfigDataSetDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ConfigDataSetService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
