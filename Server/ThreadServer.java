package Server;

/**
 * Thread que atiende una conexión de un servidor de eco
 * 
 * @author flanciskinho
 */


import HTTP.Answer;
import java.net.*;
import java.io.*;
import java.util.Date;


public class ThreadServer extends Thread {
    /* Socket con el que se establece la conexion */
    private final Socket socket;
    /* Archivo de configuracion que tiene el servidor */
    private final Config config;
    
    /* Respuesta que genera el servidor */
    private Answer answer;

    /**
     * Crea un thread para poder analizar la respuesta,
     * (se hace para que pueda recibir varias peticiones simultaneas)
     *
     * @param s Socket con el que se establecio la conexión
     * @param config Objeto que tiene la configuracion del servidor
     */
    public ThreadServer(Socket s, Config config) {
        // Almacenamos el socket de la conexion
        this.socket = s;
        this.config = config;
    }

    /**
     * Método que añade las peticiones que se realizaron con exito en un log
     *
     * @param request peticion que realizo el cliente
     * @throws IOException
     */
    private void add_log_file(String request) throws IOException{
        File nombre= new File("./src/Server/Log.txt");
        FileWriter fichlog = new FileWriter(nombre,true);
        fichlog.write("Request line: "+ request +"\n");
        fichlog.write("Client's IP address: " + socket.getInetAddress().toString()+"\n");
        fichlog.write("Date and time: "+(new Date()).toGMTString()+"\n");
        fichlog.write("Status code: "+ this.answer.getCode() +"\n");
        fichlog.write("Size(bytes): "+ answer.size() +".\n\n");
        fichlog.close();
    }

    /**
     * Método que añade las peticiones que se realizaron sin exito en un log
     *
     * @param request peticion que realizo el cliente
     * @throws IOException
     */
    private void add_file_log_error(String message) throws IOException{
        File nombre = new File("./src/Server/LogError.txt");
        FileWriter fichlog = new FileWriter(nombre,true);
        fichlog.write("Request line: "+ message+"\n");
        fichlog.write("Client's IP address: " + socket.getInetAddress().toString()+"\n");
        fichlog.write("Date and time: "+ (new Date()).toGMTString()+"\n");
        fichlog.write("Error code: "+ this.answer.getCode() +"\n\n");

        fichlog.close();
    }
    
    /**
     * Metodo en el que se analiza la peticion del cliente y el servidor
     * la analiza y genera la respuesta para enviarla
     *
     */
    @Override
    public void run() {
        try {
            BufferedReader sInput;
            PrintWriter sOutput;
            OutputStream sOutFile;
            String take;
            // Establecemos el canal de entrada
            sInput = new BufferedReader(
                    new InputStreamReader( this.socket.getInputStream()));
            // Establecemos el canal de salida
            sOutput = new PrintWriter( this.socket.getOutputStream(), true);
            // Recibimos el mensaje del cliente
            take = sInput.readLine();
            
            String info = "";
            String aux;
            if (sInput.ready()) {
                do {
                    aux = sInput.readLine();
                    if (aux != null)
                        info = info + "\n"+ aux;
                } while (sInput.ready() && (aux != null) && (!aux.equals("")));
            }
            
            if (this.config.getVerbose()) {
                System.out.println("RECIBIDO:\n"+take+"\n"+info);
                System.out.flush(); /* Para que lo escriba pero ya!!! */
            }
            // Analizamos la respuesta
            answer = new Answer(take, config, take+info);
            
            // Enviamos al cliente
            sOutput.println(answer.get_info());/* Informacion sobre la respuesta */
            
            if (this.config.getVerbose())
                System.out.println("ENVIADO:\n"+answer.get_info());
            
            byte [] data = answer.get_data();/* La respuesta propiamente dicha */
            if (data != null) {
                sOutFile = this.socket.getOutputStream();
                sOutFile.write(data);
                sOutFile.flush(); /* Para que lo envie ya!!! */
            }
            
            // Cerramos los flujos
            sInput.close();
            sOutput.close();
            
            //Fichero de configuracion
            if (answer.getCode() >= 400)
                add_file_log_error(take);
            else
                add_log_file(take);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            //Cerramos el socket
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
