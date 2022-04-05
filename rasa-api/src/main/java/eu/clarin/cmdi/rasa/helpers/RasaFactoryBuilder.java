package eu.clarin.cmdi.rasa.helpers;

import java.util.Properties;

public interface RasaFactoryBuilder {

    public RasaFactory getRasaFactory();
    
    @Deprecated(forRemoval = true)
    public RasaFactory getRasaFactory(Properties properties);
}
