package eu.clarin.rasa.data.entities;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Entity
public class Status {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;   
   private String method;
   private Integer status;
   private String contentType;
   private Long byteSize;
   private Integer duration;
   @Column(nullable = false)
   private Timestamp checkingDate;
   private String message;
   private Integer redirectCount;
   private String category;
   
   @OneToOne
   @JoinColumn(name = "url_id")
   private Url url;

}
