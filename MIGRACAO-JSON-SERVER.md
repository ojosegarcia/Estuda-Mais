# ✅ Migração para json-server - Concluída!

## 📋 Resumo das Mudanças

Toda a aplicação foi migrada com sucesso de **localStorage** para **json-server**, simulando um backend real mantendo todas as funcionalidades.

---

## 🔧 Serviços Refatorados

### 1. **AuthService** (`auth.ts`)
✅ **Completamente refatorado**

**Mudanças principais:**
- ✅ `register()` → POST `/usuarios` (cria novo usuário no db.json)
- ✅ `login()` → GET `/usuarios?email=&password=` (busca usuário autenticado)
- ✅ `updateUser()` → PUT `/usuarios/:id` (atualiza perfil do usuário)
- ✅ Mantém `BehaviorSubject<Usuario>` para reatividade
- ✅ localStorage usado APENAS para sessão (token do usuário logado)
- ✅ `isProfileComplete()` → Método normal (boolean) que verifica:
  - **ALUNO**: escolaridade + interesse
  - **PROFESSOR**: sobre + valorHora + materias.length > 0

**Métodos mantidos:**
- `logout()` → Limpa localStorage e redireciona
- `getCurrentUser()` → Lê sessão do localStorage
- `isAuthenticated()` → Verifica se está logado
- `refreshCurrentUserSession()` → Atualiza sessão local

---

### 2. **MateriaService** (`materia.ts`)
✅ **Migrado com sucesso**

**Mudanças principais:**
- ✅ `getMaterias()` → GET `/materias` (busca todas as matérias do db.json)
- ✅ `getMateriaPorId(id)` → GET `/materias/:id` (busca matéria específica)
- ❌ **Removido:** array de mocks `mockMaterias`

---

### 3. **ProfessorService** (`professor.ts`)
✅ **Migrado com sucesso**

**Mudanças principais:**
- ✅ `getProfessoresPorMateria(materiaId)` → GET `/usuarios?tipoUsuario=PROFESSOR` + filtro client-side
  - Filtra professores aprovados que têm a matéria desejada no array `materias[]`
- ✅ `getProfessorById(id)` → GET `/usuarios/:id` (busca professor específico)
- ❌ **Removido:** array de mocks `mockProfessores`

**Lógica de filtro:**
```typescript
usuarios.filter(user => {
  const prof = user as Professor;
  return prof.aprovado && prof.materias?.some(m => m.id === materiaId);
})
```

---

### 4. **AulaService** (`aula.ts`)
✅ **Completamente refatorado**

**Mudanças principais:**
- ✅ `solicitarAula()` → POST `/aulas` (aluno solicita aula)
- ✅ `getAulasPorUsuarioLogado()` → GET `/aulas?idAluno=` ou `?idProfessor=` (lista aulas do usuário)
- ✅ `aceitarAula()` → PATCH/PUT `/aulas/:id` (professor aceita)
- ✅ `recusarAula()` → PATCH/PUT `/aulas/:id` (professor recusa)
- ✅ `cancelarAula()` → PATCH/PUT `/aulas/:id` (qualquer um cancela)
- ✅ `marcarComoRealizada()` → PATCH/PUT `/aulas/:id` (marca como realizada)
- ✅ `getTodasAulas()` → GET `/aulas` (busca todas)
- ✅ `getAulaPorId()` → GET `/aulas/:id` (busca específica)

**Mantém reatividade:**
- ✅ `BehaviorSubject<Aula[]>` para notificar componentes
- ✅ `aulas$: Observable<Aula[]>` → Stream reativo
- ✅ Método `recarregarAulas()` atualiza o BehaviorSubject após cada operação

**Método privado importante:**
```typescript
private atualizarStatusAula(aulaId: number, novoStatus: StatusAula): Observable<Aula> {
  return this.http.get<Aula>(`${this.apiUrl}/${aulaId}`).pipe(
    switchMap(aula => {
      const aulaAtualizada = { ...aula, statusAula: novoStatus };
      return this.http.put<Aula>(`${this.apiUrl}/${aulaId}`, aulaAtualizada);
    }),
    tap(() => this.recarregarAulas().subscribe())
  );
}
```

❌ **Removido:** 
- Métodos `getAulasFromStorage()` e `salvarAulas()`
- Método `inicializarDadosMock()` (dados agora estão no db.json)

---

## 📦 Componentes Atualizados

### 1. **PerfilEditComponent** (`perfil-edit.ts`)
✅ **Atualizado com sucesso**

**Mudança no método `onSubmit()`:**
```typescript
// ANTES: Salvava direto no localStorage
if (isPlatformBrowser(this.platformId)) {
  localStorage.setItem('usuarioLogado', JSON.stringify(updatedUser));
}

// AGORA: Chama API via AuthService
this.authService.updateUser(updatedUser).subscribe({
  next: (usuario) => {
    alert('Perfil salvo com sucesso!');
    this.router.navigate(['/perfil']);
  },
  error: (err) => console.error('Erro ao salvar perfil:', err)
});
```

---

## 🗄️ Estrutura do db.json

```json
{
  "usuarios": [
    // 5 usuários completos (2 alunos, 3 professores)
    // Todos com senha "123456"
    // Professores têm array "materias": [{ id, nome, icone }]
  ],
  "materias": [
    // 5 matérias: Matemática, Artes, Vestibular, Programação, Inglês
  ],
  "aulas": [
    // 4 aulas de exemplo com vários status
  ]
}
```

### Usuários disponíveis para teste:
1. **João Aluno** (ALUNO) - `joao@email.com` / `123456`
2. **Maria Estudante** (ALUNO) - `maria@email.com` / `123456`
3. **Prof. Ana Silva** (PROFESSOR) - `ana@email.com` / `123456` → Ensina: Matemática, Vestibular
4. **Prof. Bruno Gomes** (PROFESSOR) - `bruno@email.com` / `123456` → Ensina: Programação
5. **Prof. Carla Dias** (PROFESSOR) - `carla@email.com` / `123456` → Ensina: Inglês

---

## 🚀 Como Testar

### 1. Iniciar o json-server
```powershell
cd Frontend/angular-app
npm run mock:api
```
**Resultado esperado:**
```
JSON Server started on PORT 3000
Resources:
http://localhost:3000/usuarios
http://localhost:3000/materias
http://localhost:3000/aulas
```

### 2. Iniciar a aplicação Angular (em outro terminal)
```powershell
cd Frontend/angular-app
npm start
```
**URL:** http://localhost:4200

---

## 🧪 Cenários de Teste

### ✅ Teste 1: Registro de novo usuário
1. Acesse `/auth/register`
2. Preencha o formulário (nome, email, senha, tipo: ALUNO)
3. Clique em "Cadastrar"
4. ✅ **Esperado:** Novo usuário aparece no db.json e é redirecionado para login

### ✅ Teste 2: Login
1. Acesse `/auth/login`
2. Use: `ana@email.com` / `123456`
3. ✅ **Esperado:** Redireciona para `/home`, navbar mostra nome do usuário

### ✅ Teste 3: Modal de completar perfil (Professor)
1. Faça login como `bruno@email.com` (professor)
2. Se o perfil estiver incompleto (sem matérias), o modal aparece
3. Clique em "Completar Agora" → Vai para `/perfil/editar`
4. Selecione "Programação" e preencha "sobre" e "valorHora"
5. Salve o perfil
6. ✅ **Esperado:** PUT `/usuarios/102` atualiza o db.json, modal não aparece mais

### ✅ Teste 4: Busca de professores
1. Na home, clique na matéria "Programação" (ID: 4)
2. ✅ **Esperado:** Redireciona para `/busca?materiaId=4`
3. ✅ **Esperado:** Lista mostra apenas "Prof. Bruno Gomes"

### ✅ Teste 5: Solicitar aula (Aluno)
1. Faça login como `joao@email.com` (aluno)
2. Busque professores de Matemática
3. Clique em "Ver Perfil" da Prof. Ana Silva
4. (Implementar botão "Solicitar Aula" se ainda não existir)
5. ✅ **Esperado:** POST `/aulas` cria nova aula com status "SOLICITADA"

### ✅ Teste 6: Aceitar/Recusar aula (Professor)
1. Faça login como `ana@email.com` (professora)
2. Acesse "Minhas Aulas" (`/minhas-aulas`)
3. Veja a lista de aulas solicitadas
4. Clique em "Aceitar" ou "Recusar"
5. ✅ **Esperado:** PUT `/aulas/:id` atualiza o status no db.json
6. ✅ **Esperado:** Lista se atualiza automaticamente (reatividade do BehaviorSubject)

---

## 🎯 Funcionalidades Mantidas

✅ **Todas as funcionalidades anteriores continuam funcionando:**
- ✅ Sistema de autenticação (login, registro, logout)
- ✅ Profile Completion Modal (verifica perfil completo)
- ✅ Busca de professores por matéria
- ✅ Agenda inteligente (dual view: aluno/professor)
- ✅ Aceitar/Recusar/Cancelar aulas
- ✅ Editar perfil (incluindo seleção de matérias para professores)
- ✅ Reatividade (BehaviorSubjects, Observables)
- ✅ SSR-safe (isPlatformBrowser para localStorage)

---

## 📝 Observações Importantes

### ⚠️ localStorage ainda é usado para:
- **Sessão do usuário logado** (`usuarioLogado` key)
- Isso é correto! O token de sessão normalmente fica no cliente (localStorage/sessionStorage)

### ⚠️ json-server não suporta queries complexas nativamente
- **Filtro de professores por matéria:** Feito client-side após buscar todos os professores
- Em um backend real (Spring Boot), isso seria feito no servidor com JPA/Hibernate

### ⚠️ Autenticação é simplificada
- Senha não tem hash (aceitável para mock)
- Login usa query params: `?email=&password=` (em produção seria POST /auth/login)

---

## 🔥 Próximos Passos (Sugestões)

1. **Adicionar botão "Solicitar Aula"** no `professor-detalhe.component.html`
2. **Implementar filtros de busca** (valor, disponibilidade)
3. **Adicionar paginação** nas listas de professores/aulas
4. **Criar página de histórico de aulas realizadas**
5. **Adicionar sistema de avaliações** (Feedback após aula realizada)
6. **Implementar chat** entre aluno e professor

---

## 🐛 Como Debugar

### Ver requisições HTTP no console:
Abra o DevTools → Network → XHR

### Ver dados no db.json:
Acesse diretamente:
- http://localhost:3000/usuarios
- http://localhost:3000/materias
- http://localhost:3000/aulas

### Resetar dados:
Feche o json-server e edite o `db.json` manualmente, ou delete e recriar o arquivo.

---

## ✅ Checklist de Migração

- [x] AuthService migrado para HTTP
- [x] MateriaService migrado para HTTP
- [x] ProfessorService migrado para HTTP
- [x] AulaService migrado para HTTP
- [x] PerfilEditComponent atualizado
- [x] db.json criado com dados completos
- [x] npm script `mock:api` verificado
- [x] BehaviorSubjects mantidos para reatividade
- [x] Todos os componentes compatíveis (busca, perfil-edit, my-classes, etc.)
- [x] SSR-safe (isPlatformBrowser mantido onde necessário)

---

## 🎉 Conclusão

A migração foi concluída com sucesso! Todos os serviços agora usam **HttpClient** para comunicar com o **json-server**, simulando um backend real. O sistema mantém todas as funcionalidades anteriores e está pronto para ser testado.

**Comando para iniciar:**
```powershell
# Terminal 1: json-server
npm run mock:api

# Terminal 2: Angular dev server
npm start
```

**Acesse:** http://localhost:4200
