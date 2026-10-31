package Pck_View;

import Pck_Control.Control_Scheduling;
import Pck_Model.Model_Scheduling;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Screen_Scheduling extends JFrame {

    private static final Color COR_PRIMARIA = new Color(86, 28, 36);
    private static final Color COR_PRIMARIA_2 = new Color(109, 41, 50);
    private static final Color COR_SECUNDARIA = new Color(199, 183, 163);
    private static final Color COR_FUNDO = new Color(244, 239, 234);
    private static final Color COR_PANEL = new Color(247, 244, 241);
    private static final Color COR_TEXT = new Color(34, 34, 34);

    private JLabel lblCodigo;
    private JLabel lblServico;
    private JLabel lblHorario;
    private JLabel lblData;

    private JTextField txtCodigo;
    private JTextField txtHorario;
    private JTextField txtData;
    private JComboBox<String> cbServico;

    private JButton btnAgendar;
    private JButton btnExcluir;
    private JButton btnConsultar;
    private JButton btnAtualizar;
    private JButton btnLimpar;

    private JTable tabela;
    private DefaultTableModel modelo;

    private static final DateTimeFormatter FORMATO_DATA_TELA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter FORMATO_HORARIO_TELA = DateTimeFormatter.ofPattern("HH:mm");

    private final Control_Scheduling control = new Control_Scheduling();

    public Screen_Scheduling() {
        setTitle("Agendamentos");
        setSize(1000, 680);
        setMinimumSize(new Dimension(780, 620));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        criarComponentes();
        criarEventos();
        consultarAgendamentos();

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel root = new JPanel(new BorderLayout(18, 18));
        root.setBackground(COR_FUNDO);
        root.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cabecalho = new JPanel(new BorderLayout());
        cabecalho.setOpaque(false);

        JLabel titulo = new JLabel("Agendamentos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        titulo.setForeground(COR_TEXT);

        JLabel subtitulo = new JLabel("Cadastre, consulte e gerencie os horários da sua agenda");
        subtitulo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(120, 120, 120));

        JPanel blocoTitulo = new JPanel(new BorderLayout(0, 6));
        blocoTitulo.setOpaque(false);
        blocoTitulo.add(titulo, BorderLayout.NORTH);
        blocoTitulo.add(subtitulo, BorderLayout.SOUTH);

        cabecalho.add(blocoTitulo, BorderLayout.WEST);

        JButton btnNovo = new JButton("Novo agendamento");
        btnNovo.setBackground(COR_PRIMARIA_2);
        btnNovo.setForeground(Color.WHITE);
        btnNovo.setFocusPainted(false);
        btnNovo.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btnNovo.setFont(new Font("SansSerif", Font.BOLD, 15));

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerRight.setOpaque(false);
        headerRight.add(btnNovo);

        cabecalho.add(headerRight, BorderLayout.EAST);
        root.add(cabecalho, BorderLayout.NORTH);

        JPanel card = new RoundedPanel(COR_PANEL, 20);
        card.setLayout(new BorderLayout(18, 18));
        card.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel tituloForm = new JLabel("Dados do agendamento");
        tituloForm.setFont(new Font("SansSerif", Font.BOLD, 22));
        tituloForm.setForeground(COR_TEXT);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);

        lblCodigo = criarLabel("Código");
        lblServico = criarLabel("Serviço");
        lblData = criarLabel("Data");
        lblHorario = criarLabel("Horário");

        txtCodigo = criarCampo();
        txtData = criarCampo();
        txtHorario = criarCampo();

        cbServico = new JComboBox<>(new String[]{"Luzes", "Alisamento", "Escova", "Progressiva"});
        cbServico.setPreferredSize(new Dimension(250, 36));
        cbServico.setFont(new Font("SansSerif", Font.PLAIN, 15));

        adicionarCampo(form, lblCodigo, txtCodigo, 0, 0);
        adicionarCampo(form, lblServico, cbServico, 1, 0);
        adicionarCampo(form, lblData, txtData, 0, 1);
        adicionarCampo(form, lblHorario, txtHorario, 1, 1);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botoes.setOpaque(false);

        btnAgendar = criarBotao("Agendar", COR_PRIMARIA_2, Color.WHITE);
        btnAtualizar = criarBotao("Atualizar", COR_SECUNDARIA, COR_TEXT);
        btnExcluir = criarBotao("Excluir", new Color(214, 72, 100), Color.WHITE);
        btnConsultar = criarBotao("Consultar", new Color(220, 220, 220), COR_TEXT);
        btnLimpar = criarBotao("Limpar", new Color(230, 230, 230), COR_TEXT);

        botoes.add(btnAgendar);
        botoes.add(btnAtualizar);
        botoes.add(btnExcluir);
        botoes.add(btnConsultar);
        botoes.add(btnLimpar);

        modelo = new DefaultTableModel(new Object[]{"Código", "Data", "Horário", "Serviço"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(32);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setBackground(COR_PANEL);
        tabela.setForeground(COR_TEXT);
        tabela.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.setBorder(null);

        card.add(tituloForm, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(botoes, BorderLayout.SOUTH);

        JPanel tableCard = new RoundedPanel(COR_PANEL, 18);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(12, 12, 12, 12));
        tableCard.add(scroll, BorderLayout.CENTER);

        root.add(card, BorderLayout.NORTH);
        root.add(tableCard, BorderLayout.CENTER);

        add(root);
    }

    private JLabel criarLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(COR_TEXT);
        return label;
    }

    private JTextField criarCampo() {
        JTextField field = new JTextField();
        field.setFont(new Font("SansSerif", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 210, 210), 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        field.setPreferredSize(new Dimension(250, 36));
        return field;
    }

    private JButton criarBotao(String texto, Color bg, Color fg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        return btn;
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

    static class RoundedPanel extends JPanel {
        private final Color color;
        private final int radius;

        RoundedPanel(Color color, int radius) {
            this.color = color;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}