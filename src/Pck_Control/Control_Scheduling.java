package Pck_Control;

import Pck_Model.Model_Scheduling;
import Pck_Persistence.Persistence_Scheduling;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Control_Scheduling {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FORMATO_HORA_BD = DateTimeFormatter.ofPattern("HH:mm:ss");

    Persistence_Scheduling oPers = new Persistence_Scheduling();

    public void Inserir(String codigo, String servico, String data, String hora) throws Exception {
        Model_Scheduling objScheduling = new Model_Scheduling();
        objScheduling.setCodigo(Integer.parseInt(codigo.trim()));
        objScheduling.setServico(servico == null ? "" : servico.trim());
        objScheduling.setData(LocalDate.parse(data.trim(), FORMATO_DATA).toString());
        objScheduling.setHorario(LocalTime.parse(hora.trim(), FORMATO_HORA).format(FORMATO_HORA_BD));
        oPers.PersInsScheduling(objScheduling);
    }

    public void Atualizar(String codigo, String servico, String data, String hora) throws Exception {
        Model_Scheduling objScheduling = new Model_Scheduling();
        objScheduling.setCodigo(Integer.parseInt(codigo.trim()));
        objScheduling.setServico(servico == null ? "" : servico.trim());
        objScheduling.setData(LocalDate.parse(data.trim(), FORMATO_DATA).toString());
        objScheduling.setHorario(LocalTime.parse(hora.trim(), FORMATO_HORA).format(FORMATO_HORA_BD));
        oPers.PersUpdScheduling(objScheduling);
    }

    public void Excluir(String codigo) throws Exception {
        oPers.PersExcScheduling(Integer.parseInt(codigo.trim()));
    }

    public ArrayList<Model_Scheduling> Consultar() throws Exception {
        return oPers.listScheduling();
    }
}