package Pck_Persistence;

import Pck_DAO.Conexao;
import Pck_Model.Model_Scheduling;

import java.sql.*;
import java.util.ArrayList;

public class Persistence_Scheduling {
    private static final String LOGIN = "root";
    private static final String SENHA = "1234";

    public void PersInsScheduling(Model_Scheduling modelScheduling) throws SQLException {
        String sql = "{CALL proc_ins_agendamento(?, ?, ?, ?, ?)}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql)) {
            call.setInt(1, modelScheduling.getCodigo());
            call.setInt(2, modelScheduling.getCodCliente());
            call.setInt(3, modelScheduling.getIdServico());
            call.setDate(4, Date.valueOf(modelScheduling.getData()));
            call.setTime(5, Time.valueOf(modelScheduling.getHorario()));
            call.execute();
        }
    }

    public void PersUpdScheduling(Model_Scheduling modelScheduling) throws SQLException {
        String sql = "{CALL proc_upd_agendamento(?, ?, ?, ?, ?)}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql)) {
            call.setInt(1, modelScheduling.getCodigo());
            call.setInt(2, modelScheduling.getCodCliente());
            call.setInt(3, modelScheduling.getIdServico());
            call.setDate(4, Date.valueOf(modelScheduling.getData()));
            call.setTime(5, Time.valueOf(modelScheduling.getHorario()));
            call.execute();
        }
    }

    public void PersExcScheduling(int codigo) throws SQLException {
        String sql = "{CALL proc_del_agendamento(?)}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql)) {
            call.setInt(1, codigo);
            call.execute();
        }
    }

    public ArrayList<Model_Scheduling> listScheduling() throws SQLException {
        ArrayList<Model_Scheduling> lista = new ArrayList<>();
        String sql = "{CALL proc_selc_agendamentos()}";
        try (Connection conn = new Conexao().getConexao(LOGIN, SENHA);
             CallableStatement call = conn.prepareCall(sql);
             ResultSet rs = call.executeQuery()) {
            while (rs.next()) {
                Model_Scheduling obj = new Model_Scheduling();
                obj.setCodigo(rs.getInt("cod_agendamento"));
                obj.setCodCliente(rs.getInt("cod_cliente"));
                obj.setIdServico(rs.getInt("id_servico"));
                obj.setServico(rs.getString("servico"));
                obj.setData(rs.getString("data_agendamento"));
                obj.setHorario(rs.getString("horario"));
                lista.add(obj);
            }
        }
        return lista;
    }
}
