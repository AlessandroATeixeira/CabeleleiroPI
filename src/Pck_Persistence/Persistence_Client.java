package Pck_Persistence;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Pck_DAO.Conexao;
import Pck_Model.Model_Client;

public class Persistence_Client {
    CallableStatement oCall;
    Conexao oConectar = new Conexao();
    PreparedStatement oPrepared;
    ResultSet oResultSet;
    private static final String login = "root";
    private static final String senha = "1234";
    public void PersInsClient(Model_Client Model_Client ) {
        try {
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_ins_cliente(?, ?, ?)");
                oCall.setInt(1,Model_Client.getCodigo());
                oCall.setString(2,Model_Client.getNome());
                oCall.setString(3,Model_Client.getTelefone());

                oCall.execute();
            } catch (SQLException e) {e.printStackTrace();}
        } finally {oConectar.desconectar();}

    }

    public void PersUpdClient(Model_Client Model_Client ) {
        try {
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_upd_cliente(?, ?, ?)");
                oCall.setInt(1,Model_Client.getCodigo());
                oCall.setString(2,Model_Client.getNome());
                oCall.setString(3,Model_Client.getTelefone());

                oCall.execute();
            } catch (SQLException e) {e.printStackTrace();}
        } finally {oConectar.desconectar();}

    }

    public void PersExcClient(int codigo ) {
        try {
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_del_cliente(?)");
                oCall.setInt(1,codigo);
                oCall.execute();
                oCall.close();
            } catch (SQLException e) {e.printStackTrace();}
        } finally {oConectar.desconectar();}

    }

    // Fazer consulta de todos clientes
    public ArrayList<Model_Client> listClient() throws SQLException {
        ArrayList<Model_Client> lista=new ArrayList<>();
        oCall=oConectar.getConexao(login,senha).prepareCall("{CALL proc_selc_clientes()}");
        oResultSet=oCall.executeQuery();
        while(oResultSet.next()){
            Model_Client objClient = new Model_Client();
            objClient.setCodigo(oResultSet.getInt("cod_cliente"));
            objClient.setNome(oResultSet.getString("c_nome"));
            objClient.setTelefone(oResultSet.getString("c_telefone"));
            lista.add(objClient);
        }
        oResultSet.close();
        oCall.close();
        return lista;
    }
}
