/* --------------------------------------------------------------------------------------------------------------------
// Copyright file="validationservice.ts"
//  © Copyright European Space Agency, 2018-2020
//
//  Author: Software developed by RHEA System S.A.
//
//  This file is subject to the terms and conditions defined in file 'LICENSE.txt', which is part of this source code package.
//  No part of the package, including this file, may be copied, modified, propagated, or distributed
//  except according to the terms contained in the file ‘LICENSE.txt’.
// --------------------------------------------------------------------------------------------------------------------
*/

export class ValidationService {


  static getValidatorErrorMessage(validatorName: string, validatorValue?: any) {
    const config = {
      'required': 'Required',
      'invalidCreditCard': 'Is invalid credit card number',
      'invalidEmailAddress': 'Invalid email address',
      'invalidPassword': 'Invalid password. Password must be at least 6 characters long,a number,a special character,one upper and lower case ',
      'invalidPasswordUser': 'Invalid password. Password must not contain the username!',
      'invalidUsername': 'Invalid username. Username must not contain the password!',
      'passwordMismatch': 'Password mismatch ',
      'minlength': `Minimum length ${validatorValue.requiredLength}`,
      'maxlength': `Maximun length ${validatorValue.requiredLength}`,
      'invalidThreatEventName': 'Wrong event name format'
    };

    return config[validatorName];
  }


  static passwordMatchValidator(control) {

    if (control.value) {


      if (control.value === control.parent.value.password) {

        return null;
      } else {

        return { 'passwordMismatch': true };

      }
    }
  }

  static passwordMatchValidatorForPassword(control) {

    if (control.value) {

      if (control.parent.value.confirm.length > 0) {
        if (control.value === control.parent.value.confirm) {

          return null;
        } else {

          return { 'passwordMismatch': true };

        }
      }
    }
  }


  static usernameCheckValidator(control) {



    if (control.value) {


      if (control.parent.value.username.length > 0) {

        if (!control.value.toUpperCase().includes(control.parent.value.username.toUpperCase())) {

          return null;
        } else {

          return { 'invalidPasswordUser': true };
        }
      }
    }


  }

  static passwordUsernameCheckValidator(control) {



    if (control.value) {

      if (control.parent.value.password.length > 0) {

        if (!control.value.toUpperCase().includes(control.parent.value.password.toUpperCase())) {

          return null;
        } else {

          return { 'invalidUsername': true };
        }
      }
    }


  }




  static passwordCheckValidator(control) {

    if (control.value) {

      if (!control.value.toUpperCase().includes(control.parent.value.password.toUpperCase())) {


        return null;
      } else {

        return { 'invalidUsername': true };
      }
    }
  }

  static emailValidator(control) {

    if (control.value.match(/[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?/)) {
      return null;
    } else {
      return { 'invalidEmailAddress': true };
    }
  }

  static passwordValidator(control) {
    // {6,100}           - Assert password is between 6 and 100 characters
    // (?=.*[0-9])       - Assert a string has at least one number
    if (control.value.match(/^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\s).{6,18}$/)) {
      return null;
    } else {
      return { 'invalidPassword': true };
    }
  }

  static threatEventNameValidator(control) {

    if (control.value.match(/^(\w+\.\w+\.\w+)$/)) {
      return null;
    } else {
      return { 'invalidThreatEventName': true };
    }
  }
}


