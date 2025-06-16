export interface Telefone {
  id: string;
  numero: string;
  tipo: 'residencial' | 'comercial' | 'celular';
}

export interface Endereco {
  id: string;
  cep: string;
  logradouro: string;
  numero: string;
  complemento?: string;
  bairro: string;
  cidade: string;
  estado: string;
  tipo: 'residencial' | 'comercial';
}

export interface Cliente {
  id: string;
  nome: string;
  cpf: string;
  email: string;
  telefones: Telefone[];
  enderecos: Endereco[];
  dataCadastro: Date;
}