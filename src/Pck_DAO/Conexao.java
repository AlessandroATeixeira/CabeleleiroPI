package Pck_DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private  String sDriver;
    private  String sServidor;
    private  String sBase;

    private  String connStr;

    static Connection conn;

    private static final String URL = "jdbc:mysql://127.0.0.1/bd_cabeleireiro";
    private static final String login = "root";
    private static final String senha = "1234";


    public Conexao()
    {
        this.sDriver 	= "com.mysql.cj.jdbc.Driver";
        this.sServidor = "localhost";
        this.connStr    = "jdbc:mysql://127.0.0.1/bd_cabeleireiro";
    }

    public Connection getConexao(String login, String senha) throws SQLException {
        conn = null;
        try {
            Class.forName(sDriver);
            conn = DriverManager.getConnection(connStr, login, senha);
            System.out.println("Conectado");
            return conn;
        }

        catch (ClassNotFoundException e) {
            String errorMsg = "Driver nao encontrado";
            throw new SQLException(errorMsg, e);
        }
        catch (SQLException e) {
            String errorMsg = "Erro ao obter a conexao";
            throw new SQLException(errorMsg, e);
        }
    }

    public void desconectar() {
        try {
            if (conn != null) {
                conn.close();
                System.out.print("Desconectado passou aqui ");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }





}