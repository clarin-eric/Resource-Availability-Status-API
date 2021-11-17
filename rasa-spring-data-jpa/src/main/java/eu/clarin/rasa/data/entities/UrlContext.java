package eu.clarin.rasa.data.entities;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class UrlContext {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   @ManyToOne
   @JoinColumn(name = "url_id")
   private Url url;
   
   @ManyToOne
   @JoinColumn(name = "context_id")
   private Context context;
   
   private Timestamp ingestionDate;
   
   private Boolean active;

}
