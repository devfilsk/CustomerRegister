# Como rodar o projeto:
1 - Clonar o projeto na sua maquina local
2 - executar o comando "docker compose up"

Com isso a aplicação será executada com todo o ambiente e banco de dados criado corretamente.

# Documentação:
http://localhost:8080/swagger-ui.html

# Sistema de Cadastro de Clientes

Este projeto é uma aplicação para gerenciamento de cadastro de clientes, permitindo o armazenamento e controle de informações pessoais e de contato.

## Descrição

O sistema permite o cadastro de clientes com suas informações pessoais, telefones e endereços, implementando validações específicas para garantir a integridade dos dados.

## Campos do Cadastro

- Nome
- CPF
- Telefones (um ou mais)
- Endereço (um ou mais)

## Requisitos Funcionais

- O cliente pode ter um ou mais telefones associados
- O nome do cliente deve ter no mínimo 10 caracteres
- O CPF deve ser válido (de acordo com a regra de validação oficial) e único no sistema
- Cada telefone deve estar em um formato válido e não pode estar vinculado a mais de um cliente
- Não deve ser permitido o cadastro de dados duplicados (nome idêntico, CPF repetido ou telefone já usado por outro cliente)

## Checklist de Implementação

### Backend
- [x] Criar modelo de dados para Cliente
- [x] Implementar validação de CPF
- [x] Implementar validação de formato de telefone
- [x] Implementar validação de nome (mínimo 10 caracteres)
- [x] Implementar verificação de duplicidade de CPF
- [x] Implementar verificação de duplicidade de telefone
- [x] Implementar verificação de duplicidade de nome (Nao será implementado)
- [x] Criar endpoints para CRUD de clientes
- [x] Implementar tratamento de erros

### Frontend
- [x] Criar interface de cadastro de cliente
- [x] Implementar formulário com validações
- [x] Implementar máscara para CPF
- [x] Implementar máscara para telefone
- [x] Um cliente pode ter múltiplos telefones
- [x] Um cliente pode ter múltiplos endereços
- [x] Implementar feedback visual de erros
- [x] Implementar listagem de clientes
- [x] Implementar edição de cliente
- [x] Implementar exclusão de cliente

### Testes
- [x] Implementar testes unitários para validações
- [x] Implementar testes de integração e unitarios

## Tecnologias Utilizadas