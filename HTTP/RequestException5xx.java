package HTTP;

/**
 * Clase que maneja las excepciones al analizar las peticiones
 * En concreto es la clase que maneja los errores propios del servidor
 * 
 * @author flanciskinho
 */
public class RequestException5xx extends RequestException{

    /**
     * Crea una excepcion con el error originado
     *
     * @param error es el tipo de error que se origino
     */
    public RequestException5xx(KindError error) {
        this.error = error;
    }
}
