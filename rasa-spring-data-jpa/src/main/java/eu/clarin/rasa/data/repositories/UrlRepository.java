package eu.clarin.rasa.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import eu.clarin.rasa.data.entities.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

}
