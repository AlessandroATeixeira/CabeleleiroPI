package Pck_View;

import Pck_DAO.Conexao;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import Pck_Model.Model_Product;
import Pck_Control.Control_Product;


public class Tela_Produto extends JFrame {

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
    private JScrollPane barraRolagem;

    Control_Product control = new Control_Product();

    public Tela_Produto() {

        setTitle("Cadastro de Produtos");
        setSize(650, 530);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        criarComponentes();
        criarEventos();

        setVisible(true);
        carregarTabela();
    }

    private void criarComponentes() {

        JLabel titulo = new JLabel(
                "Tela de Cadastro de Produtos",
                SwingConstants.CENTER
        );
        titulo.setBounds(30, 5, 570, 30);
        add(titulo);

        // Código
        lblCodigo = new JLabel("Código:");
        lblCodigo.setBounds(30, 45, 80, 25);
        add(lblCodigo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(110, 45, 150, 25);
        add(txtCodigo);

        // Nome
        lblNome = new JLabel("Produto:");
        lblNome.setBounds(30, 80, 80, 25);
        add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(110, 80, 300, 25);
        add(txtNome);

        // Valor
        lblValorProd = new JLabel("Valor:");
        lblValorProd.setBounds(30, 115, 80, 25);
        add(lblValorProd);

        txtValorProd = new JTextField();
        txtValorProd.setBounds(110, 115, 150, 25);
        add(txtValorProd);

        // Estoque
        lblEstoque = new JLabel("Estoque:");
        lblEstoque.setBounds(300, 115, 80, 25);
        add(lblEstoque);

        txtEstoque = new JTextField();
        txtEstoque.setBounds(370, 115, 100, 25);
        add(txtEstoque);

        // Botões
        btnInserir = new JButton("Inserir");
        btnInserir.setBounds(30, 160, 105, 30);
        add(btnInserir);

        btnConsultar = new JButton("Consultar");
        btnConsultar.setBounds(145, 160, 105, 30);
        add(btnConsultar);

        btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(260, 160, 105, 30);
        add(btnAlterar);

        btnRemover = new JButton("Excluir");
        btnRemover.setBounds(375, 160, 105, 30);
        add(btnRemover);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(490, 160, 105, 30);
        add(btnLimpar);

        // Tabela
        modelo = new DefaultTableModel(
                new Object[] {
                        "Código", "Nome", "Valor", "Estoque"
                },
                0
        ) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        tabela.setSelectionMode(
                javax.swing.ListSelectionModel.SINGLE_SELECTION
        );

        barraRolagem = new JScrollPane(tabela);
        barraRolagem.setBounds(30, 215, 565, 240);
        add(barraRolagem);
    }

    private void criarEventos() {

        btnAlterar.addActionListener(e -> alterarProduto());
        btnInserir.addActionListener(e -> inserirProduto());
        btnRemover.addActionListener(e -> excluirProduto());
        tabela.getSelectionModel().addListSelectionListener(e -> selecionarLinha());

    }

    private void selecionarLinha(){
        int linha=tabela.getSelectedRow();
        if(linha>=0){
            txtCodigo.setText(modelo.getValueAt(linha,0).toString());
            txtNome.setText(modelo.getValueAt(linha,1).toString());
            txtValorProd.setText(modelo.getValueAt(linha,2).toString());
            txtEstoque.setText(modelo.getValueAt(linha, 3).toString());
        }
    }


    private void inserirProduto() {
        try {
            control.Inserir(txtCodigo.getText(), txtNome.getText(), txtValorProd.getText(), txtEstoque.getText());
            JOptionPane.showMessageDialog(this,"Produto salvo com sucesso");
            carregarTabela();
        }catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void alterarProduto() {
        try {
            control.Atualizar(txtCodigo.getText(), txtNome.getText(), txtValorProd.getText(), txtEstoque.getText());
            JOptionPane.showMessageDialog(this,"Produto salvo com sucesso");
            carregarTabela();
        }catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void excluirProduto() {
        try {
            control.Excluir(txtCodigo.getText());
            JOptionPane.showMessageDialog(this,"Produto Excluido com sucesso");
            carregarTabela();
        }catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void carregarTabela(){
        try{
            modelo.setRowCount(0);
            ArrayList<Model_Product> lista=control.Consultar();
            for(Model_Product c:lista){
                modelo.addRow(new Object[]{c.getCodigo(),c.getNome(), c.getValorProd(), c.getEstoque()});
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }



}

