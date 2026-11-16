package Pck_View;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Pck_Control.Control_Service;
import Pck_Model.Model_Service;

public class Tela_Servico extends Tela_Base {
    private Tema.Campo txtCodigo;
    private Tema.Campo txtNome;
    private Tema.Campo txtValor;
    private Tema.Botao btnInserir;
    private Tema.Botao btnAlterar;
    private Tema.Botao btnExcluir;
    private Tema.Botao btnConsultar;
    private Tema.Botao btnLimpar;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel lblTotal;
    private final Control_Service control = new Control_Service();

    public Tela_Servico() {
        super("Serviços", "Cadastre e gerencie os serviços oferecidos pelo salão.");
        criarComponentes();
        criarEventos();
    }

    private void criarComponentes() {
        txtCodigo = new Tema.Campo("Gerado automaticamente");
        txtCodigo.setEditable(false);
        txtNome = new Tema.Campo("Ex.: Corte feminino");
        txtValor = new Tema.Campo("Ex.: 50,00");

        btnInserir = new Tema.Botao("Inserir", Tema.TipoBotao.PRIMARIO);
        btnAlterar = new Tema.Botao("Alterar", Tema.TipoBotao.SECUNDARIO);
        btnExcluir = new Tema.Botao("Excluir", Tema.TipoBotao.PERIGO);
        btnConsultar = new Tema.Botao("Consultar", Tema.TipoBotao.SECUNDARIO);
        btnLimpar = new Tema.Botao("Limpar", Tema.TipoBotao.NEUTRO);

        Tema.Cartao formulario = cartaoComTitulo("Dados do serviço", null);
        JPanel corpo = new JPanel(new BorderLayout(0, 18));
        corpo.setOpaque(false);
        corpo.add(linhaCampos(new double[]{1, 3, 1},
                Tema.campoComRotulo("Código", txtCodigo),
                Tema.campoComRotulo("Nome do serviço", txtNome),
                Tema.campoComRotulo("Valor (R$)", txtValor)), BorderLayout.NORTH);
        corpo.add(linhaBotoes(btnInserir, btnAlterar, btnExcluir, btnConsultar, btnLimpar), BorderLayout.SOUTH);
        formulario.add(corpo, BorderLayout.CENTER);

        modelo = new DefaultTableModel(new Object[]{"Código", "Serviço", "Valor (R$)"}, 0) {
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        tabela = new JTable(modelo);
        lblTotal = etiquetaInfo("0 serviços");
        Tema.Cartao cartaoTabela = cartaoComTitulo("Serviços cadastrados", lblTotal);
        cartaoTabela.add(Tema.tabela(tabela), BorderLayout.CENTER);

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setOpaque(false);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(cartaoTabela, BorderLayout.CENTER);
        setConteudo(conteudo);
    }

    private void criarEventos() {
        btnInserir.addActionListener(e -> inserir());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnConsultar.addActionListener(e -> carregarTabela());
        btnLimpar.addActionListener(e -> limparCampos());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionarLinha();
        });
    }

    @Override
    public void aoExibir() { carregarTabela(); }

    private void selecionarLinha() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            txtCodigo.setText(String.valueOf(modelo.getValueAt(linha, 0)));
            txtNome.setText(String.valueOf(modelo.getValueAt(linha, 1)));
            txtValor.setText(String.valueOf(modelo.getValueAt(linha, 2)).replace("R$", "").trim());
        }
    }

    private void inserir() {
        try {
            control.inserir(txtNome.getText(), txtValor.getText());
            mostrarInfo("Serviço cadastrado com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) { mostrarErro(e); }
    }

    private void alterar() {
        try {
            if (txtCodigo.getText().trim().isEmpty()) {
                mostrarInfo("Selecione um serviço na tabela para alterar.");
                return;
            }
            control.atualizar(txtCodigo.getText(), txtNome.getText(), txtValor.getText());
            mostrarInfo("Serviço atualizado com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) { mostrarErro(e); }
    }

    private void excluir() {
        try {
            if (txtCodigo.getText().trim().isEmpty()) {
                mostrarInfo("Selecione um serviço na tabela para excluir.");
                return;
            }
            if (!confirmar("Deseja realmente excluir o serviço selecionado?")) return;
            control.excluir(txtCodigo.getText());
            mostrarInfo("Serviço excluído com sucesso.");
            limparCampos();
            carregarTabela();
        } catch (Exception e) { mostrarErro(e); }
    }

    private void carregarTabela() {
        try {
            ArrayList<Model_Service> lista = control.consultar();
            modelo.setRowCount(0);
            for (Model_Service servico : lista) {
                modelo.addRow(new Object[]{servico.getIdServico(), servico.getNomeServico(),
                        String.format("%.2f", servico.getValorServico())});
            }
            lblTotal.setText(lista.size() + (lista.size() == 1 ? " serviço" : " serviços"));
        } catch (Exception e) { mostrarErro(e); }
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtNome.setText("");
        txtValor.setText("");
        tabela.clearSelection();
        txtNome.requestFocusInWindow();
    }
}
