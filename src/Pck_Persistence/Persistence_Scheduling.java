package Pck_Persistence;

import Pck_DAO.Conexao;
import Pck_Model.Model_Scheduling;

import java.sql.*;
import java.util.ArrayList;

public class Persistence_Scheduling {
    CallableStatement oCall;
    Conexao oConectar = new Conexao();
    PreparedStatement oPrepared;
    ResultSet oResultSet;
    private static final String login = "root";
    private static final String senha = "1234";
    public void PersInsScheduling(Model_Scheduling Model_Scheduling){
        try {
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_ins_agendamento(?, ?, ?, ?)");
                oCall.setInt(1, Model_Scheduling.getCodigo());
                oCall.setString(2, Model_Scheduling.getServico());
                oCall.setDate(3, Date.valueOf(Model_Scheduling.getData()));
                oCall.setTime(4, Time.valueOf(Model_Scheduling.getHorario()));

            }catch (SQLException e) {e.printStackTrace();}
        }finally { oConectar.desconectar();}
    }

    public void PersUpdScheduling(Model_Scheduling Model_Scheduling){
        try {
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_upd_agendamento(?, ?, ?, ?)");
                oCall.setInt(1, Model_Scheduling.getCodigo());
                oCall.setString(2, Model_Scheduling.getServico());
                oCall.setDate(3, Date.valueOf(Model_Scheduling.getData()));
                oCall.setTime(4, Time.valueOf(Model_Scheduling.getHorario()));

            }catch (SQLException e) {e.printStackTrace();}
        }finally { oConectar.desconectar();}
    }

    public void PersExcScheduling(int codigo){
        try {
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_del_agendamento(?)");
                oCall.setInt(1,codigo);
                oCall.execute();
                oCall.close();
            } catch (SQLException e) {e.printStackTrace();}
        } finally {oConectar.desconectar();}
    }

    public ArrayList<Model_Scheduling> listScheduling() throws SQLException {
        ArrayList<Model_Scheduling> lista=new ArrayList<>();
        oCall=oConectar.getConexao(login,senha).prepareCall("{CALL proc_selc_clientes()}");
        oResultSet=oCall.executeQuery();
        while(oResultSet.next()){
            Model_Scheduling objScheduling = new Model_Scheduling();
            objScheduling.setCodigo(oResultSet.getInt("cod_agendamento"));
            objScheduling.setServico(oResultSet.getString("servico"));
            objScheduling.setData(oResultSet.getString("data_agendamento"));
            objScheduling.setHorario(oResultSet.getString("horario"));
            lista.add(objScheduling);
        }
        oResultSet.close();
        oCall.close();
        return lista;
    }
}
