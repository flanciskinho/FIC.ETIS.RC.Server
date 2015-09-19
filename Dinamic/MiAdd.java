package Dinamic;

import java.util.Map;
/**
 *
 * @author francisco.cedron
 */
public class MiAdd implements MiniServlet {

    public MiAdd() {
        
    }

    public String doGet (Map<String, String> parameters){
            String number1 = parameters.get("number1");
            String number2 = parameters.get("number2");
            float n1 = (new Float(number1)).floatValue(),
                  n2 = (new Float(number2)).floatValue();

            return printHeader() + printBody(n1, n2) + printEnd();
    }

    private String printHeader() {
        return "<html><head> <title>Resultado</title> </head> ";
    }

    private String printBody(float n1, float n2) {
        float r = n1+n2;
        return "<body> <h1> "+n1+" + "+ n2 +" = "+ r + "</h1></body>";
    }

    private String printEnd() {
        return "</html>";
    }

}
