package Pck_Control;

import java.sql.SQLException;
import java.util.ArrayList;

import Pck_Model.Model_Service;
import Pck_Persistence.Persistence_Service;

public class Control_Service {
    private final Persistence_Service persistence = new Persistence_Service();

    public void inserir(String nome, String valor) throws Exception {
        validarNome(nome);
        Model_Service servico = criarServico(nome, valor);
        persistence.inserir(servico);
    }

    public void atualizar(String id, String nome, String valor) throws Exception {
        int codigo = converterId(id);
        validarNome(nome);
        Model_Service servico = criarServico(nome, valor);
        servico.setIdServico(codigo);
        persistence.atualizar(servico);
    }

    public void excluir(String id) throws Exception {
        persistence.excluir(converterId(id));
    }

    public ArrayList<Model_Service> consultar() throws SQLException {
        return persistence.listar();
    }

    private Model_Service criarServico(String nome, String valor) {
        double preco;
        try {
            preco = Double.parseDouble(valor.trim().replace(".", "").replace(",", "."));
        } catch (Exception e) {
            throw new IllegalArgumentException("Informe um valor válido para o serviço.");
        }
        if (!Double.isFinite(preco) || preco <= 0) {
            throw new IllegalArgumentException("O valor do serviço deve ser maior que zero.");
        }
        Model_Service servico = new Model_Service();
        servico.setNomeServico(nome.trim());
        servico.setValorServico(preco);
        return servico;
    }

    private int converterId(String id) {
        try {
            int codigo = Integer.parseInt(id.trim());
            if (codigo <= 0) throw new IllegalArgumentException();
            return codigo;
        } catch (Exception e) {
            throw new IllegalArgumentException("Selecione um serviço válido.");
        }
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
