package Pck_Persistence;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Pck_DAO.Conexao;
import Pck_Model.Model_Service;

public class Persistence_Service {
    private static final String LOGIN = "root";
    private static final String SENHA = "1234";
    private final Conexao conexao = new Conexao();

    public void inserir(Model_Service servico) throws SQLException {
        try (CallableStatement call = conexao.getConexao(LOGIN, SENHA)
                .prepareCall("{CALL proc_ins_servico(?, ?)}")) {
            call.setString(1, servico.getNomeServico());
            call.setBigDecimal(2, java.math.BigDecimal.valueOf(servico.getValorServico()));
            call.execute();
        } finally {
            conexao.desconectar();
        }
    }

    public void atualizar(Model_Service servico) throws SQLException {
        try (CallableStatement call = conexao.getConexao(LOGIN, SENHA)
                .prepareCall("{CALL proc_upd_servico(?, ?, ?)}")) {
            call.setInt(1, servico.getIdServico());
            call.setString(2, servico.getNomeServico());
            call.setBigDecimal(3, java.math.BigDecimal.valueOf(servico.getValorServico()));
            call.execute();
        } finally {
            conexao.desconectar();
        }
    }

    public void excluir(int id) throws SQLException {
        try (CallableStatement call = conexao.getConexao(LOGIN, SENHA)
                .prepareCall("{CALL proc_del_servico(?)}")) {
            call.setInt(1, id);
            call.execute();
        } finally {
            conexao.desconectar();
        }
    }

    public ArrayList<Model_Service> listar() throws SQLException {
        ArrayList<Model_Service> lista = new ArrayList<>();
        try (CallableStatement call = conexao.getConexao(LOGIN, SENHA)
                .prepareCall("{CALL proc_selc_servico()}");
             ResultSet rs = call.executeQuery()) {
            while (rs.next()) {
                Model_Service servico = new Model_Service();
                servico.setIdServico(rs.getInt("id_servico"));
                servico.setNomeServico(rs.getString("nm_servico"));
                servico.setValorServico(rs.getDouble("vl_valor"));
                lista.add(servico);
            }
        } finally {
            conexao.desconectar();
        }
        return lista;
    }
}
