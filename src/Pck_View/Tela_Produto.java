package Pck_View;

import Pck_Control.Control_Product;
import Pck_Model.Model_Product;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Tela_Produto extends JFrame {

    private static final Color COR_PRIMARIA = new Color(86, 28, 36);
    private static final Color COR_PRIMARIA_2 = new Color(109, 41, 50);
    private static final Color COR_SECUNDARIA = new Color(199, 183, 163);
    private static final Color COR_FUNDO = new Color(244, 239, 234);
    private static final Color COR_PANEL = new Color(247, 244, 241);
    private static final Color COR_TEXT = new Color(34, 34, 34);

    private JLabel lblCodigo;
    private JLabel lblNome;
    private JLabel lblValorProd;
    private JLabel lblEstoque;

    private JTextField txtCodigo;
    private JTextField txtNome;
    private JTextField txtValorProd;
    private JTextField txtEstoque;

    private JButton btnInserir;
    private JButton btnConsultar;
    private JButton btnAlterar;
    private JButton btnRemover;
    private JButton btnLimpar;

    private JTable tabela;
    private DefaultTableModel modelo;

    private final Control_Product control = new Control_Product();

    public Tela_Produto() {
        setTitle("Produtos");
        setSize(900, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        criarComponentes();
        criarEventos();
        carregarTabela();

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(COR_FUNDO);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Cadastro de Produtos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        titulo.setForeground(COR_TEXT);

        JButton btnNovo = new JButton("Novo produto");
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

        JPanel form = new JPanel(new java.awt.GridLayout(2, 4, 18, 18));
        form.setOpaque(false);

        lblCodigo = criarLabel("Código:");
        lblNome = criarLabel("Produto:");
        lblValorProd = criarLabel("Valor:");
        lblEstoque = criarLabel("Estoque:");

        txtCodigo = criarCampo();
        txtNome = criarCampo();
        txtValorProd = criarCampo();
        txtEstoque = criarCampo();

        form.add(lblCodigo);
        form.add(txtCodigo);
        form.add(lblNome);
        form.add(txtNome);
        form.add(lblValorProd);
        form.add(txtValorProd);
        form.add(lblEstoque);
        form.add(txtEstoque);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botoes.setOpaque(false);

        btnInserir = criarBotao("Inserir", COR_PRIMARIA_2, Color.WHITE);
        btnAlterar = criarBotao("Alterar", COR_SECUNDARIA, COR_TEXT);
        btnRemover = criarBotao("Excluir", new Color(214, 72, 100), Color.WHITE);
        btnLimpar = criarBotao("Limpar", new Color(220, 220, 220), COR_TEXT);

        botoes.add(btnInserir);
        botoes.add(btnAlterar);
        botoes.add(btnRemover);
        botoes.add(btnLimpar);

        JPanel tabelaCard = new JPanel(new BorderLayout());
        tabelaCard.setOpaque(false);

        modelo = new DefaultTableModel(
                new Object[]{"Código", "Produto", "Valor", "Estoque"}, 0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(34);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBackground(COR_PANEL);
        tabela.setForeground(COR_TEXT);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        tabela.getTableHeader().setForeground(COR_TEXT);

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COR_PANEL);

        tabelaCard.add(scroll, BorderLayout.CENTER);

        card.add(form, BorderLayout.NORTH);
        card.add(botoes, BorderLayout.SOUTH);
        card.add(tabelaCard, BorderLayout.CENTER);

        root.add(cabecalho, BorderLayout.NORTH);
        root.add(card, BorderLayout.CENTER);

        add(root);
    }

    private void criarEventos() {
        btnInserir.addActionListener(e -> inserirProduto());
        btnAlterar.addActionListener(e -> alterarProduto());
        btnRemover.addActionListener(e -> excluirProduto());
        btnLimpar.addActionListener(e -> limparCampos());
        btnConsultar.addActionListener(e -> carregarTabela());
        tabela.getSelectionModel().addListSelectionListener(e -> selecionarLinha());
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtValorProd.setText("");
        txtEstoque.setText("");
        tabela.clearSelection();
    }

    private void selecionarLinha() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            txtCodigo.setText(modelo.getValueAt(linha, 0).toString());
            txtNome.setText(modelo.getValueAt(linha, 1).toString());
            txtValorProd.setText(modelo.getValueAt(linha, 2).toString());
            txtEstoque.setText(modelo.getValueAt(linha, 3).toString());
        }
    }

    private void inserirProduto() {
        try {
            control.Inserir(txtCodigo.getText(), txtNome.getText(), txtValorProd.getText(), txtEstoque.getText());
            JOptionPane.showMessageDialog(this, "Produto salvo com sucesso");
            carregarTabela();
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void alterarProduto() {
        try {
            control.Atualizar(txtCodigo.getText(), txtNome.getText(), txtValorProd.getText(), txtEstoque.getText());
            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso");
            carregarTabela();
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void excluirProduto() {
        try {
            control.Excluir(txtCodigo.getText());
            JOptionPane.showMessageDialog(this, "Produto excluído com sucesso");
            carregarTabela();
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void carregarTabela() {
        try {
            modelo.setRowCount(0);
            ArrayList<Model_Product> lista = control.Consultar();
            for (Model_Product c : lista) {
                modelo.addRow(new Object[]{c.getCodigo(), c.getNome(), c.getValorProd(), c.getEstoque()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(COR_TEXT);
        return label;
    }

    private JTextField criarCampo() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(220, 35));
        return field;
    }

    private JButton criarBotao(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return btn;
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