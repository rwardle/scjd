/*
 * ApplicationMode.java
 *
 * Created on 08 June 2005, 10:26
 */


package suncertify.startup;


/**
 * 
 *
 * @author Richard Wardle
 */
public class ApplicationMode {
    
    public static final ApplicationMode CLIENT = new ApplicationMode("client");
    
    public static final ApplicationMode SERVER = new ApplicationMode("server");
    
    public static final ApplicationMode STANDALONE 
            = new ApplicationMode("standalone");
    
    private final String name;
    
    private ApplicationMode(final String name) {
        this.name = name;
    }
    
    public String toString() {
        return this.name;
    }
}
