package Pck_View;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Tela_Relatorio_Caixa extends Tela_Base {

    private JComboBox<String> comboPeriodo;
    private JLabel lblTotalEntradas;
    private JLabel lblTotalSaidas;
    private JLabel lblSaldo;

    private DefaultTableModel modelo;
    private ChartPanel grafico;

    public Tela_Relatorio_Caixa() {
        super("Relatórios", "Entradas, saídas e saldo do caixa por período.");
        criarComponentes();
    }

    private void criarComponentes() {
        // ---- Filtro no cabecalho ----
        comboPeriodo = Tema.combo("Mensal", "Semestral", "Anual");
        comboPeriodo.setPreferredSize(new Dimension(160, 40));
        Tema.Botao btnAtualizar = new Tema.Botao("Atualizar", Tema.TipoBotao.SECUNDARIO);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filtro.setOpaque(false);
        JLabel lblPeriodo = Tema.rotulo("Período");
        filtro.add(lblPeriodo);
        filtro.add(comboPeriodo);
        filtro.add(btnAtualizar);
        setControlesCabecalho(filtro);

        // ---- Indicadores ----
        lblTotalEntradas = new JLabel();
        lblTotalSaidas = new JLabel();
        lblSaldo = new JLabel();

        JPanel indicadores = new JPanel(new GridLayout(1, 3, 18, 0));
        indicadores.setOpaque(false);
        indicadores.add(criarIndicador("Entradas", lblTotalEntradas, Tema.VINHO));
        indicadores.add(criarIndicador("Saídas", lblTotalSaidas, Tema.AREIA));
        indicadores.add(criarIndicador("Saldo", lblSaldo, Tema.VINHO_ESCURO));

        // ---- Grafico ----
        grafico = new ChartPanel();
        Tema.Cartao cartaoGrafico = cartaoComTitulo("Entradas e saídas por período", null);
        cartaoGrafico.add(grafico, BorderLayout.CENTER);
        cartaoGrafico.add(criarLegenda(), BorderLayout.SOUTH);

        // ---- Tabela ----
        modelo = new DefaultTableModel(new Object[]{"Período", "Entradas", "Saídas", "Saldo"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        JTable tabela = new JTable(modelo);
        Tema.Cartao cartaoTabela = cartaoComTitulo("Resumo", null);
        cartaoTabela.add(Tema.tabela(tabela), BorderLayout.CENTER);

        JPanel centro = new JPanel(new GridLayout(1, 2, 18, 0));
        centro.setOpaque(false);
        centro.add(cartaoGrafico);
        centro.add(cartaoTabela);

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setOpaque(false);
        conteudo.add(indicadores, BorderLayout.NORTH);
        conteudo.add(centro, BorderLayout.CENTER);
        setConteudo(conteudo);

        btnAtualizar.addActionListener(e -> carregarDados());
        comboPeriodo.addActionListener(e -> carregarDados());
    }

    @Override
    public void aoExibir() {
        carregarDados();
    }

    private JPanel criarIndicador(String titulo, JLabel valor, Color destaque) {
        Tema.Cartao c = new Tema.Cartao(new BorderLayout(0, 6)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = Tema.suave(g);
                g2.setColor(destaque);
                g2.fillRoundRect(0, 18, 5, getHeight() - 36, 5, 5);
                g2.dispose();
            }
        };
        c.setBorder(BorderFactory.createEmptyBorder(16, 26, 16, 20));
        JLabel t = new JLabel(titulo);
        t.setFont(Tema.fonte(Font.BOLD, 12));
        t.setForeground(Tema.TEXTO_SUAVE);
        valor.setFont(Tema.fonte(Font.BOLD, 24));
        valor.setForeground(Tema.TEXTO);
        c.add(t, BorderLayout.NORTH);
        c.add(valor, BorderLayout.CENTER);
        return c;
    }

    private JPanel criarLegenda() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        p.setOpaque(false);
        p.add(itemLegenda("Entradas", Tema.VINHO));
        p.add(itemLegenda("Saídas", Tema.AREIA));
        return p;
    }

    private JLabel itemLegenda(String texto, Color cor) {
        JLabel l = new JLabel(texto, new javax.swing.Icon() {
            public int getIconWidth() { return 12; }
            public int getIconHeight() { return 12; }
            public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
                Graphics2D g2 = Tema.suave(g);
                g2.setColor(cor);
                g2.fillRoundRect(x, y, 12, 12, 4, 4);
                g2.dispose();
            }
        }, JLabel.LEFT);
        l.setIconTextGap(6);
        l.setFont(Tema.fonte(Font.PLAIN, 12));
        l.setForeground(Tema.TEXTO_SUAVE);
        return l;
    }

    private void carregarDados() {
        modelo.setRowCount(0);

        String periodoSelecionado = String.valueOf(comboPeriodo.getSelectedItem());
        List<ResumoPeriodo> dados = gerarDadosExemplo(periodoSelecionado);

        double totalEntradas = 0;
        double totalSaidas = 0;

        for (ResumoPeriodo item : dados) {
            modelo.addRow(new Object[]{
                    item.periodo,
                    Tema.moedaCurta(item.entradas),
                    Tema.moedaCurta(item.saidas),
                    Tema.moedaCurta(item.entradas - item.saidas)
            });
            totalEntradas += item.entradas;
            totalSaidas += item.saidas;
        }

        lblTotalEntradas.setText(Tema.moeda(totalEntradas));
        lblTotalSaidas.setText(Tema.moeda(totalSaidas));
        lblSaldo.setText(Tema.moeda(totalEntradas - totalSaidas));

        grafico.setDados(dados);
    }

    private List<ResumoPeriodo> gerarDadosExemplo(String periodo) {
        List<ResumoPeriodo> dados = new ArrayList<>();

        if (periodo.equals("Mensal")) {
            dados.add(new ResumoPeriodo("Jan", 8500, 4200));
            dados.add(new ResumoPeriodo("Fev", 9200, 4600));
            dados.add(new ResumoPeriodo("Mar", 7800, 3900));
            dados.add(new ResumoPeriodo("Abr", 10100, 5100));
            dados.add(new ResumoPeriodo("Mai", 11200, 5400));
            dados.add(new ResumoPeriodo("Jun", 9800, 4800));
        }

        if (periodo.equals("Semestral")) {
            dados.add(new ResumoPeriodo("1º Semestre", 56800, 28000));
            dados.add(new ResumoPeriodo("2º Semestre", 62100, 30500));
        }

        if (periodo.equals("Anual")) {
            dados.add(new ResumoPeriodo("2024", 118900, 58200));
            dados.add(new ResumoPeriodo("2025", 132500, 64100));
            dados.add(new ResumoPeriodo("2026", 145700, 69300));
        }

        return dados;
    }

    // ------------------------------------------------------------------
    // Grafico de barras
    // ------------------------------------------------------------------
    private static class ChartPanel extends JPanel {

        private List<ResumoPeriodo> dados = new ArrayList<>();

        ChartPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(300, 240));
        }

        void setDados(List<ResumoPeriodo> dados) {
            this.dados = dados;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = Tema.suave(graphics);

            int largura = getWidth();
            int altura = getHeight();
            int margemEsquerda = 58;
            int margemDireita = 6;
            int margemSuperior = 10;
            int margemInferior = 28;
            int areaLargura = largura - margemEsquerda - margemDireita;
            int areaAltura = altura - margemSuperior - margemInferior;

            if (dados == null || dados.isEmpty() || areaLargura <= 0 || areaAltura <= 0) {
                g.setFont(Tema.fonte(Font.PLAIN, 13));
                g.setColor(Tema.TEXTO_SUAVE);
                g.drawString("Nenhum dado disponível.", margemEsquerda, margemSuperior + 30);
                g.dispose();
                return;
            }

            double maiorValor = 1;
            for (ResumoPeriodo item : dados) {
                maiorValor = Math.max(maiorValor, Math.max(item.entradas, item.saidas));
            }

            maiorValor = escalaMaxima(maiorValor);
            int base = margemSuperior + areaAltura;
            g.setFont(Tema.fonte(Font.PLAIN, 11));

            // linhas de grade e escala
            for (int i = 0; i <= 4; i++) {
                int y = base - (i * areaAltura / 4);
                g.setColor(Tema.BORDA);
                g.setStroke(new BasicStroke(1f));
                g.drawLine(margemEsquerda, y, largura - margemDireita, y);

                g.setColor(Tema.TEXTO_SUAVE);
                String rotulo = abreviar(maiorValor * i / 4);
                int w = g.getFontMetrics().stringWidth(rotulo);
                g.drawString(rotulo, margemEsquerda - 8 - w, y + 4);
            }

            int n = dados.size();
            int larguraGrupo = areaLargura / n;
            int larguraBarra = Math.max(10, Math.min(34, larguraGrupo / 4));

            g.setClip(0, 0, largura, base);
            for (int i = 0; i < n; i++) {
                ResumoPeriodo item = dados.get(i);
                int xCentro = margemEsquerda + i * larguraGrupo + larguraGrupo / 2;

                int hEntrada = (int) (item.entradas / maiorValor * areaAltura);
                int hSaida = (int) (item.saidas / maiorValor * areaAltura);

                g.setColor(Tema.VINHO);
                g.fillRoundRect(xCentro - larguraBarra - 3, base - hEntrada, larguraBarra, hEntrada + 8, 8, 8);
                g.setColor(Tema.AREIA);
                g.fillRoundRect(xCentro + 3, base - hSaida, larguraBarra, hSaida + 8, 8, 8);
            }
            g.setClip(null);

            // rotulos do eixo x
            g.setColor(Tema.TEXTO_SUAVE);
            g.setFont(Tema.fonte(Font.PLAIN, 12));
            for (int i = 0; i < n; i++) {
                String periodo = dados.get(i).periodo;
                int xCentro = margemEsquerda + i * larguraGrupo + larguraGrupo / 2;
                int w = g.getFontMetrics().stringWidth(periodo);
                g.drawString(periodo, xCentro - w / 2, base + 19);
            }
            g.dispose();
        }

        /** Arredonda o maior valor para cima, de modo que 4 divisoes deem numeros redondos. */
        private double escalaMaxima(double maximo) {
            double magnitude = Math.pow(10, Math.floor(Math.log10(maximo)));
            double[] candidatos = {1.2, 1.6, 2, 2.4, 3.2, 4, 6, 8, 10};
            for (double c : candidatos) {
                if (c * magnitude >= maximo) {
                    return c * magnitude;
                }
            }
            return 10 * magnitude;
        }

        private String abreviar(double valor) {
            if (valor >= 1000) {
                double mil = valor / 1000;
                String fmt = Math.abs(mil - Math.rint(mil)) < 0.0001 ? "R$ %.0fk" : "R$ %.1fk";
                return String.format(fmt, mil).replace('.', ',');
            }
            return String.format("R$ %.0f", valor);
        }
    }

    private static class ResumoPeriodo {
        private final String periodo;
        private final double entradas;
        private final double saidas;

        ResumoPeriodo(String periodo, double entradas, double saidas) {
            this.periodo = periodo;
            this.entradas = entradas;
            this.saidas = saidas;
        }
    }
}
