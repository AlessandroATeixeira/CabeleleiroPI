package Pck_View;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Pck_Control.Control_Product;
import Pck_Model.Model_Product;

public class Tela_Produto extends Tela_Base {

    private Tema.Campo txtCodigo;
    private Tema.Campo txtNome;
    private Tema.Campo txtValorProd;
    private Tema.Campo txtEstoque;

    private Tema.Botao btnInserir;
    private Tema.Botao btnAlterar;
    private Tema.Botao btnRemover;
    private Tema.Botao btnLimpar;
    private Tema.Botao btnConsultar;

    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel lblTotal;

    private final Control_Product control = new Control_Product();

    public Tela_Produto() {
        super("Produtos", "Controle de produtos, valores e estoque.");
        criarComponentes();
        criarEventos();
    }

    private void criarComponentes() {
        // ---- Formulario ----
        txtCodigo = new Tema.Campo("Ex.: 1");
        txtNome = new Tema.Campo("Nome do produto");
        txtValorProd = new Tema.Campo("0,00");
        txtEstoque = new Tema.Campo("0");

        btnInserir = new Tema.Botao("Inserir", Tema.TipoBotao.PRIMARIO);
        btnAlterar = new Tema.Botao("Alterar", Tema.TipoBotao.SECUNDARIO);
        btnRemover = new Tema.Botao("Excluir", Tema.TipoBotao.PERIGO);
        btnConsultar = new Tema.Botao("Consultar", Tema.TipoBotao.SECUNDARIO);
        btnLimpar = new Tema.Botao("Limpar", Tema.TipoBotao.NEUTRO);

        Tema.Cartao formulario = cartaoComTitulo("Dados do produto", null);
        JPanel corpo = new JPanel(new BorderLayout(0, 18));
        corpo.setOpaque(false);
        corpo.add(linhaCampos(new double[]{1, 3, 1.4, 1.2},
                Tema.campoComRotulo("Código", txtCodigo),
                Tema.campoComRotulo("Produto", txtNome),
                Tema.campoComRotulo("Valor (R$)", txtValorProd),
                Tema.campoComRotulo("Estoque", txtEstoque)), BorderLayout.NORTH);
        corpo.add(linhaBotoes(btnInserir, btnAlterar, btnRemover, btnConsultar, btnLimpar), BorderLayout.SOUTH);
        formulario.add(corpo, BorderLayout.CENTER);

        // ---- Tabela ----
        modelo = new DefaultTableModel(new Object[]{"Código", "Produto", "Valor", "Estoque"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        lblTotal = etiquetaInfo("0 produtos");

        Tema.Cartao cartaoTabela = cartaoComTitulo("Produtos cadastrados", lblTotal);
        cartaoTabela.add(Tema.tabela(tabela), BorderLayout.CENTER);
        tabela.getColumnModel().getColumn(2).setCellRenderer(Tema.rendererMoeda());

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setOpaque(false);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(cartaoTabela, BorderLayout.CENTER);
        setConteudo(conteudo);
    }

    private void criarEventos() {
        btnInserir.addActionListener(e -> inserirProduto());
        btnAlterar.addActionListener(e -> alterarProduto());
        btnRemover.addActionListener(e -> excluirProduto());
        btnConsultar.addActionListener(e -> carregarTabela());
        btnLimpar.addActionListener(e -> limparCampos());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                selecionarLinha();
            }
        });
    }

    @Override
    public void aoExibir() {
        carregarTabela();
    }

    /** Aceita "35,50" ou "35.50". */
    private String valorNumerico() {
        return txtValorProd.getText().trim().replace(',', '.');
    }

    private void selecionarLinha() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            txtCodigo.setText(String.valueOf(modelo.getValueAt(linha, 0)));
            txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
            double valor = ((Number) modelo.getValueAt(linha, 2)).doubleValue();
            txtValorProd.setText(String.format("%.2f", valor).replace('.', ','));
            txtEstoque.setText(String.valueOf(modelo.getValueAt(linha, 3)));
        }
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtValorProd.setText("");
        txtEstoque.setText("");
        tabela.clearSelection();
        txtCodigo.requestFocusInWindow();
    }

    private void inserirProduto() {
        try {
            control.Inserir(txtCodigo.getText().trim(), txtNome.getText(), valorNumerico(), txtEstoque.getText().trim());
            mostrarInfo("Produto salvo com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void alterarProduto() {
        try {
            control.Atualizar(txtCodigo.getText().trim(), txtNome.getText(), valorNumerico(), txtEstoque.getText().trim());
            mostrarInfo("Produto atualizado com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void excluirProduto() {
        try {
            if (txtCodigo.getText().trim().isEmpty()) {
                mostrarInfo("Selecione um produto na tabela ou informe o código.");
                return;
            }
            if (!confirmar("Deseja realmente excluir o produto " + txtCodigo.getText().trim() + "?")) {
                return;
            }
            control.Excluir(txtCodigo.getText().trim());
            mostrarInfo("Produto excluído com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void carregarTabela() {
        try {
            ArrayList<Model_Product> lista = control.Consultar();
            modelo.setRowCount(0);
            for (Model_Product p : lista) {
                modelo.addRow(new Object[]{p.getCodigo(), p.getNome(), p.getValorProd(), p.getEstoque()});
            }
            lblTotal.setText(lista.size() + (lista.size() == 1 ? " produto" : " produtos"));
        } catch (Exception e) {
            mostrarErro(e);
        }
    }
}
