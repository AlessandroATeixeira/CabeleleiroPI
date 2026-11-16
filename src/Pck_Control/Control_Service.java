package Pck_Control;

import java.sql.SQLException;
import java.util.ArrayList;

import Pck_Model.Model_Service;
import Pck_Persistence.Persistence_Service;

public class Control_Service {
    private final Persistence_Service persistence = new Persistence_Service();

    public void inserir(String nome) throws Exception {
        validarNome(nome);
        Model_Service servico = new Model_Service();
        servico.setNomeServico(nome.trim());
        persistence.inserir(servico);
    }

    public void atualizar(String id, String nome) throws Exception {
        int codigo = converterId(id);
        validarNome(nome);
        Model_Service servico = new Model_Service();
        servico.setIdServico(codigo);
        servico.setNomeServico(nome.trim());
        persistence.atualizar(servico);
    }

    public void excluir(String id) throws Exception {
        persistence.excluir(converterId(id));
    }

    public ArrayList<Model_Service> consultar() throws SQLException {
        return persistence.listar();
    }

    private int converterId(String id) {
        int codigo = Integer.parseInt(id.trim());
        if (codigo <= 0) {
            throw new IllegalArgumentException("O código do serviço deve ser maior que zero.");
        }
        return codigo;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Informe o nome do serviço.");
        }
        if (nome.trim().length() > 100) {
            throw new IllegalArgumentException("O nome do serviço deve ter no máximo 100 caracteres.");
        }
    }
}
