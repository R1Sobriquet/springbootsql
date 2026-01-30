package com.example.accessingdatamysql;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProduitRepository extends CrudRepository<Produit, Integer> {

    Optional<Produit> findByNom(String nom);

    void deleteByNom(String nom);
}
