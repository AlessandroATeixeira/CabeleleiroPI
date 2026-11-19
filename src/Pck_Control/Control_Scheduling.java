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

    private final Persistence_Scheduling persistence = new Persistence_Scheduling();

    public void Inserir(String codigo, String codCliente, int idServico, String data, String hora) throws Exception {
        persistence.PersInsScheduling(criarModel(codigo, codCliente, idServico, data, hora));
    }

    public void Atualizar(String codigo, String codCliente, int idServico, String data, String hora) throws Exception {
        persistence.PersUpdScheduling(criarModel(codigo, codCliente, idServico, data, hora));
    }

    private Model_Scheduling criarModel(String codigo, String codCliente, int idServico, String data, String hora) throws Exception {
        int codigoInt = Integer.parseInt(codigo.trim());
        int codClienteInt = Integer.parseInt(codCliente.trim());
        if (codigoInt <= 0) throw new Exception("O código do agendamento deve ser maior que zero.");
        if (codClienteInt <= 0) throw new Exception("Informe um cliente válido.");
        if (idServico <= 0) throw new Exception("Selecione um serviço cadastrado.");

        Model_Scheduling model = new Model_Scheduling();
        model.setCodigo(codigoInt);
        model.setCodCliente(codClienteInt);
        model.setIdServico(idServico);
        model.setData(LocalDate.parse(data.trim(), FORMATO_DATA).toString());
        model.setHorario(LocalTime.parse(hora.trim(), FORMATO_HORA).format(FORMATO_HORA_BD));
        return model;
    }

    public void Excluir(String codigo) throws Exception {
        int codigoInt = Integer.parseInt(codigo.trim());
        if (codigoInt <= 0) throw new Exception("O código a ser excluído deve ser maior que zero.");
        persistence.PersExcScheduling(codigoInt);
    }

    public ArrayList<Model_Scheduling> Consultar() throws Exception {
        return persistence.listScheduling();
    }

    public ArrayList<Model_Scheduling> ConsultarDisponiveisCaixa() throws Exception {
        return persistence.listAgendamentosDisponiveisCaixa();
    }
}
