import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PoModule, PoNotificationService, PoPageAction } from '@po-ui/ng-components';
import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CustomerService } from '../../../services/customer.service';

@Component({
  selector: 'app-client-form',
  standalone: true,
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    PoModule,
    HttpClientModule
  ],
  template: `
    <div class="client-form">
      <po-page-default [p-title]="isEditMode ? 'Editar Cliente' : 'Novo Cliente'" [p-actions]="pageActions">
        <form [formGroup]="personalDataForm" class="po-row">
          <div class="po-row">
            <div class="po-sm-12 po-md-6 po-lg-4">
              <po-input
                name="name"
                formControlName="name"
                p-label="Nome Completo"
                p-placeholder="Digite o nome completo"
                [p-required]="true">
              </po-input>
            </div>
            <div class="po-sm-12 po-md-6 po-lg-4">
              <po-input
                name="cpf"
                formControlName="cpf"
                p-label="CPF"
                p-placeholder="Digite o CPF"
                [p-required]="true"
                [p-mask]="'999.999.999-99'">
              </po-input>
            </div>
            <div class="po-sm-12 po-md-6 po-lg-4">
              <po-input
                name="email"
                formControlName="email"
                p-label="E-mail"
                p-placeholder="Digite o e-mail"
                [p-required]="true"
                p-type="email">
              </po-input>
            </div>
          </div>
        </form>
      </po-page-default>
    </div>
  `,
  styles: [`
    .client-form {
      height: 100%;
    }
    .po-row {
      margin-bottom: 1rem;
    }
  `]
})
export class ClientFormComponent implements OnInit {
  loading = false;
  isEditMode = false;
  customerId: string | null = null;

  personalDataForm: FormGroup;

  pageActions: Array<PoPageAction> = [
    {
      label: 'Salvar',
      action: this.save.bind(this)
    },
    {
      label: 'Cancelar',
      action: this.cancel.bind(this)
    }
  ];

  constructor(
    private fb: FormBuilder,
    private poNotification: PoNotificationService,
    private customerService: CustomerService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.personalDataForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(10)]],
      cpf: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit() {
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.customerId = params['id'];
        this.loadCustomerData(this.customerId);
      }
    });
  }

  private loadCustomerData(id: string) {
    this.loading = true;
    this.customerService.getCustomerById(id).subscribe({
      next: (customer) => {
        this.personalDataForm.patchValue({
          name: customer.name,
          cpf: customer.cpf,
          email: customer.email
        });
        this.loading = false;
      },
      error: (error) => {
        this.poNotification.error('Erro ao carregar dados do cliente');
        console.error('Error loading customer:', error);
        this.router.navigate(['/clients']);
      }
    });
  }

  save() {
    if (this.personalDataForm.valid) {
      this.loading = true;
      const clientData = {
        ...this.personalDataForm.value
      };

      if (this.isEditMode && this.customerId) {
        this.customerService.updateCustomer(this.customerId, clientData).subscribe({
          next: (response) => {
            this.poNotification.success('Cliente atualizado com sucesso!');
            this.router.navigate(['/clients', response.id]);
          },
          error: (error) => {
            const errorMessage = error.response?.data?.message || 'Erro ao atualizar cliente. Por favor, tente novamente.';
            this.poNotification.error(errorMessage);
            console.error('Erro ao atualizar cliente:', error);
            this.loading = false;
          }
        });
      } else {
        this.customerService.createCustomer(clientData).subscribe({
          next: (response) => {
            this.poNotification.success('Cliente salvo com sucesso!');
            this.router.navigate(['/clients', response.id]);
          },
          error: (error) => {
            const errorMessage = error.response?.data?.message || 'Erro ao salvar cliente. Por favor, tente novamente.';
            this.poNotification.error(errorMessage);
            console.error('Erro ao salvar cliente:', error);
            this.loading = false;
          }
        });
      }
    } else {
      this.poNotification.error('Por favor, preencha corretamente todos os campos obrigatórios.');
    }
  }

  cancel() {
    this.router.navigate(['/clients']);
  }
} 