# ADR-004: Seleção do Algoritmo de Bisseção para o Solver do PricingEngine

**Status:** Aceito

## Contexto
O cálculo do Yield to Maturity (YTM) exige resolver uma equação de fluxo de caixa descontado não-linear por meio de métodos de aproximação numérica (solvers). O método de Newton-Raphson oferece convergência extremamente rápida (quadrática), mas exige o cálculo analítico da derivada da função de preço e pode divergir agressivamente, falhar ou entrar em loops infinitos caso o palpite inicial seja impreciso ou as condições de contorno (como preços distorcidos pelo mercado) sejam instáveis.

## Decisão
Implementar o método da Bisseção como o algoritmo padrão de busca de raízes do `PricingEngine`. O algoritmo operará delimitando um intervalo de busca (bracketing) fixado de -10% a 100% ao ano. O limite inferior negativo é mantido como margem de segurança macroeconômica defensiva para cenários extremos de estresse de mercado, enquanto o limite superior de 100% cobre qualquer distorção severa de preços.

## Consequências
- **Positiva (Robustez Absoluta):** O método da Bisseção possui convergência garantida por teorema matemático, desde que a raiz exista dentro do intervalo estipulado. Isso elimina cenários de travamento de CPU ou exceções não controladas em produção diante de inputs atípicos ou distorções de preços.
- **Negativa (Performance Computacional):** O método possui convergência linear, exigindo mais iterações na CPU do que abordagens baseadas em derivadas. Contudo, dado o volume de requisições projetado para a V1 e o custo computacional marginal de algumas dezenas de iterações aritméticas simples na memória por cálculo, esse impacto é considerado negligenciável.
- **Risco Conhecido (Falha de Bracketing):** Caso um input de preço seja tão distorcido que a raiz real caia fora do intervalo de -10% a 100%, a condição do teorema de troca de sinal falhará. O sistema disparará imediatamente a `SolverConvergenceException`, mapeada no diagrama de sequência, gerando o erro de negócio previsível (HTTP 422) em vez de um travamento.
