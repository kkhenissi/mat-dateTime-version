import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ArchivemanagerTestModule } from '../../../test.module';
import { ScenarioDeleteDialogComponent } from 'app/entities/scenario/scenario-delete-dialog.component';
import { ScenarioService } from 'app/shared/services/scenario.service';

describe('Component Tests', () => {
  describe('Scenario Management Delete Component', () => {
    let comp: ScenarioDeleteDialogComponent;
    let fixture: ComponentFixture<ScenarioDeleteDialogComponent>;
    let service: ScenarioService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [ArchivemanagerTestModule],
        declarations: [ScenarioDeleteDialogComponent]
      })
        .overrideTemplate(ScenarioDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ScenarioDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ScenarioService);
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
