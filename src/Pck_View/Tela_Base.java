package Pck_View;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.FlowLayout;
import java.time.format.DateTimeParseException;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Base das telas de funcionalidade: cabecalho (titulo + subtitulo) e area de conteudo.
 * Cada tela e um painel que a Tela_Principal exibe ao lado do menu lateral.
 */
public abstract class Tela_Base extends JPanel {

    private final JPanel cabecalho = new JPanel(new BorderLayout(16, 0));

    protected Tela_Base(String titulo, String subtitulo) {
        super(new BorderLayout(0, 20));
        setOpaque(true);
        setBackground(Tema.CREME);
        setBorder(new EmptyBorder(28, 32, 24, 32));

        cabecalho.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Tema.fonte(Font.BOLD, 26));
        lblTitulo.setForeground(Tema.TEXTO);
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel(subtitulo);
        lblSub.setFont(Tema.fonte(Font.PLAIN, 13));
        lblSub.setForeground(Tema.TEXTO_SUAVE);
        lblSub.setBorder(new EmptyBorder(4, 0, 0, 0));
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.add(lblTitulo);
        textos.add(lblSub);

        cabecalho.add(textos, BorderLayout.WEST);
        add(cabecalho, BorderLayout.NORTH);
    }

    /** Controles opcionais no canto direito do cabecalho. */
    protected void setControlesCabecalho(JComponent controles) {
        cabecalho.add(controles, BorderLayout.EAST);
    }

    protected void setConteudo(JComponent conteudo) {
        add(conteudo, BorderLayout.CENTER);
    }

    /** Chamado sempre que a tela e exibida (ex.: recarregar a tabela do banco). */
    public void aoExibir() { }

    // ------------------------------------------------------------------
    // Mensagens
    // ------------------------------------------------------------------
    protected void mostrarErro(Exception e) {
        String msg;
        if (e instanceof NumberFormatException) {
            msg = "Verifique os campos numéricos: use apenas números (ex.: código 1, valor 35,50).";
        } else if (e instanceof DateTimeParseException) {
            msg = "Data ou horário inválido. Use dd/MM/aaaa e HH:mm (ex.: 05/11/2026 e 14:30).";
        } else if (e.getMessage() != null) {
            msg = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                msg += "\n" + e.getCause().getMessage();
            }
        } else {
            msg = e.getClass().getSimpleName();
        }
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    protected void mostrarInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Cabeleireiro", JOptionPane.INFORMATION_MESSAGE);
    }

    protected boolean confirmar(String msg) {
        return JOptionPane.showConfirmDialog(this, msg, "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
    }

    // ------------------------------------------------------------------
    // Montagem de formularios
    // ------------------------------------------------------------------
    /** Linha de campos lado a lado; "pesos" define a largura relativa de cada um. */
    protected static JPanel linhaCampos(double[] pesos, JComponent... campos) {
        JPanel linha = new JPanel(new GridBagLayout());
        linha.setOpaque(false);
        for (int i = 0; i < campos.length; i++) {
            GridBagConstraints c = new GridBagConstraints();
            c.gridx = i;
            c.weightx = pesos[i];
            c.fill = GridBagConstraints.HORIZONTAL;
            c.insets = new Insets(0, i == 0 ? 0 : 8, 0, i == campos.length - 1 ? 0 : 8);
            linha.add(campos[i], c);
        }
        return linha;
    }

    protected static JPanel linhaBotoes(JComponent... botoes) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        p.setOpaque(false);
        for (int i = 0; i < botoes.length; i++) {
            if (i > 0) {
                p.add(javax.swing.Box.createHorizontalStrut(10));
            }
            p.add(botoes[i]);
        }
        return p;
    }

    /** Cartao com titulo, usado para o formulario e para a tabela. */
    protected static Tema.Cartao cartaoComTitulo(String titulo, JComponent direita) {
        Tema.Cartao cartao = new Tema.Cartao(new BorderLayout(0, 16));
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(Tema.tituloCartao(titulo), BorderLayout.WEST);
        if (direita != null) {
            topo.add(direita, BorderLayout.EAST);
        }
        cartao.add(topo, BorderLayout.NORTH);
        return cartao;
    }

    protected static JLabel etiquetaInfo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Tema.fonte(Font.PLAIN, 12));
        l.setForeground(Tema.TEXTO_SUAVE);
        return l;
    }
}
