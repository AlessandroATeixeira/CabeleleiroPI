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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import Pck_Model.Model_Client;
import Pck_Control.Control_Client;
import Pck_Model.Model_Scheduling;

public class Tela_Cliente extends JFrame {

    private JLabel lblTitulo;
    private JLabel lblCodigo;
    private JLabel lblNome;
    private JLabel lblTelefone;

    private JTextField txtCodigo;
    private JTextField txtNome;
    private JTextField txtTelefone;

    private JButton btnInserir;
    private JButton btnAlterar;
    private JButton btnRemover;
    private JButton btnLimpar;

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane barraRolagem;

    Control_Client control = new Control_Client();

    public Tela_Cliente() {

        setTitle("Cadastro de Clientes");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        criarComponentes();
        criarEventos();

        setVisible(true);
    }

    // CRIAR COMPONENTES
    private void criarComponentes() {

        lblTitulo = new JLabel(
                "Cadastro de Clientes",
                SwingConstants.CENTER
        );
        lblTitulo.setBounds(30, 5, 620, 30);
        add(lblTitulo);

        lblCodigo = new JLabel("Código:");
        lblCodigo.setBounds(30, 45, 80, 25);
        add(lblCodigo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(110, 45, 150, 25);
        add(txtCodigo);

        lblNome = new JLabel("Nome:");
        lblNome.setBounds(30, 80, 80, 25);
        add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(110, 80, 300, 25);
        add(txtNome);

        lblTelefone = new JLabel("Telefone:");
        lblTelefone.setBounds(30, 115, 80, 25);
        add(lblTelefone);

        txtTelefone = new JTextField();
        txtTelefone.setBounds(110, 115, 200, 25);
        add(txtTelefone);

        btnInserir = new JButton("Inserir");
        btnInserir.setBounds(30, 160, 110, 30);
        add(btnInserir);



        btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(270, 160, 110, 30);
        add(btnAlterar);

        btnRemover = new JButton("Excluir");
        btnRemover.setBounds(390, 160, 110, 30);
        add(btnRemover);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(510, 160, 110, 30);
        add(btnLimpar);

        modelo = new DefaultTableModel(
                new Object[] {
                        "Código", "Nome", "Telefone"
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
                ListSelectionModel.SINGLE_SELECTION
        );

        barraRolagem = new JScrollPane(tabela);
        barraRolagem.setBounds(30, 215, 620, 250);
        add(barraRolagem);
    }


    private void criarEventos() {

        btnAlterar.addActionListener(e -> alterarCliente());
        btnInserir.addActionListener(e -> inserirCliente());
        btnRemover.addActionListener(e -> excluirCliente());
        tabela.getSelectionModel().addListSelectionListener(e -> selecionarLinha());

    }

    private void selecionarLinha(){
        int linha=tabela.getSelectedRow();
        if(linha>=0){
            txtCodigo.setText(modelo.getValueAt(linha,0).toString());
            txtNome.setText(modelo.getValueAt(linha,1).toString());
            txtTelefone.setText(modelo.getValueAt(linha,2).toString());
        }
    }


    private void inserirCliente() {
        try {
            control.Inserir(txtCodigo.getText(), txtNome.getText(), txtTelefone.getText());
            JOptionPane.showMessageDialog(this,"Cliente salvo com sucesso");
            carregarTabela();
        }catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }


    private void alterarCliente() {
        try {
            control.Atualizar(txtCodigo.getText(), txtNome.getText(), txtTelefone.getText());
            JOptionPane.showMessageDialog(this,"Cliente Atualizado com sucesso");
            carregarTabela();
        }catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }
    }

    private void excluirCliente() {
        try {
            control.Excluir(txtCodigo.getText());
            JOptionPane.showMessageDialog(this,"Cliente Excluido com sucesso");
            carregarTabela();
        }catch(Exception e) {
            JOptionPane.showMessageDialog(this,e.getMessage());
        }

    }

    private void carregarTabela(){
        try{
            modelo.setRowCount(0);
            ArrayList<Model_Client> lista=control.Consultar();
            for(Model_Client c:lista){
                modelo.addRow(new Object[]{c.getCodigo(),c.getNome(), c.getTelefone()});
            }
        }catch(Exception e){
            JOptionPane.showMessageDialog(this,e.getMessage());
        }


    }

}
