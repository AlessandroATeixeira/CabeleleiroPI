package Pck_Persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        String sql = "INSERT INTO servicos (nm_servico) VALUES (?)";
        try (Connection conn = conexao.getConexao(LOGIN, SENHA);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, servico.getNomeServico());
            stmt.executeUpdate();
        } finally {
            conexao.desconectar();
        }
    }

    public void atualizar(Model_Service servico) throws SQLException {
        String sql = "UPDATE servicos SET nm_servico = ? WHERE id_servico = ?";
        try (Connection conn = conexao.getConexao(LOGIN, SENHA);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, servico.getNomeServico());
            stmt.setInt(2, servico.getIdServico());
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Serviço não encontrado.");
            }
        } finally {
            conexao.desconectar();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM servicos WHERE id_servico = ?";
        try (Connection conn = conexao.getConexao(LOGIN, SENHA);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) {
                throw new SQLException("Serviço não encontrado.");
            }
        } finally {
            conexao.desconectar();
        }
    }

    public ArrayList<Model_Service> listar() throws SQLException {
        ArrayList<Model_Service> lista = new ArrayList<>();
        String sql = "SELECT id_servico, nm_servico FROM servicos ORDER BY id_servico";
        try (Connection conn = conexao.getConexao(LOGIN, SENHA);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Model_Service servico = new Model_Service();
                servico.setIdServico(rs.getInt("id_servico"));
                servico.setNomeServico(rs.getString("nm_servico"));
                lista.add(servico);
            }
        } finally {
            conexao.desconectar();
        }
        return lista;
    }
}
