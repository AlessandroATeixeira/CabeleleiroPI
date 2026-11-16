package Pck_Persistence;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Pck_DAO.Conexao;
import Pck_Model.Model_Service;

public class Persistence_Service {
    private static final String LOGIN = "root";
    private static final String SENHA = "1234";

    public void inserir(Model_Service servico) throws SQLException {
        String sql = "{CALL proc_ins_servico(?, ?)}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql)) {
            call.setString(1, servico.getNomeServico());
            call.setBigDecimal(2, BigDecimal.valueOf(servico.getValorServico()));
            call.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao cadastrar serviço: " + e.getMessage(), e);
        }
    }

    public void atualizar(Model_Service servico) throws SQLException {
        String sql = "{CALL proc_upd_servico(?, ?, ?)}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql)) {
            call.setInt(1, servico.getIdServico());
            call.setString(2, servico.getNomeServico());
            call.setBigDecimal(3, BigDecimal.valueOf(servico.getValorServico()));
            call.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao alterar serviço: " + e.getMessage(), e);
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "{CALL proc_del_servico(?)}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql)) {
            call.setInt(1, id);
            call.execute();
        } catch (SQLException e) {
            throw new SQLException("Erro ao excluir serviço: " + e.getMessage(), e);
        }
    }

    public ArrayList<Model_Service> listar() throws SQLException {
        ArrayList<Model_Service> lista = new ArrayList<>();
        String sql = "{CALL proc_selc_servico()}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql);
             ResultSet rs = call.executeQuery()) {
            while (rs.next()) {
                Model_Service servico = new Model_Service();
                servico.setIdServico(rs.getInt("id_servico"));
                servico.setNomeServico(rs.getString("nm_servico"));
                servico.setValorServico(rs.getDouble("vl_valor"));
                lista.add(servico);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao consultar serviços: " + e.getMessage(), e);
        }
        return lista;
    }
}
