package eu.clarin.rasa.data.entities;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
@Table(indexes = {@Index(columnList = "source, record, providerGroup_id, expectedMimeType", unique = true)})
public class Context {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
   
   private String source;
   
   private String record;
   
   @OneToOne(optional = true)
   @JoinColumn(name = "providerGroup_id")
   private ProviderGroup providerGroup;
   
   private String expectedMimeType;
   
   @OneToMany
   private Set<UrlContext> urlContexts;

}
