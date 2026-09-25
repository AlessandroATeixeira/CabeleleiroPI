import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Agendamento extends JFrame {
    private JLabel lblServico;
    private JLabel lblHorario;
    private JLabel lblData;

    private JTextField txtServico;
    private JTextField txtHorario;
    private JTextField txtData;

    JButton BtnAgendar;

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane barraRolagem;

    public Agendamento() {

        setTitle("Agendamento");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        criarComponentes();

        setVisible(true);
    }

    private void criarComponentes() {

        lblServico = new JLabel("Serviço:");
        lblServico.setBounds(30, 30, 120, 25);
        add(lblServico);

        txtServico = new JTextField();
        txtServico.setBounds(110, 30, 200, 30);
        add(txtServico);

        lblHorario = new JLabel("Horário:");
        lblHorario.setBounds(30, 70, 120, 25);
        add(lblHorario);

        txtHorario = new JTextField();
        txtHorario.setBounds(110, 70, 200, 30);
        add(txtHorario);

        lblData= new JLabel("Data:");
        lblData.setBounds(30, 120, 120, 25);
        add(lblData);

        txtData = new JTextField();
        txtData.setBounds(110, 120, 200, 30);
        add(txtData);

        BtnAgendar = new JButton("Agendar:");
        BtnAgendar.setBounds(30, 160, 120, 30);
        add(BtnAgendar);



        modelo = new DefaultTableModel();
        modelo.addColumn("Data");
        modelo.addColumn("Horário");
        modelo.addColumn("Serviço");

        tabela = new JTable(modelo);

        barraRolagem = new JScrollPane(tabela);
        barraRolagem.setBounds(30, 200, 600, 230);
        add(barraRolagem);
    }

}






