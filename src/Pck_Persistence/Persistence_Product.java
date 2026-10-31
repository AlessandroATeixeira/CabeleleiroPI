package Pck_Persistence;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import Pck_DAO.Conexao;
import Pck_Model.Model_Client;


public class Persistence_Product {
    CallableStatement oCall;
    Conexao oConectar = new Conexao();
    PreparedStatement oPrepared;
    ResultSet oResultSet;
    private static final String login = "root";
    private static final String senha = "1234";


}
