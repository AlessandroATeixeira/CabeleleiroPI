package Pck_View;

import Pck_Control.Control_Scheduling;
import Pck_Model.Model_Scheduling;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Screen_Scheduling extends JFrame {

    private JLabel lblCodigo;
    private JLabel lblServico;
    private JLabel lblHorario;
    private JLabel lblData;

    private JTextField txtCodigo;
    private JTextField txtHorario;
    private JTextField txtData;
    public static JComboBox<String> cbServico;

    private JButton btnAgendar;
    private JButton btnExcluir;
    private JButton btnConsultar;
    private JButton btnAtualizar;
    private JButton btnLimpar;

    private JTable tabela;
    private DefaultTableModel modelo;

    private static final Color FUNDO = new Color(243, 246, 250);
    private static final Color AZUL = new Color(43, 78, 120);
    private static final Color AZUL_CLARO = new Color(225, 236, 248);
    private static final Color TEXTO = new Color(38, 50, 65);

    private static final DateTimeFormatter FORMATO_DATA_TELA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter FORMATO_HORARIO_TELA = DateTimeFormatter.ofPattern("HH:mm");

    Control_Scheduling control = new Control_Scheduling();

    public Screen_Scheduling() {
        setTitle("Sistema de Agendamento");
        setSize(900, 700);
        setMinimumSize(new Dimension(780, 620));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        criarComponentes();
        criarEventos();
        consultarAgendamentos();

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel raiz = new JPanel(new BorderLayout(0, 18));
        raiz.setBackground(FUNDO);
        raiz.setBorder(new EmptyBorder(24, 28, 24, 28));
        setContentPane(raiz);

        JPanel cabecalho = new JPanel(new BorderLayout(4, 6));
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Agendamentos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 28));
        titulo.setForeground(AZUL);

        JLabel subtitulo = new JLabel("Cadastre, consulte e gerencie seus horários");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(100, 112, 126));

        cabecalho.add(titulo, BorderLayout.NORTH);
        cabecalho.add(subtitulo, BorderLayout.SOUTH);
        raiz.add(cabecalho, BorderLayout.NORTH);

        JPanel cartaoFormulario = new JPanel(new BorderLayout(0, 16));
        cartaoFormulario.setBackground(Color.WHITE);
        cartaoFormulario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 230, 237)),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel tituloFormulario = new JLabel("Dados do agendamento");
        tituloFormulario.setFont(new Font("SansSerif", Font.BOLD, 17));
        tituloFormulario.setForeground(TEXTO);
        cartaoFormulario.add(tituloFormulario, BorderLayout.NORTH);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        lblCodigo = criarLabel("Código");
        lblServico = criarLabel("Serviço");
        lblData = criarLabel("Data (dd/MM/aaaa)");
        lblHorario = criarLabel("Horário (HH:mm)");

        txtCodigo = criarCampo();
        txtData = criarCampo();
        txtHorario = criarCampo();

        cbServico = new JComboBox<>(new String[]{"Luzes", "Alisamento", "Escova", "Progressiva"});
        cbServico.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cbServico.setPreferredSize(new Dimension(250, 36));
        cbServico.setBackground(Color.WHITE);

        adicionarCampo(formulario, lblCodigo, txtCodigo, 0, 0);
        adicionarCampo(formulario, lblServico, cbServico, 1, 0);
        adicionarCampo(formulario, lblData, txtData, 0, 1);
        adicionarCampo(formulario, lblHorario, txtHorario, 1, 1);

        cartaoFormulario.add(formulario, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        botoes.setOpaque(false);

        btnAgendar = criarBotao("Agendar", AZUL, Color.WHITE);
        btnAtualizar = criarBotao("Atualizar", AZUL_CLARO, AZUL);
        btnExcluir = criarBotao("Excluir", new Color(252, 232, 232), new Color(160, 45, 45));
        btnConsultar = criarBotao("Consultar", new Color(232, 239, 246), AZUL);
        btnLimpar = criarBotao("Limpar", new Color(240, 242, 245), TEXTO);

        botoes.add(btnAgendar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);
        botoes.add(btnConsultar);
        botoes.add(btnLimpar);

        cartaoFormulario.add(botoes, BorderLayout.SOUTH);

        modelo = new DefaultTableModel(new Object[]{"Código", "Data", "Horário", "Serviço"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(30);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabela.setForeground(TEXTO);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setGridColor(new Color(232, 236, 241));
        tabela.setShowVerticalLines(false);
        tabela.setSelectionBackground(AZUL_CLARO);
        tabela.setSelectionForeground(TEXTO);

        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabela.getTableHeader().setBackground(new Color(236, 241, 247));
        tabela.getTableHeader().setForeground(TEXTO);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 34));

        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(BorderFactory.createLineBorder(new Color(224, 230, 237)));
        rolagem.getViewport().setBackground(Color.WHITE);

        JPanel cartaoTabela = new JPanel(new BorderLayout(0, 12));
        cartaoTabela.setBackground(Color.WHITE);
        cartaoTabela.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(224, 230, 237)),
                new EmptyBorder(16, 18, 18, 18)
        ));

        JLabel tituloTabela = new JLabel("Agendamentos cadastrados");
        tituloTabela.setFont(new Font("SansSerif", Font.BOLD, 17));
        tituloTabela.setForeground(TEXTO);

        cartaoTabela.add(tituloTabela, BorderLayout.NORTH);
        cartaoTabela.add(rolagem, BorderLayout.CENTER);

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setOpaque(false);
        conteudo.add(cartaoFormulario, BorderLayout.NORTH);
        conteudo.add(cartaoTabela, BorderLayout.CENTER);

        raiz.add(conteudo, BorderLayout.CENTER);
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(TEXTO);
        return label;
    }

    private JTextField criarCampo() {
        JTextField campo = new JTextField();
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(250, 36));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(205, 214, 224)),
                new EmptyBorder(5, 9, 5, 9)
        ));
        return campo;
    }

    private JButton criarBotao(String texto, Color fundo, Color corTexto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, 12));
        botao.setForeground(corTexto);
        botao.setBackground(fundo);
        botao.setFocusPainted(false);
        botao.setOpaque(true);
        botao.setBorderPainted(false);
        botao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        botao.setMargin(new Insets(10, 14, 10, 14));
        return botao;
    }

    private void adicionarCampo(JPanel painel, JLabel label, JComponent campo, int coluna, int linha) {
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.gridx = coluna * 2;
        gbcLabel.gridy = linha;
        gbcLabel.anchor = GridBagConstraints.WEST;
        gbcLabel.insets = new Insets(5, 8, 5, 10);
        painel.add(label, gbcLabel);

        GridBagConstraints gbcCampo = new GridBagConstraints();
        gbcCampo.gridx = coluna * 2 + 1;
        gbcCampo.gridy = linha;
        gbcCampo.weightx = 1;
        gbcCampo.fill = GridBagConstraints.HORIZONTAL;
        gbcCampo.insets = new Insets(5, 0, 5, 16);
        painel.add(campo, gbcCampo);
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
                cbServico.setSelectedItem(modelo.getValueAt(linha, 3).toString());
            }
        });
    }

    private void agendar() {
        try {
            control.Inserir(
                    txtCodigo.getText(),
                    String.valueOf(cbServico.getSelectedItem()),
                    txtData.getText(),
                    txtHorario.getText()
            );
            JOptionPane.showMessageDialog(this, "Agendamento salvo com sucesso");
            consultarAgendamentos();
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void consultarAgendamentos() {
        try {
            modelo.setRowCount(0);
            ArrayList<Model_Scheduling> lista = control.Consultar();
            for (Model_Scheduling c : lista) {
                modelo.addRow(new Object[]{
                        c.getCodigo(),
                        formatarDataParaTela(c.getData()),
                        formatarHoraParaTela(c.getHorario()),
                        c.getServico()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void atualizar() {
        try {
            control.Atualizar(
                    txtCodigo.getText(),
                    String.valueOf(cbServico.getSelectedItem()),
                    txtData.getText(),
                    txtHorario.getText()
            );
            JOptionPane.showMessageDialog(this, "Agendamento atualizado com sucesso");
            consultarAgendamentos();
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void excluir() {
        try {
            control.Excluir(txtCodigo.getText());
            JOptionPane.showMessageDialog(this, "Agendamento excluído com sucesso");
            consultarAgendamentos();
            limparCampos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void selecionarLinha() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            txtCodigo.setText(modelo.getValueAt(linha, 0).toString());
            txtData.setText(modelo.getValueAt(linha, 1).toString());
            txtHorario.setText(modelo.getValueAt(linha, 2).toString());
            cbServico.setSelectedItem(modelo.getValueAt(linha, 3).toString());
        }
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtData.setText("");
        txtHorario.setText("");
        cbServico.setSelectedIndex(0);
        tabela.clearSelection();
        txtCodigo.requestFocusInWindow();
    }

    private String formatarDataParaTela(String dataBanco) {
        return LocalDate.parse(dataBanco).format(FORMATO_DATA_TELA);
    }

    private String formatarHoraParaTela(String horaBanco) {
        return LocalTime.parse(horaBanco).format(FORMATO_HORARIO_TELA);
    }
}
