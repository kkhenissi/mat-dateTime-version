import { Component, Input, OnInit } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators
} from "@angular/forms";
import { DateAdapter } from "@angular/material/core";
import { MomentDateAdapter, MAT_MOMENT_DATE_ADAPTER_OPTIONS } from "@angular/material-moment-adapter";
import {
  DatetimeAdapter,
  MAT_DATETIME_FORMATS,
  MatDatetimepickerFilterType
} from "@mat-datetimepicker/core";
import { MomentDatetimeAdapter } from "@mat-datetimepicker/moment";
import {
  Moment,
  utc
} from "moment/moment";

@Component({
  selector: "jhi-moment-datetime",
  templateUrl: "./date.component.html",
  providers: [
    {
      provide: DateAdapter,
      useClass: MomentDateAdapter
    },
    {
      provide: DatetimeAdapter,
      useClass: MomentDatetimeAdapter
    },
    {
      provide: MAT_MOMENT_DATE_ADAPTER_OPTIONS,
      useValue: {
        useUtc: true
      }
    },
    {
      provide: MAT_DATETIME_FORMATS,
      useValue: {
        parse: {
          dateInput: "L",
          monthInput: "MMMM",
          timeInput: "LT",
          datetimeInput: "L LT"
        },
        display: {
          dateInput: "L",
          monthInput: "MMMM",
          datetimeInput: "L LT ZZ",
          timeInput: "LT",
          monthYearLabel: "MMM YYYY",
          dateA11yLabel: "LL",
          monthYearA11yLabel: "MMMM YYYY",
          popupHeaderDateLabel: "ddd, DD MMM"
        }
      }
    }
  ]
})
export class MomentDatetimeComponent implements OnInit{

  type = "moment";
  @Input('placeHolder') placeHolder;

  group: FormGroup;
  today: Moment;
  tomorrow: Moment;
  @Input('min') min: Moment;
  @Input('max') max: Moment;
  @Input('whenDate') whenDate;
  @Input('name')  name;

  start: Moment;
  filter: (date: Moment, type: MatDatetimepickerFilterType) => boolean;

  constructor(fb: FormBuilder) {
    this.today = utc();
    this.tomorrow = utc().date(utc().date() + 1);
    this.min = this.today.clone().year(2018).month(10).date(3).hour(11).minute(10);
    this.max = this.min.clone().date(4).minute(45);
    this.start = this.today.clone().year(1930).month(9).date(28);
    this.filter = (date: Moment, type: MatDatetimepickerFilterType) => {
      switch (type) {
        case MatDatetimepickerFilterType.DATE:
          return date.year() % 2 === 0 &&
            date.month() % 2 === 0 &&
            date.date() % 2 === 0;
        case MatDatetimepickerFilterType.HOUR:
          return date.hour() % 2 === 0;
        case MatDatetimepickerFilterType.MINUTE:
          return date.minute() % 2 === 0;
      }
    };
    // , Validators.required
    this.group = fb.group({
      dateTime: [new Date("2017-11-09T12:10:00.000Z")],
      date: [null],
      time: [null],
      month: [null],
      mintest: [this.today],
      filtertest: [this.today],
      touch: [null]
    });
  }
  ngOnInit(): void {
    console.log('min ===>', this.min)
    console.log('max ===>', this.max)
    console.log(' formControlName ===>', this.whenDate)
    console.log(' name ===>', this.name)
  }
}
