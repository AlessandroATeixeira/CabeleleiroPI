package Pck_Model;

public class Model_Product {

    private int codigo;
    private String nome;
    private double valorProdB;
    private double valorProdV;
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

    public double getValorProdB() {
        return valorProdB;
    }
    public void setValorProdB(double valorProdB) {
        this.valorProdB = valorProdB;
    }

    public double getValorProdV() {
        return valorProdV;
    }
    public void setValorProdV(double valorProdV) {
        this.valorProdV = valorProdV;
    }

    public int getEstoque() {
        return estoque;
    }
    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }
}
