import java.awt.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Agendamento extends JFrame {

    private JLabel lblServico;
    private JLabel lblHorario;
    private JLabel lblData;
    private JLabel lblCodigo;

    private JTextField txtServico;
    private JTextField txtHorario;
    private JTextField txtData;
    private JTextField txtCodigo;

    private JButton btnAgendar;
    private JButton btnExcluir;
    private JButton btnConsultar;
    private JButton btnAtualizar;
    private JButton btnLimpar;

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane barraRolagem;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter FORMATO_HORARIO = DateTimeFormatter.ofPattern("HH:mm");

    public Agendamento() {
        setTitle("Agendamento");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        criarComponentes();
        criarEventos();
        consultarAgendamentos();
        setVisible(true);
    }

    private void criarComponentes() {
        lblCodigo = new JLabel("Código:");
        lblCodigo.setBounds(30, 30, 80, 25);
        add(lblCodigo);

        txtCodigo = new JTextField();
        txtCodigo.setBounds(110, 30, 200, 30);
        add(txtCodigo);

        lblServico = new JLabel("Serviço:");
        lblServico.setBounds(30, 70, 80, 25);
        add(lblServico);

        txtServico = new JTextField();
        txtServico.setBounds(110, 70, 200, 30);
        add(txtServico);

        lblHorario = new JLabel("Horário (HH:mm):");
        lblHorario.setBounds(30, 110, 110, 25);
        add(lblHorario);

        txtHorario = new JTextField();
        txtHorario.setBounds(145, 110, 165, 30);
        add(txtHorario);

        lblData = new JLabel("Data (dd/MM/aaaa):");
        lblData.setBounds(30, 150, 125, 25);
        add(lblData);

        txtData = new JTextField();
        txtData.setBounds(160, 150, 150, 30);
        add(txtData);

        btnAgendar = new JButton("Agendar");
        btnAgendar.setBounds(30, 200, 120, 30);
        add(btnAgendar);

        btnExcluir = new JButton("Excluir");
        btnExcluir.setBounds(160, 200, 120, 30);
        add(btnExcluir);

        btnConsultar = new JButton("Consultar");
        btnConsultar.setBounds(290, 200, 120, 30);
        add(btnConsultar);

        btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setBounds(420, 200, 120, 30);
        add(btnAtualizar);

        btnLimpar = new JButton("Limpar");
        btnLimpar.setBounds(550, 200, 120, 30);
        add(btnLimpar);

        modelo = new DefaultTableModel(new Object[]{"Código", "Data", "Horário", "Serviço"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        barraRolagem = new JScrollPane(tabela);
        barraRolagem.setBounds(30, 250, 700, 270);
        add(barraRolagem);
    }

    private void criarEventos() {
        btnAgendar.addActionListener(e -> agendar());
        btnConsultar.addActionListener(e -> consultarAgendamentos());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e -> limparCampos());

        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabela.getSelectedRow() != -1) {
                int linha = tabela.getSelectedRow();
                txtCodigo.setText(modelo.getValueAt(linha, 0).toString());
                txtData.setText(modelo.getValueAt(linha, 1).toString());
                txtHorario.setText(modelo.getValueAt(linha, 2).toString());
                txtServico.setText(modelo.getValueAt(linha, 3).toString());
            }
        });
    }

    // Os códigos são informados manualmente; não usamos AUTO_INCREMENT.
    private void agendar() {
        if (!validarCampos(true)) {
            return;
        }

        try (Connection conexao = Conexao.conectar();
             CallableStatement comando = conexao.prepareCall("{CALL proc_ins_agendamento(?, ?, ?, ?)}")) {

            comando.setInt(1, lerCodigo());
            comando.setString(2, txtServico.getText().trim());
            comando.setDate(3, converterData(txtData.getText()));
            comando.setTime(4, converterHorario(txtHorario.getText()));
            comando.execute();

            JOptionPane.showMessageDialog(this, "Agendamento cadastrado.");
            limparCampos();
            consultarAgendamentos();

        } catch (NumberFormatException erro) {
            mostrarErro("O código deve ser um número inteiro positivo.");
        } catch (DateTimeParseException erro) {
            mostrarErro("Data ou horário inválido. Para a data, dia/mês/ano e para horário, horas:minutos, nessa ordem.");
        } catch (SQLException erro) {
            mostrarErro("Erro ao agendar: " + erro.getMessage());
        }
    }

    private void consultarAgendamentos() {
        modelo.setRowCount(0);

        try (Connection conexao = Conexao.conectar();
             CallableStatement comando = conexao.prepareCall("{CALL proc_selc_agendamentos()}");
             ResultSet resultado = comando.executeQuery()) {

            while (resultado.next()) {
                modelo.addRow(new Object[]{
                        resultado.getInt("cod_agendamento"),
                        resultado.getDate("data_agendamento").toLocalDate().format(FORMATO_DATA),
                        resultado.getTime("horario").toLocalTime().format(FORMATO_HORARIO),
                        resultado.getString("servico")
                });
            }

        } catch (SQLException erro) {
            mostrarErro("Erro ao consultar agendamentos: " + erro.getMessage());
            erro.printStackTrace();
        }
    }

    private void atualizar() {
        if (!validarCampos(true)) {
            return;
        }

        try (Connection conexao = Conexao.conectar();
             CallableStatement comando = conexao.prepareCall("{CALL proc_upd_agendamento(?, ?, ?, ?)}")) {

            comando.setInt(1, lerCodigo());
            comando.setString(2, txtServico.getText().trim());
            comando.setDate(3, converterData(txtData.getText()));
            comando.setTime(4, converterHorario(txtHorario.getText()));
            comando.execute();

            JOptionPane.showMessageDialog(this, "As informações de agendamento foram atualizadas.");
            limparCampos();
            consultarAgendamentos();

        } catch (NumberFormatException erro) {
            mostrarErro("O código deve ser um número inteiro positivo.");
        } catch (DateTimeParseException erro) {
            mostrarErro("Data ou horário inválido. Para a data, dia/mês/ano e para horário, horas:minutos, nessa ordem.");
        } catch (SQLException erro) {
            mostrarErro("Erro ao atualizar: " + erro.getMessage());
        }
    }

    private void excluir() {
        if (txtCodigo.getText().trim().isEmpty()) {
            mostrarErro("Informe o código ou selecione um agendamento.");
            return;
        }

        try {
            int codigo = lerCodigo();
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente excluir o agendamento " + codigo + "?",
                    "Confirmar exclusão",
                    JOptionPane.YES_NO_OPTION
            );

            if (resposta != JOptionPane.YES_OPTION) {
                return;
            }

            try (Connection conexao = Conexao.conectar();
                 CallableStatement comando = conexao.prepareCall("{CALL proc_del_agendamento(?)}")) {
                comando.setInt(1, codigo);
                comando.execute();
            }

            JOptionPane.showMessageDialog(this, "As informações foram apagadas.");
            limparCampos();
            consultarAgendamentos();

        } catch (NumberFormatException erro) {
            mostrarErro("O código deve ser um número inteiro positivo.");
        } catch (SQLException erro) {
            mostrarErro("Erro ao excluir: " + erro.getMessage());
        }
    }

    private boolean validarCampos(boolean exigirCodigo) {
        if (exigirCodigo) {
            try {
                lerCodigo();
            } catch (NumberFormatException erro) {
                mostrarErro("Informe um código numérico inteiro maior que zero.");
                return false;
            }
        }

        if (txtServico.getText().trim().isEmpty()
                || txtData.getText().trim().isEmpty()
                || txtHorario.getText().trim().isEmpty()) {
            mostrarErro("Preencha o serviço, a data e o horário.");
            return false;
        }

        try {
            converterData(txtData.getText());
            converterHorario(txtHorario.getText());
        } catch (DateTimeParseException erro) {
            mostrarErro("Data ou horário inválido. Use data dia/mês/ano e horário HH:mm.");
            return false;
        }
        return true;
    }

    private int lerCodigo() {
        int codigo = Integer.parseInt(txtCodigo.getText().trim());
        if (codigo <= 0) {
            throw new NumberFormatException("O código deve ser positivo.");
        }
        return codigo;
    }

    private java.sql.Date converterData(String data) {
        LocalDate dataConvertida = LocalDate.parse(data.trim(), FORMATO_DATA);
        return java.sql.Date.valueOf(dataConvertida);
    }

    private java.sql.Time converterHorario(String horario) {
        LocalTime horarioConvertido = LocalTime.parse(horario.trim(), FORMATO_HORARIO);
        return java.sql.Time.valueOf(horarioConvertido);
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtServico.setText("");
        txtHorario.setText("");
        txtData.setText("");
        tabela.clearSelection();
        txtCodigo.requestFocusInWindow();
    }

    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Atenção", JOptionPane.WARNING_MESSAGE);
    }
}
