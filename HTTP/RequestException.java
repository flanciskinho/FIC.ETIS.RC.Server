package HTTP;

/**
 * Clase abstracta que sirve de guía para las excepciones
 * 
 * @author flanciskinho
 */
public abstract class RequestException extends Exception{
    
    protected KindError error;

    /**
     * Obtienes el error que origino la excepcion
     *
     * @return Devuelve un objeto con el error que hizo saltar la excepcion
     */
    public KindError getError() {
        return error;
    }

    /**
     * Devuelve el numero de error que se origino
     *
     * @return Devuelve un int que idintifica el error que se produjo
     */
    public int getCode() {
        return this.error.getCode();
    }

    /**
     * El mítico toString
     *
     * @return Devuelve una cadena en la que especefica que fue lo que origino
     * el error
     */
    @Override
    public String toString() {
        return this.error.toString();
    }
}
