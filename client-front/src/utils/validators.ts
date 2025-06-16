import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export class CustomValidators {
  static cpf(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null;
    }

    const cpf = control.value.replace(/\D/g, '');

    if (cpf.length !== 11) {
      return { cpf: true };
    }

    // Verifica se todos os dígitos são iguais
    if (/^(\d)\1{10}$/.test(cpf)) {
      return { cpf: true };
    }

    // Validação do primeiro dígito verificador
    let soma = 0;
    for (let i = 0; i < 9; i++) {
      soma += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let resto = 11 - (soma % 11);
    if (resto === 10 || resto === 11) {
      resto = 0;
    }
    if (resto !== parseInt(cpf.charAt(9))) {
      return { cpf: true };
    }

    // Validação do segundo dígito verificador
    soma = 0;
    for (let i = 0; i < 10; i++) {
      soma += parseInt(cpf.charAt(i)) * (11 - i);
    }
    resto = 11 - (soma % 11);
    if (resto === 10 || resto === 11) {
      resto = 0;
    }
    if (resto !== parseInt(cpf.charAt(10))) {
      return { cpf: true };
    }

    return null;
  }

  static telefone(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null;
    }

    const telefone = control.value.replace(/\D/g, '');
    
    if (telefone.length < 10 || telefone.length > 11) {
      return { telefone: true };
    }

    return null;
  }

  static cep(control: AbstractControl): ValidationErrors | null {
    if (!control.value) {
      return null;
    }

    const cep = control.value.replace(/\D/g, '');
    
    if (cep.length !== 8) {
      return { cep: true };
    }

    return null;
  }
}