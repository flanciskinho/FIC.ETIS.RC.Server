package HTTP;

import Server.Config;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Clase que genera el cuerpo de una respuesta en http
 * 
 * @author flanciskinho
 */
public class Body {
    /* Es el archivo que se usara en el cuerpo */
    private File file;
    
    /* Tamaño en bytes que tiene el archivo */
    private int size;
    
    /* Informacion que tiene el archivo */
    private byte [] data;
    
    /* Archivo de configuracion del sevidor */
    private Config config;
    
    /**
     * Crea el cuerpo de la respuesta
     * 
     * @param file File que tendra el cuerpo de la respuesta
     */
    public Body(File file, Config config) throws RequestException4xx {
        this.config = config;
        this.file = file;
        
        /* Antes de nada miramos si existe */
        if (!this.file.exists())
            throw new RequestException4xx(KindError.E_404);
        
        
        /* Miramos si el archivo es un directorio o no */
        if (file.isDirectory()) {
            this.read_directory();
        } else {
            this.read_file();
        }
    }
    
    /**
     * Devuelve el tamaño del cuerpo de la respuesta
     * 
     * @return devuelve un int indicando el tamano en bytes del fichero creado/leido
     */
    public int size() {
        return this.size;
    }
    
    /**
     * Devuelve la informacion del cuerpo de la respuesta
     * 
     * @return devuelve un array de bytes con la informacion que se genero
     */
    public byte [] get_data() {
        return this.data;
    }

    /**
     * Es lo que se origina cuando se quiere crear visualizar lo que contiene
     * un directorio
     *
     */
    private void read_directory() {
        File [] list = this.file.listFiles();
        int cut = this.config.getDirectory().length();

        String tmp = (cut >= this.file.toString().length()) ?
                            "/" :
                            this.file.toString().substring(cut);
        
        String aux = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\"\n"+
                     "\t\"http://www.w3.org/TR/html4/strict.dtd\">\n" +

                     "<html lang='es'>\n\n"+
                     "<head>\n"+
                     "\t<title>" + "INDEX: " + tmp + "</title>\n"+
                     "\t<link rel='stylesheet' type='text/css' href='./css/ls.css'>\n"+
                     "</head>\n\n"+

                     "<body>\n" +
                     "\t<h2 id='title'>" + "INDEX: " + tmp + "</h2>\n";
        String type = "";
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory())
                type = "dir";
            else if (list[i].canExecute())
                type = "exe";
            else
                type = "file";
            tmp = list[i].toString().substring(cut);
            aux = aux + "\t<p class='"+type+"'><a href=\"/" + tmp + "\">" + tmp + "</a></p>\n";
        }
        
        aux = aux + "</body>\n\n</html>";
        
        this.data = aux.getBytes();
        
        this.size = this.data.length;
    }

    /**
     * Metodo que se ejecuta cuando el cliente solicita un archivo,
     * en this.data se mete la informacion que se debe enviar
     *
     * @throws RequestException
     */
    private void read_file() throws RequestException4xx {
        try 
        {
            FileInputStream tmp = new FileInputStream(this.file);
            
            this.size = (int) this.file.length();

            this.data = new byte[this.size];

            tmp.read(this.data);

            tmp.close();
        } catch (FileNotFoundException ex) {
            System.err.println("(Body.read_file)File doesn't exist: "+this.file);
            throw new RequestException4xx(KindError.E_404);
        } catch (IOException ex) {
            System.out.println("(Body.read_file)Cannot read file: "+this.file);
            throw new RequestException4xx(KindError.E_403);
        }
    }
    
}
