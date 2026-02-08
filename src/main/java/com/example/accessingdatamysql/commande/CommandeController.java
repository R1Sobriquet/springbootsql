package com.example.accessingdatamysql.commande;

import com.example.accessingdatamysql.commande.dto.CommandeRequest;
import com.example.accessingdatamysql.commande.dto.CommandeResponse;
import com.example.accessingdatamysql.commande.dto.LigneCommandeRequest;
import com.example.accessingdatamysql.produit.Produit;
import com.example.accessingdatamysql.produit.ProduitRepository;
import com.example.accessingdatamysql.user.User;
import com.example.accessingdatamysql.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(path = "/api/commandes")
public class CommandeController {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProduitRepository produitRepository;

    @GetMapping
    public ResponseEntity<List<CommandeResponse>> getAllCommandes() {
        List<CommandeResponse> commandes = StreamSupport
            .stream(commandeRepository.findAll().spliterator(), false)
            .map(CommandeResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(commandes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommandeResponse> getCommandeById(@PathVariable Integer id) {
        Optional<Commande> commande = commandeRepository.findById(id);
        return commande.map(c -> ResponseEntity.ok(new CommandeResponse(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CommandeResponse>> getCommandesByUser(@PathVariable Integer userId) {
        List<CommandeResponse> commandes = commandeRepository.findByUserIdOrderByDateCommandeDesc(userId)
            .stream()
            .map(CommandeResponse::new)
            .collect(Collectors.toList());
        return ResponseEntity.ok(commandes);
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<CommandeResponse>> getCommandesByStatut(@PathVariable String statut) {
        try {
            StatutCommande statutCommande = StatutCommande.valueOf(statut.toUpperCase());
            List<CommandeResponse> commandes = commandeRepository.findByStatut(statutCommande)
                .stream()
                .map(CommandeResponse::new)
                .collect(Collectors.toList());
            return ResponseEntity.ok(commandes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createCommande(@RequestBody CommandeRequest request) {
        // Validate user
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé");
        }

        // Create commande
        Commande commande = new Commande();
        commande.setUser(userOpt.get());

        // Process order lines
        List<String> errors = new ArrayList<>();
        for (LigneCommandeRequest ligneReq : request.getLignes()) {
            Optional<Produit> produitOpt = produitRepository.findByNom(ligneReq.getNomProduit());
            if (produitOpt.isEmpty()) {
                errors.add("Produit non trouvé: " + ligneReq.getNomProduit());
                continue;
            }

            Produit produit = produitOpt.get();

            // Check stock
            if (produit.getStock() < ligneReq.getQuantite()) {
                errors.add("Stock insuffisant pour " + produit.getNom() +
                          " (disponible: " + produit.getStock() + ", demandé: " + ligneReq.getQuantite() + ")");
                continue;
            }

            // Create order line
            LigneCommande ligne = new LigneCommande(produit, ligneReq.getQuantite());
            commande.addLigne(ligne);

            // Update stock
            produit.setStock(produit.getStock() - ligneReq.getQuantite());
            produitRepository.save(produit);
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        if (commande.getLignes().isEmpty()) {
            return ResponseEntity.badRequest().body("La commande doit contenir au moins un produit");
        }

        Commande saved = commandeRepository.save(commande);
        return ResponseEntity.status(HttpStatus.CREATED).body(new CommandeResponse(saved));
    }

    @PutMapping("/{id}/statut")
    public ResponseEntity<?> updateStatut(@PathVariable Integer id, @RequestParam String statut) {
        Optional<Commande> commandeOpt = commandeRepository.findById(id);
        if (commandeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            StatutCommande nouveauStatut = StatutCommande.valueOf(statut.toUpperCase());
            Commande commande = commandeOpt.get();

            // If cancelling, restore stock
            if (nouveauStatut == StatutCommande.ANNULEE && commande.getStatut() != StatutCommande.ANNULEE) {
                for (LigneCommande ligne : commande.getLignes()) {
                    Produit produit = ligne.getProduit();
                    produit.setStock(produit.getStock() + ligne.getQuantite());
                    produitRepository.save(produit);
                }
            }

            commande.setStatut(nouveauStatut);
            Commande updated = commandeRepository.save(commande);
            return ResponseEntity.ok(new CommandeResponse(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Statut invalide: " + statut);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommande(@PathVariable Integer id) {
        Optional<Commande> commandeOpt = commandeRepository.findById(id);
        if (commandeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Commande commande = commandeOpt.get();

        // Restore stock before deleting
        for (LigneCommande ligne : commande.getLignes()) {
            Produit produit = ligne.getProduit();
            produit.setStock(produit.getStock() + ligne.getQuantite());
            produitRepository.save(produit);
        }

        commandeRepository.delete(commande);
        return ResponseEntity.ok("Commande supprimée");
    }

    @GetMapping("/statuts")
    public ResponseEntity<List<StatutInfo>> getStatuts() {
        List<StatutInfo> statuts = new ArrayList<>();
        for (StatutCommande s : StatutCommande.values()) {
            statuts.add(new StatutInfo(s.name(), s.getLabel(), s.getColor()));
        }
        return ResponseEntity.ok(statuts);
    }

    // Inner class for statut list response
    public static class StatutInfo {
        private String code;
        private String label;
        private String color;

        public StatutInfo(String code, String label, String color) {
            this.code = code;
            this.label = label;
            this.color = color;
        }

        public String getCode() { return code; }
        public String getLabel() { return label; }
        public String getColor() { return color; }
    }
}
