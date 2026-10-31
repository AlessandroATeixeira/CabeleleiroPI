package Pck_Control;

import Pck_Model.Model_Scheduling;
import Pck_Persistence.Persistence_Scheduling;
import Pck_View.Screen_Scheduling;

import java.util.ArrayList;


public class Control_Scheduling {

    Persistence_Scheduling oPers = new Persistence_Scheduling();

    public void Inserir(String codigo, String servico, String data, String hora ) throws Exception{
        Model_Scheduling objScheduling = new Model_Scheduling();
        objScheduling.setCodigo(Integer.parseInt(codigo));
        objScheduling.setServico(Screen_Scheduling.cbServico.getSelectedItem().toString());
        objScheduling.setData(data);
        objScheduling.setHorario(hora);
        oPers.PersInsScheduling(objScheduling);
    }
    public void Atualizar(String codigo, String servico, String data, String hora ) throws Exception{
        Model_Scheduling objScheduling = new Model_Scheduling();
        objScheduling.setCodigo(Integer.parseInt(codigo));
        objScheduling.setServico(servico);
        objScheduling.setData(data);
        objScheduling.setHorario(hora);
        oPers.PersInsScheduling(objScheduling);
    }

    public void Excluir(String codigo) throws Exception{
        oPers.PersExcScheduling(Integer.parseInt(codigo));

    }

    public ArrayList<Model_Scheduling> Consultar() throws Exception {
        return oPers.listScheduling();
    }


}
