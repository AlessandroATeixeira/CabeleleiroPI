import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class MainScreen extends JFrame {

    private JButton btnClientes;
    private JButton btnProdutos;
    private JButton btnCaixa;
    private JButton btnRelatorios;
    private JButton btnSair;
    private  JButton btnAgendamento;

    public MainScreen() {
        setTitle("Sistema do Cabeleireiro");
        setSize(600, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(20, 20));

        criarComponentes();
        criarEventos();

        setVisible(true);
    }

    private void criarComponentes() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(20, 20));
        painelPrincipal.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 20, 30)
        );

        JLabel lblTitulo = new JLabel(
                "Gerenciamento Cabeleleiro",
                SwingConstants.CENTER
        );
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(39, 83, 130));
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelBotoes = new JPanel(new GridLayout(5, 1, 15, 20));
        painelBotoes.setBorder(
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        );

        btnClientes = criarBotao("Clientes");
        btnProdutos = criarBotao("Produtos");
        btnCaixa = criarBotao("Caixa");
        btnRelatorios = criarBotao("Relatórios");
        btnSair = criarBotaoSair("Sair");
        btnAgendamento = criarBotaoSair("Agendamento");

        painelBotoes.add(btnClientes);
        painelBotoes.add(btnProdutos);
        painelBotoes.add(btnCaixa);
        painelBotoes.add(btnRelatorios);
        painelBotoes.add(btnAgendamento);

        painelPrincipal.add(painelBotoes, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelRodape.add(btnSair);

        painelPrincipal.add(painelRodape, BorderLayout.SOUTH);

        add(painelPrincipal);
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 18));
        botao.setFocusPainted(false);
        botao.setBackground(new Color(53, 105, 168));
        botao.setForeground(Color.WHITE);
        botao.setPreferredSize(new Dimension(250, 55));

        return botao;
    }

    private JButton criarBotaoSair(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setFocusPainted(false);
        botao.setBackground(new Color(185, 47, 47));
        botao.setForeground(Color.WHITE);
        botao.setPreferredSize(new Dimension(120, 40));
        return botao;
    }

    private JButton criarBotaAgendamento(String texto) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("Arial", Font.BOLD, 14));
        botao.setFocusPainted(false);
        botao.setBackground(new Color(185, 47, 47));
        botao.setForeground(Color.WHITE);
        botao.setPreferredSize(new Dimension(120, 40));
        return botao;
    }



    private void criarEventos() {
        btnClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTela(new Screen());
            }
        });

        btnProdutos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTela(new ProductScreen());
            }
        });

        btnCaixa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTela(new CashManagementScreen());
            }
        });

        btnRelatorios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTela(new CashReportScreen());
            }
        });

        btnAgendamento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirTela(new Agendamento());
            }
        });

        btnSair.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opcao = JOptionPane.showConfirmDialog(
                        MainScreen.this,
                        "Deseja realmente sair?",
                        "Confirmar saída",
                        JOptionPane.YES_NO_OPTION
                );

                if (opcao == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    private void abrirTela(JFrame tela) {
        tela.setLocationRelativeTo(this);
        tela.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainScreen();
            }
        });
    }
}