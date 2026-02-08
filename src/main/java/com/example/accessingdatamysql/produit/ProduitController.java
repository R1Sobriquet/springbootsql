package com.example.accessingdatamysql.produit;

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
import org.springframework.web.bind.annotation.RestController;

import jakarta.transaction.Transactional;

import java.util.Optional;

@RestController
@RequestMapping(path = "/catalogue")
public class ProduitController {

    @Autowired
    private ProduitRepository produitRepository;

    @GetMapping(path = "/produits")
    public Iterable<Produit> getAllProduits() {
        return produitRepository.findAll();
    }

    @GetMapping(path = "/produits/{nom}")
    public ResponseEntity<Produit> getProduitByNom(@PathVariable String nom) {
        Optional<Produit> produit = produitRepository.findByNom(nom);
        return produit.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(path = "/produits")
    public ResponseEntity<Produit> addProduit(@RequestBody Produit produit) {
        Produit saved = produitRepository.save(produit);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(path = "/produits/{nom}")
    public ResponseEntity<Produit> updateProduit(@PathVariable String nom, @RequestBody Produit produitDetails) {
        Optional<Produit> optionalProduit = produitRepository.findByNom(nom);

        if (optionalProduit.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Produit produit = optionalProduit.get();
        produit.setNom(produitDetails.getNom());
        produit.setAnnee(produitDetails.getAnnee());
        produit.setRegion(produitDetails.getRegion());
        produit.setPrix(produitDetails.getPrix());
        produit.setStock(produitDetails.getStock());
        produit.setNotes(produitDetails.getNotes());

        Produit updated = produitRepository.save(produit);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping(path = "/produits/{nom}")
    @Transactional
    public ResponseEntity<String> deleteProduit(@PathVariable String nom) {
        Optional<Produit> produit = produitRepository.findByNom(nom);

        if (produit.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        produitRepository.deleteByNom(nom);
        return ResponseEntity.ok("Produit supprime");
    }
}
