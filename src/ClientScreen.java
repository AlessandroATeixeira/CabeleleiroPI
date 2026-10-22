import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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

public class ClientScreen extends JFrame {

    private JLabel lblTitulo;
    private JLabel lblCodigo;
    private JLabel lblNome;
    private JLabel lblTelefone;

    private JTextField txtCodigo;
    private JTextField txtNome;
    private JTextField txtTelefone;

    private JButton btnInserir;
    private JButton btnConsultar;
    private JButton btnAlterar;
    private JButton btnRemover;
    private JButton btnLimpar;

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane barraRolagem;

    public ClientScreen() {

        setTitle("Cadastro de Clientes");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        criarComponentes();
        criarEventos();

        setVisible(true);

        consultarClientes();
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

        btnConsultar = new JButton("Consultar");
        btnConsultar.setBounds(150, 160, 110, 30);
        add(btnConsultar);

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

    // CRIAR EVENTOS
    private void criarEventos() {

        // INSERIR
        btnInserir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                inserirCliente();
            }
        });

        // CONSULTAR
        btnConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                consultarClientes();
            }
        });

        // ALTERAR
        btnAlterar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                alterarCliente();
            }
        });

        // EXCLUIR
        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                excluirCliente();
            }
        });

        // LIMPAR
        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                limparCampos();
            }
        });

        // SELECIONAR LINHA DA TABELA
        tabela.getSelectionModel().addListSelectionListener(evento -> {

            if (!evento.getValueIsAdjusting()
                    && tabela.getSelectedRow() != -1) {

                int linha = tabela.getSelectedRow();

                txtCodigo.setText(
                        modelo.getValueAt(linha, 0).toString()
                );

                txtNome.setText(
                        modelo.getValueAt(linha, 1).toString()
                );

                txtTelefone.setText(
                        modelo.getValueAt(linha, 2).toString()
                );
            }
        });
    }

    // INSERIR CLIENTE
    private void inserirCliente() {

        int codigo;

        try {
            codigo = Integer.parseInt(
                    txtCodigo.getText().trim()
            );

            if (codigo <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "O código deve ser maior que zero."
                );
                return;
            }

        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe um código inteiro válido."
            );
            return;
        }

        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o nome do cliente."
            );
            return;
        }

        if (!validarTelefone(telefone)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o telefone com DDD e 11 dígitos, por favor."
            );
            return;
        }

        try (
                Connection conexao = Conexao.conectar();
                CallableStatement comando =
                        conexao.prepareCall(
                                "{CALL proc_ins_cliente(?, ?, ?)}"
                        )
        ) {

            comando.setInt(1, codigo);
            comando.setString(2, nome);
            comando.setString(3, telefone);

            comando.execute();

            JOptionPane.showMessageDialog(
                    this,
                    "Cliente incluído na base de dados."
            );

            limparCampos();
            consultarClientes();

        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao cadastrar cliente: "
                            + erro.getMessage()
            );
        }
    }

    // CONSULTAR CLIENTES
    private void consultarClientes() {

        try (
                Connection conexao = Conexao.conectar();
                CallableStatement comando =
                        conexao.prepareCall(
                                "{CALL proc_selc_clientes()}"
                        );
                ResultSet resultado = comando.executeQuery()
        ) {

            modelo.setRowCount(0);

            while (resultado.next()) {

                int codigo = resultado.getInt("cod_cliente");
                String nome = resultado.getString("c_nome");
                String telefone = resultado.getString("c_telefone");

                modelo.addRow(new Object[] {
                        codigo, nome, telefone
                });
            }

        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao consultar clientes: "
                            + erro.getMessage()
            );
        }
    }

    // ALTERAR CLIENTE
    private void alterarCliente() {

        int codigo;

        try {
            codigo = Integer.parseInt(
                    txtCodigo.getText().trim()
            );

            if (codigo <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "O código deve ser maior que zero."
                );
                return;
            }

        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe um código inteiro válido."
            );
            return;
        }

        String nome = txtNome.getText().trim();
        String telefone = txtTelefone.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o nome do cliente."
            );
            return;
        }

        if (!validarTelefone(telefone)) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o telefone com DDD e 11 dígitos, por obséquio."
            );
            return;
        }

        try (
                Connection conexao = Conexao.conectar();
                CallableStatement comando =
                        conexao.prepareCall(
                                "{CALL proc_upd_cliente(?, ?, ?)}"
                        )
        ) {

            comando.setInt(1, codigo);
            comando.setString(2, nome);
            comando.setString(3, telefone);

            comando.execute();

            JOptionPane.showMessageDialog(
                    this,
                    "As informações foram atualizadas."
            );

            limparCampos();
            consultarClientes();

        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao alterar cliente: "
                            + erro.getMessage()
            );
        }
    }

    // EXCLUIR CLIENTE
    private void excluirCliente() {

        int codigo;

        try {
            codigo = Integer.parseInt(
                    txtCodigo.getText().trim()
            );

            if (codigo <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "O código deve ser maior que zero."
                );
                return;
            }

        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe um código inteiro válido."
            );
            return;
        }

        int resposta = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o cliente "
                        + codigo + "?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (resposta != JOptionPane.YES_OPTION) {
            return;
        }

        try (
                Connection conexao = Conexao.conectar();
                CallableStatement comando =
                        conexao.prepareCall(
                                "{CALL proc_del_cliente(?)}"
                        )
        ) {

            comando.setInt(1, codigo);
            comando.execute();

            JOptionPane.showMessageDialog(
                    this,
                    "As informações referenciadas foram apagadas."
            );

            limparCampos();
            consultarClientes();

        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao excluir cliente: "
                            + erro.getMessage()
            );
        }
    }

    // VALIDAR TELEFONE
    private boolean validarTelefone(String telefone) {

        String somenteNumeros = telefone.replaceAll("\\D", "");

        return somenteNumeros.matches("\\d{11}");
    }

    // LIMPAR CAMPOS
    private void limparCampos() {

        txtCodigo.setText("");
        txtNome.setText("");
        txtTelefone.setText("");

        tabela.clearSelection();
        txtCodigo.requestFocus();
    }
}
