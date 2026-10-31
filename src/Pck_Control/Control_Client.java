package Pck_Control;

import Pck_Model.Model_Client;
import Pck_Persistence.Persistence_Client;

import java.util.ArrayList;

public class Control_Client {
    Persistence_Client oPers = new Persistence_Client();

    public void Inserir(String codigo, String nome, String telefone ) throws Exception{
        Model_Client objClient = new Model_Client();
        objClient.setCodigo(Integer.parseInt(codigo));
        objClient.setNome(nome);
        objClient.setTelefone(telefone);
        oPers.PersInsClient(objClient);
    }

    public void Atualizar(String codigo, String nome, String telefone ) throws Exception{
        Model_Client objClient = new Model_Client();
        objClient.setCodigo(Integer.parseInt(codigo));
        objClient.setNome(nome);
        objClient.setTelefone(telefone);
        oPers.PersUpdClient(objClient);
    }

    public void Excluir(String codigo) throws Exception{
        oPers.PersExcClient(Integer.parseInt(codigo));
    }

    public ArrayList<Model_Client> Consultar() throws Exception {
        return oPers.listClient();
    }


}
