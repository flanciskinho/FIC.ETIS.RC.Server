package HTTP;

/**
 * Enumerado que nos ayuda a trabajar con los tipos de errores
 * 
 * @author flanciskinho
 */
enum KindError {
    /* Tipos de errores disponibles */
    
//2xx
    /**
     * 200 -> todo correcto
     */
    E_200(200, "OK"),
    /**
     * 201 -> creado correctamente
     */
    E_201(201, "Created"),
    
    
//3xx
    /**
     * 304 -> Sin cambios desde la ultima vez que se solicito
     */
    E_304(304, "Not Modified"), /* Es que no fue modificado, entonces no tiene que tener fichero de error */
    
    
//4xx
    /**
     * 400 -> peticion no valida
     */
    E_400(400, "Bad Request"),
    /**
     * 403 -> Acceso no permitido
     */
    E_403(403, "Forbidden"),
    /**
     * 404 -> fichero no encontrado
     */
    E_404(404, "Not Found"),

    
//5xx
    /**
     * 500 -> fallo en el servidor
     */
    E_500(500, "Internal Server Error"),
    /**
     * 501 -> Método solicitado no implementado por el servidor
     */
    E_501(501, "Not Implemeted");
    
    private int code;
    private String string;
    
    KindError (int c, String s) {
        this.code = c;
        this.string = s;
    }
    
    /**
     * Metodo para conseguir el numero de error
     * 
     * @return devuelve el numero de error que es 
     */
    public int getCode() {
        return this.code;
    }
    
    /**
     * Metodo para conocer de que error se trata
     * 
     * @return devuelve un string con el codigo de error y que significa 
     */
    @Override
    public String toString() {
        return (this.code + " " + this.string);
    }
    
    /**
     * Genera el codigo html para un código de error
     * 
     * @return devuelve un String que contiene una pagina html indicando el error
     */
    public String get_html() {
        
        return 
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\""+
            "    \"http://www.w3.org/TR/html4/strict.dtd\""+
            "    >"+

            "<html lang=\"en\">"+


            "<head>"+
            "<style type=\"text/css\">"+

            "#code {color: red; font-size: 4em;}"+
            "#message {color: blue; font-size: 3em;}"+
            "body {margin: 0px; padding: 0px;"+
            "	padding-top: 20px;"+
            "	text-align: center;"+

            "	background-color: #C4C4C4; /* Para los que no admiten degradado */"+

            "	background: -webkit-linear-gradient(#EAEAEA, #f00);"+
            "	background: -moz-linear-gradient(#EAEAEA, #C4C4C4);" +
            "	background: -o-linear-gradient(#EAEAEA, #C4C4C4);" +
            "	background: linear-gradient(#EAEAEA, #C4C4C4);"+
            "}"+

            "body {"+
            "    background-image: url('./images/"+this.code+".png');"+
            "    background-repeat: no-repeat;"+
            "    background-attachment: fixed; /* Para que est fija en el fondo */"+
            "    background-position: 90% 95%;/* bottom right; */"+
            "    background-size: 350px, 350px;"+
            "}"+

            "</style>"+
            "<title>" + this.string + "</title>"+
            "</head>"+

            "<body>"+
            "	<h1 id = \"code\">"+
            "		"+this.code+""+
            "	</h1>"+

            "	<h1 id = \"message\">"+
            "		"+this.string+""+
            "	</h1>"+
            "</body>"+

            "</html>"
        ;
    }
}