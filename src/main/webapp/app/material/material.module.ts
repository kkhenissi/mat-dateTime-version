import { NgModule } from '@angular/core';
import { MatTabsModule, MatIconModule, MatRadioModule, MatSelectModule, MatListModule, MatInputModule, MatSlideToggleModule, MatRippleModule, MatDatepickerModule, MatNativeDateModule, MatCardModule, MatFormFieldModule, MatCheckboxModule, MatSnackBarModule, MatTableModule, MatPaginatorModule, MatSortModule, MatGridListModule, MatAutocompleteModule, MatToolbarModule, MatProgressSpinnerModule } from '@angular/material';
import {MatButtonModule} from '@angular/material/button'
import { NgxMaterialTimepickerModule } from 'ngx-material-timepicker';
import { MatMomentDateModule } from '@angular/material-moment-adapter';
import { ScrollingModule } from '@angular/cdk/scrolling';
import { AmazingTimePickerModule } from 'amazing-time-picker';
import { MatDatetimepickerModule } from '@mat-datetimepicker/core';
import { MatMomentDatetimeModule } from '@mat-datetimepicker/moment';

const MaterialComponents = [
  NgxMaterialTimepickerModule,
  MatButtonModule,
  MatTabsModule,
  MatIconModule,
  ScrollingModule,
  AmazingTimePickerModule,
  MatRadioModule,
  MatSelectModule,
  MatGridListModule,
  MatListModule,
  MatInputModule,
  MatSlideToggleModule,
  MatInputModule,
  MatRippleModule,
  MatFormFieldModule,
  MatDatepickerModule,
  MatMomentDateModule,
  MatNativeDateModule,
  MatCardModule,
  MatCheckboxModule,
  MatSnackBarModule,
  MatTableModule,
  MatPaginatorModule,
  MatSortModule,
  MatAutocompleteModule,
  MatToolbarModule,
  MatProgressSpinnerModule,
  MatMomentDatetimeModule,
  MatDatetimepickerModule
]
@NgModule({

  imports: [
    MaterialComponents
  ],
  exports: [
    MaterialComponents
  ]
})
export class MaterialModule { }

