import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PoModule, PoPageAction, PoNotificationService, PoModalComponent } from '@po-ui/ng-components';
import { CustomerService, Customer } from '../../../services/customer.service';

@Component({
  selector: 'app-client-details',
  standalone: true,
  imports: [CommonModule, RouterModule, PoModule, FormsModule],
  template: `
    <div class="client-details">
      <po-page-default p-title="Detalhes do Cliente" [p-actions]="pageActions">
        <div class="po-row" *ngIf="customer">
          <!-- Dados Pessoais -->
          <div class="po-sm-12">
            <div class="section-card">
              <h3 class="section-title">Dados Pessoais</h3>
              <div class="po-row">
                <div class="po-sm-12 po-md-4">
                  <po-info p-label="Nome" [p-value]="customer.name"></po-info>
                </div>
                <div class="po-sm-12 po-md-4">
                  <po-info p-label="CPF" [p-value]="customer.cpf"></po-info>
                </div>
                <div class="po-sm-12 po-md-4">
                  <po-info p-label="E-mail" [p-value]="customer.email"></po-info>
                </div>
              </div>
            </div>
          </div>

          <!-- Telefones -->
          <div class="po-sm-12">
            <div class="section-card">
              <div class="section-header">
                <h3 class="section-title">Telefones</h3>
                <po-button
                  p-icon="po-icon-plus"
                  p-label="Adicionar Telefone"
                  p-type="primary"
                  (p-click)="openPhoneModal()">
                </po-button>
              </div>
              <div class="po-row" *ngIf="customer.phones?.length">
                <div class="po-sm-12" *ngFor="let contact of customer.phones">
                  <div class="item-card">
                    <div class="po-row">
                      <div class="po-sm-12 po-md-6">
                        <po-info p-label="Telefone" [p-value]="contact.number"></po-info>
                      </div>
                      <div class="po-sm-12 po-md-6 po-align-right">
                        <po-button
                          p-icon="po-icon-edit"
                          p-label="Editar"
                          p-type="primary"
                          (p-click)="editPhone(contact)">
                        </po-button>
                        <po-button
                          p-icon="po-icon-delete"
                          p-label="Excluir"
                          p-type="danger"
                          (p-click)="deletePhone(contact)">
                        </po-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- Endereços -->
          <div class="po-sm-12">
            <div class="section-card">
              <div class="section-header">
                <h3 class="section-title">Endereços</h3>
                <po-button
                  p-icon="po-icon-plus"
                  p-label="Adicionar Endereço"
                  p-type="primary"
                  (p-click)="openAddressModal()">
                </po-button>
              </div>
              <div class="po-row" *ngIf="customer.addresses?.length">
                <div class="po-sm-12" *ngFor="let address of customer.addresses">
                  <div class="item-card">
                    <div class="po-row">
                      <div class="po-sm-12 po-md-6">
                        <po-info p-label="CEP" [p-value]="address.cep"></po-info>
                        <po-info p-label="Logradouro" [p-value]="address.street"></po-info>
                        <po-info p-label="Número" [p-value]="address.number"></po-info>
                        <po-info p-label="Complemento" [p-value]="address.complement || '-'"></po-info>
                        <po-info p-label="Bairro" [p-value]="address.neighborhood"></po-info>
                        <po-info p-label="Cidade" [p-value]="address.city"></po-info>
                        <po-info p-label="Estado" [p-value]="address.uf"></po-info>
                      </div>
                      <div class="po-sm-12 po-md-6 po-align-right">
                        <po-button
                          p-icon="po-icon-edit"
                          p-label="Editar"
                          p-type="primary"
                          (p-click)="editAddress(address)">
                        </po-button>
                        <po-button
                          p-icon="po-icon-delete"
                          p-label="Excluir"
                          p-type="danger"
                          (p-click)="deleteAddress(address)">
                        </po-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="po-row" *ngIf="!customer">
          <div class="po-sm-12">
            <po-loading></po-loading>
          </div>
        </div>
      </po-page-default>

      <!-- Modal de Telefone -->
      <po-modal
        #phoneModal
        p-title="{{ editingPhone ? 'Editar Telefone' : 'Novo Telefone' }}"
        [p-primary-action]="phoneModalPrimaryAction"
        [p-secondary-action]="phoneModalSecondaryAction">
        <div class="po-row">
          <div class="po-sm-12">
            <po-input
              name="number"
              [(ngModel)]="phoneForm.number"
              p-label="Telefone"
              p-placeholder="Digite o telefone"
              [p-required]="true"
              [p-mask]="'(99) 99999-9999'">
            </po-input>
          </div>
        </div>
      </po-modal>

      <!-- Modal de Endereço -->
      <po-modal
        #addressModal
        p-title="{{ editingAddress ? 'Editar Endereço' : 'Novo Endereço' }}"
        [p-primary-action]="addressModalPrimaryAction"
        [p-secondary-action]="addressModalSecondaryAction">
        <div class="po-row">
           <div class="po-sm-12 po-md-6">
            <po-input
              name="name"
              [(ngModel)]="addressForm.name"
              p-label="Nome do endereço"
              p-placeholder="Digite o nome"
              [p-required]="true">
            </po-input>
          </div>
          <div class="po-sm-12 po-md-6">
            <po-input
              name="cep"
              [(ngModel)]="addressForm.cep"
              p-label="CEP"
              p-placeholder="Digite o CEP"
              [p-required]="true"
              [p-mask]="'99999-999'">
            </po-input>
          </div>
          <div class="po-sm-12 po-md-6">
            <po-input
              name="street"
              [(ngModel)]="addressForm.street"
              p-label="Logradouro"
              p-placeholder="Digite o logradouro"
              [p-required]="true">
            </po-input>
          </div>
          <div class="po-sm-12 po-md-6">
            <po-input
              name="number"
              [(ngModel)]="addressForm.number"
              p-label="Número"
              p-placeholder="Digite o número"
              [p-required]="true">
            </po-input>
          </div>
          <div class="po-sm-12 po-md-6">
            <po-input
              name="complement"
              [(ngModel)]="addressForm.complement"
              p-label="Complemento"
              p-placeholder="Digite o complemento">
            </po-input>
          </div>
          <div class="po-sm-12 po-md-6">
            <po-input
              name="neighborhood"
              [(ngModel)]="addressForm.neighborhood"
              p-label="Bairro"
              p-placeholder="Digite o bairro"
              [p-required]="true">
            </po-input>
          </div>
          <div class="po-sm-12 po-md-6">
            <po-input
              name="city"
              [(ngModel)]="addressForm.city"
              p-label="Cidade"
              p-placeholder="Digite a cidade"
              [p-required]="true">
            </po-input>
          </div>
          <div class="po-sm-12 po-md-6">
            <po-select
              name="uf"
              [(ngModel)]="addressForm.uf"
              p-label="Estado"
              [p-options]="ufs"
              [p-required]="true">
            </po-select>
          </div>
        </div>
      </po-modal>
    </div>
  `,
  styles: [`
    .client-details {
      height: 100%;
    }
    .po-row {
      margin-bottom: 1rem;
    }
    .section-card {
      background-color: white;
      border-radius: 4px;
      padding: 1rem;
      margin-bottom: 1rem;
      box-shadow: 0 1px 3px rgba(0,0,0,0.1);
    }
    .section-title {
      color: #0c9abe;
      margin-bottom: 1rem;
      font-size: 1.2rem;
    }
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
    }
    .item-card {
      background-color: #f8f9fa;
      border-radius: 4px;
      padding: 1rem;
      margin-bottom: 1rem;
    }
  `]
})
export class ClientDetailsComponent implements OnInit {
  customer: Customer | null = null;
  loading = false;

  @ViewChild('phoneModal') phoneModal!: PoModalComponent;
  @ViewChild('addressModal') addressModal!: PoModalComponent;

  // Phone Modal
  phoneForm = { number: '' };
  editingPhone: any = null;
  phoneModalPrimaryAction = {
    label: 'Salvar',
    action: () => this.savePhone()
  };
  phoneModalSecondaryAction = {
    label: 'Cancelar',
    action: () => this.closePhoneModal()
  };

  // Address Modal
  addressForm = {
    name: '',
    cep: '',
    street: '',
    number: '',
    complement: '',
    neighborhood: '',
    city: '',
    uf: ''
  };
  editingAddress: any = null;
  addressModalPrimaryAction = {
    label: 'Salvar',
    action: () => this.saveAddress()
  };
  addressModalSecondaryAction = {
    label: 'Cancelar',
    action: () => this.closeAddressModal()
  };

  ufs = [
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
  ];

  pageActions: Array<PoPageAction> = [
    {
      label: 'Voltar',
      action: () => this.router.navigate(['/clients'])
    }
  ];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerService: CustomerService,
    private poNotification: PoNotificationService
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadCustomer(id);
    }
  }

  private loadCustomer(id: string) {
    this.loading = true;
    this.customerService.getCustomerById(id).subscribe({
      next: (customer) => {
        this.customer = customer;
        this.loading = false;
      },
      error: (error) => {
        this.poNotification.error('Erro ao carregar dados do cliente');
        console.error('Error loading customer:', error);
        this.router.navigate(['/clients']);
      }
    });
  }

  // Phone Modal Methods
  openPhoneModal(phone?: any) {
    this.editingPhone = phone;
    this.phoneForm = phone ? { ...phone } : { number: '' };
    this.phoneModal.open();
  }

  closePhoneModal() {
    this.editingPhone = null;
    this.phoneForm = { number: '' };
    this.phoneModal.close();
  }

  savePhone() {
    if (!this.phoneForm.number) {
      this.poNotification.error('Por favor, preencha o telefone');
      return;
    }

    if (!this.customer?.id) {
      this.poNotification.error('Cliente não encontrado');
      return;
    }

    const phoneData = {
      number: this.phoneForm.number,
      phoneType: 'mobile' // Default type since we removed the type field
    };

    if (this.editingPhone) {
      // Update existing phone
      this.customerService.updateCustomerContact(this.customer.id, this.editingPhone.id, phoneData).subscribe({
        next: () => {
          this.poNotification.success('Telefone atualizado com sucesso');
          this.loadCustomer(this.customer!.id);
          this.closePhoneModal();
        },
        error: (error) => {
          this.poNotification.error(error.error?.message || 'Erro ao atualizar telefone');
          console.error('Error updating phone:', error);
        }
      });
    } else {
      // Add new phone
      this.customerService.addCustomerContact(this.customer.id, phoneData).subscribe({
        next: () => {
          this.poNotification.success('Telefone adicionado com sucesso');
          this.loadCustomer(this.customer!.id);
          this.closePhoneModal();
        },
        error: (error) => {
          this.poNotification.error(error.error?.message || 'Erro ao adicionar telefone');
          console.error('Error adding phone:', error);
        }
      });
    }
  }

  editPhone(phone: any) {
    this.openPhoneModal(phone);
  }

  deletePhone(phone: any) {
    if (!this.customer?.id) {
      this.poNotification.error('Cliente não encontrado');
      return;
    }

    this.customerService.deleteCustomerContact(this.customer.id, phone.id).subscribe({
      next: () => {
        this.poNotification.success('Telefone excluído com sucesso');
        this.loadCustomer(this.customer!.id);
      },
      error: (error) => {
        this.poNotification.error(error.error?.message || 'Erro ao excluir telefone');
        console.error('Error deleting phone:', error);
      }
    });
  }

  // Address Modal Methods
  openAddressModal(address?: any) {
    this.editingAddress = address;
    this.addressForm = address ? { ...address } : {
      name: '',
      cep: '',
      street: '',
      number: '',
      complement: '',
      neighborhood: '',
      city: '',
      uf: ''
    };
    this.addressModal.open();
  }

  closeAddressModal() {
    this.editingAddress = null;
    this.addressForm = {
      name: '',
      cep: '',
      street: '',
      number: '',
      complement: '',
      neighborhood: '',
      city: '',
      uf: ''
    };
    this.addressModal.close();
  }

  saveAddress() {
    if (!this.validateAddressForm()) {
      this.poNotification.error('Por favor, preencha todos os campos obrigatórios');
      return;
    }

    if (!this.customer?.id) {
      this.poNotification.error('Cliente não encontrado');
      return;
    }

    if (this.editingAddress) {
      // Update existing address
      this.customerService.updateCustomerAddress(this.customer.id, this.editingAddress.id, this.addressForm).subscribe({
        next: () => {
          this.poNotification.success('Endereço atualizado com sucesso');
          this.loadCustomer(this.customer!.id);
          this.closeAddressModal();
        },
        error: (error) => {
          this.poNotification.error(error.error?.message || 'Erro ao atualizar endereço');
          console.error('Error updating address:', error);
        }
      });
    } else {
      // Add new address
      this.customerService.addCustomerAddress(this.customer.id, this.addressForm).subscribe({
        next: () => {
          this.poNotification.success('Endereço adicionado com sucesso');
          this.loadCustomer(this.customer!.id);
          this.closeAddressModal();
        },
        error: (error) => {
          this.poNotification.error(error.error?.message || 'Erro ao adicionar endereço');
          console.error('Error adding address:', error);
        }
      });
    }
  }

  editAddress(address: any) {
    this.openAddressModal(address);
  }

  deleteAddress(address: any) {
    if (!this.customer?.id) {
      this.poNotification.error('Cliente não encontrado');
      return;
    }

    this.customerService.deleteCustomerAddress(this.customer.id, address.id).subscribe({
      next: () => {
        this.poNotification.success('Endereço excluído com sucesso');
        this.loadCustomer(this.customer!.id);
      },
      error: (error) => {
        this.poNotification.error(error.error?.message || 'Erro ao excluir endereço');
        console.error('Error deleting address:', error);
      }
    });
  }

  private validateAddressForm(): boolean {
    return !!(this.addressForm.cep &&
      this.addressForm.street &&
      this.addressForm.number &&
      this.addressForm.neighborhood &&
      this.addressForm.city &&
      this.addressForm.uf);
  }
} 