import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { PoModule, PoPageAction, PoTableColumn, PoNotificationService, PoTableComponent } from '@po-ui/ng-components';
import { HttpClientModule } from '@angular/common/http';
import { CustomerService, Customer } from '../../../services/customer.service';
import { Page } from '../../../models/pagination.model';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

@Component({
  selector: 'app-client-list',
  standalone: true,
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [CommonModule, RouterModule, PoModule, HttpClientModule],
  template: `
    <div class="client-list">
      <po-page-default p-title="Lista de Clientes" [p-actions]="pageActions">
        <po-table
          #table
          [p-items]="customers"
          [p-columns]="columns"
          [p-striped]="true"
          [p-loading]="loading"
          [p-height]="400"
          [p-page-size]="pageSize"
          [p-total]="totalElements"
          (p-page-change)="onPageChange($event)">
        </po-table>
      </po-page-default>
    </div>
  `,
  styles: [`
    .client-list {
      height: 100%;
    }
  `]
})
export class ClientListComponent implements OnInit {
  customers: Customer[] = [];
  loading = false;
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;

  columns: PoTableColumn[] = [
    { property: 'name', label: 'Nome' },
    { property: 'cpf', label: 'CPF' },
    { property: 'email', label: 'E-mail' },
    {
      property: 'createdAt',
      label: 'Data de Cadastro',
      type: 'date',
      format: 'dd/MM/yyyy HH:mm'
    },
    {
      property: 'actions',
      label: 'Ações',
      type: 'icon',
      icons: [
        {
          value: 'view',
          icon: 'po-icon-eye',
          color: 'color-08',
          tooltip: 'Ver detalhes',
          action: (row: Customer) => this.viewDetails(row)
        },
        {
          value: 'edit',
          icon: 'po-icon-edit',
          color: 'color-07',
          tooltip: 'Editar',
          action: (row: Customer) => this.editCustomer(row)
        }
      ]
    }
  ];

  pageActions: Array<PoPageAction> = [
    {
      label: 'Novo Cliente',
      action: () => this.router.navigate(['/clients/new'])
    }
  ];

  constructor(
    private customerService: CustomerService,
    private router: Router,
    private poNotification: PoNotificationService
  ) {}

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading = true;
    this.customerService.getCustomers(this.currentPage, this.pageSize).subscribe({
      next: (page: Page<Customer>) => {
        this.customers = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: (error) => {
        this.poNotification.error('Erro ao carregar lista de clientes');
        console.error('Error loading customers:', error);
        this.loading = false;
      }
    });
  }

  onPageChange(page: number) {
    this.currentPage = page;
    this.loadCustomers();
  }

  viewDetails(customer: Customer) {
    if (customer.id) {
      this.router.navigate(['/clients', customer.id]);
    }
  }

  editCustomer(customer: Customer) {
    if (customer.id) {
      this.router.navigate(['/clients/edit', customer.id]);
    }
  }
} 