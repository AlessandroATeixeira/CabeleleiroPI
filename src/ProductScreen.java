import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ProductScreen extends JFrame {

    public ProductScreen() {
        setTitle("Cadastro de Produtos");
        setSize(600, 430);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel titulo = new JLabel(
                "Tela de cadastro de produtos",
                SwingConstants.CENTER
        );

        add(titulo);
        setVisible(true);
    }


}