package Pck_Persistence;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Pck_DAO.Conexao;
import Pck_Model.Model_Product;


public class Persistence_Product {
    CallableStatement oCall;
    Conexao oConectar = new Conexao();
    PreparedStatement oPrepared;
    ResultSet oResultSet;
    private static final String login = "root";
    private static final String senha = "1234";

    public void PersInsProduct(Model_Product Model_Product){
        try {
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_ins_produto(?, ?, ?, ?)");
                oCall.setInt(1, Model_Product.getCodigo());
                oCall.setString(2, Model_Product.getNome());
                oCall.setDouble(3, Model_Product.getValorProd());
                oCall.setInt(4, Model_Product.getEstoque());
            }catch (SQLException e){e.printStackTrace();}
        }finally {oConectar.desconectar();}
    }

    public void PersUpdProduct(Model_Product Model_Product){
        try{
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_upd_produto(?, ?, ?, ?)");
                oCall.setInt(1, Model_Product.getCodigo());
                oCall.setString(2, Model_Product.getNome());
                oCall.setDouble(3, Model_Product.getValorProd());
                oCall.setInt(4, Model_Product.getEstoque());
            }catch (SQLException e){e.printStackTrace();}
        }finally {oConectar.desconectar();}
    }

    public void PersExcProduct(int codigo){
        try {
            try {
                oCall = oConectar.getConexao(login,senha).prepareCall("CALL proc_del_produto(?)");
                oCall.setInt(1,codigo);
                oCall.execute();
                oCall.close();
            } catch (SQLException e) {e.printStackTrace();}
        } finally {oConectar.desconectar();}
    }

    public ArrayList<Model_Product> listProduct() throws SQLException {
        ArrayList<Model_Product> lista=new ArrayList<>();
        oCall=oConectar.getConexao(login,senha).prepareCall("{CALL proc_selc_produtos()}");
        oResultSet=oCall.executeQuery();
        while(oResultSet.next()){
            Model_Product objProduct = new Model_Product();
            objProduct.setCodigo(oResultSet.getInt("cod_produto"));
            objProduct.setNome(oResultSet.getString("nome_prod"));
            objProduct.setValorProd(oResultSet.getDouble("valor_prod"));
            objProduct.setEstoque(oResultSet.getInt("estoque"));
            lista.add(objProduct);
        }
        oResultSet.close();
        oCall.close();
        return lista;
    }




}
