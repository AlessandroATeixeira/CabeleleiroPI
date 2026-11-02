package Pck_Control;

import Pck_Model.Model_Product;
import Pck_Persistence.Persistence_Product;

import java.util.ArrayList;

public class Control_Product {
    Persistence_Product oPers = new Persistence_Product();

    public void Inserir(String codigo, String nome, String valorProd, String estoque) throws Exception {
        int codigoInt = Integer.parseInt(codigo);
        if (codigoInt <= 0) {
            throw new Exception("O código do produto deve ser maior que zero.");
        }

        int estoqueInt = Integer.parseInt(estoque);
        if (estoqueInt < 0) {
            throw new Exception("O estoque não pode ser negativo.");
        }

        double valorDouble = Double.parseDouble(valorProd.replace(",", "."));
        if (valorDouble < 0) {
            throw new Exception("O valor do produto não pode ser negativo.");
        }

        Model_Product objProduct = new Model_Product();
        objProduct.setCodigo(codigoInt);
        objProduct.setNome(nome);
        objProduct.setValorProd(valorDouble);
        objProduct.setEstoque(estoqueInt);
        oPers.PersInsProduct(objProduct);
    }

    public void Atualizar(String codigo, String nome, String valorProd, String estoque) throws Exception {
        int codigoInt = Integer.parseInt(codigo);
        if (codigoInt <= 0) {
            throw new Exception("O código do produto deve ser maior que zero.");
        }

        int estoqueInt = Integer.parseInt(estoque);
        if (estoqueInt < 0) {
            throw new Exception("O estoque não pode ser negativo.");
        }

        double valorDouble = Double.parseDouble(valorProd.replace(",", "."));
        if (valorDouble < 0) {
            throw new Exception("O valor do produto não pode ser negativo.");
        }

        Model_Product objProduct = new Model_Product();
        objProduct.setCodigo(codigoInt);
        objProduct.setNome(nome);
        objProduct.setValorProd(valorDouble);
        objProduct.setEstoque(estoqueInt);
        oPers.PersUpdProduct(objProduct);
    }

    public void Excluir(String codigo) throws Exception {
        int codigoInt = Integer.parseInt(codigo);
        if (codigoInt <= 0) {
            throw new Exception("O código a ser excluído deve ser maior que zero.");
        }
        oPers.PersExcProduct(codigoInt);
    }

    public ArrayList<Model_Product> Consultar() throws Exception {
        return oPers.listProduct();
    }
}