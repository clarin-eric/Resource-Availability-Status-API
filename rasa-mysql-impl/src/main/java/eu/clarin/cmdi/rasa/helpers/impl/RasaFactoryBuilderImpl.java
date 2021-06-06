package eu.clarin.cmdi.rasa.helpers.impl;

import java.util.Properties;
import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.RasaFactoryBuilder;

public class RasaFactoryBuilderImpl extends RasaFactoryBuilder {

	@Override
	public RasaFactory getRasaFactory(Properties properties) {		
		return new ACDHRasaFactory(properties);
	}
}
