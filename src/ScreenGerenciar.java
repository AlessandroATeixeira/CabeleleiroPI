import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class ScreenGerenciar extends JFrame {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane barraRolagem;

    public ScreenGerenciar() {
        setTitle("Gerenciar Registros");
        setSize(700, 450);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        modelo = new DefaultTableModel(new Object[] { "Código", "Nome", "Alterar", "Remover" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Nome editável + colunas de botão editáveis (para receber clique)
                return column == 1 || column == 2 || column == 3;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return String.class;
            }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(28);

        tabela.getColumn("Alterar").setCellRenderer(new ButtonRenderer("Alterar"));
        tabela.getColumn("Alterar").setCellEditor(new ButtonEditor(new JTextField(), "Alterar"));

        tabela.getColumn("Remover").setCellRenderer(new ButtonRenderer("Remover"));
        tabela.getColumn("Remover").setCellEditor(new ButtonEditor(new JTextField(), "Remover"));

        barraRolagem = new JScrollPane(tabela);
        barraRolagem.setBounds(20, 20, 650, 370);
        add(barraRolagem);

        carregarDados();
        setVisible(true);
    }

    private void carregarDados() {
        try {
            Connection conexao = Conexao.conectar();
            CallableStatement comando = conexao.prepareCall("{CALL PROC_SEL_AGENDOCA_02()}");
            ResultSet resultado = comando.executeQuery();

            modelo.setRowCount(0);

            while (resultado.next()) {
                int codigo = resultado.getInt("codigo");
                String nome = resultado.getString("nome");
                modelo.addRow(new Object[] { codigo, nome, "Alterar", "Remover" });
            }

            resultado.close();
            comando.close();
            conexao.close();
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Erro ao consultar: " + erro.getMessage());
        }
    }

    private void alterarRegistro(int row) {
        try {
            int codigo = (Integer) modelo.getValueAt(row, 0);
            String nome = String.valueOf(modelo.getValueAt(row, 1));

            Connection conexao = Conexao.conectar();
            CallableStatement comando = conexao.prepareCall("{CALL PROC_UPD_AGENDOCA_03(?, ?)}");
            comando.setInt(1, codigo);
            comando.setString(2, nome);

            int linhasAfetadas = comando.executeUpdate();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Registro alterado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum registro encontrado para alterar.");
            }

            comando.close();
            conexao.close();
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Erro ao alterar: " + erro.getMessage());
        }
    }

    private void removerRegistro(int row) {
        try {
            int codigo = (Integer) modelo.getValueAt(row, 0);

            int confirma = JOptionPane.showConfirmDialog(
                    null,
                    "Deseja realmente remover o registro " + codigo + "?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirma != JOptionPane.YES_OPTION) return;

            Connection conexao = Conexao.conectar();
            CallableStatement comando = conexao.prepareCall("{CALL PROC_DEL_AGENDOCA_04(?)}");
            comando.setInt(1, codigo);

            int linhasAfetadas = comando.executeUpdate();

            if (linhasAfetadas > 0) {
                JOptionPane.showMessageDialog(null, "Registro removido com sucesso!");
                carregarDados();
            } else {
                JOptionPane.showMessageDialog(null, "Nenhum registro encontrado para remover.");
            }

            comando.close();
            conexao.close();
        } catch (SQLException erro) {
            JOptionPane.showMessageDialog(null, "Erro ao remover: " + erro.getMessage());
        }
    }

    // Renderiza o botão dentro da célula
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String texto) {
            setText(texto);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // Editor que captura o clique do botão na célula
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton botao;
        private String rotulo;
        private int linhaAtual;

        public ButtonEditor(JTextField campo, final String rotulo) {
            this.rotulo = rotulo;
            botao = new JButton(rotulo);

            botao.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    if ("Alterar".equals(rotulo)) {
                        alterarRegistro(linhaAtual);
                    } else if ("Remover".equals(rotulo)) {
                        removerRegistro(linhaAtual);
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            linhaAtual = row;
            botao.setText(rotulo);
            return botao;
        }

        @Override
        public Object getCellEditorValue() {
            return rotulo;
        }
    }
}
