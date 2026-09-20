# ADR-001: Ingestão Assíncrona e Cache de Cotações para Resiliência

**Status:** Aceito

## Contexto
A API da DTolini depende de dados de taxas de juros fornecidos pela Treasury Fiscal Data API (EUA) para executar o cálculo do Yield to Maturity (YTM). Chamadas de rede síncronas realizadas diretamente no fluxo de requisição do usuário final (request path) introduzem latência internacional imprevisível e expõem a plataforma a falhas em cascata, indisponibilidade ou timeouts caso o serviço do governo americano apresente instabilidade.

## Decisão
Isolar completamente a dependência externa do caminho crítico do usuário. A comunicação com a Treasury API será realizada exclusivamente por um processo em segundo plano (`@Scheduled` Job) executado de forma agendada. Este componente efetuará a ingestão dos dados diários e os persistirá em uma camada de banco de dados local. O fluxo de leitura do usuário consumirá estritamente os dados já saneados e armazenados localmente.

## Consequências
- **Positiva (Resiliência):** O sistema mitiga o risco de rede externa. Se a Treasury API ficar temporariamente indisponível, os usuários continuam operando normalmente com base no último estado válido dos dados.
- **Positiva (Performance):** O tempo de resposta da API de cálculo cai drasticamente ao eliminar operações de I/O internacional do caminho crítico.
- **Negativa (Staleness):** Aceita-se o trade-off de trabalhar com dados potencialmente defasados por até 24 horas (intervalo tolerável dado o comportamento macroeconômico e o horizonte de investimento de longo prazo das Treasuries).
