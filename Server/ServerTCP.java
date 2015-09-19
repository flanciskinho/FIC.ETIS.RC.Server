package Server;

/**
 *
 * @author flanciskinho
 */

import java.net.*;
import java.io.*;


/** Ejemplo que implementa un servidor de eco usando TCP. */

public class ServerTCP {

    public static void main(String argv[]) {
        if (argv.length > 1) {
            System.err.println("Formato: ServidorTCP [<puerto>]");
            System.exit(-1);
        }
        
        /* Cargamos el archivo de configuracion */
        Config config = new Config("./src/Server/config");
        
        
        ServerSocket socket = null;
        try {
            int port = (argv.length == 0)? config.getPort(): Integer.parseInt(argv[0]);
            // Creamos el socket del servidor
            socket = new ServerSocket(port);
            
            Socket s;
            ThreadServer thread;
            while (true) {
                // Esperamos posibles conexiones
                s = socket.accept();
                // Creamos un objeto ThreadServidor, pasandole la nueva conexion
                thread = new ThreadServer(s, config);
                // Ejecutamos su ejecución con el método start
                thread.start();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            //e.printStackTrace();
        } finally {
            // Cerramos el socket del servidor
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
