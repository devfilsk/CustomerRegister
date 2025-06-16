import { Component, Input, Output, EventEmitter, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { PoModule } from '@po-ui/ng-components';
import { Cliente, Telefone, Endereco } from '../../models/cliente.model';
import { CustomValidators } from '../../utils/validators';
import { ClienteService } from '../../services/cliente.service';
import { PoNotificationService } from '@po-ui/ng-components';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PoModule],
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.scss']
})
export class ClienteFormComponent implements OnInit, OnChanges {
  @Input() clienteEdicao?: Cliente;
  @Output() clienteSalvo = new EventEmitter<Cliente>();
  @Output() cancelado = new EventEmitter<void>();

  clienteForm!: FormGroup;
  loading = false;

  readonly tiposTelefone = [
    { label: 'Residencial', value: 'residencial' },
    { label: 'Comercial', value: 'comercial' },
    { label: 'Celular', value: 'celular' }
  ] as const;

  readonly tiposEndereco = [
    { label: 'Residencial', value: 'residencial' },
    { label: 'Comercial', value: 'comercial' }
  ] as const;

  readonly estados = [
    { label: 'Acre', value: 'AC' },
    { label: 'Alagoas', value: 'AL' },
    { label: 'Amapá', value: 'AP' },
    { label: 'Amazonas', value: 'AM' },
    { label: 'Bahia', value: 'BA' },
    { label: 'Ceará', value: 'CE' },
    { label: 'Distrito Federal', value: 'DF' },
    { label: 'Espírito Santo', value: 'ES' },
    { label: 'Goiás', value: 'GO' },
    { label: 'Maranhão', value: 'MA' },
    { label: 'Mato Grosso', value: 'MT' },
    { label: 'Mato Grosso do Sul', value: 'MS' },
    { label: 'Minas Gerais', value: 'MG' },
    { label: 'Pará', value: 'PA' },
    { label: 'Paraíba', value: 'PB' },
    { label: 'Paraná', value: 'PR' },
    { label: 'Pernambuco', value: 'PE' },
    { label: 'Piauí', value: 'PI' },
    { label: 'Rio de Janeiro', value: 'RJ' },
    { label: 'Rio Grande do Norte', value: 'RN' },
    { label: 'Rio Grande do Sul', value: 'RS' },
    { label: 'Rondônia', value: 'RO' },
    { label: 'Roraima', value: 'RR' },
    { label: 'Santa Catarina', value: 'SC' },
    { label: 'São Paulo', value: 'SP' },
    { label: 'Sergipe', value: 'SE' },
    { label: 'Tocantins', value: 'TO' }
  ] as const;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly clienteService: ClienteService,
    private readonly poNotification: PoNotificationService
  ) {}

  ngOnInit(): void {
    this.criarFormulario();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['clienteEdicao'] && this.clienteForm) {
      this.preencherFormulario();
    }
  }

  get telefones(): FormArray {
    return this.clienteForm.get('telefones') as FormArray;
  }

  get enderecos(): FormArray {
    return this.clienteForm.get('enderecos') as FormArray;
  }

  private criarFormulario(): void {
    this.clienteForm = this.formBuilder.group({
      nome: ['', [Validators.required, Validators.minLength(3)]],
      cpf: ['', [Validators.required, CustomValidators.cpf]],
      email: ['', [Validators.required, Validators.email]],
      telefones: this.formBuilder.array([this.criarTelefoneFormGroup()]),
      enderecos: this.formBuilder.array([this.criarEnderecoFormGroup()])
    });

    if (this.clienteEdicao) {
      this.preencherFormulario();
    }
  }

  private preencherFormulario(): void {
    if (!this.clienteEdicao) return;

    this.clienteForm.patchValue({
      nome: this.clienteEdicao.nome,
      cpf: this.clienteEdicao.cpf,
      email: this.clienteEdicao.email
    });

    // Limpar arrays existentes
    while (this.telefones.length) {
      this.telefones.removeAt(0);
    }
    while (this.enderecos.length) {
      this.enderecos.removeAt(0);
    }

    // Adicionar telefones
    this.clienteEdicao.telefones.forEach(telefone => {
      this.telefones.push(this.criarTelefoneFormGroup(telefone));
    });

    // Adicionar endereços
    this.clienteEdicao.enderecos.forEach(endereco => {
      this.enderecos.push(this.criarEnderecoFormGroup(endereco));
    });
  }

  criarTelefoneFormGroup(telefone?: Telefone): FormGroup {
    return this.formBuilder.group({
      tipo: [telefone?.tipo || 'celular', Validators.required],
      numero: [telefone?.numero || '', [Validators.required, CustomValidators.telefone]]
    });
  }

  criarEnderecoFormGroup(endereco?: Endereco): FormGroup {
    return this.formBuilder.group({
      tipo: [endereco?.tipo || 'residencial', Validators.required],
      cep: [endereco?.cep || '', [Validators.required, CustomValidators.cep]],
      logradouro: [endereco?.logradouro || '', Validators.required],
      numero: [endereco?.numero || '', Validators.required],
      complemento: [endereco?.complemento || ''],
      bairro: [endereco?.bairro || '', Validators.required],
      cidade: [endereco?.cidade || '', Validators.required],
      estado: [endereco?.estado || '', Validators.required]
    });
  }

  adicionarTelefone(): void {
    this.telefones.push(this.criarTelefoneFormGroup());
  }

  removerTelefone(index: number): void {
    if (this.telefones.length > 1) {
      this.telefones.removeAt(index);
    }
  }

  adicionarEndereco(): void {
    this.enderecos.push(this.criarEnderecoFormGroup());
  }

  removerEndereco(index: number): void {
    if (this.enderecos.length > 1) {
      this.enderecos.removeAt(index);
    }
  }

  onSubmit(): void {
    if (this.clienteForm.valid) {
      this.loading = true;
      const clienteData = {
        ...this.clienteForm.value,
        id: this.clienteEdicao?.id,
        dataCadastro: this.clienteEdicao?.dataCadastro || new Date()
      };

      const action = this.clienteEdicao
        ? this.clienteService.atualizarCliente(clienteData.id!, clienteData)
        : this.clienteService.adicionarCliente(clienteData);

      action.subscribe({
        next: (cliente) => {
          this.poNotification.success(
            this.clienteEdicao
              ? 'Cliente atualizado com sucesso!'
              : 'Cliente cadastrado com sucesso!'
          );
          this.clienteSalvo.emit(cliente);
        },
        error: (error: Error) => {
          this.poNotification.error(
            this.clienteEdicao
              ? 'Erro ao atualizar cliente'
              : 'Erro ao cadastrar cliente'
          );
          console.error('Erro ao salvar cliente:', error);
        },
        complete: () => {
          this.loading = false;
        }
      });
    }
  }

  onCancel(): void {
    this.cancelado.emit();
  }
}