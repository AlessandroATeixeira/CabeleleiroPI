package Pck_View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Tela_Caixa extends JFrame {

    private static final Color COR_PRIMARIA = new Color(86, 28, 36);
    private static final Color COR_PRIMARIA_2 = new Color(109, 41, 50);
    private static final Color COR_SECUNDARIA = new Color(199, 183, 163);
    private static final Color COR_FUNDO = new Color(244, 239, 234);
    private static final Color COR_PANEL = new Color(247, 244, 241);
    private static final Color COR_TEXT = new Color(34, 34, 34);

    private JComboBox<String> comboTipo;
    private JComboBox<String> cbServProd;
    private JTextField txtValor;
    private JTextField txtDescricao;

    private JLabel lblSaldo;

    private JTable tabela;
    private DefaultTableModel modelo;

    private double saldo = 0.0;

    public Tela_Caixa() {
        setTitle("Caixa");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        criarComponentes();
        setVisible(true);
    }

    private void criarComponentes() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(COR_FUNDO);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Controle de Caixa");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        titulo.setForeground(COR_TEXT);

        JButton btnNovo = new JButton("Nova movimentação");
        btnNovo.setBackground(COR_PRIMARIA_2);
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btnNovo.setFont(new Font("SansSerif", Font.BOLD, 15));

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setOpaque(false);
        headerRight.add(btnNovo);

        cabecalho.add(titulo, BorderLayout.WEST);
        cabecalho.add(headerRight, BorderLayout.EAST);

        JPanel card = new RoundedPanel(COR_PANEL, 20);
        card.setLayout(new BorderLayout(18, 18));
        card.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel formulario = new JPanel(new GridLayout(4, 2, 18, 18));
        formulario.setOpaque(false);

        comboTipo = new JComboBox<>(new String[]{"ENTRADA", "SAÍDA"});
        cbServProd = new JComboBox<>(new String[]{"Produto", "Serviço"});
        txtValor = new JTextField();
        txtDescricao = new JTextField();

        JLabel lblTipo = new JLabel("Tipo:");
        lblTipo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTipo.setForeground(COR_TEXT);

        JLabel lblProduto = new JLabel("Produto:");
        lblProduto.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblProduto.setForeground(COR_TEXT);

        JLabel lblDescricao = new JLabel("Descrição:");
        lblDescricao.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblDescricao.setForeground(COR_TEXT);

        JLabel lblValor = new JLabel("Valor:");
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblValor.setForeground(COR_TEXT);

        formulario.add(lblTipo);
        formulario.add(comboTipo);
        formulario.add(lblProduto);
        formulario.add(cbServProd);
        formulario.add(lblDescricao);
        formulario.add(txtDescricao);
        formulario.add(lblValor);
        formulario.add(txtValor);

        JButton btnRegistrar = new JButton("Registrar");
        btnRegistrar.setBackground(COR_PRIMARIA_2);
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btnRegistrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegistrar.addActionListener(e -> registrarMovimentacao());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botoes.setOpaque(false);
        botoes.add(btnRegistrar);

        modelo = new DefaultTableModel(new Object[]{"Data/Hora", "Tipo", "Produto", "Descrição", "Valor"}, 0);

        tabela = new JTable(modelo);
        tabela.setRowHeight(34);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBackground(COR_PANEL);
        tabela.setForeground(COR_TEXT);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COR_PANEL);

        lblSaldo = new JLabel("Saldo atual: R$ 0,00");
        lblSaldo.setForeground(COR_TEXT);
        lblSaldo.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblSaldo.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(formulario, BorderLayout.NORTH);
        card.add(botoes, BorderLayout.SOUTH);
        card.add(scroll, BorderLayout.CENTER);
        card.add(lblSaldo, BorderLayout.SOUTH);

        root.add(cabecalho, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);

        add(root);
    }

    private void registrarMovimentacao() {
        try {
            double valor = Double.parseDouble(txtValor.getText().replace(".", "").replace(",", "."));

            if (valor <= 0) {
                JOptionPane.showMessageDialog(this, "O valor deve ser maior que zero.");
                return;
            }

            String tipo = comboTipo.getSelectedItem().toString();
            String descricao = txtDescricao.getText().trim();

            if (descricao.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Informe uma descrição.");
                return;
            }

            if ("SAÍDA".equals(tipo) && valor > saldo) {
                JOptionPane.showMessageDialog(this, "A saída não pode ser maior que o saldo disponível.");
                return;
            }

            if ("ENTRADA".equals(tipo)) {
                saldo += valor;
            } else {
                saldo -= valor;
            }

            modelo.addRow(new Object[] {
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    tipo,
                    cbServProd.getSelectedItem(),
                    descricao,
                    String.format("R$ %.2f", valor).replace(".", ",")
            });

            txtValor.setText("");
            txtDescricao.setText("");

            lblSaldo.setText("Saldo atual: " + String.format("R$ %.2f", saldo).replace(".", ","));

            JOptionPane.showMessageDialog(this, "Movimentação registrada com sucesso.");
        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(this, "Informe um valor válido.");
        }
    }

    static class RoundedPanel extends JPanel {
        private final Color color;
        private final int radius;

        RoundedPanel(Color color, int radius) {
            this.color = color;
            this.radius = radius;
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