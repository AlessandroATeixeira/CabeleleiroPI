package Pck_Control;

import Pck_Model.Model_Client;
import Pck_Persistence.Persistence_Client;

import java.util.ArrayList;

public class Control_Client {
    Persistence_Client oPers = new Persistence_Client();

    public void Inserir(String codigo, String nome, String telefone) throws Exception {
        int codigoInt = Integer.parseInt(codigo);
        if (codigoInt <= 0) {
            throw new Exception("O código do cliente deve ser maior que zero.");
        }

        String apenasNumeros = telefone.replaceAll("\\D", "");
        if (!apenasNumeros.matches("\\d{11}")) {
            throw new Exception("Informe o telefone com DDD e 11 dígitos, por favor.");
        }

        Model_Client objClient = new Model_Client();
        objClient.setCodigo(codigoInt);
        objClient.setNome(nome);
        objClient.setTelefone(telefone);
        oPers.PersInsClient(objClient);
    }

    public void Atualizar(String codigo, String nome, String telefone) throws Exception {
        int codigoInt = Integer.parseInt(codigo);
        if (codigoInt <= 0) {
            throw new Exception("O código do cliente deve ser maior que zero.");
        }

        String apenasNumeros = telefone.replaceAll("\\D", "");
        if (!apenasNumeros.matches("\\d{11}")) {
            throw new Exception("Informe o telefone com DDD e 11 dígitos, por favor.");
        }

        Model_Client objClient = new Model_Client();
        objClient.setCodigo(codigoInt);
        objClient.setNome(nome);
        objClient.setTelefone(telefone);
        oPers.PersUpdClient(objClient);
    }

    public void Excluir(String codigo) throws Exception {
        int codigoInt = Integer.parseInt(codigo);
        if (codigoInt <= 0) {
            throw new Exception("O código a ser excluído deve ser maior que zero.");
        }
        oPers.PersExcClient(codigoInt);
    }

    public ArrayList<Model_Client> Consultar() throws Exception {
        return oPers.listClient();
    }
}