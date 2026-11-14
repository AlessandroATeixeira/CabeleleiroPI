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

        for(Model_Client client : oPers.listClient()){
            if(client.getCodigo() == codigoInt){
                throw new Exception("Ja existe um cliente com esse codigo");
            }
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