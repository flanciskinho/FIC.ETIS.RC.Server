package HTTP;

/**
 * Clase que maneja las excepciones al analizar las peticiones
 * En concreto es el que genera los errores 3xx
 * 
 * @author flanciskinho
 */
public class RequestException3xx extends RequestException{

    /**
     * Crea una excepcion con el error originado
     *
     * @param error es el tipo de error que se origino
     */
    public RequestException3xx(KindError error) {
        this.error = error;
    }

   
}
