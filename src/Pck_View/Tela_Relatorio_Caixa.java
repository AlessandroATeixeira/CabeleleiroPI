package Pck_View;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Tela_Relatorio_Caixa extends JFrame {

    private static final Color COR_PRIMARIA = new Color(86, 28, 36);
    private static final Color COR_PRIMARIA_2 = new Color(109, 41, 50);
    private static final Color COR_SECUNDARIA = new Color(199, 183, 163);
    private static final Color COR_FUNDO = new Color(244, 239, 234);
    private static final Color COR_PANEL = new Color(247, 244, 241);
    private static final Color COR_TEXT = new Color(34, 34, 34);

    private JComboBox<String> comboPeriodo;
    private JLabel lblTotalEntradas;
    private JLabel lblTotalSaidas;
    private JLabel lblSaldo;

    private DefaultTableModel modelo;
    private ChartPanel grafico;

    public Tela_Relatorio_Caixa() {
        setTitle("Relatórios");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        criarComponentes();
        carregarDados();

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(COR_FUNDO);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("Relatório de Caixa");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        titulo.setForeground(COR_TEXT);

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        filtro.setOpaque(false);

        JLabel lblPeriodo = new JLabel("Período:");
        lblPeriodo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblPeriodo.setForeground(COR_TEXT);

        comboPeriodo = new JComboBox<>(new String[]{"Mensal", "Semestral", "Anual"});
        comboPeriodo.setPreferredSize(new Dimension(150, 36));

        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBackground(COR_PRIMARIA_2);
        btnAtualizar.setForeground(Color.WHITE);
        btnAtualizar.setFocusPainted(false);
        btnAtualizar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btnAtualizar.setFont(new Font("SansSerif", Font.BOLD, 14));

        filtro.add(lblPeriodo);
        filtro.add(comboPeriodo);
        filtro.add(btnAtualizar);

        header.add(titulo, BorderLayout.WEST);
        header.add(filtro, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);

        JPanel resumo = new JPanel(new BorderLayout());
        resumo.setOpaque(false);

        JPanel cards = new JPanel(new java.awt.GridLayout(1, 3, 20, 0));
        cards.setOpaque(false);

        lblTotalEntradas = criarResumo("Entradas", "R$ 0,00", new Color(40, 160, 70));
        lblTotalSaidas = criarResumo("Saídas", "R$ 0,00", new Color(180, 45, 45));
        lblSaldo = criarResumo("Saldo", "R$ 0,00", new Color(26, 77, 164));

        cards.add(lblTotalEntradas);
        cards.add(lblTotalSaidas);
        cards.add(lblSaldo);

        resumo.add(cards, BorderLayout.CENTER);

        root.add(resumo, BorderLayout.CENTER);

        JPanel corpo = new JPanel(new BorderLayout(18, 18));
        corpo.setOpaque(false);

        grafico = new ChartPanel();
        grafico.setPreferredSize(new Dimension(650, 260));

        modelo = new DefaultTableModel(new Object[]{"Período", "Entradas", "Saídas", "Saldo"}, 0);

        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(30);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBackground(COR_PANEL);
        tabela.setForeground(COR_TEXT);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);

        JPanel chartCard = new RoundedPanel(COR_PANEL, 18);
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        chartCard.add(grafico, BorderLayout.CENTER);

        JPanel tableCard = new RoundedPanel(COR_PANEL, 18);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        tableCard.add(scroll, BorderLayout.CENTER);

        corpo.add(chartCard, BorderLayout.CENTER);
        corpo.add(tableCard, BorderLayout.SOUTH);

        root.add(corpo, BorderLayout.CENTER);

        btnAtualizar.addActionListener(evento -> carregarDados());
        comboPeriodo.addActionListener(evento -> carregarDados());

        add(root);
    }

    private JLabel criarResumo(String titulo, String valor, Color cor) {
        JLabel label = new JLabel("<html><b>" + titulo + "</b><br>" + valor + "</html>");
        label.setForeground(cor);
        label.setBackground(COR_PANEL);
        label.setOpaque(true);
        label.setBorder(new EmptyBorder(12, 16, 12, 16));
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void carregarDados() {
        modelo.setRowCount(0);

        String periodoSelecionado = comboPeriodo.getSelectedItem().toString();
        List<ResumoPeriodo> dados = gerarDadosExemplo(periodoSelecionado);

        double totalEntradas = 0;
        double totalSaidas = 0;

        for (ResumoPeriodo item : dados) {
            double saldo = item.entradas - item.saidas;
            modelo.addRow(new Object[]{
                    item.periodo,
                    formatarValor(item.entradas),
                    formatarValor(item.saidas),
                    formatarValor(saldo)
            });
            totalEntradas += item.entradas;
            totalSaidas += item.saidas;
        }

        double saldoTotal = totalEntradas - totalSaidas;

        lblTotalEntradas.setText("<html><b>Entradas</b><br>" + formatarValor(totalEntradas) + "</html>");
        lblTotalSaidas.setText("<html><b>Saídas</b><br>" + formatarValor(totalSaidas) + "</html>");
        lblSaldo.setText("<html><b>Saldo</b><br>" + formatarValor(saldoTotal) + "</html>");

        grafico.setDados(dados);
    }

    private List<ResumoPeriodo> gerarDadosExemplo(String periodo) {
        List<ResumoPeriodo> dados = new ArrayList<>();

        if ("Mensal".equals(periodo)) {
            dados.add(new ResumoPeriodo("Janeiro", 8500, 4200));
            dados.add(new ResumoPeriodo("Fevereiro", 9200, 4600));
            dados.add(new ResumoPeriodo("Março", 7800, 3900));
            dados.add(new ResumoPeriodo("Abril", 10100, 5100));
            dados.add(new ResumoPeriodo("Maio", 11200, 5400));
            dados.add(new ResumoPeriodo("Junho", 9800, 4800));
        }

        if ("Semestral".equals(periodo)) {
            dados.add(new ResumoPeriodo("1º Semestre", 56800, 28000));
            dados.add(new ResumoPeriodo("2º Semestre", 62100, 30500));
        }

        if ("Anual".equals(periodo)) {
            dados.add(new ResumoPeriodo("2024", 118900, 58200));
            dados.add(new ResumoPeriodo("2025", 132500, 64100));
            dados.add(new ResumoPeriodo("2026", 145700, 69300));
        }

        return dados;
    }

    private String formatarValor(double valor) {
        return String.format("R$ %.2f", valor).replace(".", ",");
    }

    private class ChartPanel extends JPanel {
        private List<ResumoPeriodo> dados = new ArrayList<>();

        public void setDados(List<ResumoPeriodo> dados) {
            this.dados = dados;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int largura = getWidth();
            int altura = getHeight();
            int margemEsquerda = 70;
            int margemDireita = 30;
            int margemSuperior = 50;
            int margemInferior = 70;

            int areaLargura = largura - margemEsquerda - margemDireita;
            int areaAltura = altura - margemSuperior - margemInferior;

            g.setColor(COR_PANEL);
            g.fillRect(0, 0, largura, altura);

            g.setColor(COR_TEXT);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));

            String titulo = "Entradas e saídas por período";
            g.drawString(titulo, margemEsquerda, 25);

            if (dados == null || dados.isEmpty()) {
                g.setFont(new Font("SansSerif", Font.PLAIN, 14));
                g.drawString("Nenhum dado disponível.", margemEsquerda, margemSuperior + 30);
                return;
            }

            double maiorValor = obterMaiorValor();
            int quantidadePeriodos = dados.size();
            int larguraGrupo = areaLargura / quantidadePeriodos;
            int larguraBarra = Math.max(10, larguraGrupo / 4);

            for (int i = 0; i <= 5; i++) {
                int y = margemSuperior + areaAltura - (i * areaAltura / 5);

                g.setColor(new Color(220, 220, 220));
                g.drawLine(margemEsquerda, y, largura - margemDireita, y);

                double valorLinha = maiorValor * i / 5;
                g.setColor(COR_TEXT);
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));

                g.drawString(formatarValorAbreviado(valorLinha), 5, y + 5);
            }

            for (int i = 0; i < dados.size(); i++) {
                ResumoPeriodo item = dados.get(i);

                int xCentro = margemEsquerda + i * larguraGrupo + larguraGrupo / 2;
                int alturaEntrada = calcularAltura(item.entradas, maiorValor, areaAltura);
                int alturaSaida = calcularAltura(item.saidas, maiorValor, areaAltura);

                int xEntrada = xCentro - larguraBarra - 3;
                int xSaida = xCentro + 3;

                int yEntrada = margemSuperior + areaAltura - alturaEntrada;
                int ySaida = margemSuperior + areaAltura - alturaSaida;

                g.setColor(new Color(40, 160, 70));
                g.fillRect(xEntrada, yEntrada, larguraBarra, alturaEntrada);

                g.setColor(new Color(210, 60, 60));
                g.fillRect(xSaida, ySaida, larguraBarra, alturaSaida);

                g.setColor(COR_TEXT);
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));

                String periodo = item.periodo;
                int textoX = xCentro - (periodo.length() * 3);

                g.drawString(periodo, textoX, margemSuperior + areaAltura + 20);
            }

            g.setColor(new Color(40, 160, 70));
            g.fillRect(20, 20, 15, 15);
            g.setColor(COR_TEXT);
            g.drawString("Entradas", 40, 40);

            g.setColor(new Color(210, 60, 60));
            g.fillRect(120, 20, 15, 15);
            g.setColor(COR_TEXT);
            g.drawString("Saídas", 140, 40);

            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(2));
            g.drawLine(margemEsquerda, margemSuperior, margemEsquerda, margemSuperior + areaAltura);
            g.drawLine(margemEsquerda, margemSuperior + areaAltura, largura - margemDireita, margemSuperior + areaAltura);
        }

        private double obterMaiorValor() {
            double maiorValor = 1;

            for (ResumoPeriodo item : dados) {
                maiorValor = Math.max(maiorValor, Math.max(item.entradas, item.saidas));
            }

            return maiorValor;
        }

        private int calcularAltura(double valor, double maiorValor, int areaAltura) {
            return (int) (valor / maiorValor * areaAltura);
        }

        private String formatarValorAbreviado(double valor) {
            if (valor >= 1000) {
                return String.format("R$ %.1fk", valor / 1000).replace(".", ",");
            }
            return String.format("R$ %.0f", valor);
        }
    }

    private static class ResumoPeriodo {
        private String periodo;
        private double entradas;
        private double saidas;

        public ResumoPeriodo(String periodo, double entradas, double saidas) {
            this.periodo = periodo;
            this.entradas = entradas;
            this.saidas = saidas;
        }
    }

    static class RoundedPanel extends JPanel {
        private final Color color;
        private final int radius;

        RoundedPanel(Color color, int radius) {
            this.color = color;
            this.radius = color == null ? 0 : radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}