package Server;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Clase que carga la configuracion del servidor
 * 
 * @author flanciskinho
 */
public class Config {

    private final Boolean ALLOW;
    private final Integer PORT;
    private final String DIRECTORY;
    private final String DIRECTORY_INDEX;
    private final Boolean VERBOSE;
    
    /**
     * 
     * @param path Ruta donde se encuentra el archivo de configuracion
     */
    public Config(String path) {
        Properties properties = null;
        try {
            //String curDir = System.getProperty("user.dir");
            //System.out.println("curDir: " + curDir);
            //System.out.println("path: " + path);
            FileInputStream configFile = new FileInputStream(path);
            properties = new Properties();

            properties.load(configFile);//Carga el config en properties
            
        } catch (Exception ex) {
            System.err.println("Cannot read config file");
            System.exit(-1);
        }   
            
            
            this.DIRECTORY = properties.getProperty("defaultDirectory");
            this.DIRECTORY_INDEX = properties.getProperty("defaultFile");
            this.PORT = new Integer(properties.getProperty("port"));
            this.ALLOW = new Boolean(properties.getProperty("allow"));
            this.VERBOSE = new Boolean(properties.getProperty("verbose"));

    }
    
    /**
     * Indica si se pueden ver por pantalla lo que recibe y parte de lo que
     * envia el servidor
     * 
     * @return  devuelve el valor de <b>verbose</b> que tiene el fichero de 
     * configuracion
     */
    public boolean getVerbose() {
        return this.VERBOSE.booleanValue();
    }
    
    /**
     * Indica si se podran listar los directorios
     * 
     * @return devuelve el valor de <b>allow</b> que tiene el fichero de
     * configuracion
     */
    public boolean getAllow() {
        return this.ALLOW.booleanValue();
    }
    
    /**
     * Indica el puerto a usar por defecto
     * 
     * @return devuelve el valor de <b>port</b> que tiene el fichero de
     * configuracion
     */
    public int getPort() {
        return this.PORT.intValue();
    }
    
    /**
     * Indica el directorio a usar por defecto
     * 
     * @return devuelve el valor de <b>defaultDirectory</b> que tiene el
     * fichero de configuracion
     */
    public String getDirectory() {
        return this.DIRECTORY;
    }
    
    /**
     * Indica el archivo a usar por defecto
     * 
     * @return devuelve el valor de <b>defaultFile</b> que tiene el fichero
     * de configuracion
     */
    public String getDirectoryIndex() {
        return this.DIRECTORY_INDEX;
    }
    
}
