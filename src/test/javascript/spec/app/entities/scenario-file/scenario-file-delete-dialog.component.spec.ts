import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ScenarioFileDeleteDialogComponent } from 'app/entities/scenario/scenario-file-delete-dialog.component';
import { ScenarioFileService } from 'app/shared/services/scenario-file.service';

describe('Component Tests', () => {
  describe('ScenarioFile Management Delete Component', () => {
    let comp: ScenarioFileDeleteDialogComponent;
    let fixture: ComponentFixture<ScenarioFileDeleteDialogComponent>;
    let service: ScenarioFileService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ScenarioFileDeleteDialogComponent]
      })
        .overrideTemplate(ScenarioFileDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ScenarioFileDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ScenarioFileService);
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
          comp.confirmDelete('123');
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
