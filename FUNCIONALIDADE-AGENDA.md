# 📅 Funcionalidade: Agenda Inteligente (Itens 10 e 11 do MVP)

## 🎯 Visão Geral

Sistema de agendamento de aulas com uma interface **"dois em um"** que adapta-se automaticamente ao tipo de usuário logado (Aluno ou Professor).

## ✅ Implementação Completa

### 📁 Arquivos Modificados/Criados

1. **`AulaModel.ts`** - Modelo de dados da aula
   - ✅ 5 status possíveis: `SOLICITADA`, `CONFIRMADA`, `RECUSADA`, `CANCELADA`, `REALIZADA`
   - ✅ Campos completos: datas, horários, valores, links de reunião
   - ✅ Relacionamentos com Professor, Aluno e Matéria

2. **`aula.ts`** (AulaService) - Lógica de negócio
   - ✅ BehaviorSubject para reatividade
   - ✅ CRUD completo no localStorage
   - ✅ Métodos: `solicitarAula()`, `aceitarAula()`, `recusarAula()`, `cancelarAula()`
   - ✅ Filtragem por usuário logado
   - ✅ Dados mock para testes

3. **`my-classes.ts`** (Componente) - Lógica da UI
   - ✅ Detecção automática do tipo de usuário
   - ✅ Estatísticas para professores
   - ✅ Funções de ação (aceitar, recusar, cancelar)
   - ✅ Formatação de datas e labels

4. **`my-classes.html`** - Interface
   - ✅ Visão do Aluno: cards com suas aulas
   - ✅ Visão do Professor: lista com solicitações
   - ✅ Estados vazios elegantes
   - ✅ Badges coloridos por status
   - ✅ Botões de ação contextuais

5. **`my-classes.css`** - Estilos
   - ✅ Design moderno e responsivo
   - ✅ Cores por status (verde=confirmada, amarelo=pendente, etc)
   - ✅ Animações e hover effects
   - ✅ Mobile-first

6. **`app.ts`** - Inicialização
   - ✅ Popula dados mock automaticamente

## 🔄 Fluxo Completo

### 1️⃣ Visão do ALUNO

**O que ele vê:**
- Lista de todas as aulas que solicitou
- Status de cada aula
- Detalhes: professor, matéria, data, hora, valor
- Link da reunião (se confirmada)

**O que ele pode fazer:**
- ❌ Cancelar aula `SOLICITADA` ou `CONFIRMADA`
- 🔗 Acessar link de reunião (quando disponível)

**Exemplo de tela:**
```
┌─────────────────────────────────────┐
│ 📅 Minhas Aulas                     │
│ Acompanhe suas aulas marcadas       │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│ 📚 Matemática          [Aguardando] │
│ 👨‍🏫 Prof. João Silva                │
│ 📅 15/11/2025                        │
│ 🕐 14:00 - 15:00                    │
│ 💰 R$ 80,00                          │
│                                     │
│ [❌ Cancelar Aula]                  │
└─────────────────────────────────────┘
```

### 2️⃣ Visão do PROFESSOR

**O que ele vê:**
- Estatísticas (total, confirmadas, pendentes)
- Lista de todas as solicitações
- Detalhes de cada aluno

**O que ele pode fazer:**
- ✅ Aceitar solicitação → vira `CONFIRMADA`
- ❌ Recusar solicitação → vira `RECUSADA`
- ❌ Cancelar aula já confirmada → vira `CANCELADA`

**Exemplo de tela:**
```
┌──────────────────────────────────────┐
│ 📅 Minhas Aulas                      │
│ Gerencie suas aulas agendadas        │
└──────────────────────────────────────┘

┌─────────┐ ┌─────────┐ ┌─────────┐
│   8     │ │   5     │ │   3     │
│  Total  │ │Confirm. │ │Pendent. │
└─────────┘ └─────────┘ └─────────┘

┌──────────────────────────────────────┐
│ MA  Maria Silva        [Aguardando]  │
│ 📚 Física • 16/11/2025 às 10:00      │
│ 💰 R$ 100,00                          │
│                           [✔️] [❌]   │
└──────────────────────────────────────┘
```

## 🧪 Como Testar

### 1. Iniciar a aplicação
```bash
cd Frontend/angular-app
npm start
```

### 2. Fazer login

**Como ALUNO:**
- Use qualquer usuário com `tipoUsuario: 'ALUNO'` e `id: 2`
- Acesse `/minhas-aulas`
- Verá suas aulas solicitadas

**Como PROFESSOR:**
- Use qualquer usuário com `tipoUsuario: 'PROFESSOR'` e `id: 1`
- Acesse `/minhas-aulas`
- Verá solicitações para aceitar/recusar

### 3. Dados Mock Automáticos

Ao iniciar a aplicação, são criadas automaticamente 3 aulas de exemplo:

**Aula 1:**
- Professor ID 1 ← Aluno ID 2
- Status: `SOLICITADA`
- Matéria: Matemática
- Data: 15/11/2025 às 14:00

**Aula 2:**
- Professor ID 1 ← Aluno ID 3
- Status: `CONFIRMADA`
- Matéria: Física
- Data: 16/11/2025 às 10:00
- ✅ Com link de reunião

**Aula 3:**
- Professor ID 2 ← Aluno ID 2
- Status: `RECUSADA`
- Matéria: Química
- Data: 12/11/2025 às 16:00

## 🎨 Status e Cores

| Status | Cor | Significado | Quem pode ver |
|--------|-----|-------------|---------------|
| 🟡 SOLICITADA | Amarelo | Aguardando resposta | Aluno + Professor |
| 🟢 CONFIRMADA | Verde | Aula marcada | Aluno + Professor |
| 🔴 RECUSADA | Vermelho | Professor recusou | Aluno + Professor |
| ⚫ CANCELADA | Cinza | Cancelada por alguém | Aluno + Professor |
| 🔵 REALIZADA | Azul | Aula já aconteceu | Aluno + Professor |

## 📊 Regras de Negócio

### Aluno pode:
- ❌ Cancelar aula `SOLICITADA`
- ❌ Cancelar aula `CONFIRMADA`
- ❌ NÃO pode alterar aula `RECUSADA` ou `REALIZADA`

### Professor pode:
- ✅ Aceitar aula `SOLICITADA` → vira `CONFIRMADA`
- ❌ Recusar aula `SOLICITADA` → vira `RECUSADA`
- ❌ Cancelar aula `CONFIRMADA` → vira `CANCELADA`
- ❌ NÃO pode alterar aula `RECUSADA`, `CANCELADA` ou `REALIZADA`

## 🔧 Estrutura Técnica

### AulaService (BehaviorSubject Pattern)

```typescript
// Quando o professor aceita uma aula:
aceitarAula(aulaId: number) {
  // 1. Atualiza no localStorage
  // 2. Emite novo valor no BehaviorSubject
  // 3. Todos os componentes "ouvindo" se atualizam automaticamente
}
```

**Benefícios:**
- ✅ UI reativa (atualização automática)
- ✅ Não precisa recarregar página
- ✅ Estado centralizado
- ✅ Fácil de testar

### Detecção de Usuário

```typescript
ngOnInit() {
  this.currentUser = this.authService.getCurrentUser();
  // Carrega apenas as aulas relevantes
  this.aulas$ = this.aulaService.getAulasPorUsuarioLogado();
}
```

**HTML Condicional:**
```html
<div *ngIf="isAluno()">
  <!-- Visão do aluno -->
</div>

<div *ngIf="isProfessor()">
  <!-- Visão do professor -->
</div>
```

## 🚀 Próximos Passos (Futuro)

1. **Integração com Backend**
   - Substituir `localStorage` por chamadas HTTP
   - Manter a mesma estrutura de Observables

2. **Feedback de Aulas**
   - Permitir aluno avaliar após `REALIZADA`
   - Modal de feedback com estrelas

3. **Notificações**
   - Avisar professor de novas solicitações
   - Avisar aluno quando aceita/recusa

4. **Filtros e Busca**
   - Filtrar por status, data, matéria
   - Ordenação customizável

5. **Calendário Visual**
   - Integração com biblioteca de calendário
   - Visão mensal/semanal

## ❓ FAQ

**P: Como adicionar uma nova aula?**
R: Use `aulaService.solicitarAula(novaAula)` - isso será implementado na página de detalhes do professor.

**P: Os dados persistem entre reloads?**
R: Sim! Estão no `localStorage` do navegador.

**P: Como limpar os dados mock?**
R: Abra o DevTools → Application → Local Storage → Delete "aulas"

**P: Por que usar BehaviorSubject?**
R: Para reatividade! Quando o professor aceita uma aula, todos os componentes que estão "ouvindo" se atualizam automaticamente.

## 🎓 Conceitos Aprendidos

✅ **Programação Reativa** - RxJS Observables e BehaviorSubject  
✅ **Component Communication** - Services como mediadores  
✅ **Conditional Rendering** - *ngIf com lógica de permissões  
✅ **State Management** - Centralização no Service  
✅ **TypeScript Types** - Union Types para status  
✅ **SSR-Safe** - isPlatformBrowser para localStorage  

---

**Desenvolvido com ❤️ para o TCC Estuda+**
