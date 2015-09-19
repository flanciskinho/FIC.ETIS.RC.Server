package HTTP;

import java.io.File;
import java.util.Date;


/**
 * Clase que sirve para conocer los datos de la línas de cabecera
 * para una respuesta  enhttp
 * 
 * @author flanciskinho
 */
public class Head {

    private static final String server = "My_server";
    
    private Date date;
    private long length;
    private String type;
    private File file;
    private boolean dinamic;
    
    /**
     * 
     * @param length longitud del cuerpo
     * @param file Nombre del archivo a usar. 
     * @param dinamic Booleano para indicar que se trata de una pagina dinamica
     */
    public Head(long length, File file, boolean dinamic) {
        this.length = length;
        this.type   = getType(file);
        this.date = new Date();
        this.file = file;
        this.dinamic = dinamic;
    }
    
    /**
     * Devuelve el tipo de archivo que que va a poner en la cabecera
     * 
     * @param f nombre del archivo
     * @return String con el tipo de archivo que es
     */
    private String getType(File fi) {
        if (this.dinamic) /* Es el mismo caso que el directorio */
            return "text/html";
        
        if (fi == null) /* Si no se pasa archivo */
            return "application/octet-stream";
        
        if (fi.isDirectory()) /* Al ser un directorio el servidor genera su propio codigo html */
            return "text/html";
        
        String f = fi.toString();
        
        if (f.toUpperCase().endsWith(".HTML") || f.toUpperCase().endsWith(".HTM"))
            return "text/html";

        if (f.toUpperCase().endsWith(".GIF"))
            return "image/gif";

        if (f.toUpperCase().endsWith(".JPEG"))
            return "image/jpeg";

        if (f.toUpperCase().endsWith(".JPG"))
            return "image/jpg";

        if (f.toUpperCase().endsWith(".PNG"))
            return "image/png";


        if (f.toUpperCase().endsWith("TXT") || f.toUpperCase().endsWith("CSS") || f.toUpperCase().endsWith("JS") || f.toUpperCase().endsWith("JAVA"))
            return "text/plain";

        return "application/octet-stream";
    }
    
    /**
     * Metodo que nos da las líneas de cabecera de una respuesta http
     * 
     * @return devuelve un string con las lineas de cabecera de una respuesta
     */
    public String getHead() {
        if ((this.file != null) || !this.dinamic) {
            return 
                    "Date: " + this.date.toString() + "\n" +
                    "Server: " + Head.server + "\n" +
                    "Last-Modified: " + this.file.lastModified() + "\n" +
                    "Content_length: " + this.length + "\n" +
                    "Content_type: " + this.type + "\n";
            
        } else {
            return 
                    "Date: " + this.date.toString() + "\n" +
                    "Server: " + Head.server + "\n" +
                    "Content_length: " + this.length + "\n" +
                    "Content_type: " + this.type + "\n";
        }
    }
}