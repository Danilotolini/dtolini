# ADR-003: Sequenciamento Estratégico da Integração com o BACEN para a V2

**Status:** Aceito

## Contexto
O investidor brasileiro leigo necessita de contextualização de mercado para tomar decisões. A inclusão de dados do BACEN (taxa Selic como benchmark de renda fixa nacional e taxa PTAX para conversão cambial) permitiria exibir o rendimento das Treasuries traduzido para Reais e comparado ao ecossistema local ("CDBzão"). No entanto, injetar essas variáveis simultaneamente na V1 expande drasticamente o escopo, introduzindo armadilhas severas de modelagem cambial e diluindo o foco de validação do núcleo do sistema.

## Decisão
Adiar a integração com as APIs do BACEN para o escopo exclusivo da V2 (v1.5+). A V1 focará estritamente no isolamento e na precisão matemática do motor de cálculo de títulos americanos em Dólares. A arquitetura de software contudo já refletirá essa expansão futura no diagrama de componentes, utilizando a interface `MarketDataSource` para blindar o núcleo contra essa alteração.

## Consequências
- **Positiva (Foco e Entrega):** Redução severa do Time-to-Market e foco absoluto da engenharia no comportamento do motor de cálculo complexo de renda fixa americana.
- **Positiva (Design Extensível):** A abstração introduzida por portas e adaptadores (`MarketDataSource`) valida o princípio de design de que adicionar uma nova fonte brasileira no futuro exigirá apenas a criação de uma nova classe especializada, sem qualquer impacto ou refatoração no núcleo de domínio (`PricingEngine`).
- **Negativa (Valor Comercial Inicial Limitado):** A entrega da V1 atuará primariamente como uma validação técnica robusta via Postman/curl operando inteiramente em Dólares, sem a camada de apelo comercial da comparação cambial para o cliente final.
