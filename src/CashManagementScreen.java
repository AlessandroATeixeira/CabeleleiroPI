import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

public class CashManagementScreen extends JFrame {

    private JComboBox<String> comboTipo;
    private JTextField txtValor;
    private JTextField txtDescricao;

    private JLabel lblSaldo;
    private JTable tabela;
    private DefaultTableModel modelo;

    private double saldo = 0.0;

    public CashManagementScreen() {
        setTitle("Gerenciamento de Caixa");
        setSize(750, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        criarComponentes();
        setVisible(true);
    }

    private void criarComponentes() {
        JPanel painelFormulario = new JPanel(new GridLayout(4, 2, 10, 10));
        painelFormulario.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        comboTipo = new JComboBox<>(
                new String[] {"ENTRADA", "SAÍDA"}
        );

        txtValor = new JTextField();
        txtDescricao = new JTextField();

        JButton btnRegistrar = new JButton("Registrar movimentação");

        painelFormulario.add(new JLabel("Tipo:"));
        painelFormulario.add(comboTipo);

        painelFormulario.add(new JLabel("Valor:"));
        painelFormulario.add(txtValor);

        painelFormulario.add(new JLabel("Descrição:"));
        painelFormulario.add(txtDescricao);

        painelFormulario.add(new JLabel(""));
        painelFormulario.add(btnRegistrar);

        add(painelFormulario, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new Object[] {
                        "Data/Hora",
                        "Tipo",
                        "Valor",
                        "Descrição"
                },
                0
        );

        tabela = new JTable(modelo);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        lblSaldo = new JLabel("Saldo atual: R$ 0,00");
        lblSaldo.setHorizontalAlignment(SwingConstants.CENTER);
        lblSaldo.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        add(lblSaldo, BorderLayout.SOUTH);

        btnRegistrar.addActionListener(evento -> registrarMovimentacao());
    }

    private void registrarMovimentacao() {
        try {
            double valor = Double.parseDouble(
                    txtValor.getText()
                            .replace(".", "")
                            .replace(",", ".")
            );

            if (valor <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "O valor deve ser maior que zero."
                );
                return;
            }

            String tipo = comboTipo.getSelectedItem().toString();
            String descricao = txtDescricao.getText().trim();

            if (descricao.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Informe uma descrição."
                );
                return;
            }

            if (tipo.equals("SAÍDA") && valor > saldo) {
                JOptionPane.showMessageDialog(
                        this,
                        "A saída não pode ser maior que o saldo disponível."
                );
                return;
            }

            if (tipo.equals("ENTRADA")) {
                saldo += valor;
            } else {
                saldo -= valor;
            }



            modelo.addRow(new Object[] {
                    tipo,
                    String.format("R$ %.2f", valor),
                    descricao
            });

            atualizarSaldo();

            txtValor.setText("");
            txtDescricao.setText("");

            JOptionPane.showMessageDialog(
                    this,
                    "Movimentação registrada com sucesso."
            );

            /*
             * In a real application, save the movement in the database here.
             *
             * Example:
             *
             * Connection conexao = Conexao.conectar();
             * CallableStatement comando = conexao.prepareCall(
             *     "{CALL PROC_INS_MOVIMENTO_CAIXA(?, ?, ?)}"
             * );
             *
             * comando.setString(1, tipo);
             * comando.setDouble(2, valor);
             * comando.setString(3, descricao);
             * comando.executeUpdate();
             */

        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe um valor válido."
            );
        }
    }

    private void atualizarSaldo() {
        lblSaldo.setText(
                String.format("Saldo atual: R$ %.2f", saldo)
        );
    }
}