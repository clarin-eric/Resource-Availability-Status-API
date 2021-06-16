package eu.clarin.cmdi.rasa.helpers.impl;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.RasaFactoryBuilder;
import eu.clarin.cmdi.rasa.linkResources.impl.CheckedLinkResourceImpl;
import eu.clarin.cmdi.rasa.linkResources.impl.LinkToBeCheckedResourceImpl;

public class RasaFactoryBuilderImpl extends RasaFactoryBuilder {

	@Override
	public RasaFactory getRasaFactory(Properties properties) {		
		return new RasaFactory() {
		    private final Logger logger = LoggerFactory.getLogger(getClass());
		    private HikariDataSource ds;
		    private ConnectionProvider connectionProvider;
		    
		    {
		        logger.info("Connecting to database...");

		        HikariConfig config = new HikariConfig(properties);

		        ds = new HikariDataSource(config);
		        connectionProvider = () -> ds.getConnection();

		        logger.info("Connected to database.");

		    }
		    
		    @Override
		    public CheckedLinkResourceImpl getCheckedLinkResource() {
		        return new CheckedLinkResourceImpl(connectionProvider);
		    }

		    @Override
		    public LinkToBeCheckedResourceImpl getLinkToBeCheckedResource() {
		        return new LinkToBeCheckedResourceImpl(connectionProvider);
		    }
		    
		    @Override
		    public void tearDown() {
		        this.ds.close();
		    }
		};
	}
}
