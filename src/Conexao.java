import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public static Connection conectar() throws SQLException {

        String url = "jdbc:mysql://127.0.0.1/bd_cabeleireiro";
        String usuario = "root";
        String senha = "";

        Connection conexao =
                DriverManager.getConnection(url, usuario, senha);

        return conexao;
    }
} 