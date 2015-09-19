package HTTP;

import java.util.StringTokenizer;

/**
 * Clase que analiza una petición en http, separando los tres elementos clave
 * metodo, url y version
 * 
 * @author flanciskinho
 */
public class Request {
    
    private final String method, version;
    private String url;
    
    
    /**
     * Crea un objeto Request
     * 
     * @param r linea de peticion que hace el cliente
     * @throws RequestException 
     */
    public Request (String r) throws RequestException4xx {
        String aux = r;
        
        StringTokenizer st = new StringTokenizer(aux, " ");
        
        if (st.countTokens() != 3)
            throw new RequestException4xx(KindError.E_400);
        
        this.method = (st.nextToken()).toUpperCase();
        this.url = st.nextToken();
        this.version= (st.nextToken()).toUpperCase();
        
        /* Las diferentes versiones de http que puede recibir */
        if (this.version.equals("HTTP/1.1"))
            return;
        if (this.version.equals("HTTP/1.0"))
            return;
        
        /*
        if (this.version.startsWith("HTTP/"))
            return;
         */
        
        throw new RequestException4xx(KindError.E_400);
    }
    
    /**
     * Recupera el metodo que quiere usar el usuario
     * 
     * @return devuelve un string con el tipo de metodo que pido el cliente
     */
    public String getMethod() {
        return this.method;
    }
    
    /**
     * Devuelve el recurso que pide el usuario
     * 
     * @return devuelve un string con el objeto que quiere el cliente
     */
    public String getUrl() {
        return this.url;
    }
    
    /**
     * Para modificar la url que le pasaras al usuario
     * 
     * @param newUrl Nueva url a almacenar
     */
    public void setUrl(String newUrl) {
        this.url = newUrl;
    }
    
    /**
     * Devuelve la version que el cliente sugiere para usar
     * 
     * @return devuelve un string con la version http con la que se comunica
     * el cliente
     */
    public String getVersion() {
        return this.version;
    }
    
}