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
        lblTelefone.setBounds(30, 100, 80, 25);
        add(lblTelefone);

        txtTelefone = new JTextField();
        txtTelefone.setBounds(110, 100, 120, 25);
        add(txtTelefone);


        btnInserir = new JButton("Inserir");
        btnInserir.setBounds(30, 145, 120, 30);
        add(btnInserir);

        btnConsultar = new JButton("Consultar");
        btnConsultar.setBounds(160, 145, 120, 30);
        add(btnConsultar);

        btnAlterar = new JButton("Alterar");
        btnAlterar.setBounds(290, 145, 120, 30);
        add(btnAlterar);

        btnRemover = new JButton("exclude");
        btnRemover.setBounds(420, 145, 120, 30);
        add(btnRemover);

        modelo = new DefaultTableModel();
        modelo.addColumn("Código");
        modelo.addColumn("Nome");
        modelo.addColumn("Telefone");

        tabela = new JTable(modelo);

        barraRolagem = new JScrollPane(tabela);
        barraRolagem.setBounds(30, 210, 510, 170);
        add(barraRolagem);
    }

    private void criarEventos() {

        btnInserir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                try {
                    Connection conexao = Conexao.conectar();

                    CallableStatement comando =
                            conexao.prepareCall(
                                    "{CALL PROC_INS_CLIENTE(?, ?)}"
                            );

                    String nome = txtNome.getText();
                    int telefone = Integer.parseInt(txtTelefone.getText());

                    comando.setString(1, nome);
                    comando.setInt(2, telefone);

                    comando.executeUpdate();

                    JOptionPane.showMessageDialog(
                            null,
                            "Registro inserido com sucesso!"
                    );

                    comando.close();
                    conexao.close();

                    btnConsultar.doClick();

                } catch (NumberFormatException erro) {

                    JOptionPane.showMessageDialog(
                            null,
                            "O telefone deve ter apenas numeros"
                    );

                } catch (SQLException erro) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Erro ao inserir: " + erro.getMessage()
                    );
                }
            }
        });

        btnConsultar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                try {
                    Connection conexao = Conexao.conectar();

                    CallableStatement comando =
                            conexao.prepareCall(
                                    "{CALL proc_selc_cliente()}"
                            );

                    ResultSet resultado = comando.executeQuery();

                    modelo.setRowCount(0);

                    while (resultado.next()) {

                        int codigo = resultado.getInt("cod_cliente");
                        String nome = resultado.getString("c_nome");
                        int telefone = resultado.getInt("c_telefone");


                        modelo.addRow(
                                new Object[] {codigo, nome, telefone}
                        );
                    }

                    resultado.close();
                    comando.close();
                    conexao.close();

                } catch (SQLException erro) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Erro ao consultar: " + erro.getMessage()
                    );
                }
            }
        });

        btnAlterar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                try {
                    Connection conexao = Conexao.conectar();

                    CallableStatement comando =
                            conexao.prepareCall(
                                    "{CALL proc_upd_cliente(?, ?, ?)}"
                            );

                    int codigo = Integer.parseInt(txtCodigo.getText());
                    String nome = txtNome.getText();
                    int telefone = Integer.parseInt(txtTelefone.getText());

                    comando.setInt(1, codigo);
                    comando.setString(2, nome);
                    comando.setInt(3, telefone);

                    int linhasAfetadas =
                            comando.executeUpdate();

                    if (linhasAfetadas > 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Registro alterado com sucesso!"
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Nenhum registro encontrado para alterar."
                        );
                    }

                    comando.close();
                    conexao.close();

                    btnConsultar.doClick();

                } catch (NumberFormatException erro) {

                    JOptionPane.showMessageDialog(
                            null,
                            "O código deve ser um número inteiro."
                    );

                } catch (SQLException erro) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Erro ao alterar: " + erro.getMessage()
                    );
                }
            }
        });

        btnRemover.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evento) {

                try {
                    Connection conexao = Conexao.conectar();

                    CallableStatement comando =
                            conexao.prepareCall(
                                    "{CALL proc_del_cliente(?)}"
                            );

                    int codigo = Integer.parseInt(txtTelefone.getText());

                    comando.setInt(1, codigo);

                    int linhasAfetadas =
                            comando.executeUpdate();

                    if (linhasAfetadas > 0) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Registro removido com sucesso!"
                        );
                    } else {
                        JOptionPane.showMessageDialog(
                                null,
                                "Nenhum registro encontrado para remover."
                        );
                    }

                    comando.close();
                    conexao.close();

                    btnConsultar.doClick();

                } catch (NumberFormatException erro) {

                    JOptionPane.showMessageDialog(
                            null,
                            "O código deve ser um número inteiro."
                    );

                } catch (SQLException erro) {

                    JOptionPane.showMessageDialog(
                            null,
                            "Erro ao remover: " + erro.getMessage()
                    );
                }
            }
        });
    }


} 