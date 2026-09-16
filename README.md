<div align="center">

# DTolini

**Transforma taxas cruas de Treasuries americanas em respostas acionáveis.**

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Arquitetura](https://img.shields.io/badge/Arquitetura-Hexagonal-6366f1)](#arquitetura)
[![Status](https://img.shields.io/badge/Status-Em%20Desenvolvimento-f59e0b)](#status)

</div>

---

## O problema

O investidor brasileiro que compra Treasuries americanas não tem uma ferramenta direta que responda:

- *Qual o yield to maturity desse título com esse preço de mercado?*
- *Quanto de juro acumulado vou pagar comprando entre cupons?*
- *Qual a escada de vencimentos que otimiza meu fluxo de caixa?*

DTolini responde essas perguntas via API, consumindo dados reais da Treasury Fiscal Data API e executando os cálculos de forma precisa e auditável.

---

## Escopo da V1

### O que está incluído

| Funcionalidade | Descrição |
|---|---|
| **Yield to Maturity** | Solver numérico por bisseção, convergência garantida por teorema |
| **Accrued Interest** | Juro acumulado entre datas de cupom com day-count convention |
| **Preço sujo / limpo** | Precificação a partir do yield ou do preço de mercado |
| **Bond Ladder** | Escada de vencimentos a partir de um portfólio de títulos |
| **Atualização diária** | Taxas obtidas automaticamente da Treasury Fiscal Data API |

### O que **não** está na V1

- Integração com BACEN (Selic, PTAX) — [ADR-003](docs/adr/adr-003-bacen-v2.md) documenta o motivo
- Conversão cambial ou comparativo com renda fixa brasileira
- Autenticação e controle de acesso
- Interface gráfica de qualquer tipo
- Outros países, moedas ou classes de ativo além de Treasuries em USD

---

## Arquitetura

Monólito Java + Spring Boot em estilo **hexagonal (ports & adapters)**.

```
┌─────────────────────────────────────────────┐
│              Adaptadores de entrada          │
│           (REST Controllers / Jobs)          │
└────────────────────┬────────────────────────┘
                     │ porta
          ┌──────────▼──────────┐
          │    PricingEngine    │  ← núcleo puro, sem I/O
          │  (domínio isolado)  │
          └──────────┬──────────┘
                     │ porta
┌────────────────────▼────────────────────────┐
│           Adaptadores de saída               │
│   MarketDataSource │ InvestmentRepository    │
│  (Treasury API)    │     (PostgreSQL)        │
└─────────────────────────────────────────────┘
```

A dependência da Treasury API fica fora do caminho crítico do usuário. Um processo agendado obtém as taxas diariamente e as armazena localmente. O fluxo de requisição lê apenas dados já processados, detalhado no [ADR-001](docs/adr/adr-001-ingestao-assincrona.md).

---

## Documentação de design

### Decisões de Arquitetura (ADRs)

| ADR | Título | Decisão resumida |
|-----|--------|-----------------|
| [ADR-001](docs/adr/adr-001-ingestao-assincrona.md) | Ingestão Assíncrona | Processo agendado isola a Treasury API do caminho do usuário |
| [ADR-002](docs/adr/adr-002-postgresql.md) | PostgreSQL como persistência | Modelo relacional para dados financeiros com séries temporais |
| [ADR-003](docs/adr/adr-003-bacen-v2.md) | Integração BACEN adiada para V2 | V1 foca no motor de cálculo; BACEN/PTAX entram depois |
| [ADR-004](docs/adr/adr-004-bisseccao.md) | Bisseção como solver de YTM | Convergência garantida vs. Newton-Raphson |

### Diagramas C4

| Diagrama | Nível | Descrição |
|----------|-------|-----------|
| [c4-l1-contexto.mermaid](docs/diagrams/c4-l1-contexto.mermaid) | C1 | O sistema no mundo: usuário e dependências externas |
| [c4-l2-conteiner.mermaid](docs/diagrams/c4-l2-conteiner.mermaid) | C2 | Contêineres: aplicação, banco de dados |
| [c4-l3-componentes.mermaid](docs/diagrams/c4-l3-componentes.mermaid) | C3 | Componentes internos da aplicação |
| [c4-l4-codigo.mermaid](docs/diagrams/c4-l4-codigo.mermaid) | C4 | Classes e interfaces do domínio |
| [sequencia-ytm.mermaid](docs/diagrams/sequencia-ytm.mermaid) | Sequência | Fluxo completo do cálculo de YTM |

> Os arquivos `.mermaid` são renderizados automaticamente pelo GitHub e também podem ser
> visualizados em [mermaid.live](https://mermaid.live).

---

## Como executar

```bash
# clonar
git clone https://github.com/Danilotolini/dtolini.git
cd dtolini

# rodar os testes (Java 21 + Maven necessários)
mvn test
```

> Para subir a aplicação completa é necessário Docker. Consulte o `docker-compose.yml` na raiz do projeto.

---

## Status

**Semana 1 — núcleo de cálculo.**

O repositório está estruturado com a documentação de design completa (4 ADRs + 5 diagramas). O próximo passo é implementar o `PricingEngine` em Java puro: day-count conventions, accrued interest e o solver de YTM por bisseção.
