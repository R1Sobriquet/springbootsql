package com.example.accessingdatamysql;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommandeRepository extends CrudRepository<Commande, Integer> {

    List<Commande> findByUserId(Integer userId);

    List<Commande> findByStatut(StatutCommande statut);

    List<Commande> findByUserIdOrderByDateCommandeDesc(Integer userId);
}
