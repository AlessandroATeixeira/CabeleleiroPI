import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class CashReportScreen extends JFrame {

    private JComboBox<String> comboPeriodo;
    private JLabel lblTotalEntradas;
    private JLabel lblTotalSaidas;
    private JLabel lblSaldo;

    private DefaultTableModel modelo;
    private ChartPanel grafico;

    public CashReportScreen() {
        setTitle("Relatórios de Caixa");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        criarComponentes();
        carregarDados();

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel painelSuperior = new JPanel();

        JLabel lblPeriodo = new JLabel("Período:");
        comboPeriodo = new JComboBox<>(
                new String[] {
                        "Mensal",
                        "Semestral",
                        "Anual"
                }
        );

        JButton btnAtualizar = new JButton("Atualizar");

        painelSuperior.add(lblPeriodo);
        painelSuperior.add(comboPeriodo);
        painelSuperior.add(btnAtualizar);

        add(painelSuperior, BorderLayout.NORTH);

        JPanel painelResumo = new JPanel();

        lblTotalEntradas = criarLabelResumo(
                "Entradas: R$ 0,00",
                new Color(0, 130, 0)
        );

        lblTotalSaidas = criarLabelResumo(
                "Saídas: R$ 0,00",
                new Color(180, 0, 0)
        );

        lblSaldo = criarLabelResumo(
                "Saldo: R$ 0,00",
                new Color(0, 70, 150)
        );

        painelResumo.add(lblTotalEntradas);
        painelResumo.add(lblTotalSaidas);
        painelResumo.add(lblSaldo);

        add(painelResumo, BorderLayout.SOUTH);

        String[] colunas = {
                "Período",
                "Entradas",
                "Saídas",
                "Saldo"
        };

        modelo = new DefaultTableModel(colunas, 0);

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(25);

        JScrollPane barraRolagem = new JScrollPane(tabela);

        grafico = new ChartPanel();
        grafico.setPreferredSize(new Dimension(850, 350));

        JPanel painelCentro = new JPanel(new BorderLayout(10, 10));
        painelCentro.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        painelCentro.add(grafico, BorderLayout.CENTER);
        painelCentro.add(barraRolagem, BorderLayout.SOUTH);

        add(painelCentro, BorderLayout.CENTER);

        btnAtualizar.addActionListener(evento -> carregarDados());

        comboPeriodo.addActionListener(evento -> carregarDados());
    }

    private JLabel criarLabelResumo(String texto, Color cor) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(cor);
        label.setBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        );
        return label;
    }

    private void carregarDados() {
        modelo.setRowCount(0);

        String periodoSelecionado =
                comboPeriodo.getSelectedItem().toString();

        /*
         * Estes valores são exemplos.
         *
         * Posteriormente, substitua este método por uma consulta ao banco
         * de dados usando os registros reais da tabela de caixa.
         */
        List<ResumoPeriodo> dados = gerarDadosExemplo(
                periodoSelecionado
        );

        double totalEntradas = 0;
        double totalSaidas = 0;

        for (ResumoPeriodo item : dados) {
            double saldo = item.entradas - item.saidas;

            modelo.addRow(new Object[] {
                    item.periodo,
                    formatarValor(item.entradas),
                    formatarValor(item.saidas),
                    formatarValor(saldo)
            });

            totalEntradas += item.entradas;
            totalSaidas += item.saidas;
        }

        double saldoTotal = totalEntradas - totalSaidas;

        lblTotalEntradas.setText(
                "Entradas: " + formatarValor(totalEntradas)
        );

        lblTotalSaidas.setText(
                "Saídas: " + formatarValor(totalSaidas)
        );

        lblSaldo.setText(
                "Saldo: " + formatarValor(saldoTotal)
        );

        grafico.setDados(dados);
    }

    private List<ResumoPeriodo> gerarDadosExemplo(String periodo) {
        List<ResumoPeriodo> dados = new ArrayList<>();

        if (periodo.equals("Mensal")) {
            dados.add(new ResumoPeriodo("Janeiro", 8500, 4200));
            dados.add(new ResumoPeriodo("Fevereiro", 9200, 4600));
            dados.add(new ResumoPeriodo("Março", 7800, 3900));
            dados.add(new ResumoPeriodo("Abril", 10100, 5100));
            dados.add(new ResumoPeriodo("Maio", 11200, 5400));
            dados.add(new ResumoPeriodo("Junho", 9800, 4800));
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

    private String formatarValor(double valor) {
        return String.format(
                "R$ %.2f",
                valor
        ).replace(".", ",");
    }

    private class ChartPanel extends JPanel {

        private List<ResumoPeriodo> dados =
                new ArrayList<>();

        public void setDados(List<ResumoPeriodo> dados) {
            this.dados = dados;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics;

            g.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int largura = getWidth();
            int altura = getHeight();

            int margemEsquerda = 70;
            int margemDireita = 30;
            int margemSuperior = 50;
            int margemInferior = 70;

            int areaLargura =
                    largura - margemEsquerda - margemDireita;

            int areaAltura =
                    altura - margemSuperior - margemInferior;

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, largura, altura);

            g.setColor(Color.DARK_GRAY);
            g.setFont(new Font("Arial", Font.BOLD, 16));

            String titulo = "Entradas e saídas por período";

            g.drawString(
                    titulo,
                    margemEsquerda,
                    25
            );

            if (dados == null || dados.isEmpty()) {
                g.setFont(new Font("Arial", Font.PLAIN, 14));
                g.drawString(
                        "Nenhum dado disponível.",
                        margemEsquerda,
                        margemSuperior + 30
                );
                return;
            }

            double maiorValor = obterMaiorValor();

            int quantidadePeriodos = dados.size();
            int larguraGrupo =
                    areaLargura / quantidadePeriodos;

            int larguraBarra = Math.max(
                    10,
                    larguraGrupo / 4
            );

            for (int i = 0; i <= 5; i++) {
                int y =
                        margemSuperior
                                + areaAltura
                                - (i * areaAltura / 5);

                g.setColor(new Color(220, 220, 220));
                g.drawLine(
                        margemEsquerda,
                        y,
                        largura - margemDireita,
                        y
                );

                double valorLinha =
                        maiorValor * i / 5;

                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("Arial", Font.PLAIN, 11));

                g.drawString(
                        formatarValorAbreviado(valorLinha),
                        5,
                        y + 5
                );
            }

            for (int i = 0; i < dados.size(); i++) {
                ResumoPeriodo item = dados.get(i);

                int xCentro =
                        margemEsquerda
                                + i * larguraGrupo
                                + larguraGrupo / 2;

                int alturaEntrada =
                        calcularAltura(item.entradas, maiorValor, areaAltura);

                int alturaSaida =
                        calcularAltura(item.saidas, maiorValor, areaAltura);

                int xEntrada =
                        xCentro - larguraBarra - 3;

                int xSaida =
                        xCentro + 3;

                int yEntrada =
                        margemSuperior
                                + areaAltura
                                - alturaEntrada;

                int ySaida =
                        margemSuperior
                                + areaAltura
                                - alturaSaida;

                g.setColor(new Color(40, 160, 70));
                g.fillRect(
                        xEntrada,
                        yEntrada,
                        larguraBarra,
                        alturaEntrada
                );

                g.setColor(new Color(210, 60, 60));
                g.fillRect(
                        xSaida,
                        ySaida,
                        larguraBarra,
                        alturaSaida
                );

                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font("Arial", Font.PLAIN, 11));

                String periodo = item.periodo;

                int textoX =
                        xCentro
                                - (periodo.length() * 3);

                g.drawString(
                        periodo,
                        textoX,
                        margemSuperior + areaAltura + 20
                );
            }

            g.setColor(new Color(40, 160, 70));
            g.fillRect(20, 20, 15, 15);

            g.setColor(Color.DARK_GRAY);
            g.drawString("Entradas", 40, 33);

            g.setColor(new Color(210, 60, 60));
            g.fillRect(120, 20, 15, 15);

            g.setColor(Color.DARK_GRAY);
            g.drawString("Saídas", 140, 33);

            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));

            g.drawLine(
                    margemEsquerda,
                    margemSuperior,
                    margemEsquerda,
                    margemSuperior + areaAltura
            );

            g.drawLine(
                    margemEsquerda,
                    margemSuperior + areaAltura,
                    largura - margemDireita,
                    margemSuperior + areaAltura
            );
        }

        private double obterMaiorValor() {
            double maiorValor = 1;

            for (ResumoPeriodo item : dados) {
                maiorValor = Math.max(
                        maiorValor,
                        Math.max(item.entradas, item.saidas)
                );
            }

            return maiorValor;
        }

        private int calcularAltura(
                double valor,
                double maiorValor,
                int areaAltura
        ) {
            return (int) (
                    valor / maiorValor * areaAltura
            );
        }

        private String formatarValorAbreviado(double valor) {
            if (valor >= 1000) {
                return String.format(
                        "R$ %.1fk",
                        valor / 1000
                ).replace(".", ",");
            }

            return String.format(
                    "R$ %.0f",
                    valor
            );
        }
    }

    private static class ResumoPeriodo {

        private String periodo;
        private double entradas;
        private double saidas;

        public ResumoPeriodo(
                String periodo,
                double entradas,
                double saidas
        ) {
            this.periodo = periodo;
            this.entradas = entradas;
            this.saidas = saidas;
        }
    }
}