package Pck_Model;

public class Model_Product {

    private int codigo;
    private String nome;
    private double valorProd;
    private int estoque;


    public Model_Product(int codigo, String nome, double valorProd, int estoque) {
        this.codigo = codigo;
        this.nome = nome;
        this.valorProd = valorProd;
        this.estoque = estoque;
    }

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
