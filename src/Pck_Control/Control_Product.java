package Pck_Control;


import Pck_Model.Model_Product;
import Pck_Persistence.Persistence_Product;

import java.util.ArrayList;

public class Control_Product {
    Persistence_Product oPers = new Persistence_Product();

    public void Inserir(String codigo, String nome, String valorProd, String estoque){
        Model_Product objProduct = new Model_Product();
        objProduct.setCodigo(Integer.parseInt(codigo));
        objProduct.setNome(nome);
        objProduct.setValorProd(Double.parseDouble(valorProd));
        objProduct.setEstoque(Integer.parseInt(estoque));
        oPers.PersInsProduct(objProduct);
    }

    public void Atualizar(String codigo, String nome, String valorProd, String estoque){
        Model_Product objProduct = new Model_Product();
        objProduct.setCodigo(Integer.parseInt(codigo));
        objProduct.setNome(nome);
        objProduct.setValorProd(Double.parseDouble(valorProd));
        objProduct.setEstoque(Integer.parseInt(estoque));
        oPers.PersInsProduct(objProduct);
    }

    public void Excluir(String codigo) throws Exception{
        oPers.PersExcProduct(Integer.parseInt(codigo));
    }

    public ArrayList<Model_Product> Consultar() throws Exception {
        return oPers.listProduct();
    }

}
