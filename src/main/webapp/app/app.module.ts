import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FlexLayoutModule } from '@angular/flex-layout';

import './vendor';
import { AuthInterceptor } from './blocks/interceptor/auth.interceptor';
import { AuthExpiredInterceptor } from './blocks/interceptor/auth-expired.interceptor';
import { ErrorHandlerInterceptor } from './blocks/interceptor/errorhandler.interceptor';
import { NotificationInterceptor } from './blocks/interceptor/notification.interceptor';
import { ArchivemanagerSharedModule } from 'app/shared/shared.module';
import { ArchivemanagerCoreModule } from 'app/core/core.module';
import { ArchivemanagerAppRoutingModule } from './app-routing.module';
import { ArchivemanagerHomeModule } from './home/home.module';
import { ArchivemanagerEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { JhiMainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';
// import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';
import { MaterialModule } from './material/material.module';
import { FormsModule } from '@angular/forms';
import { PlatformModule } from '@angular/cdk/platform';
import { ObserversModule } from '@angular/cdk/observers';
import { MAT_DATE_LOCALE, MAT_DATE_FORMATS } from '@angular/material';
import { MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';
import { MY_FORMAT } from './shared/util/frenshDateFormat';
 
 
// import { DatetimepickerComponent } from './shared/util/datetimepicker/dateTimePicker.component';
@NgModule({
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    ArchivemanagerSharedModule,
    ArchivemanagerCoreModule,
    ArchivemanagerHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    ArchivemanagerEntityModule,
    ArchivemanagerAppRoutingModule,
   // NgxMaterialTimepickerModule,
    MaterialModule,
    FlexLayoutModule,
    FormsModule,
    PlatformModule,
    ObserversModule,


  ],
  declarations: [JhiMainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthExpiredInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorHandlerInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: NotificationInterceptor,
      multi: true
    },
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' },
    { provide: MAT_DATE_FORMATS, useValue: MY_FORMAT },
    { provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS, useValue: { useUtc: true } }
  ],
  bootstrap: [JhiMainComponent]
})
export class ArchivemanagerAppModule {}
