package Pck_View;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Pck_Control.Control_Client;
import Pck_Model.Model_Client;

public class Tela_Cliente extends Tela_Base {

    private Tema.Campo txtCodigo;
    private Tema.Campo txtNome;
    private Tema.Campo txtTelefone;

    private Tema.Botao btnInserir;
    private Tema.Botao btnAlterar;
    private Tema.Botao btnRemover;
    private Tema.Botao btnLimpar;

    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel lblTotal;

    private final Control_Client control = new Control_Client();

    public Tela_Cliente() {
        super("Clientes", "Cadastro, consulta e manutenção dos clientes do salão.");
        criarComponentes();
        criarEventos();
    }

    private void criarComponentes() {
        // ---- Formulario ----
        txtCodigo = new Tema.Campo("Ex.: 1");
        txtNome = new Tema.Campo("Nome completo");
        txtTelefone = new Tema.Campo("(11) 90000-0000");

        btnInserir = new Tema.Botao("Inserir", Tema.TipoBotao.PRIMARIO);
        btnAlterar = new Tema.Botao("Alterar", Tema.TipoBotao.SECUNDARIO);
        btnRemover = new Tema.Botao("Excluir", Tema.TipoBotao.PERIGO);
        btnLimpar = new Tema.Botao("Limpar", Tema.TipoBotao.NEUTRO);

        Tema.Cartao formulario = cartaoComTitulo("Dados do cliente", null);
        JPanel corpo = new JPanel(new BorderLayout(0, 18));
        corpo.setOpaque(false);
        corpo.add(linhaCampos(new double[]{1, 3, 2},
                Tema.campoComRotulo("Código", txtCodigo),
                Tema.campoComRotulo("Nome", txtNome),
                Tema.campoComRotulo("Telefone", txtTelefone)), BorderLayout.NORTH);
        corpo.add(linhaBotoes(btnInserir, btnAlterar, btnRemover, btnLimpar), BorderLayout.SOUTH);
        formulario.add(corpo, BorderLayout.CENTER);

        // ---- Tabela ----
        modelo = new DefaultTableModel(new Object[]{"Código", "Nome", "Telefone"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        lblTotal = etiquetaInfo("0 clientes");

        Tema.Cartao cartaoTabela = cartaoComTitulo("Clientes cadastrados", lblTotal);
        cartaoTabela.add(Tema.tabela(tabela), BorderLayout.CENTER);

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setOpaque(false);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(cartaoTabela, BorderLayout.CENTER);
        setConteudo(conteudo);
    }

    private void criarEventos() {
        btnInserir.addActionListener(e -> inserirCliente());
        btnAlterar.addActionListener(e -> alterarCliente());
        btnRemover.addActionListener(e -> excluirCliente());
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

    private void selecionarLinha() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            txtCodigo.setText(String.valueOf(modelo.getValueAt(linha, 0)));
            txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
            txtTelefone.setText(String.valueOf(modelo.getValueAt(linha, 2)));
        }
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtTelefone.setText("");
        tabela.clearSelection();
        txtCodigo.requestFocusInWindow();
    }

    private void inserirCliente() {
        try {
            control.Inserir(txtCodigo.getText(), txtNome.getText(), txtTelefone.getText());
            mostrarInfo("Cliente salvo com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void alterarCliente() {
        try {
            control.Atualizar(txtCodigo.getText(), txtNome.getText(), txtTelefone.getText());
            mostrarInfo("Cliente atualizado com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void excluirCliente() {
        try {
            if (txtCodigo.getText().trim().isEmpty()) {
                mostrarInfo("Selecione um cliente na tabela ou informe o código.");
                return;
            }
            if (!confirmar("Deseja realmente excluir o cliente " + txtCodigo.getText().trim() + "?")) {
                return;
            }
            control.Excluir(txtCodigo.getText());
            mostrarInfo("Cliente excluído com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    private void carregarTabela() {
        try {
            ArrayList<Model_Client> lista = control.Consultar();
            modelo.setRowCount(0);
            for (Model_Client c : lista) {
                modelo.addRow(new Object[]{c.getCodigo(), c.getNome(), c.getTelefone()});
            }
            lblTotal.setText(lista.size() + (lista.size() == 1 ? " cliente" : " clientes"));
        } catch (Exception e) {
            mostrarErro(e);
        }
    }
}
