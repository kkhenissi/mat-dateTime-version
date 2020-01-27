import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ArchivemanagerTestModule } from '../../../test.module';
import { RunDeleteDialogComponent } from 'app/entities/run/run-delete-dialog.component';
import { RunService } from 'app/entities/run/run.service';

describe('Component Tests', () => {
  describe('Run Management Delete Component', () => {
    let comp: RunDeleteDialogComponent;
    let fixture: ComponentFixture<RunDeleteDialogComponent>;
    let service: RunService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [RunDeleteDialogComponent]
      })
        .overrideTemplate(RunDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(RunDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(RunService);
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
