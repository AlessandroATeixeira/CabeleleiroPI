package Pck_Model;

public class Model_Scheduling {
    private int codigo;
    private int codCliente;
    private int idServico;
    private String cliente;
    private String servico;
    private String horario;
    private String data;
    private double valorServico;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public int getCodCliente() { return codCliente; }
    public void setCodCliente(int codCliente) { this.codCliente = codCliente; }

    public int getIdServico() { return idServico; }
    public void setIdServico(int idServico) { this.idServico = idServico; }

    public String getNomeCliente() { return cliente; }
    public void setNomeCliente(String cliente) { this.cliente = cliente; }

    public String getServico() { return servico; }
    public void setServico(String servico) { this.servico = servico; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public double getValorServico() { return valorServico; }
    public void setValorServico(double valorServico) { this.valorServico = valorServico; }
}
