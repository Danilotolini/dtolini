package com.dtolini.domain.engine;

import com.dtolini.domain.exception.SolverConvergenceException;
import com.dtolini.domain.model.Bond;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Motor de cálculo puro de renda fixa americana.
 *
 * <p><strong>Esta classe é a sua zona de trabalho.</strong>
 * Implemente os três métodos abaixo. Nenhum outro arquivo precisa ser alterado
 * para a API funcionar de ponta a ponta.
 *
 * <h2>Contratos</h2>
 * <ol>
 *   <li>{@link #yieldToMaturity} — preço limpo → YTM via bisseção (ADR-004)
 *   <li>{@link #dirtyPrice}      — YTM → preço sujo via DCF
 *   <li>{@link #accruedInterest} — juro acumulado via Actual/Actual (ICMA)
 * </ol>
 *
 * <h2>Referências</h2>
 * <ul>
 *   <li>Frank Fabozzi — "Fixed Income Mathematics", caps. 2 e 3
 *   <li><a href="../../../../../docs/adr/adr-004-bisseccao.md">ADR-004</a> — justifica a bisseção como solver
 *   <li><a href="../../../../../docs/diagrams/sequencia-ytm.mermaid">sequencia-ytm.mermaid</a> — fluxo de chamada
 * </ul>
 */
@Service
public class PricingEngine {

    // ┌──────────────────────────────────────────────────────────────────────┐
    // │  IMPLEMENTE OS TRÊS MÉTODOS ABAIXO                                   │
    // │  Os métodos lançam UnsupportedOperationException até serem escritos. │
    // └──────────────────────────────────────────────────────────────────────┘

    /**
     * Calcula o Yield to Maturity dado o preço limpo do título.
     *
     * <p>Use o método da bisseção sobre o intervalo {@code [-0.10, 1.00]} (ADR-004).
     * A função {@code f(y) = dirtyPrice(y) − (cleanPrice + accruedInterest(y))}
     * deve trocar de sinal dentro do intervalo para a convergência ser garantida.
     *
     * <p>Critério de parada sugerido: {@code |f(y)| < 1e-8} ou {@code 100} iterações.
     *
     * @param bond           título com cupom, vencimento e frequência de pagamento
     * @param cleanPrice     preço limpo como % do par (ex: {@code 98.75})
     * @param settlementDate data de liquidação (T+1 para Treasuries)
     * @return YTM como decimal anual (ex: {@code 0.0462} para 4,62%)
     * @throws SolverConvergenceException se não houver raiz no intervalo de busca
     */
    public BigDecimal yieldToMaturity(Bond bond, BigDecimal cleanPrice, LocalDate settlementDate) {
        throw new UnsupportedOperationException(
                "TODO — implemente o solver de bisseção. Leia ADR-004 e Fabozzi cap. 3.");
    }

    /**
     * Calcula o preço sujo (dirty price) dado o YTM.
     *
     * <p>Para um Treasury semi-anual ({@code frequency = 2}):
     * <pre>
     *   Preço Sujo = Σ [ (C/2) / (1 + y/2)^t ] + F / (1 + y/2)^n
     * </pre>
     * onde {@code t} é o tempo em semestres até cada fluxo de caixa,
     * incluindo a fração do período corrente calculada pelo juro acumulado.
     *
     * @param bond           título
     * @param yield          YTM anual como decimal (ex: {@code 0.0462})
     * @param settlementDate data de liquidação
     * @return preço sujo como % do par
     */
    public BigDecimal dirtyPrice(Bond bond, BigDecimal yield, LocalDate settlementDate) {
        throw new UnsupportedOperationException(
                "TODO — implemente a fórmula DCF para preço sujo. Fabozzi cap. 2.");
    }

    /**
     * Calcula o juro acumulado usando a convenção Actual/Actual (ICMA).
     *
     * <p>Todos os Treasuries usam Actual/Actual:
     * <pre>
     *   AI = (C / 2) × (dias desde o último cupom / dias no período do cupom)
     * </pre>
     * O "último cupom" e o "próximo cupom" devem ser derivados de
     * {@link Bond#maturityDate()} retrocedendo em múltiplos de 6 meses.
     *
     * @param bond           título
     * @param settlementDate data de liquidação
     * @return juro acumulado como % do par
     */
    public BigDecimal accruedInterest(Bond bond, LocalDate settlementDate) {
        throw new UnsupportedOperationException(
                "TODO — implemente o juro acumulado Actual/Actual. Fabozzi cap. 2, seção 'Accrued Interest'.");
    }
}
