package eu.clarin.cmdi.rasa.helpers.impl;

import java.util.Properties;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;


import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.RasaFactoryBuilder;
import eu.clarin.cmdi.rasa.linkResources.impl.CheckedLinkResourceImpl;
import eu.clarin.cmdi.rasa.linkResources.impl.LinkToBeCheckedResourceImpl;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;

@Slf4j
public class RasaFactoryBuilderImpl implements RasaFactoryBuilder {

   @Override
   public RasaFactory getRasaFactory() {
      return new RasaFactory() {
         private HikariDataSource ds;
         
         @Override
         public RasaFactory init(Properties properties) {
            log.info("Connecting to database...");

            HikariConfig config = new HikariConfig(properties);

            ds = new HikariDataSource(config);
            
            return this;
         }

         @Override
         public CheckedLinkResourceImpl getCheckedLinkResource() {
            return new CheckedLinkResourceImpl(() -> {
               try {
                  return ds.getConnection();
               }
               catch (SQLException e) {
                  log.error("can't get connection. You might not have called the init()-method");
                  throw new RuntimeException();
               }
            });
         }

         @Override
         public LinkToBeCheckedResourceImpl getLinkToBeCheckedResource() {
            return new LinkToBeCheckedResourceImpl(() -> {
               try {
                  return ds.getConnection();
               }
               catch (SQLException e) {
                  log.error("can't get connection. You might not have called the init()-method");
                  throw new RuntimeException();
               }
            });
         }

         @Override
         public void tearDown() {
            this.ds.close();
         }

         @Override
         public void writeStatusSummary(Writer writer) throws IOException {
            final HikariPoolMXBean hikariPoolMXBean = ds.getHikariPoolMXBean();
            writer.write(String.format(
                  "HikariDataSource <active connections: %d, idle connections: %d, threads awaiting connection: %d, total connections: %d>",
                  hikariPoolMXBean.getActiveConnections(), hikariPoolMXBean.getIdleConnections(),
                  hikariPoolMXBean.getThreadsAwaitingConnection(), hikariPoolMXBean.getTotalConnections()));
            writer.flush();
         }
      };
   }

   @Override
   public RasaFactory getRasaFactory(Properties properties) {
      return getRasaFactory().init(properties);
   }
}
