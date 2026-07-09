# ADR-002: Escolha do PostgreSQL como Camada de Persistência Relacional

**Status:** Aceito

## Contexto
A implementação da estratégia de cache assíncrono descrita na ADR-001 exige uma camada de persistência robusta. Cogitou-se o uso de uma arquitetura estritamente stateless na V1 ou a adoção de um banco NoSQL como DynamoDB visando escalabilidade horizontal automática. No entanto, os dados estruturados de instrumentos financeiros possuem fortes restrições de integridade, relações lógicas e necessidade de consultas baseadas em séries temporais (histórico de taxas).

## Decisão
Adotar o PostgreSQL como banco de dados principal desde a V1 do monólito. A escolha baseia-se na maturidade do ecossistema relacional, suporte rigoroso a transações ACID e facilidade de modelagem para dados financeiros estruturados.

## Consequências
- **Positiva (Segurança de Modelagem):** Garantia de consistência e integridade referencial ao correlacionar instrumentos de renda fixa e seus respectivos históricos de taxas diárias.
- **Positiva (Velocidade de Desenvolvimento):** Facilidade de manutenção de esquemas relacionais flexíveis na fase inicial do produto e simplicidade de setup local (Docker).
- **Negativa (Estado no Monólito):** O contêiner da aplicação deixa de ser puramente stateless, introduzindo a complexidade de gerenciamento de conexões (JDBC/HikariCP), migrações de esquema (Flyway) e barreiras para escalabilidade horizontal linear simples sem a adoção futura de um mecanismo de lock distribuído para o processo agendado.
