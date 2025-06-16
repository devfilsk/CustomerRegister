import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { Cliente } from '../models/cliente.model';

@Injectable({
  providedIn: 'root'
})
export class ClienteService {
  private clientes: Cliente[] = [];
  private clientesSubject = new BehaviorSubject<Cliente[]>([]);

  constructor() {
    this.carregarClientes();
  }

  getClientes(): Observable<Cliente[]> {
    return this.clientesSubject.asObservable();
  }

  getClientePorId(id: string): Observable<Cliente | undefined> {
    const cliente = this.clientes.find(cliente => cliente.id === id);
    return of(cliente);
  }

  adicionarCliente(cliente: Omit<Cliente, 'id' | 'dataCadastro'>): Observable<Cliente> {
    try {
      const novoCliente: Cliente = {
        ...cliente,
        id: this.gerarId(),
        dataCadastro: new Date()
      };
      
      this.clientes.push(novoCliente);
      this.salvarClientes();
      this.clientesSubject.next([...this.clientes]);
      return of(novoCliente);
    } catch (error) {
      return throwError(() => new Error('Erro ao adicionar cliente'));
    }
  }

  atualizarCliente(id: string, clienteAtualizado: Omit<Cliente, 'id' | 'dataCadastro'>): Observable<Cliente> {
    try {
      const index = this.clientes.findIndex(cliente => cliente.id === id);
      if (index === -1) {
        return throwError(() => new Error('Cliente não encontrado'));
      }

      const clienteAtualizadoCompleto: Cliente = {
        ...clienteAtualizado,
        id,
        dataCadastro: this.clientes[index].dataCadastro
      };

      this.clientes[index] = clienteAtualizadoCompleto;
      this.salvarClientes();
      this.clientesSubject.next([...this.clientes]);
      return of(clienteAtualizadoCompleto);
    } catch (error) {
      return throwError(() => new Error('Erro ao atualizar cliente'));
    }
  }

  excluirCliente(id: string): Observable<void> {
    try {
      const index = this.clientes.findIndex(cliente => cliente.id === id);
      if (index === -1) {
        return throwError(() => new Error('Cliente não encontrado'));
      }

      this.clientes = this.clientes.filter(cliente => cliente.id !== id);
      this.salvarClientes();
      this.clientesSubject.next([...this.clientes]);
      return of(void 0);
    } catch (error) {
      return throwError(() => new Error('Erro ao excluir cliente'));
    }
  }

  private gerarId(): string {
    return Math.random().toString(36).substr(2, 9);
  }

  private carregarClientes(): void {
    try {
      const clientesSalvos = localStorage.getItem('clientes');
      if (clientesSalvos) {
        this.clientes = JSON.parse(clientesSalvos);
        this.clientesSubject.next([...this.clientes]);
      }
    } catch (error) {
      console.error('Erro ao carregar clientes:', error);
      this.clientes = [];
      this.clientesSubject.next([]);
    }
  }

  private salvarClientes(): void {
    try {
      localStorage.setItem('clientes', JSON.stringify(this.clientes));
    } catch (error) {
      console.error('Erro ao salvar clientes:', error);
    }
  }
}