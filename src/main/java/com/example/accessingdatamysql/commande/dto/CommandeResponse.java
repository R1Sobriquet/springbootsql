package com.example.accessingdatamysql.commande.dto;

import com.example.accessingdatamysql.commande.Commande;
import com.example.accessingdatamysql.commande.LigneCommande;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CommandeResponse {
    private Integer id;
    private UserInfo user;
    private LocalDateTime dateCommande;
    private String dateFormatted;
    private StatutInfo statut;
    private List<LigneResponse> lignes;
    private Double total;
    private Integer nombreArticles;

    public CommandeResponse(Commande commande) {
        this.id = commande.getId();
        this.user = new UserInfo(
            commande.getUser().getId(),
            commande.getUser().getName(),
            commande.getUser().getEmail()
        );
        this.dateCommande = commande.getDateCommande();
        this.dateFormatted = commande.getDateCommande()
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.statut = new StatutInfo(
            commande.getStatut().name(),
            commande.getStatut().getLabel(),
            commande.getStatut().getColor()
        );
        this.lignes = commande.getLignes().stream()
                .map(LigneResponse::new)
                .collect(Collectors.toList());
        this.total = this.lignes.stream()
                .mapToDouble(LigneResponse::getSousTotal)
                .sum();
        this.nombreArticles = this.lignes.stream()
                .mapToInt(LigneResponse::getQuantite)
                .sum();
    }

    // Nested class for user info
    public static class UserInfo {
        private Integer id;
        private String name;
        private String email;

        public UserInfo(Integer id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Integer getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
    }

    // Nested class for statut info
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

    // Nested class for ligne response
    public static class LigneResponse {
        private Integer id;
        private ProduitInfo produit;
        private Integer quantite;
        private Double prixUnitaire;
        private Double sousTotal;

        public LigneResponse(LigneCommande ligne) {
            this.id = ligne.getId();
            this.produit = new ProduitInfo(
                ligne.getProduit().getId(),
                ligne.getProduit().getNom()
            );
            this.quantite = ligne.getQuantite();
            this.prixUnitaire = ligne.getPrixUnitaire();
            this.sousTotal = this.prixUnitaire * this.quantite;
        }

        public Integer getId() { return id; }
        public ProduitInfo getProduit() { return produit; }
        public Integer getQuantite() { return quantite; }
        public Double getPrixUnitaire() { return prixUnitaire; }
        public Double getSousTotal() { return sousTotal; }
    }

    // Nested class for produit info in ligne
    public static class ProduitInfo {
        private Integer id;
        private String nom;

        public ProduitInfo(Integer id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        public Integer getId() { return id; }
        public String getNom() { return nom; }
    }

    // Getters
    public Integer getId() { return id; }
    public UserInfo getUser() { return user; }
    public LocalDateTime getDateCommande() { return dateCommande; }
    public String getDateFormatted() { return dateFormatted; }
    public StatutInfo getStatut() { return statut; }
    public List<LigneResponse> getLignes() { return lignes; }
    public Double getTotal() { return total; }
    public Integer getNombreArticles() { return nombreArticles; }
}
