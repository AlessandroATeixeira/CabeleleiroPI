package Pck_View;

import java.awt.BorderLayout;
import java.awt.Font;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Tela_Caixa extends Tela_Base {

    private JComboBox<String> comboTipo;
    private JComboBox<String> cbServProd;
    private Tema.Campo txtValor;
    private Tema.Campo txtDescricao;

    private Tema.Botao btnRegistrar;
    private Tema.Botao btnLimpar;

    private JLabel lblSaldo;
    private JTable tabela;
    private DefaultTableModel modelo;

    private double saldo = 0.0;

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    public Tela_Caixa() {
        super("Caixa", "Registre entradas e saídas e acompanhe o saldo.");
        criarComponentes();
        criarEventos();
    }

    private void criarComponentes() {
        // ---- Formulario ----
        comboTipo = Tema.combo("ENTRADA", "SAÍDA");
        cbServProd = Tema.combo("Produto", "Serviço");
        txtDescricao = new Tema.Campo("Ex.: Escova + hidratação");
        txtValor = new Tema.Campo("0,00");

        btnRegistrar = new Tema.Botao("Registrar movimentação", Tema.TipoBotao.PRIMARIO);
        btnLimpar = new Tema.Botao("Limpar", Tema.TipoBotao.NEUTRO);

        Tema.Cartao formulario = cartaoComTitulo("Nova movimentação", null);
        JPanel corpo = new JPanel(new BorderLayout(0, 18));
        corpo.setOpaque(false);
        corpo.add(linhaCampos(new double[]{1.1, 1.1, 3, 1.3},
                Tema.campoComRotulo("Tipo", comboTipo),
                Tema.campoComRotulo("Origem", cbServProd),
                Tema.campoComRotulo("Descrição", txtDescricao),
                Tema.campoComRotulo("Valor (R$)", txtValor)), BorderLayout.NORTH);
        corpo.add(linhaBotoes(btnRegistrar, btnLimpar), BorderLayout.SOUTH);
        formulario.add(corpo, BorderLayout.CENTER);

        // ---- Tabela ----
        modelo = new DefaultTableModel(
                new Object[]{"Data/Hora", "Tipo", "Origem", "Descrição", "Valor"}, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        tabela = new JTable(modelo);

        lblSaldo = new JLabel("Saldo atual: " + Tema.moeda(0));
        lblSaldo.setFont(Tema.fonte(Font.BOLD, 15));
        lblSaldo.setForeground(Tema.VINHO);

        Tema.Cartao cartaoTabela = cartaoComTitulo("Movimentações", lblSaldo);
        cartaoTabela.add(Tema.tabela(tabela), BorderLayout.CENTER);

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setOpaque(false);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(cartaoTabela, BorderLayout.CENTER);
        setConteudo(conteudo);
    }

    private void criarEventos() {
        btnRegistrar.addActionListener(e -> registrarMovimentacao());
        btnLimpar.addActionListener(e -> limparCampos());
    }

    private void limparCampos() {
        txtValor.setText("");
        txtDescricao.setText("");
        comboTipo.setSelectedIndex(0);
        cbServProd.setSelectedIndex(0);
        txtDescricao.requestFocusInWindow();
    }

    private void registrarMovimentacao() {
        try {
            double valor = Double.parseDouble(
                    txtValor.getText().trim().replace(".", "").replace(",", "."));

            if (valor <= 0) {
                mostrarInfo("O valor deve ser maior que zero.");
                return;
            }

            String tipo = String.valueOf(comboTipo.getSelectedItem());
            String origem = String.valueOf(cbServProd.getSelectedItem());
            String descricao = txtDescricao.getText().trim();

            if (descricao.isEmpty()) {
                mostrarInfo("Informe uma descrição.");
                return;
            }

            if (tipo.equals("SAÍDA") && valor > saldo) {
                mostrarInfo("A saída não pode ser maior que o saldo disponível.");
                return;
            }

            saldo += tipo.equals("ENTRADA") ? valor : -valor;

            modelo.insertRow(0, new Object[]{
                    LocalDateTime.now().format(FORMATO),
                    tipo,
                    origem,
                    descricao,
                    (tipo.equals("SAÍDA") ? "- " : "+ ") + Tema.moeda(valor)
            });

            lblSaldo.setText("Saldo atual: " + Tema.moeda(saldo));
            limparCampos();
            mostrarInfo("Movimentação registrada com sucesso.");
        } catch (NumberFormatException erro) {
            mostrarInfo("Informe um valor válido (ex.: 35,50).");
        }
    }
}
