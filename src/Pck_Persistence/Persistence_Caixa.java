package Pck_Persistence;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import Pck_DAO.Conexao;
import Pck_Model.Model_Caixa;

public class Persistence_Caixa {

    CallableStatement oCall;
    Conexao oConectar = new Conexao();
    ResultSet oResultSet;
    private static final String login = "root";
    private static final String senha = "1234";

    /** Grava a movimentacao; para PRODUTO a procedure tambem ajusta o estoque (mesma transacao). */
    public void PersInsCaixa(Model_Caixa m) throws SQLException {
        try {
            oCall = oConectar.getConexao(login, senha).prepareCall("CALL proc_ins_caixa(?, ?, ?, ?, ?, ?)");
            oCall.setString(1, m.getTipo());
            oCall.setString(2, m.getOrigem());
            if (m.getCodProduto() == null) {
                oCall.setNull(3, Types.INTEGER);
            } else {
                oCall.setInt(3, m.getCodProduto());
            }
            if (m.getQuantidade() == null) {
                oCall.setNull(4, Types.INTEGER);
            } else {
                oCall.setInt(4, m.getQuantidade());
            }
            oCall.setString(5, m.getDescricao());
            oCall.setBigDecimal(6, BigDecimal.valueOf(m.getValor()).setScale(2, RoundingMode.HALF_UP));
            oCall.execute();
            oCall.close();
        } finally {
            oConectar.desconectar();
        }
    }

    public ArrayList<Model_Caixa> listCaixa() throws SQLException {
        ArrayList<Model_Caixa> lista = new ArrayList<>();
        try {
            oCall = oConectar.getConexao(login, senha).prepareCall("{CALL proc_selc_caixa()}");
            oResultSet = oCall.executeQuery();
            while (oResultSet.next()) {
                Model_Caixa m = new Model_Caixa();
                m.setCodigo(oResultSet.getInt("cod_mov"));
                Timestamp ts = oResultSet.getTimestamp("data_hora");
                m.setDataHora(ts == null ? null : ts.toLocalDateTime());
                m.setTipo(oResultSet.getString("tipo"));
                m.setOrigem(oResultSet.getString("origem"));
                int cod = oResultSet.getInt("cod_produto");
                m.setCodProduto(oResultSet.wasNull() ? null : cod);
                m.setNomeProduto(oResultSet.getString("nome_prod"));
                int qtd = oResultSet.getInt("quantidade");
                m.setQuantidade(oResultSet.wasNull() ? null : qtd);
                m.setDescricao(oResultSet.getString("descricao"));
                m.setValor(oResultSet.getDouble("valor"));
                lista.add(m);
            }
            oResultSet.close();
            oCall.close();
        } finally {
            oConectar.desconectar();
        }
        return lista;
    }
}
