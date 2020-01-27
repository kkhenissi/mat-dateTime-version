import { NgModule } from '@angular/core';
import { ArchivemanagerSharedLibsModule } from './shared-libs.module';
import { JhiAlertComponent } from './alert/alert.component';
import { JhiAlertErrorComponent } from './alert/alert-error.component';
import { JhiLoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { MaterialModule } from 'app/material/material.module';
import { FlexLayoutModule } from '@angular/flex-layout';
import { ConfirmPopupComponent } from './confirm-popup/confirm-popup.component';
import { DeletePopupComponent } from './delete-popup/delete-popup.component';

/**
 * import for mat-DatTime-component
 */
import { ReactiveFormsModule } from "@angular/forms";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatInputModule } from "@angular/material/input";
import { BrowserModule } from "@angular/platform-browser";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MatDatetimepickerModule } from "@mat-datetimepicker/core";
import { MomentDatetimeComponent } from './util/moment.component';
//import { MomentDatetimeComponent } from "./moment.component";

// import { SearchPopupComponent } from '../entities/scenario/search-popup/search-popup.component';
// import { AddPopupComponent } from './add-popup/add-popup.component';

@NgModule({
  imports: [ArchivemanagerSharedLibsModule,MaterialModule,FlexLayoutModule],
  declarations: [JhiAlertComponent, JhiAlertErrorComponent, JhiLoginModalComponent, HasAnyAuthorityDirective, ConfirmPopupComponent, DeletePopupComponent,MomentDatetimeComponent],
  entryComponents: [JhiLoginModalComponent,MomentDatetimeComponent],
  exports: [ArchivemanagerSharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent,
     JhiLoginModalComponent, HasAnyAuthorityDirective, MaterialModule, FlexLayoutModule,MomentDatetimeComponent]
})
// , AddPopupComponent SearchPopupComponent
export class ArchivemanagerSharedModule {}
