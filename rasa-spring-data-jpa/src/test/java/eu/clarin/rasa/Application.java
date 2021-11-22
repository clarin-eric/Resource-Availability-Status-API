package eu.clarin.rasa;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import eu.clarin.rasa.data.DataConfiguration;
import eu.clarin.rasa.data.repositories.UrlRepository;

public class Application {

   public static void main(String[] args) {
      
      try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(DataConfiguration.class)){
         
         UrlRepository repository = context.getBean(UrlRepository.class);
         
         System.out.println(repository.findById(1L).get().getUrlContexts().size());
      }

   }

}
