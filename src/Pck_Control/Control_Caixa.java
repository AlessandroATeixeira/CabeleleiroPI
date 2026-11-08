package Pck_Control;


import Pck_Model.Model_Caixa;
import Pck_Model.Model_RelatorioCaixa;
import Pck_Persistence.Persistence_Caixa;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Control_Caixa {
    private static final String[] MESES = {
            "Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

    Persistence_Caixa oPers = new Persistence_Caixa();

    /**
     * Registra a movimentacao. Para PRODUTO o banco ajusta o estoque na mesma transacao:
     * ENTRADA = venda (estoque diminui) e SAIDA = compra (estoque aumenta).
     *
     * @param valorTotal valor total da movimentacao (quantidade x valor unitario, no caso de produto)
     */
    public void Registrar(String tipo, String origem, Integer codProduto, String quantidade,
                          String descricao, double valorTotal) throws Exception {
        Model_Caixa m = new Model_Caixa();
        m.setTipo(tipo);
        m.setOrigem(origem);
        m.setDescricao(descricao == null ? "" : descricao.trim());
        m.setValor(Math.round(valorTotal * 100) / 100.0);

        if ("PRODUTO".equals(origem)) {
            if (codProduto == null) {
                throw new Exception("Selecione um produto.");
            }
            int qtd = Integer.parseInt(quantidade.trim());
            if (qtd <= 0) {
                throw new Exception("A quantidade deve ser maior que zero.");
            }
            m.setCodProduto(codProduto);
            m.setQuantidade(qtd);
        }
        if (m.getDescricao().isEmpty()) {
            throw new Exception("Informe uma descrição.");
        }
        if (m.getValor() <= 0) {
            throw new Exception("O valor deve ser maior que zero.");
        }
        oPers.PersInsCaixa(m);
    }

    public ArrayList<Model_Caixa> Consultar() throws Exception {
        return oPers.listCaixa();
    }

    public static double saldo(List<Model_Caixa> movimentos) {
        double saldo = 0;
        for (Model_Caixa m : movimentos) {
            saldo += m.isEntrada() ? m.getValor() : -m.getValor();
        }
        return saldo;
    }

    /** Resumo do caixa para a tela de relatorios, a partir das movimentacoes gravadas. */
    public List<Model_RelatorioCaixa> Resumo(String periodo) throws Exception {
        return resumo(periodo, Consultar(), LocalDate.now());
    }

    /**
     * Agrupa as movimentacoes por periodo.
     * Mensal = ultimos 6 meses, Semestral = ultimos 4 semestres, Anual = ultimos 3 anos
     * (periodos sem movimento aparecem zerados).
     */
    public static List<Model_RelatorioCaixa> resumo(String periodo, List<Model_Caixa> movimentos, LocalDate hoje) {
        Map<String, Model_RelatorioCaixa> grupos = new LinkedHashMap<>();

        if ("Semestral".equals(periodo)) {
            int ano = hoje.getYear();
            int sem = hoje.getMonthValue() <= 6 ? 1 : 2;
            List<String[]> lista = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                lista.add(0, new String[]{ano + "-" + sem, sem + "º Sem/" + String.format("%02d", ano % 100)});
                if (--sem == 0) {
                    sem = 2;
                    ano--;
                }
            }
            for (String[] c : lista) {
                grupos.put(c[0], new Model_RelatorioCaixa(c[1]));
            }
        } else if ("Anual".equals(periodo)) {
            for (int a = hoje.getYear() - 2; a <= hoje.getYear(); a++) {
                grupos.put(String.valueOf(a), new Model_RelatorioCaixa(String.valueOf(a)));
            }
        } else { // Mensal
            YearMonth atual = YearMonth.from(hoje);
            for (int i = 5; i >= 0; i--) {
                YearMonth ym = atual.minusMonths(i);
                grupos.put(ym.toString(), new Model_RelatorioCaixa(
                        MESES[ym.getMonthValue() - 1] + "/" + String.format("%02d", ym.getYear() % 100)));
            }
        }

        for (Model_Caixa m : movimentos) {
            LocalDate d = m.getDataHora().toLocalDate();
            String chave;
            if ("Semestral".equals(periodo)) {
                chave = d.getYear() + "-" + (d.getMonthValue() <= 6 ? 1 : 2);
            } else if ("Anual".equals(periodo)) {
                chave = String.valueOf(d.getYear());
            } else {
                chave = YearMonth.from(d).toString();
            }
            Model_RelatorioCaixa g = grupos.get(chave);
            if (g != null) {
                g.somar(m);
            }
        }
        return new ArrayList<>(grupos.values());
    }
}
