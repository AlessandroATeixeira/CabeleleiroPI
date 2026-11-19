package Pck_Persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import Pck_DAO.Conexao;
import Pck_Model.Model_Caixa;

public class Persistence_Caixa {
    private static final String LOGIN = "root";
    private static final String SENHA = "1234";

    public void PersInsCaixa(Model_Caixa movimento) throws SQLException {
        if (movimento == null) {
            throw new IllegalArgumentException("A movimentação não foi informada.");
        }

        String sql = "{CALL proc_ins_caixa(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql)) {
            call.setString(1, movimento.getTipo());
            call.setString(2, movimento.getOrigem());

            if (movimento.getCodProduto() == null) call.setNull(3, Types.INTEGER);
            else call.setInt(3, movimento.getCodProduto());

            if (movimento.getCodServico() == null) call.setNull(4, Types.INTEGER);
            else call.setInt(4, movimento.getCodServico());

            if (movimento.getQuantidade() == null) call.setNull(5, Types.INTEGER);
            else call.setInt(5, movimento.getQuantidade());

            call.setString(6, movimento.getDescricao());
            call.setBigDecimal(7, BigDecimal.valueOf(movimento.getValorB())
                    .setScale(2, RoundingMode.HALF_UP));
            if (movimento.getCodAgendamento() == null) call.setNull(8, Types.INTEGER);
            else call.setInt(8, movimento.getCodAgendamento());
            call.execute();
        } catch (SQLException e) {
            throw new SQLException("Não foi possível registrar a movimentação no caixa: " + e.getMessage(), e);
        }
    }

    public ArrayList<Model_Caixa> listCaixa() throws SQLException {
        ArrayList<Model_Caixa> lista = new ArrayList<>();
        String sql = "{CALL proc_selc_caixa()}";

        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql);
             ResultSet rs = call.executeQuery()) {
            while (rs.next()) {
                Model_Caixa movimento = new Model_Caixa();
                movimento.setCodigo(rs.getInt("cod_mov"));

                Timestamp dataHora = rs.getTimestamp("data_hora");
                movimento.setDataHora(dataHora == null ? null : dataHora.toLocalDateTime());

                movimento.setTipo(rs.getString("tipo"));
                movimento.setOrigem(rs.getString("origem"));

                int codProduto = rs.getInt("cod_produto");
                movimento.setCodProduto(rs.wasNull() ? null : codProduto);
                movimento.setNomeProduto(rs.getString("nome_prod"));

                int idServico = rs.getInt("id_servico");
                movimento.setCodServico(rs.wasNull() ? null : idServico);
                movimento.setNomeServico(rs.getString("nm_servico"));
                int codAgendamento = rs.getInt("cod_agendamento");
                movimento.setCodAgendamento(rs.wasNull() ? null : codAgendamento);
                movimento.setNomeCliente(rs.getString("nome_cliente"));

                int quantidade = rs.getInt("quantidade");
                movimento.setQuantidade(rs.wasNull() ? null : quantidade);

                movimento.setDescricao(rs.getString("descricao"));
                movimento.setValor(rs.getDouble("valor"));
                lista.add(movimento);
            }
        } catch (SQLException e) {
            throw new SQLException("Não foi possível consultar as movimentações do caixa: " + e.getMessage(), e);
        }
        return lista;
    }
}
