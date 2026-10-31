package Pck_View;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.DefaultListCellRenderer;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

/**
 * Tema visual do sistema (paleta "Coffee") e fabrica de componentes estilizados.
 */
public final class Tema {

    // ---- Paleta sugerida ----
    public static final Color VINHO_ESCURO = new Color(0x561C24);
    public static final Color VINHO        = new Color(0x6D2932);
    public static final Color BEGE         = new Color(0xC7B7A3);
    public static final Color CREME        = new Color(0xE8D8C4);

    // ---- Tons derivados da paleta ----
    public static final Color CARTAO       = new Color(0xF8F2E9);
    public static final Color CAMPO        = new Color(0xFFFCF7);
    public static final Color BORDA        = new Color(0xD9CAB6);
    public static final Color TEXTO        = new Color(0x3B1218);
    public static final Color TEXTO_SUAVE  = new Color(0x8C7869);
    public static final Color VINHO_HOVER  = new Color(0x7E3440);
    public static final Color AREIA        = new Color(0xAD8F6B);

    private static final String FAMILIA = escolherFamilia();

    private Tema() { }

    private static String escolherFamilia() {
        String[] preferidas = {"Segoe UI", "Helvetica Neue", "Inter", "Roboto", "Ubuntu"};
        Set<String> disponiveis = new HashSet<>(Arrays.asList(
                java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment()
                        .getAvailableFontFamilyNames()));
        for (String f : preferidas) {
            if (disponiveis.contains(f)) {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    public static Font fonte(int estilo, int tamanho) {
        return new Font(FAMILIA, estilo, tamanho);
    }

    /** Aplica look and feel e cores padrao (inclusive nas caixas de dialogo). */
    public static void instalar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) {
            // segue com o padrao
        }
        UIManager.put("Panel.background", CREME);
        UIManager.put("OptionPane.background", CREME);
        UIManager.put("OptionPane.messageForeground", TEXTO);
        UIManager.put("OptionPane.messageFont", fonte(Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", fonte(Font.BOLD, 13));
        UIManager.put("Button.background", VINHO);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.select", VINHO_ESCURO);
        UIManager.put("Button.focus", VINHO);
        UIManager.put("Button.font", fonte(Font.BOLD, 13));
        UIManager.put("Label.foreground", TEXTO);
        UIManager.put("Label.font", fonte(Font.PLAIN, 13));
    }

    // ------------------------------------------------------------------
    // Utilitarios de pintura
    // ------------------------------------------------------------------
    static Graphics2D suave(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        return g2;
    }

    // ------------------------------------------------------------------
    // Cartao arredondado (equivalente aos "cards" brancos do modelo)
    // ------------------------------------------------------------------
    public static class Cartao extends JPanel {
        public Cartao(java.awt.LayoutManager layout) {
            super(layout);
            setOpaque(false);
            setBorder(new EmptyBorder(20, 22, 20, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            g2.setColor(CARTAO);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.setColor(BORDA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ------------------------------------------------------------------
    // Botao arredondado
    // ------------------------------------------------------------------
    public enum TipoBotao { PRIMARIO, SECUNDARIO, NEUTRO, PERIGO }

    public static class Botao extends JButton {
        private final TipoBotao tipo;
        private boolean hover;

        public Botao(String texto, TipoBotao tipo) {
            super(texto);
            this.tipo = tipo;
            setFont(fonte(Font.BOLD, 13));
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 22, 10, 22));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            boolean pressionado = getModel().isPressed();
            Color fundo;
            Color borda = null;
            Color texto;
            switch (tipo) {
                case PRIMARIO:
                    fundo = pressionado ? VINHO_ESCURO : (hover ? VINHO_HOVER : VINHO);
                    texto = Color.WHITE;
                    break;
                case PERIGO:
                    fundo = pressionado ? new Color(0x3F1319) : (hover ? new Color(0x6A2330) : VINHO_ESCURO);
                    texto = CREME;
                    break;
                case SECUNDARIO:
                    fundo = pressionado ? BEGE : (hover ? CREME : new Color(0, 0, 0, 0));
                    borda = VINHO;
                    texto = VINHO;
                    break;
                default: // NEUTRO
                    fundo = pressionado ? new Color(0xB5A38D) : (hover ? new Color(0xD3C4B0) : BEGE);
                    texto = VINHO_ESCURO;
                    break;
            }
            g2.setColor(fundo);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            if (borda != null) {
                g2.setColor(borda);
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            }
            g2.dispose();
            setForeground(texto);
            super.paintComponent(g);
        }
    }

    // ------------------------------------------------------------------
    // Campo de texto com placeholder
    // ------------------------------------------------------------------
    private static class BordaCampo extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = suave(g);
            boolean foco = c.hasFocus() || (c instanceof JComboBox && ((JComboBox<?>) c).isPopupVisible());
            g2.setColor(foco ? VINHO : BORDA);
            g2.setStroke(new BasicStroke(foco ? 1.6f : 1f));
            g2.drawRoundRect(x, y, w - 1, h - 1, 12, 12);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }

    public static class Campo extends JTextField {
        private final String placeholder;

        public Campo(String placeholder) {
            this.placeholder = placeholder;
            setFont(fonte(Font.PLAIN, 14));
            setForeground(TEXTO);
            setCaretColor(VINHO);
            setSelectionColor(BEGE);
            setSelectedTextColor(TEXTO);
            setOpaque(false);
            setBorder(new BordaCampo());
            setPreferredSize(new Dimension(160, 40));
            addFocusListener(new FocusAdapter() {
                @Override public void focusGained(FocusEvent e) { repaint(); }
                @Override public void focusLost(FocusEvent e) { repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = suave(g);
            g2.setColor(CAMPO);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
            g2.dispose();
            super.paintComponent(g);
            if (placeholder != null && getText().isEmpty()) {
                Graphics2D t = suave(g);
                t.setFont(getFont());
                t.setColor(TEXTO_SUAVE);
                Insets in = getInsets();
                int y = (getHeight() + t.getFontMetrics().getAscent() - t.getFontMetrics().getDescent()) / 2;
                t.drawString(placeholder, in.left, y);
                t.dispose();
            }
        }
    }

    // ------------------------------------------------------------------
    // ComboBox
    // ------------------------------------------------------------------
    private static class RendererCombo extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean selecionado, boolean foco) {
            super.getListCellRendererComponent(list, value, index, selecionado, false);
            setFont(fonte(Font.PLAIN, 14));
            setBorder(new EmptyBorder(8, 12, 8, 12));
            if (index == -1) {            // valor exibido no proprio combo
                setOpaque(false);
                setBorder(new EmptyBorder(0, 0, 0, 0));
                setForeground(TEXTO);
            } else {
                setOpaque(true);
                setBackground(selecionado ? BEGE : CARTAO);
                setForeground(TEXTO);
            }
            return this;
        }
    }

    private static class ComboUI extends BasicComboBoxUI {
        @Override
        protected javax.swing.JButton createArrowButton() {
            JButton b = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = suave(g);
                    g2.setColor(VINHO);
                    g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2;
                    g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
                    g2.drawLine(cx, cy + 3, cx + 5, cy - 2);
                    g2.dispose();
                }
            };
            b.setBorder(new EmptyBorder(0, 0, 0, 0));
            b.setContentAreaFilled(false);
            b.setFocusPainted(false);
            b.setOpaque(false);
            return b;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            // o fundo arredondado e pintado pelo proprio combo
        }

        @Override
        public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus) {
            super.paintCurrentValue(g, bounds, false);
        }

        @Override
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox);
            popup.setBorder(BorderFactory.createLineBorder(BORDA));
            return popup;
        }
    }

    public static JComboBox<String> combo(String... itens) {
        JComboBox<String> c = new JComboBox<String>(itens) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = suave(g);
                g2.setColor(CAMPO);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        c.setUI(new ComboUI());
        c.setOpaque(false);
        c.setFont(fonte(Font.PLAIN, 14));
        c.setForeground(TEXTO);
        c.setRenderer(new RendererCombo());
        c.setBorder(new BordaCampo());
        c.setPreferredSize(new Dimension(160, 40));
        c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return c;
    }

    // ------------------------------------------------------------------
    // Rotulos e campos com legenda
    // ------------------------------------------------------------------
    public static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.BOLD, 12));
        l.setForeground(VINHO_ESCURO);
        return l;
    }

    public static JLabel tituloCartao(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(fonte(Font.BOLD, 16));
        l.setForeground(TEXTO);
        return l;
    }

    /** Legenda em cima e componente embaixo. */
    public static JPanel campoComRotulo(String legenda, JComponent componente) {
        JPanel p = new JPanel(new java.awt.BorderLayout(0, 6));
        p.setOpaque(false);
        p.add(rotulo(legenda), java.awt.BorderLayout.NORTH);
        p.add(componente, java.awt.BorderLayout.CENTER);
        return p;
    }

    // ------------------------------------------------------------------
    // Tabela
    // ------------------------------------------------------------------
    private static class RendererCelula extends DefaultTableCellRenderer {
        private final int alinhamento;

        RendererCelula(int alinhamento) {
            this.alinhamento = alinhamento;
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                       boolean foco, int linha, int coluna) {
            super.getTableCellRendererComponent(t, v, sel, false, linha, coluna);
            setBorder(new EmptyBorder(0, 14, 0, 14));
            setHorizontalAlignment(alinhamento);
            setFont(fonte(Font.PLAIN, 13));
            setForeground(TEXTO);
            setBackground(sel ? BEGE : CARTAO);
            return this;
        }
    }

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    public static String moeda(double valor) {
        return NumberFormat.getCurrencyInstance(PT_BR).format(valor);
    }

    public static String moedaCurta(double valor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(PT_BR);
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);
        return nf.format(valor);
    }

    /** Renderer que mostra numeros como moeda (R$ 1.234,56), alinhado a esquerda como as demais colunas. */
    public static DefaultTableCellRenderer rendererMoeda() {
        return new RendererCelula(JLabel.LEFT) {
            @Override
            protected void setValue(Object v) {
                setText(v instanceof Number ? moeda(((Number) v).doubleValue()) : (v == null ? "" : v.toString()));
            }
        };
    }

    public static JScrollPane tabela(JTable t) {
        t.setRowHeight(42);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(true);
        t.setGridColor(BORDA);
        t.setIntercellSpacing(new Dimension(0, 1));
        t.setFillsViewportHeight(true);
        t.setBackground(CARTAO);
        t.setSelectionBackground(BEGE);
        t.setSelectionForeground(TEXTO);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setFocusable(false);
        t.setDefaultRenderer(Object.class, new RendererCelula(JLabel.LEFT));

        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 40));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean s,
                                                           boolean f, int l, int c) {
                super.getTableCellRendererComponent(tb, v, false, false, l, c);
                setFont(fonte(Font.BOLD, 12));
                setForeground(VINHO_ESCURO);
                setBackground(CREME);
                setBorder(new EmptyBorder(0, 14, 0, 14));
                setHorizontalAlignment(JLabel.LEFT);
                return this;
            }
        });

        JScrollPane sp = new JScrollPane(t);
        sp.setBorder(BorderFactory.createLineBorder(BORDA));
        sp.getViewport().setBackground(CARTAO);
        sp.getVerticalScrollBar().setUI(new BarraRolagem());
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        sp.getHorizontalScrollBar().setUI(new BarraRolagem());
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 10));
        sp.setCorner(JScrollPane.UPPER_RIGHT_CORNER, cantoVazio());
        sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, cantoVazio());
        return sp;
    }

    private static JPanel cantoVazio() {
        JPanel p = new JPanel();
        p.setBackground(CREME);
        return p;
    }

    private static class BarraRolagem extends BasicScrollBarUI {
        @Override protected void configureScrollBarColors() {
            thumbColor = BEGE;
            trackColor = CARTAO;
        }

        @Override protected JButton createDecreaseButton(int o) { return botaoZero(); }
        @Override protected JButton createIncreaseButton(int o) { return botaoZero(); }

        private JButton botaoZero() {
            JButton b = new JButton();
            b.setPreferredSize(new Dimension(0, 0));
            b.setBorder(null);
            return b;
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
            g.setColor(CARTAO);
            g.fillRect(r.x, r.y, r.width, r.height);
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
            if (r.isEmpty() || !scrollbar.isEnabled()) {
                return;
            }
            Graphics2D g2 = suave(g);
            g2.setColor(BEGE);
            g2.fillRoundRect(r.x + 1, r.y + 1, r.width - 2, r.height - 2, 8, 8);
            g2.dispose();
        }
    }
}
