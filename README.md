# Estuda Mais
Uma aplicação full-stack de agendamento de aulas particulares utilizando Spring Boot e Angular.

## Prévia da aplicação

## Tecnologias 
**Backend**
- Java 21
- Spring Boot 3.5.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- H2 Database
- Maven

**Frontend**
- Angular 20.2.0
- Angular CLI
- Bootstrap
- TypeScript 5.9
- RxJS 7.8
- Node.js
- Express 5.1

## Diagramas
### Caso de Uso    

### Classe

## Como rodar

### Pré-requisitos
- Node.js instalado (versão 18+)
- npm instalado

### Opção 1: Usando o script automatizado (RECOMENDADO)

Na raiz do projeto, execute:

```bash
.\INICIAR-SERVIDORES.bat
```

Isso irá:
1. Instalar dependências automaticamente (se necessário)
2. Abrir Terminal 1: json-server (API mock) na porta 3000
3. Abrir Terminal 2: Angular dev server na porta 4200

### Opção 2: Manual

Você precisa rodar 2 servidores simultaneamente em terminais separados:

**Terminal 1 - API Mock (json-server):**
```bash
cd Frontend/angular-app
npm run mock:api
```
✅ API rodando em `http://localhost:3000`

**Terminal 2 - Angular:**
```bash
cd Frontend/angular-app
npm start
```
✅ Aplicação rodando em `http://localhost:4200`

### Credenciais de Teste

**Aluno:**
- Email: `aluno@email.com`
- Senha: `123456`

**Professor:**
- Email: `ana@email.com`
- Senha: `123456`

---

## Funcionalidades Implementadas

✅ Sistema de autenticação (login/registro)  
✅ Busca de professores por matéria  
✅ Agendamento de aulas  
✅ Agenda inteligente (Aluno e Professor)  
✅ Edição de perfil com seleção de matérias  
✅ Modal de completar perfil (onboarding)  
✅ SSR (Server-Side Rendering)  
✅ Mock API com json-server

---

## Arquitetura

- **Frontend:** Angular 20.2.0 com standalone components
- **Mock Backend:** json-server (simulando REST API)
- **Estilização:** CSS3 puro com design responsivo
- **State Management:** RxJS BehaviorSubjects
- **Routing:** Angular Router com guards de autenticação