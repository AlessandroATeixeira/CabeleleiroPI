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
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class ProductScreen extends JFrame {

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

    public ProductScreen() {

        setTitle("Cadastro de Produtos");
        setSize(650, 530);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        criarComponentes();
        criarEventos();

        setVisible(true);

        // Carrega os produtos ao abrir a tela.
        consultarProdutos();
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
        lblNome = new JLabel("Nome:");
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

        // INSERIR
        btnInserir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                inserirProduto();
            }
        });

        // CONSULTAR
        btnConsultar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                consultarProdutos();
            }
        });

        // ALTERAR
        btnAlterar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                alterarProduto();
            }
        });

        // EXCLUIR
        btnRemover.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                excluirProduto();
            }
        });

        // LIMPAR CAMPOS
        btnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                limparCampos();
            }
        });

        // Preenche os campos ao selecionar uma linha da tabela.
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

                txtValorProd.setText(
                        modelo.getValueAt(linha, 2).toString()
                );

                txtEstoque.setText(
                        modelo.getValueAt(linha, 3).toString()
                );
            }
        });
    }

    // MÉTODO PARA INSERIR PRODUTO
    private void inserirProduto() {

        String nome = txtNome.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o nome do produto."
            );
            return;
        }

        try {

            double valor = lerValor();
            int estoque = lerEstoque();

            try (
                    Connection conexao = Conexao.conectar();
                    CallableStatement comando =
                            conexao.prepareCall(
                                    "{CALL proc_ins_produto(?, ?, ?, ?)}"
                            );
            ) {

                comando.setInt(1, Integer.parseInt(txtCodigo.getText()));
                comando.setString(2, txtNome.getText());
                comando.setDouble(3, Double.parseDouble(txtValorProd.getText()));
                comando.setInt(4, Integer.parseInt(txtEstoque.getText()));

                comando.execute();

                JOptionPane.showMessageDialog(
                        this,
                        "Produto inserido com sucesso!"
                );
            }

            limparCampos();
            consultarProdutos();

        } catch (NumberFormatException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Informe um valor decimal válido e um estoque inteiro."
            );

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao inserir produto: " + erro.getMessage()
            );
        }
    }

    // MÉTODO PARA CONSULTAR PRODUTOS
    private void consultarProdutos() {

        try (
                Connection conexao = Conexao.conectar();
                CallableStatement comando =
                        conexao.prepareCall(
                                "{CALL proc_selc_produtos()}"
                        );
                ResultSet resultado = comando.executeQuery()
        ) {

            modelo.setRowCount(0);

            while (resultado.next()) {

                int codigo = resultado.getInt("cod_produto");
                String nome = resultado.getString("nome_prod");
                double valor = resultado.getDouble("valor_prod");
                int estoque = resultado.getInt("estoque");

                modelo.addRow(new Object[] {
                        codigo,
                        nome,
                        valor,
                        estoque
                });
            }

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao consultar produtos: " + erro.getMessage()
            );
        }
    }

    // MÉTODO PARA ALTERAR PRODUTO
    private void alterarProduto() {

        String nome = txtNome.getText().trim();

        if (txtCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o código ou selecione um produto na tabela."
            );
            return;
        }

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o nome do produto."
            );
            return;
        }

        try {

            int codigo = Integer.parseInt(
                    txtCodigo.getText().trim()
            );

            double valor = lerValor();
            int estoque = lerEstoque();

            try (
                    Connection conexao = Conexao.conectar();
                    CallableStatement comando =
                            conexao.prepareCall(
                                    "{CALL proc_upd_produto(?, ?, ?, ?)}"
                            )
            ) {

                comando.setInt(1, codigo);
                comando.setString(2, nome);
                comando.setDouble(3, valor);
                comando.setInt(4, estoque);

                comando.execute();

                JOptionPane.showMessageDialog(
                        this,
                        "Comando de alteração executado. "
                                + "Confira a tabela para verificar o resultado."
                );
            }

            limparCampos();
            consultarProdutos();

        } catch (NumberFormatException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Código e estoque devem ser inteiros. "
                            + "O valor deve ser numérico."
            );

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao alterar produto: " + erro.getMessage()
            );
        }
    }

    // MÉTODO PARA EXCLUIR PRODUTO
    private void excluirProduto() {

        if (txtCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o código ou selecione um produto na tabela."
            );
            return;
        }

        try {

            int codigo = Integer.parseInt(
                    txtCodigo.getText().trim()
            );

            int confirmacao = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente excluir o produto "
                            + codigo + "?",
                    "Confirmar exclusão",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirmacao != JOptionPane.YES_OPTION) {
                return;
            }

            try (
                    Connection conexao = Conexao.conectar();
                    CallableStatement comando =
                            conexao.prepareCall(
                                    "{CALL proc_del_produto(?)}"
                            )
            ) {

                comando.setInt(1, codigo);
                comando.execute();

                JOptionPane.showMessageDialog(
                        this,
                        "Produto excluído com sucesso!"
                );
            }

            limparCampos();
            consultarProdutos();

        } catch (NumberFormatException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "O código deve ser um número inteiro."
            );

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao excluir produto: " + erro.getMessage()
            );
        }
    }

    // LÊ E VALIDA O VALOR DO PRODUTO
    private double lerValor() {

        String texto = txtValorProd.getText()
                .trim()
                .replace(",", ".");

        double valor = Double.parseDouble(texto);

        if (!Double.isFinite(valor) || valor < 0) {
            throw new NumberFormatException(
                    "O valor não pode ser negativo."
            );
        }

        return valor;
    }

    // LÊ E VALIDA O ESTOQUE
    private int lerEstoque() {

        int estoque = Integer.parseInt(
                txtEstoque.getText().trim()
        );

        if (estoque < 0) {
            throw new NumberFormatException(
                    "O estoque não pode ser negativo."
            );
        }

        return estoque;
    }

    // LIMPA OS CAMPOS
    private void limparCampos() {

        txtCodigo.setText("");
        txtNome.setText("");
        txtValorProd.setText("");
        txtEstoque.setText("");

        tabela.clearSelection();
        txtCodigo.requestFocus();
    }
}