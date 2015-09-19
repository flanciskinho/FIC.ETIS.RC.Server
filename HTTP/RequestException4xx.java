package HTTP;

/**
 * Clase que maneja las excepciones al analizar las peticiones
 * En concreto es la que genera los errores 4xx
 * 
 * @author flanciskinho
 */
public class RequestException4xx extends RequestException{

    /**
     * Crea una excepcion con el error originado
     *
     * @param error es el tipo de error que se origino
     */
    public RequestException4xx(KindError error) {
        this.error = error;
    }

}
