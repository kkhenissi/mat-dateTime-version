import { FormControl, FormGroup, SelectControlValueAccessor, SelectMultipleControlValueAccessor } from '@angular/forms';
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import { EnumSecurityLevel } from './model/enumerations/security-level.model';

export default class MethodeGenerique {

  /**
  * Fixes IE11 forms bug : Placeholders trigger and set an empty value ('') in the control.
  * This allows to reset the values to null to allow manual filtering, detection and comparison.
  * @param formGroup any angular form
   */
  static resetEmptyFormValues(formGroup: FormGroup) {
    for (const key in formGroup.controls) {
      if (formGroup.controls[key] && formGroup.controls[key].value === '') {
        formGroup.controls[key].setValue(null, { emitValue: false });
      }
    }
  }


  /**
   * Manages the reset of an autocomplete control (and sets the control to dirty to trigger canDeactivate)
   * @param control a control of a Form
   */
  static setToNullValue(control: FormControl) {
    if(control instanceof SelectMultipleControlValueAccessor) {
        // control.setValue(EnumSecurityLevel.NORMAL);
    }
    control.setValue(null);
    control.markAsDirty();
  }
}

