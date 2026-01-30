package com.example.accessingdatamysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProduitRepositoryTest {

    @Autowired
    private ProduitRepository produitRepository;

    private Produit produit;

    @BeforeEach
    void setUp() {
        produitRepository.deleteAll();

        produit = new Produit();
        produit.setNom("Cote du Rhone");
        produit.setAnnee(2019);
        produit.setRegion("Rhone");
        produit.setPrix(10.0);
        produit.setStock(40);
        produit.setNotes(new ArrayList<>(Arrays.asList("sec", "epices")));
    }

    @Test
    void shouldSaveProduit() {
        Produit saved = produitRepository.save(produit);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNom()).isEqualTo("Cote du Rhone");
        assertThat(saved.getAnnee()).isEqualTo(2019);
        assertThat(saved.getRegion()).isEqualTo("Rhone");
        assertThat(saved.getPrix()).isEqualTo(10.0);
        assertThat(saved.getStock()).isEqualTo(40);
        assertThat(saved.getNotes()).containsExactly("sec", "epices");
    }

    @Test
    void shouldFindProduitByNom() {
        produitRepository.save(produit);

        Optional<Produit> found = produitRepository.findByNom("Cote du Rhone");

        assertThat(found).isPresent();
        assertThat(found.get().getNom()).isEqualTo("Cote du Rhone");
        assertThat(found.get().getAnnee()).isEqualTo(2019);
    }

    @Test
    void shouldReturnEmptyWhenProduitNotFound() {
        Optional<Produit> found = produitRepository.findByNom("Inexistant");

        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllProduits() {
        produitRepository.save(produit);

        Produit produit2 = new Produit();
        produit2.setNom("Bordeaux");
        produit2.setAnnee(2018);
        produit2.setRegion("Bordeaux");
        produit2.setPrix(25.0);
        produit2.setStock(20);
        produit2.setNotes(new ArrayList<>(Arrays.asList("tannique", "fruits rouges")));
        produitRepository.save(produit2);

        List<Produit> produits = (List<Produit>) produitRepository.findAll();

        assertThat(produits).hasSize(2);
    }

    @Test
    void shouldUpdateProduit() {
        Produit saved = produitRepository.save(produit);

        saved.setPrix(15.0);
        saved.setStock(30);
        Produit updated = produitRepository.save(saved);

        assertThat(updated.getPrix()).isEqualTo(15.0);
        assertThat(updated.getStock()).isEqualTo(30);
    }

    @Test
    void shouldDeleteProduitByNom() {
        produitRepository.save(produit);

        produitRepository.deleteByNom("Cote du Rhone");

        Optional<Produit> found = produitRepository.findByNom("Cote du Rhone");
        assertThat(found).isEmpty();
    }

    @Test
    void shouldDeleteProduitById() {
        Produit saved = produitRepository.save(produit);

        produitRepository.deleteById(saved.getId());

        Optional<Produit> found = produitRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCountProduits() {
        produitRepository.save(produit);

        Produit produit2 = new Produit();
        produit2.setNom("Champagne");
        produit2.setAnnee(2020);
        produit2.setRegion("Champagne");
        produit2.setPrix(45.0);
        produit2.setStock(15);
        produitRepository.save(produit2);

        long count = produitRepository.count();

        assertThat(count).isEqualTo(2);
    }
}
