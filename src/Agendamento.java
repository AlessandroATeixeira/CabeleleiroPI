import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    private JButton BtnAgendar;
    private JButton BtnExcluir;
    private JButton BtnConsultar;
    private JButton BtnAtualizar;
    private JButton BtnLimpar;

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane barraRolagem;

    public Agendamento() {

        setTitle("Agendamento");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        criarComponentes();
        criarEventos();

        setVisible(true);

        consultarAgendamentos();
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

        lblHorario = new JLabel("Horário:");
        lblHorario.setBounds(30, 110, 80, 25);
        add(lblHorario);

        txtHorario = new JTextField();
        txtHorario.setBounds(110, 110, 200, 30);
        add(txtHorario);

        lblData = new JLabel("Data:");
        lblData.setBounds(30, 150, 80, 25);
        add(lblData);

        txtData = new JTextField();
        txtData.setBounds(110, 150, 200, 30);
        add(txtData);

        BtnAgendar = new JButton("Agendar");
        BtnAgendar.setBounds(30, 200, 120, 30);
        add(BtnAgendar);

        BtnExcluir = new JButton("Excluir");
        BtnExcluir.setBounds(160, 200, 120, 30);
        add(BtnExcluir);

        BtnConsultar = new JButton("Consultar");
        BtnConsultar.setBounds(290, 200, 120, 30);
        add(BtnConsultar);

        BtnAtualizar = new JButton("Atualizar");
        BtnAtualizar.setBounds(420, 200, 120, 30);
        add(BtnAtualizar);

        BtnLimpar = new JButton("Limpar");
        BtnLimpar.setBounds(550, 200, 120, 30);
        add(BtnLimpar);

        modelo = new DefaultTableModel(
                new Object[] {
                        "Código", "Data", "Horário", "Serviço"
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
        barraRolagem.setBounds(30, 250, 700, 270);
        add(barraRolagem);
    }

    private void criarEventos() {

        BtnAgendar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                agendar();
            }
        });

        BtnConsultar.addActionListener(e -> {
            consultarAgendamentos();
        });

        BtnAtualizar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                atualizar();
            }
        });

        BtnExcluir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                excluir();
            }
        });

        BtnLimpar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evento) {
                limparCampos();
            }
        });

        // Ao selecionar uma linha, preenche os campos.
        tabela.getSelectionModel().addListSelectionListener(evento -> {

            if (!evento.getValueIsAdjusting()
                    && tabela.getSelectedRow() != -1) {

                int linha = tabela.getSelectedRow();

                txtCodigo.setText(
                        modelo.getValueAt(linha, 0).toString()
                );

                txtData.setText(
                        modelo.getValueAt(linha, 1).toString()
                );

                txtHorario.setText(
                        modelo.getValueAt(linha, 2).toString()
                );

                txtServico.setText(
                        modelo.getValueAt(linha, 3).toString()
                );
            }
        });
    }

    // CADASTRAR AGENDAMENTO
    private void agendar() {

        if (!validarCampos(false)) {
            return;
        }

        try (
                Connection conexao = Conexao.conectar();
                CallableStatement comando =
                        conexao.prepareCall(
                                "{CALL proc_ins_agendamento(?, ?, ?)}"
                        )
        ) {

            comando.setString(1, txtServico.getText().trim());
            comando.setDate(2, converterData(txtData.getText()));
            comando.setTime(3, converterHorario(txtHorario.getText()));

            comando.execute();

            JOptionPane.showMessageDialog(
                    this,
                    "Agendamento cadastrado com sucesso!"
            );

            limparCampos();
            consultarAgendamentos();

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao agendar: " + erro.getMessage()
            );
        }
    }

    // CONSULTAR AGENDAMENTOS
    private void consultarAgendamentos() {

        modelo.setRowCount(0);

        try (
                Connection conexao = Conexao.conectar();
                CallableStatement comando =
                        conexao.prepareCall("{CALL proc_selc_agendamentos()}");
                ResultSet resultado = comando.executeQuery()
        ) {

            while (resultado.next()) {

                int codigo = resultado.getInt("cod_agendamento");
                String servico = resultado.getString("servico");
                String data = resultado.getString("data_agendamento");
                String horario = resultado.getString("horario");

                modelo.addRow(new Object[] {
                        codigo,
                        data,
                        horario,
                        servico
                });
            }

            if (modelo.getRowCount() == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Nenhum agendamento cadastrado."
                );
            }

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao consultar agendamentos:\n"
                            + erro.getMessage()
            );

            erro.printStackTrace();
        }
    }
    // ATUALIZAR AGENDAMENTO
    private void atualizar() {

        if (!validarCampos(true)) {
            return;
        }

        try {

            int codigo = Integer.parseInt(
                    txtCodigo.getText().trim()
            );

            try (
                    Connection conexao = Conexao.conectar();
                    CallableStatement comando =
                            conexao.prepareCall(
                                    "{CALL proc_upd_agendamento(?, ?, ?, ?)}"
                            )
            ) {

                comando.setInt(1, codigo);
                comando.setString(2, txtServico.getText().trim());
                comando.setDate(3, converterData(txtData.getText()));
                comando.setTime(4, converterHorario(txtHorario.getText()));

                comando.execute();

                JOptionPane.showMessageDialog(
                        this,
                        "Solicitação de atualização executada."
                );
            }

            limparCampos();
            consultarAgendamentos();

        } catch (NumberFormatException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "O código deve ser um número inteiro."
            );

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao atualizar: " + erro.getMessage()
            );
        }
    }

    // EXCLUIR AGENDAMENTO
    private void excluir() {

        if (txtCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o código ou selecione um agendamento."
            );
            return;
        }

        try {

            int codigo = Integer.parseInt(
                    txtCodigo.getText().trim()
            );

            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente excluir o agendamento "
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
                                    "{CALL proc_del_agendamento(?)}"
                            )
            ) {

                comando.setInt(1, codigo);
                comando.execute();

                JOptionPane.showMessageDialog(
                        this,
                        "Agendamento excluído com sucesso!"
                );
            }

            limparCampos();
            consultarAgendamentos();

        } catch (NumberFormatException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "O código deve ser um número inteiro."
            );

        } catch (SQLException erro) {

            JOptionPane.showMessageDialog(
                    this,
                    "Erro ao excluir: " + erro.getMessage()
            );
        }
    }

    // VALIDAR CAMPOS
    private boolean validarCampos(boolean exigirCodigo) {

        if (exigirCodigo && txtCodigo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Informe o código do agendamento."
            );
            return false;
        }

        if (txtServico.getText().trim().isEmpty()
                || txtData.getText().trim().isEmpty()
                || txtHorario.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Preencha o serviço, a data e o horário."
            );
            return false;
        }

        if (exigirCodigo) {
            try {
                int codigo = Integer.parseInt(
                        txtCodigo.getText().trim()
                );

                if (codigo <= 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "O código deve ser maior que zero."
                    );
                    return false;
                }

            } catch (NumberFormatException erro) {
                JOptionPane.showMessageDialog(
                        this,
                        "O código deve ser um número inteiro."
                );
                return false;
            }
        }

        return true;
    }

    // LIMPAR CAMPOS
    private void limparCampos() {

        txtCodigo.setText("");
        txtServico.setText("");
        txtHorario.setText("");
        txtData.setText("");

        tabela.clearSelection();
        txtCodigo.requestFocus();
    }

    private java.sql.Date converterData(String data) {

        java.time.format.DateTimeFormatter formato =
                java.time.format.DateTimeFormatter.ofPattern("ddMMyyyy");

        java.time.LocalDate dataConvertida =
                java.time.LocalDate.parse(data.trim(), formato);

        return java.sql.Date.valueOf(dataConvertida);
    }
    private java.sql.Time converterHorario(String horario) {

        java.time.format.DateTimeFormatter formato =
                java.time.format.DateTimeFormatter.ofPattern("HH:mm");

        java.time.LocalTime horarioConvertido =
                java.time.LocalTime.parse(horario.trim(), formato);

        return java.sql.Time.valueOf(horarioConvertido);
    }

}