package Pck_Model;

import java.time.LocalDateTime;

public class Model_Caixa {

    private int codigo;
    private LocalDateTime dataHora;
    private String tipo;
    private String origem;
    private Integer codProduto;
    private String nomeProduto;
    private Integer quantidade;
    private String descricao;
    private double valor;

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }

    public Integer getCodProduto() { return codProduto; }
    public void setCodProduto(Integer codProduto) { this.codProduto = codProduto; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    public boolean isEntrada() { return "ENTRADA".equals(tipo); }

}
