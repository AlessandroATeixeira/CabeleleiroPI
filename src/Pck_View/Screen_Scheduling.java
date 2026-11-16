package Pck_View;

import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import Pck_Control.Control_Scheduling;
import Pck_Control.Control_Service;
import Pck_Model.Model_Scheduling;
import Pck_Model.Model_Service;

public class Screen_Scheduling extends Tela_Base {
    private Tema.Campo txtCodigo, txtCodCliente, txtData, txtHorario;
    private JComboBox<Model_Service> cbServico;
    private Tema.Botao btnAgendar, btnAtualizar, btnExcluir, btnConsultar, btnLimpar;
    private JTable tabela;
    private DefaultTableModel modelo;
    private JLabel lblTotal;
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter FORMATO_HORARIO = DateTimeFormatter.ofPattern("HH:mm");
    private final Control_Scheduling control = new Control_Scheduling();
    private final Control_Service controlServico = new Control_Service();
    private final List<Model_Service> servicos = new ArrayList<>();
    private boolean carregandoServicos = false;

    public Screen_Scheduling() {
        super("Agendamentos", "Cadastre, consulte e gerencie os horários do salão.");
        criarComponentes();
        criarEventos();
    }

    private void criarComponentes() {
        txtCodigo = new Tema.Campo("Ex.: 1");
        txtCodCliente = new Tema.Campo("Ex.: 1");
        txtData = new Tema.Campo("dd/mm/aaaa");
        txtHorario = new Tema.Campo("HH:mm");
        cbServico = new JComboBox<>();

        btnAgendar = new Tema.Botao("Agendar", Tema.TipoBotao.PRIMARIO);
        btnAtualizar = new Tema.Botao("Atualizar", Tema.TipoBotao.SECUNDARIO);
        btnExcluir = new Tema.Botao("Excluir", Tema.TipoBotao.PERIGO);
        btnConsultar = new Tema.Botao("Consultar", Tema.TipoBotao.SECUNDARIO);
        btnLimpar = new Tema.Botao("Limpar", Tema.TipoBotao.NEUTRO);

        Tema.Cartao formulario = cartaoComTitulo("Dados do agendamento", null);
        JPanel corpo = new JPanel(new BorderLayout(0, 18));
        corpo.setOpaque(false);
        corpo.add(linhaCampos(new double[]{1,1,2,1.5,1.2},
                Tema.campoComRotulo("Código", txtCodigo),
                Tema.campoComRotulo("ID Cliente", txtCodCliente),
                Tema.campoComRotulo("Serviço cadastrado", cbServico),
                Tema.campoComRotulo("Data", txtData),
                Tema.campoComRotulo("Horário", txtHorario)), BorderLayout.NORTH);
        corpo.add(linhaBotoes(btnAgendar, btnAtualizar, btnExcluir, btnConsultar, btnLimpar), BorderLayout.SOUTH);
        formulario.add(corpo, BorderLayout.CENTER);

        modelo = new DefaultTableModel(new Object[]{"Código","ID Cliente","Data","Horário","Serviço"}, 0) {
            @Override public boolean isCellEditable(int linha, int coluna) { return false; }
        };
        tabela = new JTable(modelo);
        lblTotal = etiquetaInfo("0 agendamentos");
        Tema.Cartao cartaoTabela = cartaoComTitulo("Agendamentos cadastrados", lblTotal);
        cartaoTabela.add(Tema.tabela(tabela), BorderLayout.CENTER);

        JPanel conteudo = new JPanel(new BorderLayout(0,18));
        conteudo.setOpaque(false);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(cartaoTabela, BorderLayout.CENTER);
        setConteudo(conteudo);
    }

    private void criarEventos() {
        btnAgendar.addActionListener(e -> agendar());
        btnAtualizar.addActionListener(e -> atualizar());
        btnExcluir.addActionListener(e -> excluir());
        btnConsultar.addActionListener(e -> consultarAgendamentos());
        btnLimpar.addActionListener(e -> limparCampos());
        tabela.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) selecionarLinha();
        });
    }

    @Override public void aoExibir() {
        carregarServicos();
        consultarAgendamentos();
    }

    private Model_Service servicoSelecionado() {
        Object selecionado = cbServico.getSelectedItem();
        return selecionado instanceof Model_Service ? (Model_Service) selecionado : null;
    }

    private void carregarServicos() {
        try {
            Integer idAtual = servicoSelecionado() == null ? null : servicoSelecionado().getIdServico();
            ArrayList<Model_Service> lista = controlServico.consultar();
            carregandoServicos = true;
            servicos.clear();
            servicos.addAll(lista);
            cbServico.removeAllItems();
            int indice = 0;
            for (int i=0; i<lista.size(); i++) {
                Model_Service s = lista.get(i);
                cbServico.addItem(s);
                if (idAtual != null && idAtual == s.getIdServico()) indice = i;
            }
            if (!lista.isEmpty()) cbServico.setSelectedIndex(indice);
            carregandoServicos = false;
            if (lista.isEmpty()) mostrarInfo("Cadastre pelo menos um serviço antes de criar agendamentos.");
        } catch (Exception e) {
            carregandoServicos = false;
            mostrarErro(e);
        }
    }

    private void agendar() {
        try {
            Model_Service s = servicoSelecionado();
            if (s == null) { mostrarInfo("Cadastre e selecione um serviço."); return; }
            control.Inserir(txtCodigo.getText(), txtCodCliente.getText(), s.getIdServico(), txtData.getText(), txtHorario.getText());
            mostrarInfo("Agendamento salvo com sucesso.");
            limparCampos();
            consultarAgendamentos();
        } catch (Exception e) { mostrarErro(e); }
    }

    private void atualizar() {
        try {
            Model_Service s = servicoSelecionado();
            if (s == null) { mostrarInfo("Cadastre e selecione um serviço."); return; }
            control.Atualizar(txtCodigo.getText(), txtCodCliente.getText(), s.getIdServico(), txtData.getText(), txtHorario.getText());
            mostrarInfo("Agendamento atualizado com sucesso.");
            limparCampos();
            consultarAgendamentos();
        } catch (Exception e) { mostrarErro(e); }
    }

    private void excluir() {
        try {
            if (txtCodigo.getText().trim().isEmpty()) { mostrarInfo("Selecione um agendamento na tabela ou informe o código."); return; }
            if (!confirmar("Deseja realmente excluir o agendamento " + txtCodigo.getText().trim() + "?")) return;
            control.Excluir(txtCodigo.getText());
            mostrarInfo("Agendamento excluído com sucesso.");
            limparCampos();
            consultarAgendamentos();
        } catch (Exception e) { mostrarErro(e); }
    }

    private void consultarAgendamentos() {
        try {
            ArrayList<Model_Scheduling> lista = control.Consultar();
            modelo.setRowCount(0);
            for (Model_Scheduling c : lista) {
                modelo.addRow(new Object[]{c.getCodigo(), c.getCodCliente(),
                        LocalDate.parse(c.getData()).format(FORMATO_DATA),
                        LocalTime.parse(c.getHorario()).format(FORMATO_HORARIO), c.getServico()});
            }
            lblTotal.setText(lista.size() + (lista.size()==1 ? " agendamento" : " agendamentos"));
        } catch (Exception e) { mostrarErro(e); }
    }

    private void selecionarLinha() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            txtCodigo.setText(String.valueOf(modelo.getValueAt(linha,0)));
            txtCodCliente.setText(String.valueOf(modelo.getValueAt(linha,1)));
            txtData.setText(String.valueOf(modelo.getValueAt(linha,2)));
            txtHorario.setText(String.valueOf(modelo.getValueAt(linha,3)));
            String nomeServico = String.valueOf(modelo.getValueAt(linha,4));
            for (int i=0; i<servicos.size(); i++) {
                if (servicos.get(i).getNomeServico().equals(nomeServico)) {
                    cbServico.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void limparCampos() {
        txtCodigo.setText("");
        txtCodCliente.setText("");
        txtData.setText("");
        txtHorario.setText("");
        if (cbServico.getItemCount()>0) cbServico.setSelectedIndex(0);
        tabela.clearSelection();
        txtCodigo.requestFocusInWindow();
    }
}
