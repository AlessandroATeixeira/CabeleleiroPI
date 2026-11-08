package Pck_Model;

public class Model_RelatorioCaixa {
    private final String periodo;
    private double entradas;
    private double saidas;

    public Model_RelatorioCaixa(String periodo) {
        this.periodo = periodo;
    }

    public void somar(Model_Caixa mov) {
        if (mov.isEntrada()) {
            entradas += mov.getValor();
        } else {
            saidas += mov.getValor();
        }
    }

    public String getPeriodo() { return periodo; }
    public double getEntradas() { return entradas; }
    public double getSaidas() { return saidas; }
    public double getSaldo() { return entradas - saidas; }
}
