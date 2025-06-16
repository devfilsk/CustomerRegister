import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { PoModule, PoTableColumn, PoTableAction, PoDialogService, PoNotificationService } from '@po-ui/ng-components';
import { CustomerService, Customer } from '../../services/customer.service';
import { Page } from '../../models/pagination.model';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

@Component({
  selector: 'app-cliente-lista',
  standalone: true,
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  imports: [CommonModule, RouterModule, PoModule],
  templateUrl: './cliente-lista.component.html',
  styleUrls: ['./cliente-lista.component.scss']
})
export class ClienteListaComponent implements OnInit {
  customers: Customer[] = [];
  loading = false;
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;

  readonly columns: PoTableColumn[] = [
    {
      property: 'name',
      label: 'Nome',
      width: '25%'
    },
    {
      property: 'cpf',
      label: 'CPF',
      width: '15%',
      type: 'string'
    },
    {
      property: 'email',
      label: 'E-mail',
      width: '25%'
    },
    {
      property: 'createdAt',
      label: 'Data Cadastro',
      width: '15%',
      type: 'date',
      format: 'dd/MM/yyyy'
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
        },
        {
          value: 'delete',
          icon: 'po-icon-delete',
          color: 'color-11',
          tooltip: 'Excluir',
          action: (row: Customer) => this.deleteCustomer(row)
        }
      ]
    }
  ];

  constructor(
    private readonly customerService: CustomerService,
    public readonly router: Router,
    private readonly poDialog: PoDialogService,
    private readonly poNotification: PoNotificationService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
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

  viewDetails(customer: Customer): void {
    if (customer.id) {
      this.router.navigate(['/clients', customer.id]);
    }
  }

  editCustomer(customer: Customer): void {
    if (customer.id) {
      this.router.navigate(['/clients/edit', customer.id]);
    }
  }

  deleteCustomer(customer: Customer): void {
    this.poDialog.confirm({
      title: 'Confirmar Exclusão',
      message: `Deseja realmente excluir o cliente ${customer.name}?`,
      confirm: () => {
        if (customer.id) {
          this.customerService.deleteCustomer(customer.id).subscribe({
            next: () => {
              this.poNotification.success('Cliente excluído com sucesso!');
              this.loadCustomers();
            },
            error: (error) => {
              this.poNotification.error('Erro ao excluir cliente');
              console.error('Error deleting customer:', error);
            }
          });
        }
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadCustomers();
  }
}