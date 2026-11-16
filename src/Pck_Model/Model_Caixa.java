package Pck_Model;

import java.time.LocalDateTime;

public class Model_Caixa {

    private int codigo;
    private LocalDateTime dataHora;
    private String tipo;
    private String origem;
    private Integer codProduto;
    private Integer codServico;
    private String nomeServico;
    private String nomeProduto;
    private Integer quantidade;
    private String descricao;
    private double valorB;
    private double valorV;

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

    public Integer getCodServico() { return codServico; }
    public void setCodServico(Integer codServico) { this.codServico = codServico; }

    public String getNomeServico() { return nomeServico; }
    public void setNomeServico(String nomeServico) { this.nomeServico = nomeServico; }

    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public double getValorB() { return valorB; }
    public void setValorB(double valorB) { this.valorB = valorB; }

    public double getValorV() { return valorV; }
    public void setValor(double valor) { this.valorB = valor; this.valorV = valor; }

    public boolean isEntrada() { return "ENTRADA".equals(tipo); }

}
