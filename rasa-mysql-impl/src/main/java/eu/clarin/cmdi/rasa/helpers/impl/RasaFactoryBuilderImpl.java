package eu.clarin.cmdi.rasa.helpers.impl;

import java.util.Properties;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;


import eu.clarin.cmdi.rasa.helpers.RasaFactory;
import eu.clarin.cmdi.rasa.helpers.RasaFactoryBuilder;
import eu.clarin.cmdi.rasa.linkResources.impl.CheckedLinkResourceImpl;
import eu.clarin.cmdi.rasa.linkResources.impl.LinkToBeCheckedResourceImpl;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RasaFactoryBuilderImpl implements RasaFactoryBuilder {

   @Override
   public RasaFactory getRasaFactory() {
      return new RasaFactory() {
         private DataSource datasource;
         
         @Override
         public RasaFactory init(Properties properties) {
            
            log.info("Connecting to database...");

            HikariConfig config = new HikariConfig(properties);

            return init(new HikariDataSource(config));
         }
         
         @Override
         public RasaFactory init(DataSource dataSource) {

            datasource = dataSource;
            
            return this;
         }

         @Override
         public CheckedLinkResourceImpl getCheckedLinkResource() {
            return new CheckedLinkResourceImpl(this.datasource);
         }

         @Override
         public LinkToBeCheckedResourceImpl getLinkToBeCheckedResource() {
            return new LinkToBeCheckedResourceImpl(this.datasource);
         }

         @Override
         public void tearDown() {
            
            try {
               HikariDataSource.class.cast(this.datasource).close();
            }
            catch(ClassCastException ex) {
               log.error("datasource not an instance of HikariDataSource");
            }

         }
      };
   }

   @Override
   public RasaFactory getRasaFactory(Properties properties) {
      return getRasaFactory().init(properties);
   }
}
