import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ToolVersionDeleteDialogComponent } from 'app/entities/tool-version/tool-version-delete-dialog.component';
import { ToolVersionService } from 'app/entities/tool-version/tool-version.service';

describe('Component Tests', () => {
  describe('ToolVersion Management Delete Component', () => {
    let comp: ToolVersionDeleteDialogComponent;
    let fixture: ComponentFixture<ToolVersionDeleteDialogComponent>;
    let service: ToolVersionService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ToolVersionDeleteDialogComponent]
      })
        .overrideTemplate(ToolVersionDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ToolVersionDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ToolVersionService);
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
