package Pck_Model;

public class Model_Service {
    private int idServico;
    private String nomeServico;
    private double valorServico;

    public int getIdServico() { return idServico; }
    public void setIdServico(int idServico) { this.idServico = idServico; }

    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }

    public double getValorServico() { return valorServico; }
    public void setValorServico(double valorServico) { this.valorServico = valorServico; }

    @Override
    public String toString() {
        return idServico + " - " + nomeServico;
    }
}
