# Estuda Mais - Frontend Development Guidelines

> **⚠️ IMPORTANTE:** Este projeto está em processo de migração do backend mock (db.json) para o backend real (Spring Boot + PostgreSQL).  
> **📚 Documentação completa:** Veja `README_MIGRACAO.md` na raiz do projeto para o plano completo.

## Architecture Overview

**Full-stack tutoring platform** built with Angular 20.2.0 and Spring Boot 3.5.5 backend. Frontend uses **standalone components** (no NgModules for features) with SSR support via `@angular/ssr`.

### Current State (Migration in Progress)
- **Frontend:** Angular 20 usando `db.json` mock (json-server)
- **Backend:** Spring Boot 80% implementado, aguardando migração
- **Target:** Migrar 100% das funcionalidades para backend real

### Project Structure
- `Frontend/angular-app/src/app/` - Main application code
  - `auth/` - Authentication flows (login, register) with lazy-loaded routes
  - `core/` - Singleton services (`AuthService`, `ProfessorService`, `MateriaService`) and guards
  - `pages/` - Feature pages (home, busca, perfil, perfil-edit, my-classes, professor-detalhe)
  - `shared/` - Reusable components, models, and layouts
    - `models/` - TypeScript interfaces (centralized exports via `index.ts`)
    - `components/` - Shared UI components (navbar, footer, card-professor, profile-completion-modal)
    - `layouts/` - Layout wrappers (main-layout, auth-layout)

### Routing Architecture
- **Root routes** (`app.routes.ts`): Top-level navigation with auth guard protecting authenticated routes
- **Lazy-loaded auth** (`auth.routes.ts`): Authentication flows loaded on-demand
- **Layout-based routing**: Protected routes wrapped in `MainLayoutComponent` (navbar + footer + outlet)

## Critical Development Patterns

### 1. File Naming Conventions (Non-Standard)
**IMPORTANT**: This project uses **unconventional naming**:
- Components: `login.ts`, `navbar.ts`, `perfil-edit.ts` (NOT `.component.ts`)
- Templates: `login.html`, `busca.html` (NOT `.component.html`)
- Styles: `login.css`, `perfil.css` (NOT `.component.css`)
- Services: `auth.ts`, `professor.ts` (NOT `.service.ts`)
- Models: `usuarioModel.ts`, `professorModel.ts` (PascalCase + `Model` suffix)

**Exception**: `home.component.ts` uses standard Angular naming (likely migration artifact).

### 2. Standalone Components Pattern
All components are standalone with explicit imports:
```typescript
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent implements OnInit { }
```

### 3. SSR-Safe Browser Code
When accessing browser APIs (localStorage, etc.), **always use PLATFORM_ID check**:
```typescript
import { Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

constructor(@Inject(PLATFORM_ID) private platformId: Object) {}

someMethod() {
  if (isPlatformBrowser(this.platformId)) {
    localStorage.setItem('key', 'value');
  }
}
```
See `AuthService` and `NavbarComponent` for reference implementations.

### 4. Data Management (No Backend Integration Yet)
Services use **mock data with RxJS observables**:
```typescript
// ProfessorService pattern
private mockProfessores: Professor[] = [ /* ... */ ];

getProfessoresPorMateria(materiaId: number): Observable<Professor[]> {
  const filtered = this.mockProfessores.filter(/* ... */);
  return of(filtered).pipe(delay(500)); // Simulate API latency
}
```

### 5. Authentication Flow
- `AuthService` manages auth state via `BehaviorSubject<boolean>`
- User data stored in localStorage as `usuarioLogado` (JSON string)
- `authGuard` (functional guard) protects routes, redirects to `/auth/login` if unauthenticated
- All user types inherit from `Usuario` interface with `tipoUsuario: 'ALUNO' | 'PROFESSOR'`
- Profile completion check: `isProfileComplete()` verifies required fields
  - **ALUNO**: requires `escolaridade` AND `interesse`
  - **PROFESSOR**: requires `sobre` AND `valorHora > 0`

### 6. Model Inheritance Pattern
```typescript
export interface Usuario {
  id: number;
  email: string;
  tipoUsuario: 'ALUNO' | 'PROFESSOR';
  // common fields...
}

export interface Aluno extends Usuario {
  escolaridade?: string;
  interesse?: string; // Objetivo: APRENDER_NOVO, REFORCAR_CONHECIMENTO, PREPARAR_CONQUISTA
}

export interface Professor extends Usuario {
  sobre?: string;
  valorHora?: number;
  materias?: Materia[];
  // professor-specific fields...
}
```
Import models from barrel file: `import { Professor, Usuario, Materia } from '../../shared/models';`

## Development Workflow

### Running the Application
```powershell
cd Frontend/angular-app
npm start                    # Dev server (http://localhost:4200)
npm run build                # Production build
npm run serve:ssr:angular-app # SSR preview
npm test                     # Karma/Jasmine tests
```

### Code Style
- **Prettier configured**: 100 char width, single quotes, Angular HTML parser
- **TypeScript 5.9**: Strict mode enabled
- Components use `OnInit` lifecycle hook pattern consistently

### Testing
- Jasmine + Karma setup
- Spec files co-located with implementation (e.g., `login.ts` + `login.spec.ts`)
- Run tests before committing changes

## Common Gotchas

1. **Don't use `.component.ts` suffix** - Files are named without it (except `home.component.ts`)
2. **Always inject PLATFORM_ID** when accessing browser APIs for SSR compatibility
3. **Import from barrel files** - Use `'../../shared/models'` instead of individual model imports
4. **Mock data is temporary** - Services currently use mock data; prepare for backend integration
5. **Angular 20 standalone-first** - No feature modules, but `CoreModule` and `SharedModule` exist as empty placeholders

## Key Files Reference

- **Routing setup**: `src/app/app.routes.ts`, `src/app/auth/auth.routes.ts`
- **Auth logic**: `src/app/core/services/auth.ts`, `src/app/core/guards/auth.guard.ts`
- **Layout wrapper**: `src/app/shared/layouts/main-layout/main-layout.ts`
- **Model definitions**: `src/app/shared/models/*.ts` (exported via `index.ts`)
- **SSR config**: `src/app/app.config.server.ts`, `src/main.server.ts`
- **Build config**: `angular.json` (SSR enabled via `outputMode: "server"`)
