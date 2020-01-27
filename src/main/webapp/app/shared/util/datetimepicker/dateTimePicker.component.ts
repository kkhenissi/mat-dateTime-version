import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { DateAdapter, MatDatepickerInputEvent } from '@angular/material';

import * as moment_ from 'moment';

const moment = moment_;

import { MAT_MOMENT_DATE_ADAPTER_OPTIONS } from '@angular/material-moment-adapter';

class DateConfig {
  startView: 'month' | 'year' | 'multi-year';
  touchUi: boolean;
  minDate: moment_.Moment;
  maxDate: moment_.Moment;
}

@Component({
  selector: 'jhi-datetimepicker',
  templateUrl: './dateTimePicker.component.html',
  styleUrls: ['./dateTimePicker.component.scss']
})
export class DatetimePickerComponent implements OnInit {
  @Input() disabled: boolean;
  @Input() placeholderDate: string;
  @Input() placeholderTime: string;
  @Input() model: Date;
  @Input() purpose: string;
  @Input() dateOnly: boolean;

  @Output() dateUpdate = new EventEmitter<Date>();

  public pickerId: string =
    '_' +
    Math.random()
      .toString(36)
      .substr(2, 9);

  public dateForm: FormControl;
  public timeFormGroup: FormGroup;
  public endTime: FormControl;

  public momentDate: moment_.Moment;
  public config: DateConfig;

  //myGroup: FormGroup;

  constructor(private adapter: DateAdapter<any>) {}

  ngOnInit() {
    this.adapter.setLocale('nl-NL'); //todo: configurable
    this.config = new DateConfig();
    if (this.purpose === 'birthday') {
      this.config.startView = 'multi-year';
      this.config.maxDate = moment().add('year', -15);
      this.config.minDate = moment().add('year', -90);
      this.dateOnly = true;
    } //add more configurations
    else {
      this.config.startView = 'month';
      this.config.maxDate = moment().add('year', 100);
      this.config.minDate = moment().add('year', -100);
    }

    if (window.screen.width < 767) {
      this.config.touchUi = true;
    }

    if (this.model) {
      let mom = moment(this.model);
      if (mom.isBefore(moment('1900-01-01'))) {
        this.momentDate = moment();
      } else {
        this.momentDate = mom;
      }
    } else {
      this.momentDate = moment();
    }

    this.dateForm = new FormControl(this.momentDate);
    if (this.disabled) {
      this.dateForm.disable();
    }
    this.endTime = new FormControl(this.momentDate.format('HH:mm'));

    this.timeFormGroup = new FormGroup({
      endTime: this.endTime
    });
  }

  public dateChange(date: MatDatepickerInputEvent<any>) {
    if (moment.isMoment(date.value)) {
      this.momentDate = moment(date.value);
      if (this.dateOnly) {
        this.momentDate = this.momentDate.utc(true);
      }
      let newDate = this.momentDate.toDate();
      this.model = newDate;
      this.dateUpdate.emit(newDate);
    }

    console.log('datechange', date);
  }

  public timeChange(time: string) {
    let splitted = time.split(':');
    let hour = splitted[0];
    let minute = splitted[1];

    console.log('time change', time);
    this.momentDate = this.momentDate.set('hour', parseInt(hour));
    this.momentDate = this.momentDate.set('minute', parseInt(minute));

    let newDate = this.momentDate.toDate();
    this.model = newDate;
    this.dateUpdate.emit(newDate);
  }
}
