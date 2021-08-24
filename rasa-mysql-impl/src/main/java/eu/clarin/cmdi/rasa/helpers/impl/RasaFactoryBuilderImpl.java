package eu.clarin.cmdi.rasa.helpers.impl;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import eu.clarin.cmdi.rasa.helpers.ConnectionProvider;
import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.RasaFactoryBuilder;
import eu.clarin.cmdi.rasa.linkResources.impl.CheckedLinkResourceImpl;
import eu.clarin.cmdi.rasa.linkResources.impl.LinkToBeCheckedResourceImpl;
import java.io.IOException;
import java.io.Writer;

public class RasaFactoryBuilderImpl implements RasaFactoryBuilder {

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
                    
                    @Override
                    public void writeStatusSummary(Writer writer) throws IOException {
                        final HikariPoolMXBean hikariPoolMXBean = ds.getHikariPoolMXBean();
                        writer.write(String.format("HikariDataSource <active connections: %d, idle connections: %d, threads awaiting connection: %d, total connections: %d>",
                                hikariPoolMXBean.getActiveConnections(),
                                hikariPoolMXBean.getIdleConnections(), 
                                hikariPoolMXBean.getThreadsAwaitingConnection(),
                                hikariPoolMXBean.getTotalConnections()));
                        writer.flush();
                    }
		};
	}
}
