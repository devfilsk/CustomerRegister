import { Injectable } from '@angular/core';
import api from '../utils/api';
import { Observable, from } from 'rxjs';
import { Page } from '../models/pagination.model';

export interface Customer {
  id?: string;
  name: string;
  cpf: string;
  email: string;
  createdAt?: string;
  updatedAt?: string;
  contacts?: {
    id?: string;
    phone: string;
    phoneType: string;
  }[];
  addresses?: {
    id?: string;
    cep: string;
    street: string;
    number: string;
    complement?: string;
    neighborhood: string;
    city: string;
    uf: string;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  // private readonly API_URL = '';

  constructor() {}

  getCustomers(page: number = 0, size: number = 10): Observable<Page<Customer>> {
    return from(
      api.get<Page<Customer>>(`customers?page=${page}&size=${size}`)
        .then(response => response.data)
    );
  }

  getCustomerById(id: string): Observable<Customer> {
    return from(api.get<Customer>(`customers/${id}`).then(response => response.data));
  }

  createCustomer(customer: Customer): Observable<Customer> {
    return from(api.post<Customer>("customers", customer).then(response => response.data));
  }

  updateCustomer(id: string, customer: Customer): Observable<Customer> {
    return from(api.put<Customer>(`customers/${id}`, customer).then(response => response.data));
  }

  deleteCustomer(id: string): Observable<void> {
    return from(api.delete(`customers/${id}`).then(() => {}));
  }

  // Contact Management
  addCustomerContact(customerId: string, contact: { number: string; phoneType: string }): Observable<Customer> {
    return from(
      api.post<Customer>(`phones/${customerId}`, contact)
        .then(response => response.data)
    );
  }

  updateCustomerContact(customerId: string, contactId: string, contact: { number: string; phoneType: string }): Observable<Customer> {
    return from(
      api.put<Customer>(`customers/${customerId}/contacts/${contactId}`, contact)
        .then(response => response.data)
    );
  }

  deleteCustomerContact(customerId: string, contactId: string): Observable<void> {
    return from(
      api.delete(`phones/${contactId}`)
        .then(() => {})
    );
  }

  // Address Management
  addCustomerAddress(customerId: string, address: Omit<Customer['addresses'][0], 'id'>): Observable<Customer> {
    return from(
      api.post<Customer>(`/addresses/${customerId}`, address)
        .then(response => response.data)
    );
  }

  updateCustomerAddress(customerId: string, addressId: string, address: Omit<Customer['addresses'][0], 'id'>): Observable<Customer> {
    return from(
      api.put<Customer>(`addresses/${addressId}`, address)
        .then(response => response.data)
    );
  }

  deleteCustomerAddress(customerId: string, addressId: string): Observable<void> {
    return from(
      api.delete(`addresses/${addressId}`)
        .then(() => {})
    );
  }
} 