package Dinamic;

import java.util.Map;

import Server.*;
import HTTP.*;

public class ServerUtils {

	public static String processDynRequest(String nombreclase,
			Map<String, String> parameters) throws ClassNotFoundException, InstantiationException, IllegalAccessException  {

		MiniServlet servlet;
		Class<?> instancia;

		instancia = Class.forName(nombreclase);
		servlet = (MiniServlet) instancia.newInstance();
                try {
                    return servlet.doGet(parameters);
                } catch (Exception e) {
                    return null;
                }
        

	}
}
