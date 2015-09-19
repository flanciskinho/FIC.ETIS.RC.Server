package HTTP;

import Dinamic.*;
import Server.Config;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que se encarga de generar la respuesta
 * 
 * @author flanciskinho
 */
public class Answer {
    
    
    /* Version en la que trabaja nuestro servidor */
    private final static String VERSION = "HTTP/1.0";
    
    /* Objeto que almacena la peticion del cliente */
    private Request request;
    
    /* Configuracion que tiene nuestro servidor */
    private Config config;
    
    /* En caso de que podamos analizar la url, se guardará aqui el archivo */
    private File file;
    
    /* Es el cuerpo de la respuesta */
    private Body body;
    
    /* Informacion que tiene el cuerpo de la respuesta */
    private byte[] data = null;
    
    /* Esta es la cabecera de la respuesta */
    private Head head;
    
    /* Es la linea de estado de la respuesta */
    private String state;
    
    /* Codigo de error */
    private KindError code;

    /* Numero para saber si se modifico el archivo por ultima vez */
    private long modified;

    /**
     * Prepara la respuesta que enviará el servidor
     * 
     * @param r String con la peticion del cliente
     * @param config Configuracion del servidor
     */
    public Answer(String r, Config config, String recived) {
        
        modified = last_modified(recived);

        this.config = config;
        
        try {
            /* Guardamos la peticion del cliente */
            this.request = new Request(r);
            
            /* Comprobamos que nos pide un metodo valido */
            checkMethod(this.request.getMethod());
            
            /* Comprobamos la url */
            checkURL(this.request.getUrl());
            /* Hora de procesar la respuesta */

            /* Miramos si tiene la opcion is-modified-since */
            this.if_modified_since();

            /* En primer lugar creamos el cuerpo */
            this.body = new Body(this.file, this.config);
            this.data = this.body.get_data();
            /* Ahora rellenamos la cabecera */
            this.head = new Head(body.size(), this.file, false);
            
             /* Si no salto ninguna excepcion, es que es todo correcto */
            this.code = KindError.E_200;
            this.state = Answer.VERSION + " " + this.code.toString() + "\n";
        } catch (RequestException4xx ex) {
            answer4xx(ex);
        } catch (RequestException3xx ex) {
            answer3xx(ex);
        } catch (DinamicException ex) {
            try {
                answerDinamic();
            } catch (RequestException5xx e) {
                answer5xx(e);
            }
        } catch (Exception ex) {
            answer5xx(new RequestException5xx(KindError.E_500));
        }
    }

    /**
     * Metodo que genera el código que tiene que enviar el servidor cuando se
     * produce un error de tipo 500
     * 
     * @param exception Excepcion que provocó el error
     */
    private void answer5xx(RequestException exception) {
        this.code = exception.getError();
        this.body = null;
        
        this.data = this.code.get_html().getBytes();
        
        this.head = new Head(this.data.length, this.file, true);
        
        this.state = 
                Answer.VERSION + " " +
                this.code.toString() + "\n";
    }
    
    /**
     * Método que se encarga de generar la respuestas dinámicas que tiene que
     * dar el servidor
     * 
     * @throws RequestException5xx 
     */
    private void answerDinamic() throws RequestException5xx {
        /* Pongo que se va a crear la pagina */
        this.code = KindError.E_200;
        String aux = null;
        try {
            aux = ServerUtils.processDynRequest(this.get_class(),
                                                this.separate());
        } catch (ClassNotFoundException ex) {
            throw new RequestException5xx(KindError.E_501);
        } catch (InstantiationException ex) {
            throw new RequestException5xx(KindError.E_500);
        } catch (IllegalAccessException ex) {
            throw new RequestException5xx(KindError.E_500);
        }

        /* Almaceno la informacion a enviar */
        this.data = aux.getBytes();
        
        /* Creo la cabecera que me toca enviar */
        this.head = new Head(this.data.length, this.file, true);
        
        /* Actualizo la línea de estado */
        this.state = 
                Answer.VERSION + " " +
                this.code.toString() + "\n";
    }
    
    /**
     * Método que se encarga de generar las respuestas cuando se produce un
     * código del tipo 300
     * 
     * (De momento solo está implementado para el código 304)
     * 
     * @param exception error que provocó la excepcion
     */
    private void answer3xx(RequestException exception) {
        /* Almaceno el error que se genero */
        this.code = exception.getError();
        this.body = null;
        
        /* Creo la cabecera */
        this.head = new Head(0, this.file, true);
        
        /* Actualizo la linea de estado */
        this.state = 
                Answer.VERSION + " " +
                this.code.toString() + "\n";
    }
    
    /**
     * Método que se encarga de generar las respuesta cuando se produce un
     * error del tipo 400
     * 
     * (De momento solo está implementado para el codigo 400, 403, 404)
     * 
     * @param exception error que provoco la excepcion
     */
    private void answer4xx(RequestException exception) {
        /* Almaceno el error que se genero */
        this.code = exception.getError();
        this.body = null;
        /* Almaceno el mensaje que se enviará */
        this.data = this.code.get_html().getBytes();
        
        /* Creo la cabecera */
        this.head = new Head(this.data.length, this.file, true);
        
        /* Actualizo la linea de estado */
        this.state = 
                Answer.VERSION + " " +
                this.code.toString() + "\n";
    }
    
    /**
     * Método que se encarga de coger el nombre de la clase a instanciar cuando
     * la petición es dinámica
     * 
     * @return <b>String</b> Con el nombre de la clase que debe instanciar
     *  <i>La clase tiene que estar dentro del paquete dinamic</i>
     */
    private String get_class() {
        String url = this.request.getUrl();
        int index = url.indexOf(".");
        if (index == -1)
            return null;
        
        return "Dinamic."+url.substring(1, index);/* El cero es / */
    }
    
    /**
     * Método que se encarga de separar los argumentos que se pasan por la url
     * (el servidor no admite la petición POST)
     * 
     * @return <b>Map</b> con el par de valor atributo=valorAtributo
     */
    private Map<String, String> separate() {
        /* Cogo la url para trocearla */
        String url = this.request.getUrl();
        int index = url.indexOf("?");
        if (index == -1)
            return null;
        
        url = url.substring(index+1);
        StringTokenizer st = new StringTokenizer(url, "&");
        Map<String, String> map = new HashMap<String, String>();
        String aux;
        while (st.hasMoreTokens()) {
            aux = st.nextToken();
//System.out.println(aux);
            if ((index = aux.indexOf("=")) == -1)
                continue;
            try {
                map.put(aux.substring(0, index), URLDecoder.decode(aux.substring(index + 1), "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                map.put(aux.substring(0, index), aux.substring(index + 1));
            }
        }
        
        
        return map;
    }
    
    /**
     * Método que se encarga de comprobar si hubo cambios en un archivo desde la
     * última vez que el servidor lo solicito
     * 
     * @throws RequestException3xx Excepcion que se genera si no hubo cambios
     */
    private void if_modified_since() throws RequestException3xx {
//System.out.println("archivo: "+this.file.lastModified()+"\nNavegador: "+this.modified);
        if (this.modified >= this.file.lastModified())
            throw new RequestException3xx(KindError.E_304);

        //System.out.println(this.request.getUrl()+": modified");
    }

    /**
     * Para mirar si tiene la opcion modified
     *
     * @param string
     * @return Devuelve la el numer que tiene la 
     */
    private long last_modified(String string) {
        int index;
        if ((index = string.indexOf("If-Modified-Since:")) != -1) {
            String s = string.substring(index);
            s = s.substring(s.indexOf(":")+1, s.indexOf("\n")).trim();
            
            try {
                Long l = new Long(s);

                return l;
            } catch (Exception e) { }
        }

        return -1;
  
    }
    
    /**
     * Metodo para comprobar si podemos usar el metodo que nos pide el usuario
     * 
     * @param method String con el metodo que quiere usar el usuario.
     * (Tiene que estar en mayusculas)
     * 
     * @throws RequestException En el caso de que el servidor no pueda realizar
     * el metodo que sugiere el usuario lanzara una excepcion con el error
     * adecuado
     */
    private void checkMethod(String method) throws RequestException4xx {
        if (method.equals("GET"))
            return;
        if (method.equals("HEAD"))
            return;
        
        throw new RequestException4xx(KindError.E_400);
    }
    
    
    /**
     * Funcion que comprueba el recurso que pide el usuario, y almacena el tipo
     * de archivo en el objeto
     * 
     * @param url String con el recurso que quiere el cliente
     * 
     * @throws RequestException  En caso del que el archivo no exista, o el
     * usuario no tengas permisos, se lanzara una excepcion con el error
     * adecuado
     */
    private void checkURL(String url) throws RequestException4xx, DinamicException{
        String new_url;
        File tmpFile;
        
        /* Analizo si piden contenido dinámico */
        if (url.indexOf(".do?") != -1)
            throw new DinamicException();
                
        
        if (url.equals("/") || url.equals(""))
            this.request.setUrl(this.config.getDirectoryIndex());
        
        new_url = this.config.getDirectory().concat(this.request.getUrl());
        tmpFile = new File(new_url);
        
        if (!tmpFile.exists()) {
            System.err.println("(Answer.checkURL) File doesn't exist: "+ new_url);
            throw new RequestException4xx(KindError.E_404);
        }
        
        if (tmpFile.isDirectory()) {
            /* Miramos si en el directorio hay archivo por defecto */
            String aux = new_url + "/" + this.config.getDirectoryIndex();
            File auxFile = new File(aux);
//System.out.println("auxFile: "+auxFile);
            if (auxFile.exists()){
                System.out.println("here");
                this.request.setUrl(aux);
                tmpFile = auxFile;
            } else {
                if (this.config.getAllow()) {
                    this.file = tmpFile;

                } else {
                    System.err.println("(Answer.checkURL) (403) It's a directory. Config.file = "+this.config.getAllow());
                    throw new RequestException4xx(KindError.E_403);
                }
            }
        }
        
        if (!tmpFile.canRead()) {
            System.err.println("(Anwer.checkURL) Cannot read file " + new_url);
            throw new RequestException4xx(KindError.E_403);
        }



        this.file = tmpFile;
System.out.println("url File: " + tmpFile);
    }
    
     /**
     * Devuelve la longitud de la cabecera
     * 
     * @return long con la longitud de la cabecera
     */
    public long size() {
        return this.toString().length();
    }
    
    /**
     * Metodo para conocer el codigo de error que lleva el mensaje
     * 
     * @return int con el numero de error
     */
    public int getCode() {
        return this.code.getCode();
    }
    
    /**
     * Metodo para conocer que error lleva el mensaje
     * 
     * @return String con el tipo de error
     */
    public String getError() {
        return this.code.toString();
    }

    /**
     * Devuelve las linea de estado, las lineas de cabecera y la linea en blanco
     *
     * @return Devuelve un string con las lineas de estado y la linea
     * de cabecera y la linea en blanco
     */
    public String get_info() {
        if (this.head != null)
            return this.state + this.head.getHead();
        else
            return this.state;
    }
    
    /**
     * Devuelve la informacino que solicito el usuario
     * (o null en caso de que no se pueda)
     *
     * @return devuelve en un array de bytes el cuerpo de la entidad
     */
    public byte[] get_data() {
        if (this.request == null)
            return null;
        
        if (this.request.getMethod().equals("GET"))
            return this.data;
        else
            return null;    
    }
}
