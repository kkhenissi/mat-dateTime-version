import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ArchivemanagerTestModule } from '../../../test.module';
import { OutputFileDeleteDialogComponent } from 'app/entities/output-file/output-file-delete-dialog.component';
import { OutputFileService } from 'app/shared/services/output-file.service';

describe('Component Tests', () => {
  describe('OutputFile Management Delete Component', () => {
    let comp: OutputFileDeleteDialogComponent;
    let fixture: ComponentFixture<OutputFileDeleteDialogComponent>;
    let service: OutputFileService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [OutputFileDeleteDialogComponent]
      })
        .overrideTemplate(OutputFileDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(OutputFileDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(OutputFileService);
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
