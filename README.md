# D.S Tolini

**API de renda fixa americana que transforma taxas cruas em respostas acionáveis.**

---

## O problema que resolve

Investidores brasileiros têm acesso a Treasuries americanas, mas nenhuma ferramenta simples
que responda perguntas diretas: *Qual o yield to maturity desse título com esse preço? Quanto
de juro acumulado vou pagar se comprar hoje? Qual a curva de vencimentos que eu deveria montar?*

D.S Tolini consome a Treasury Fiscal Data API, armazena os dados localmente e expõe endpoints
de cálculo financeiro precisos — YTM via bisseção numérica, juro acumulado (accrued interest),
preço sujo/limpo e bond ladder — validáveis via Postman ou curl.

---

## Escopo da V1

**Inclui:**
- Cálculo de Yield to Maturity (YTM) com solver de bisseção
- Cálculo de juro acumulado (accrued interest) e preço sujo/limpo
- Geração de escada de títulos (bond ladder)
- Ingestão assíncrona de taxas via Treasury Fiscal Data API (cache em PostgreSQL)
- Respostas em dólar, validáveis via Postman/curl

**Não inclui (V2):**
- Integração com BACEN (Selic, PTAX) — adiado conforme [ADR-003](docs/adr/adr-003-bacen-v2.md)
- Conversão cambial ou comparativo com renda fixa brasileira
- Autenticação e controle de acesso
- Front-end de qualquer natureza
- Outros países, moedas ou classes de ativo além de Treasuries em USD

---

## Arquitetura

Monólito Java + Spring Boot em estilo hexagonal (ports & adapters). Um núcleo de cálculo puro
(`PricingEngine`) isolado de todo I/O por trás de portas: `MarketDataSource` para fontes externas
e `InvestmentRepository` para o banco. A dependência da Treasury API fica fora do caminho crítico
do usuário — um `@Scheduled` Job faz a ingestão e popula o cache em PostgreSQL; o fluxo de
requisição lê apenas os dados locais já saneados.

---

## Documentação de design

### Decisões de Arquitetura (ADRs)

| # | Título | Status |
|---|--------|--------|
| [ADR-001](docs/adr/adr-001-ingestao-assincrona.md) | Ingestão Assíncrona e Cache de Cotações para Resiliência | Aceito |
| [ADR-002](docs/adr/adr-002-postgresql.md) | Escolha do PostgreSQL como Camada de Persistência Relacional | Aceito |
| [ADR-003](docs/adr/adr-003-bacen-v2.md) | Sequenciamento Estratégico da Integração com o BACEN para a V2 | Aceito |
| [ADR-004](docs/adr/adr-004-bisseccao.md) | Seleção do Algoritmo de Bisseção para o Solver do PricingEngine | Aceito |

### Diagramas

| Arquivo | Descrição |
|---------|-----------|
| [c4-l2-conteiner.mermaid](docs/diagrams/c4-l2-conteiner.mermaid) | Diagrama C4 nível 2 — visão de contêineres |
| [c4-l3-componentes.mermaid](docs/diagrams/c4-l3-componentes.mermaid) | Diagrama C4 nível 3 — componentes internos |
| [sequencia-ytm.mermaid](docs/diagrams/sequencia-ytm.mermaid) | Diagrama de sequência — fluxo de cálculo YTM |

> Os arquivos `.mermaid` podem ser visualizados em [mermaid.live](https://mermaid.live)
> ou diretamente no GitHub, que renderiza Mermaid nativamente em arquivos `.md` e `.mermaid`.

---

## Status

**Em desenvolvimento — Semana 1, núcleo de cálculo.**

O repositório está bootstrapado com a documentação de design (ADRs + diagramas). O próximo
passo é implementar o `PricingEngine` puro em Java: day-count conventions, cálculo de
accrued interest e o solver de YTM por bisseção — sem framework, sem I/O.
