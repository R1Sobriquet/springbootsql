package com.example.accessingdatamysql;

import com.example.accessingdatamysql.commande.Commande;
import com.example.accessingdatamysql.commande.CommandeRepository;
import com.example.accessingdatamysql.commande.LigneCommande;
import com.example.accessingdatamysql.commande.StatutCommande;
import com.example.accessingdatamysql.produit.Produit;
import com.example.accessingdatamysql.produit.ProduitRepository;
import com.example.accessingdatamysql.user.User;
import com.example.accessingdatamysql.user.UserRepository;

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
class CommandeRepositoryTest {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProduitRepository produitRepository;

    private User user;
    private Produit produit1;
    private Produit produit2;

    @BeforeEach
    void setUp() {
        commandeRepository.deleteAll();
        produitRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user = userRepository.save(user);

        produit1 = new Produit();
        produit1.setNom("Bordeaux");
        produit1.setAnnee(2019);
        produit1.setRegion("Bordeaux");
        produit1.setPrix(25.0);
        produit1.setStock(50);
        produit1.setNotes(new ArrayList<>(Arrays.asList("tannique", "fruits rouges")));
        produit1 = produitRepository.save(produit1);

        produit2 = new Produit();
        produit2.setNom("Champagne");
        produit2.setAnnee(2020);
        produit2.setRegion("Champagne");
        produit2.setPrix(45.0);
        produit2.setStock(30);
        produit2.setNotes(new ArrayList<>(Arrays.asList("bulles", "frais")));
        produit2 = produitRepository.save(produit2);
    }

    @Test
    void shouldSaveCommande() {
        Commande commande = new Commande();
        commande.setUser(user);

        LigneCommande ligne = new LigneCommande(produit1, 2);
        commande.addLigne(ligne);

        Commande saved = commandeRepository.save(commande);

        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser().getId()).isEqualTo(user.getId());
        assertThat(saved.getStatut()).isEqualTo(StatutCommande.EN_ATTENTE);
        assertThat(saved.getLignes()).hasSize(1);
    }

    @Test
    void shouldFindCommandeById() {
        Commande commande = new Commande();
        commande.setUser(user);
        commande.addLigne(new LigneCommande(produit1, 1));
        Commande saved = commandeRepository.save(commande);

        Optional<Commande> found = commandeRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getName()).isEqualTo("John Doe");
    }

    @Test
    void shouldFindCommandesByUserId() {
        Commande commande1 = new Commande();
        commande1.setUser(user);
        commande1.addLigne(new LigneCommande(produit1, 1));
        commandeRepository.save(commande1);

        Commande commande2 = new Commande();
        commande2.setUser(user);
        commande2.addLigne(new LigneCommande(produit2, 2));
        commandeRepository.save(commande2);

        List<Commande> commandes = commandeRepository.findByUserId(user.getId());

        assertThat(commandes).hasSize(2);
    }

    @Test
    void shouldFindCommandesByStatut() {
        Commande commande1 = new Commande();
        commande1.setUser(user);
        commande1.addLigne(new LigneCommande(produit1, 1));
        commandeRepository.save(commande1);

        Commande commande2 = new Commande();
        commande2.setUser(user);
        commande2.setStatut(StatutCommande.VALIDEE);
        commande2.addLigne(new LigneCommande(produit2, 2));
        commandeRepository.save(commande2);

        List<Commande> enAttente = commandeRepository.findByStatut(StatutCommande.EN_ATTENTE);
        List<Commande> validees = commandeRepository.findByStatut(StatutCommande.VALIDEE);

        assertThat(enAttente).hasSize(1);
        assertThat(validees).hasSize(1);
    }

    @Test
    void shouldCalculateTotal() {
        Commande commande = new Commande();
        commande.setUser(user);
        commande.addLigne(new LigneCommande(produit1, 2)); // 25 * 2 = 50
        commande.addLigne(new LigneCommande(produit2, 1)); // 45 * 1 = 45
        Commande saved = commandeRepository.save(commande);

        assertThat(saved.getTotal()).isEqualTo(95.0);
    }

    @Test
    void shouldUpdateCommandeStatut() {
        Commande commande = new Commande();
        commande.setUser(user);
        commande.addLigne(new LigneCommande(produit1, 1));
        Commande saved = commandeRepository.save(commande);

        saved.setStatut(StatutCommande.VALIDEE);
        Commande updated = commandeRepository.save(saved);

        assertThat(updated.getStatut()).isEqualTo(StatutCommande.VALIDEE);
    }

    @Test
    void shouldDeleteCommande() {
        Commande commande = new Commande();
        commande.setUser(user);
        commande.addLigne(new LigneCommande(produit1, 1));
        Commande saved = commandeRepository.save(commande);

        commandeRepository.deleteById(saved.getId());

        Optional<Commande> found = commandeRepository.findById(saved.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void shouldCountCommandes() {
        Commande commande1 = new Commande();
        commande1.setUser(user);
        commande1.addLigne(new LigneCommande(produit1, 1));
        commandeRepository.save(commande1);

        Commande commande2 = new Commande();
        commande2.setUser(user);
        commande2.addLigne(new LigneCommande(produit2, 1));
        commandeRepository.save(commande2);

        long count = commandeRepository.count();

        assertThat(count).isEqualTo(2);
    }
}
