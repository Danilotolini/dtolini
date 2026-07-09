# Contribuindo com D.S Tolini

## Modelo de branches — GitFlow

```
main          ←── merge de release/* e hotfix/* apenas
  │
develop       ←── integração contínua; base para feature/*
  │
  ├── feature/ytm-solver
  ├── feature/accrued-interest
  └── feature/bond-ladder

release/0.1.0 ←── criado de develop quando pronto para ship
hotfix/fix-X  ←── criado de main para correção emergencial
```

### Fluxo padrão (feature)

```bash
# 1. Criar branch a partir de develop
git checkout develop
git pull origin develop
git checkout -b feature/nome-curto

# 2. Desenvolver, commitar com Conventional Commits
git commit -m "feat: implementa solver de bisseção para YTM"

# 3. Abrir PR de feature/nome-curto → develop
gh pr create --base develop --title "feat: ..."

# 4. CI passa → merge via squash
```

### Fluxo de release

```bash
# 1. Criar branch de release a partir de develop
git checkout develop && git pull
git checkout -b release/0.1.0

# 2. Ajustes de versão e changelog apenas — sem features novas
mvn versions:set -DnewVersion=0.1.0

# 3. PR release/0.1.0 → main  (e merge de volta em develop)
gh pr create --base main --title "release: v0.1.0"

# 4. Tag na main dispara o workflow de Release
git tag v0.1.0
git push origin v0.1.0
```

### Fluxo de hotfix

```bash
git checkout main && git pull
git checkout -b hotfix/solver-overflow

# corrigir, testar, commitar
git commit -m "fix: corrige overflow no solver de bisseção para preços extremos"

# PR → main  E  cherry-pick ou PR → develop
gh pr create --base main
```

---

## Conventional Commits

| Prefixo | Quando usar |
|---------|-------------|
| `feat:` | Nova funcionalidade |
| `fix:` | Correção de bug |
| `test:` | Adição ou correção de testes |
| `refactor:` | Refatoração sem mudança de comportamento |
| `docs:` | Documentação (ADRs, README, diagramas) |
| `chore:` | Build, CI, dependências, configuração |
| `perf:` | Melhoria de performance |

Breaking changes: adicionar `!` após o prefixo → `feat!: remove endpoint legacy`.

---

## CI

O pipeline (`ci.yml`) roda em todo push para `feature/*`, `develop`, `release/*`, `hotfix/*`
e em todo PR aberto para `develop` ou `main`. Um PR só deve ser mergeado com CI verde.

## Pré-requisitos locais

- Java 21+
- Maven 3.9+

```bash
mvn test        # roda os testes
mvn verify      # build completo com checagens
```
