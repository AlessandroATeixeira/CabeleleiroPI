package Pck_Control;

public class Control_Valor {


    private Control_Valor() { }

    public static double parse(String texto) {
        if (texto == null) {
            throw new NumberFormatException("valor vazio");
        }
        String t = texto.trim().replace("R$", "").replace(" ", "");
        if (t.contains(",")) {
            t = t.replace(".", "").replace(",", ".");   // formato brasileiro
        }
        if (!t.matches("\\d+(\\.\\d+)?")) {
            throw new NumberFormatException("valor invalido: " + texto);
        }
        return Double.parseDouble(t);
    }
}
