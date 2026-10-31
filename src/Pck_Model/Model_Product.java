package Pck_Model;

public class Model_Product {

    private int codigo;
    private String nome;
    private double valorProd;
    private int estoque;


    public int getCodigo() {
        return codigo;
    }
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getValorProd() {
        return valorProd;
    }
    public void setValorProd(double valorProd) {
        this.valorProd = valorProd;
    }

    public int getEstoque() {
        return estoque;
    }
    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
}
