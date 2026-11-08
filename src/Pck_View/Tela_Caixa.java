package Pck_View;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import Pck_Control.Control_Caixa;
import Pck_Control.Control_Product;
import Pck_Control.Control_Valor;
import Pck_Model.Model_Caixa;
import Pck_Model.Model_Product;

/**
 * Caixa integrado ao estoque:
 *  - Produto + ENTRADA = venda  -> o estoque do produto diminui
 *  - Produto + SAIDA   = compra -> o estoque do produto aumenta
 *  - Servico            -> so movimenta o caixa
 * Os valores gravados alimentam a tela de Relatorios.
 */
public class Tela_Caixa extends Tela_Base {

    private JComboBox<String> comboTipo;
    private JComboBox<String> cbOrigem;
    private JComboBox<String> cbProduto;
    private Tema.Campo txtQuantidade;
    private Tema.Campo txtValor;
    private Tema.Campo txtDescricao;
    private JLabel lblValor;
    private JLabel lblDica;
    private JLabel lblTotal;

    private Tema.Botao btnRegistrar;
    private Tema.Botao btnLimpar;

    private JLabel lblSaldo;
    private JTable tabela;
    private DefaultTableModel modelo;

    private final List<Model_Product> produtos = new ArrayList<>();
    private boolean atualizandoProdutos = false;

    private final Control_Caixa control = new Control_Caixa();
    private final Control_Product controlProduto = new Control_Product();

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm");

    public Tela_Caixa() {
        super("Caixa", "Vendas e compras de produtos movimentam o estoque; tudo alimenta os relatórios.");
        criarComponentes();
        criarEventos();
        atualizarModo();
    }

    private void criarComponentes() {
        // ---- Formulario ----
        comboTipo = Tema.combo("ENTRADA", "SAÍDA");
        cbOrigem = Tema.combo("Produto", "Serviço");
        cbProduto = Tema.combo();
        txtQuantidade = new Tema.Campo("1");
        txtValor = new Tema.Campo("0,00");
        txtDescricao = new Tema.Campo("Opcional para produto");

        lblValor = Tema.rotulo("Valor unitário (R$)");
        lblDica = etiquetaInfo(" ");
        lblTotal = new JLabel("Total: " + Tema.moeda(0));
        lblTotal.setFont(Tema.fonte(Font.BOLD, 15));
        lblTotal.setForeground(Tema.VINHO_ESCURO);

        btnRegistrar = new Tema.Botao("Registrar movimentação", Tema.TipoBotao.PRIMARIO);
        btnLimpar = new Tema.Botao("Limpar", Tema.TipoBotao.NEUTRO);

        JPanel linha1 = linhaCampos(new double[]{1, 1, 3},
                Tema.campoComRotulo("Tipo", comboTipo),
                Tema.campoComRotulo("Origem", cbOrigem),
                Tema.campoComRotulo("Produto", cbProduto));
        JPanel linha2 = linhaCampos(new double[]{1, 1, 3},
                Tema.campoComRotulo("Quantidade", txtQuantidade),
                campoComRotulo(lblValor, txtValor),
                Tema.campoComRotulo("Descrição", txtDescricao));

        JPanel campos = new JPanel(new GridLayout(2, 1, 0, 14));
        campos.setOpaque(false);
        campos.add(linha1);
        campos.add(linha2);

        JPanel rodape = new JPanel(new BorderLayout(16, 0));
        rodape.setOpaque(false);
        rodape.add(linhaBotoes(btnRegistrar, btnLimpar), BorderLayout.WEST);
        JPanel direita = new JPanel(new GridLayout(2, 1, 0, 2));
        direita.setOpaque(false);
        lblDica.setHorizontalAlignment(JLabel.RIGHT);
        lblTotal.setHorizontalAlignment(JLabel.RIGHT);
        direita.add(lblTotal);
        direita.add(lblDica);
        rodape.add(direita, BorderLayout.CENTER);

        Tema.Cartao formulario = cartaoComTitulo("Nova movimentação", null);
        JPanel corpo = new JPanel(new BorderLayout(0, 18));
        corpo.setOpaque(false);
        corpo.add(campos, BorderLayout.NORTH);
        corpo.add(rodape, BorderLayout.SOUTH);
        formulario.add(corpo, BorderLayout.CENTER);

        // ---- Tabela ----
        modelo = new DefaultTableModel(
                new Object[]{"Data/Hora", "Tipo", "Origem", "Descrição", "Qtd.", "Valor"}, 0) {
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
        int[] larguras = {165, 100, 95, 250, 70, 140};   // Data/Hora, Tipo, Origem, Descrição, Qtd., Valor
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }

        JPanel conteudo = new JPanel(new BorderLayout(0, 18));
        conteudo.setOpaque(false);
        conteudo.add(formulario, BorderLayout.NORTH);
        conteudo.add(cartaoTabela, BorderLayout.CENTER);
        setConteudo(conteudo);
    }

    /** Campo com legenda cujo texto pode mudar (valor unitario x valor). */
    private static JPanel campoComRotulo(JLabel legenda, JComponent componente) {
        JPanel p = new JPanel(new BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(legenda, BorderLayout.NORTH);
        p.add(componente, BorderLayout.CENTER);
        return p;
    }

    private void criarEventos() {
        btnRegistrar.addActionListener(e -> registrarMovimentacao());
        btnLimpar.addActionListener(e -> limparCampos());

        comboTipo.addActionListener(e -> atualizarModo());
        cbOrigem.addActionListener(e -> atualizarModo());
        cbProduto.addActionListener(e -> {
            if (!atualizandoProdutos) {
                preencherValorDoProduto();
            }
        });

        DocumentListener recalcula = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { atualizarTotal(); }
            @Override public void removeUpdate(DocumentEvent e) { atualizarTotal(); }
            @Override public void changedUpdate(DocumentEvent e) { atualizarTotal(); }
        };
        txtQuantidade.getDocument().addDocumentListener(recalcula);
        txtValor.getDocument().addDocumentListener(recalcula);
    }

    @Override
    public void aoExibir() {
        carregarProdutos();
        carregarMovimentos();
    }

    // ------------------------------------------------------------------
    // Estado do formulario
    // ------------------------------------------------------------------
    private boolean origemProduto() {
        return cbOrigem.getSelectedIndex() == 0;
    }

    private boolean entrada() {
        return comboTipo.getSelectedIndex() == 0;
    }

    private void atualizarModo() {
        boolean produto = origemProduto();
        cbProduto.setEnabled(produto);
        txtQuantidade.setEnabled(produto);
        lblValor.setText(produto ? "Valor unitário (R$)" : "Valor (R$)");
        txtDescricao.setToolTipText(produto ? "Se ficar em branco, a descrição é gerada automaticamente." : null);

        if (produto) {
            lblDica.setText(entrada()
                    ? "Venda: o estoque do produto será reduzido."
                    : "Compra: o estoque do produto será reposto.");
            if (txtValor.getText().trim().isEmpty()) {
                preencherValorDoProduto();
            }
        } else {
            lblDica.setText(entrada()
                    ? "Receita de serviço: soma ao saldo do caixa."
                    : "Despesa: reduz o saldo do caixa.");
        }
        atualizarTotal();
    }

    private Model_Product produtoSelecionado() {
        int i = cbProduto.getSelectedIndex();
        return (i >= 0 && i < produtos.size()) ? produtos.get(i) : null;
    }

    private void preencherValorDoProduto() {
        Model_Product p = produtoSelecionado();
        if (origemProduto() && p != null) {
            txtValor.setText(String.format("%.2f", p.getValorProd()).replace('.', ','));
        }
    }

    private void atualizarTotal() {
        try {
            double unit = Control_Valor.parse(txtValor.getText());
            double total = origemProduto()
                    ? unit * Integer.parseInt(txtQuantidade.getText().trim())
                    : unit;
            lblTotal.setText("Total: " + Tema.moeda(total));
        } catch (NumberFormatException e) {
            lblTotal.setText("Total: —");
        }
    }

    private void limparCampos() {
        txtQuantidade.setText("");
        txtValor.setText("");
        txtDescricao.setText("");
        comboTipo.setSelectedIndex(0);
        cbOrigem.setSelectedIndex(0);
        if (cbProduto.getItemCount() > 0) {
            cbProduto.setSelectedIndex(0);
        }
        atualizarModo();
        tabela.clearSelection();
    }

    // ------------------------------------------------------------------
    // Registro
    // ------------------------------------------------------------------
    private void registrarMovimentacao() {
        try {
            boolean produto = origemProduto();
            Model_Product p = produtoSelecionado();
            if (produto && p == null) {
                mostrarInfo("Cadastre e selecione um produto antes de registrar uma movimentação de produto.");
                return;
            }

            String descricao = txtDescricao.getText().trim();
            if (descricao.isEmpty() && produto) {
                descricao = (entrada() ? "Venda: " : "Compra: ") + p.getNome();
            }

            double unit = Control_Valor.parse(txtValor.getText());
            int qtd = produto ? Integer.parseInt(txtQuantidade.getText().trim()) : 1;
            double total = unit * qtd;

            control.Registrar(
                    entrada() ? "ENTRADA" : "SAIDA",
                    produto ? "PRODUTO" : "SERVICO",
                    produto ? p.getCodigo() : null,
                    produto ? txtQuantidade.getText() : null,
                    descricao,
                    total);

            mostrarInfo(produto
                    ? "Movimentação registrada. Estoque de \"" + p.getNome() + "\" atualizado."
                    : "Movimentação registrada com sucesso.");
            limparCampos();
            carregarProdutos();   // estoque novo aparece na lista de produtos
            carregarMovimentos();
        } catch (Exception e) {
            mostrarErro(e);
        }
    }

    // ------------------------------------------------------------------
    // Carga de dados
    // ------------------------------------------------------------------
    private void carregarProdutos() {
        try {
            ArrayList<Model_Product> lista = controlProduto.Consultar();
            Integer codigoAtual = produtoSelecionado() == null ? null : produtoSelecionado().getCodigo();

            atualizandoProdutos = true;
            produtos.clear();
            produtos.addAll(lista);
            cbProduto.removeAllItems();
            int indiceAtual = 0;
            for (int i = 0; i < lista.size(); i++) {
                Model_Product p = lista.get(i);
                cbProduto.addItem(p.getCodigo() + " - " + p.getNome() + "  (estoque: " + p.getEstoque() + ")");
                if (codigoAtual != null && codigoAtual == p.getCodigo()) {
                    indiceAtual = i;
                }
            }
            if (lista.isEmpty()) {
                cbProduto.addItem("Nenhum produto cadastrado");
            } else {
                cbProduto.setSelectedIndex(indiceAtual);
            }
            atualizandoProdutos = false;
            atualizarModo();
        } catch (Exception e) {
            atualizandoProdutos = false;
            mostrarErro(e);
        }
    }

    private void carregarMovimentos() {
        try {
            ArrayList<Model_Caixa> lista = control.Consultar();
            modelo.setRowCount(0);
            for (Model_Caixa m : lista) {
                String sinal = m.isEntrada() ? "+ " : "- ";
                modelo.addRow(new Object[]{
                        m.getDataHora() == null ? "" : m.getDataHora().format(FORMATO),
                        m.isEntrada() ? "ENTRADA" : "SAÍDA",
                        "PRODUTO".equals(m.getOrigem()) ? "Produto" : "Serviço",
                        m.getDescricao(),
                        m.getQuantidade() == null ? "—" : String.valueOf(m.getQuantidade()),
                        sinal + Tema.moeda(m.getValor())
                });
            }
            lblSaldo.setText("Saldo atual: " + Tema.moeda(Control_Caixa.saldo(lista)));
        } catch (Exception e) {
            mostrarErro(e);
        }
    }
}
