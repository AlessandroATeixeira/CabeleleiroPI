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
import javax.swing.table.DefaultTableModel;

public class ClientScreen extends JFrame {

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

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane barraRolagem;

    public ClientScreen() {

        setTitle("Gerenciamento de Clientes");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        criarComponentes();
        criarEventos();

        setVisible(true);
    }

    private void criarComponentes() {

        lblCodigo = new JLabel("Código:");
        lblCodigo.setBounds(30, 30, 80, 25);
        add(lblCodigo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(110, 30, 300, 25);
        add(txtCodigo);

        lblNome = new JLabel("Nome:");
        lblNome.setBounds(30, 70, 80, 25);
        add(lblNome);

        txtNome = new JTextField();
        txtNome.setBounds(110, 70, 300, 25);
        add(txtNome);

        lblTelefone = new JLabel("Telefone:");
        lblTelefone.setBounds(30, 110, 80, 25);
        add(lblTelefone);

        txtTelefone = new JTextField();
        txtTelefone.setBounds(110, 110, 150, 25);
        add(txtTelefone);

        btnInserir = new JButton("Inserir");
        btnInserir.setBounds(30, 155, 120, 30);
        add(btnInserir);

        btnConsultar = new JButton("Consultar");
        btnConsultar.setBounds(160, 155, 120, 30);
        add(btnConsultar);

        btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(290, 155, 120, 30);
        add(btnAlterar);

        btnRemover = new JButton("Remover");
        btnRemover.setBounds(420, 155, 120, 30);
        add(btnRemover);

        modelo = new DefaultTableModel(
                new String[]{"Código", "Nome", "Telefone"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabela = new JTable(modelo);

        barraRolagem = new JScrollPane(tabela);
        barraRolagem.setBounds(30, 210, 510, 200);
        add(barraRolagem);

        // Preenche os campos ao selecionar uma linha.
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()
                    && tabela.getSelectedRow() >= 0) {

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

    private boolean validarTelefone(String telefone) {

        return telefone != null
                && telefone.matches("\\d{11}");
    }

    private void criarEventos() {

        // INSERIR
        btnInserir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                String nome = txtNome.getText().trim();
                String telefone = txtTelefone.getText().trim();

                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Informe o nome do cliente."
                    );
                    return;
                }

                if (!validarTelefone(telefone)) {
                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Informe o telefone com DDD e 11 dígitos."
                    );
                    return;
                }

                String sql = "{CALL proc_ins_cliente(?, ?)}";

                try (Connection conexao = Conexao.conectar();
                     CallableStatement comando =
                             conexao.prepareCall(sql)) {

                    comando.setString(1, nome);
                    comando.setString(2, telefone);

                    comando.execute();

                    JOptionPane.showMessageDialog(
                            this == null ? null : ClientScreen.this,
                            "Cliente inserido com sucesso!"
                    );

                    txtCodigo.setText("");
                    txtNome.setText("");
                    txtTelefone.setText("");

                    btnConsultar.doClick();

                } catch (SQLException erro) {

                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Erro ao inserir: " + erro.getMessage()
                    );
                }
            }
        });

        // CONSULTAR
        btnConsultar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                String sql = "{CALL proc_selc_cliente()}";

                try (Connection conexao = Conexao.conectar();
                     CallableStatement comando =
                             conexao.prepareCall(sql);
                     ResultSet resultado = comando.executeQuery()) {

                    modelo.setRowCount(0);

                    while (resultado.next()) {

                        int codigo =
                                resultado.getInt("cod_cliente");

                        String nome =
                                resultado.getString("c_nome");

                        String telefone =
                                resultado.getString("c_telefone");

                        modelo.addRow(new Object[]{
                                codigo, nome, telefone
                        });
                    }

                } catch (SQLException erro) {

                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Erro ao consultar: " + erro.getMessage()
                    );
                }
            }
        });

        // ALTERAR
        btnAlterar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                int codigo;
                String nome = txtNome.getText().trim();
                String telefone = txtTelefone.getText().trim();

                try {
                    codigo = Integer.parseInt(
                            txtCodigo.getText().trim()
                    );
                } catch (NumberFormatException erro) {
                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Selecione um cliente ou informe um código válido."
                    );
                    return;
                }

                if (nome.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Informe o nome do cliente."
                    );
                    return;
                }

                if (!validarTelefone(telefone)) {
                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Informe o telefone com DDD e 11 dígitos."
                    );
                    return;
                }

                String sql = "{CALL proc_upd_cliente(?, ?, ?)}";

                try (Connection conexao = Conexao.conectar();
                     CallableStatement comando =
                             conexao.prepareCall(sql)) {

                    comando.setInt(1, codigo);
                    comando.setString(2, nome);
                    comando.setString(3, telefone);

                    comando.execute();

                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Operação de alteração executada."
                    );

                    btnConsultar.doClick();

                } catch (SQLException erro) {

                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Erro ao alterar: " + erro.getMessage()
                    );
                }
            }
        });

        // REMOVER
        btnRemover.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                int codigo;

                try {
                    codigo = Integer.parseInt(
                            txtCodigo.getText().trim()
                    );
                } catch (NumberFormatException erro) {
                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Selecione um cliente na tabela."
                    );
                    return;
                }

                int confirmacao = JOptionPane.showConfirmDialog(
                        ClientScreen.this,
                        "Deseja realmente remover este cliente?",
                        "Confirmar exclusão",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmacao != JOptionPane.YES_OPTION) {
                    return;
                }

                String sql = "{CALL proc_del_cliente(?)}";

                try (Connection conexao = Conexao.conectar();
                     CallableStatement comando =
                             conexao.prepareCall(sql)) {

                    comando.setInt(1, codigo);
                    comando.execute();

                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Operação de exclusão executada."
                    );

                    txtCodigo.setText("");
                    txtNome.setText("");
                    txtTelefone.setText("");

                    btnConsultar.doClick();

                } catch (SQLException erro) {

                    JOptionPane.showMessageDialog(
                            ClientScreen.this,
                            "Erro ao remover: " + erro.getMessage()
                    );
                }
            }
        });
    }
}