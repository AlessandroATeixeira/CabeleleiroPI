package Pck_View;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Janela unica do sistema: menu lateral + area de conteudo onde as telas
 * de funcionalidade sao exibidas (sem abrir janelas separadas).
 */
public class Tela_Principal extends JFrame {

    private static final int LARGURA_MENU = 250;

    private final CardLayout cartas = new CardLayout();
    private final JPanel area = new JPanel(cartas);
    private final Map<String, Tela_Base> telas = new LinkedHashMap<>();
    private final Map<String, LinhaMenu> itens = new LinkedHashMap<>();

    public Tela_Principal() {
        setTitle("Sistema do Cabeleireiro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 720);
        setMinimumSize(new Dimension(1020, 640));
        setLocationRelativeTo(null);

        JPanel raiz = new JPanel(new BorderLayout());
        raiz.setBackground(Tema.CREME);
        setContentPane(raiz);

        area.setBackground(Tema.CREME);
        raiz.add(criarBarraLateral(), BorderLayout.WEST);
        raiz.add(area, BorderLayout.CENTER);

        navegar("Clientes");
    }

    // ------------------------------------------------------------------
    // Navegacao
    // ------------------------------------------------------------------
    private void navegar(String chave) {
        cartas.show(area, chave);
        for (Map.Entry<String, LinhaMenu> e : itens.entrySet()) {
            e.getValue().setSelecionado(e.getKey().equals(chave));
        }
        telas.get(chave).aoExibir();
    }

    private LinhaMenu itemTela(String chave, Tela_Base tela) {
        telas.put(chave, tela);
        area.add(tela, chave);
        LinhaMenu item = new LinhaMenu(chave, null, false, true, () -> navegar(chave));
        itens.put(chave, item);
        return item;
    }

    private void confirmarSaida() {
        int opcao = JOptionPane.showConfirmDialog(this, "Deseja realmente sair?",
                "Confirmar saída", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (opcao == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    // ------------------------------------------------------------------
    // Barra lateral
    // ------------------------------------------------------------------
    private JPanel criarBarraLateral() {
        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBackground(Tema.VINHO_ESCURO);
        barra.setPreferredSize(new Dimension(LARGURA_MENU, 0));
        barra.setBorder(new EmptyBorder(18, 0, 18, 0));

        barra.add(new Logo());
        barra.add(Box.createVerticalStrut(18));

        barra.add(new Grupo("Cadastros", Icone.PESSOA,
                itemTela("Clientes", new Tela_Cliente()),
                itemTela("Produtos", new Tela_Produto())));
        barra.add(new Grupo("Operação", Icone.CALENDARIO,
                itemTela("Agendamento", new Screen_Scheduling()),
                itemTela("Caixa", new Tela_Caixa())));
        barra.add(new Grupo("Análises", Icone.GRAFICO,
                itemTela("Relatórios", new Tela_Relatorio_Caixa())));

        barra.add(Box.createVerticalGlue());
        barra.add(new LinhaMenu("Sair", Icone.SAIR, false, false, this::confirmarSaida));
        return barra;
    }

    /** Grupo recolhivel do menu (cabecalho + subitens), como no modelo. */
    private static class Grupo extends JPanel {
        private final LinhaMenu cabecalho;
        private final JPanel filhos = new JPanel();
        private boolean aberto = true;

        Grupo(String titulo, Icone icone, LinhaMenu... subitens) {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setAlignmentX(LEFT_ALIGNMENT);

            cabecalho = new LinhaMenu(titulo, icone, true, false, this::alternar);

            filhos.setLayout(new BoxLayout(filhos, BoxLayout.Y_AXIS));
            filhos.setBackground(Tema.VINHO);
            filhos.setBorder(new EmptyBorder(8, 0, 8, 0));
            filhos.setAlignmentX(LEFT_ALIGNMENT);
            for (LinhaMenu s : subitens) {
                filhos.add(s);
            }

            add(cabecalho);
            add(filhos);
        }

        private void alternar() {
            aberto = !aberto;
            filhos.setVisible(aberto);
            cabecalho.setAberto(aberto);
            revalidate();
            if (getParent() != null) {
                getParent().revalidate();
                getParent().repaint();
            }
        }
    }

    // ------------------------------------------------------------------
    // Icones desenhados (sem arquivos de imagem)
    // ------------------------------------------------------------------
    private enum Icone {
        PESSOA, CALENDARIO, GRAFICO, SAIR;

        void desenhar(Graphics2D g, int x, int y, Color cor) {
            g.setColor(cor);
            g.setStroke(new BasicStroke(1.7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            switch (this) {
                case PESSOA:
                    g.fill(new Ellipse2D.Float(x + 5.5f, y + 1, 7, 7));
                    g.fill(new Arc2D.Float(x + 1.5f, y + 10, 15, 14, 0, 180, Arc2D.CHORD));
                    break;
                case CALENDARIO:
                    g.draw(new RoundRectangle2D.Float(x + 1, y + 3, 16, 14, 4, 4));
                    g.drawLine(x + 1, y + 8, x + 17, y + 8);
                    g.drawLine(x + 5, y + 1, x + 5, y + 5);
                    g.drawLine(x + 13, y + 1, x + 13, y + 5);
                    break;
                case GRAFICO:
                    g.fillRoundRect(x + 2, y + 9, 4, 8, 2, 2);
                    g.fillRoundRect(x + 7, y + 4, 4, 13, 2, 2);
                    g.fillRoundRect(x + 12, y + 1, 4, 16, 2, 2);
                    break;
                default: // SAIR
                    Path2D porta = new Path2D.Float();
                    porta.moveTo(x + 8, y + 2);
                    porta.lineTo(x + 3, y + 2);
                    porta.lineTo(x + 3, y + 16);
                    porta.lineTo(x + 8, y + 16);
                    g.draw(porta);
                    g.drawLine(x + 7, y + 9, x + 16, y + 9);
                    g.drawLine(x + 12, y + 5, x + 16, y + 9);
                    g.drawLine(x + 16, y + 9, x + 12, y + 13);
                    break;
            }
        }
    }

    // ------------------------------------------------------------------
    // Linha de menu (cabecalho de grupo, subitem ou "Sair")
    // ------------------------------------------------------------------
    private static class LinhaMenu extends JComponent {
        private final String texto;
        private final Icone icone;
        private final boolean chevron;
        private final boolean subitem;
        private boolean selecionado;
        private boolean hover;
        private boolean aberto = true;

        LinhaMenu(String texto, Icone icone, boolean chevron, boolean subitem, Runnable acao) {
            this.texto = texto;
            this.icone = icone;
            this.chevron = chevron;
            this.subitem = subitem;
            int altura = subitem ? 44 : 50;
            setPreferredSize(new Dimension(LARGURA_MENU, altura));
            setMinimumSize(new Dimension(LARGURA_MENU, altura));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, altura));
            setAlignmentX(LEFT_ALIGNMENT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hover = false; repaint(); }
                @Override public void mouseReleased(MouseEvent e) {
                    if (contains(e.getPoint())) {
                        acao.run();
                    }
                }
            });
        }

        void setSelecionado(boolean s) { selecionado = s; repaint(); }
        void setAberto(boolean a) { aberto = a; repaint(); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suave(g);
            int w = getWidth();
            int h = getHeight();

            if (subitem) {
                if (selecionado || hover) {
                    g2.setColor(selecionado ? Tema.CREME : Tema.VINHO_HOVER);
                    // canto direito fica fora da area: o item "emenda" no conteudo
                    g2.fillRoundRect(18, 2, w - 18 + 40, h - 4, 26, 26);
                }
                g2.setFont(Tema.fonte(selecionado ? Font.BOLD : Font.PLAIN, 14));
                g2.setColor(selecionado ? Tema.VINHO_ESCURO : Tema.CREME);
                desenharTexto(g2, 52, h);
            } else {
                if (hover) {
                    g2.setColor(new Color(255, 255, 255, 20));
                    g2.fillRect(0, 0, w, h);
                }
                if (icone != null) {
                    icone.desenhar(g2, 22, (h - 18) / 2, Tema.CREME);
                }
                g2.setFont(Tema.fonte(Font.BOLD, 14));
                g2.setColor(Tema.CREME);
                desenharTexto(g2, 54, h);

                if (chevron) {
                    g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    int cx = w - 30;
                    int cy = h / 2;
                    if (aberto) {   // aberto: seta para cima
                        g2.drawLine(cx - 5, cy + 2, cx, cy - 3);
                        g2.drawLine(cx, cy - 3, cx + 5, cy + 2);
                    } else {        // fechado: seta para baixo
                        g2.drawLine(cx - 5, cy - 2, cx, cy + 3);
                        g2.drawLine(cx, cy + 3, cx + 5, cy - 2);
                    }
                }
            }
            g2.dispose();
        }

        private void desenharTexto(Graphics2D g2, int x, int h) {
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(texto, x, (h + fm.getAscent() - fm.getDescent()) / 2);
        }
    }

    // ------------------------------------------------------------------
    // Logotipo
    // ------------------------------------------------------------------
    private static class Logo extends JPanel {
        Logo() {
            setOpaque(false);
            setPreferredSize(new Dimension(LARGURA_MENU, 68));
            setMinimumSize(new Dimension(LARGURA_MENU, 68));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
            setAlignmentX(LEFT_ALIGNMENT);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = Tema.suave(g);
            int w = getWidth();

            g2.setColor(Tema.CREME);
            g2.fillRoundRect(16, 0, w - 32, 68, 22, 22);

            // tesoura
            int sx = 34;
            int sy = 20;
            g2.setColor(Tema.VINHO);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(sx + 20, sy + 2, sx + 9, sy + 19);
            g2.drawLine(sx + 8, sy + 2, sx + 19, sy + 19);
            g2.setStroke(new BasicStroke(1.9f));
            g2.drawOval(sx + 2, sy + 18, 9, 9);
            g2.drawOval(sx + 17, sy + 18, 9, 9);

            g2.setColor(Tema.VINHO_ESCURO);
            g2.setFont(Tema.fonte(Font.BOLD, 17));
            g2.drawString("Cabeleireiro", 78, 31);
            g2.setColor(Tema.TEXTO_SUAVE);
            g2.setFont(Tema.fonte(Font.PLAIN, 12));
            g2.drawString("Gestão do salão", 78, 49);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Tema.instalar();
            new Tela_Principal().setVisible(true);
        });
    }
}
