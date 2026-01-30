package com.example.accessingdatamysql.dto;

import com.example.accessingdatamysql.Commande;
import com.example.accessingdatamysql.LigneCommande;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CommandeResponse {
    private Integer id;
    private UserInfo user;
    private String dateCommande;
    private String dateFormatted;
    private StatutInfo statut;
    private List<LigneInfo> lignes;
    private Double total;
    private Integer nombreArticles;

    public CommandeResponse(Commande commande) {
        this.id = commande.getId();
        this.user = new UserInfo(commande.getUser().getId(), commande.getUser().getName(), commande.getUser().getEmail());
        this.dateCommande = commande.getDateCommande().toString();
        this.dateFormatted = commande.getDateCommande().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        this.statut = new StatutInfo(
            commande.getStatut().name(),
            commande.getStatut().getLabel(),
            commande.getStatut().getColor()
        );
        this.lignes = commande.getLignes().stream()
            .map(LigneInfo::new)
            .collect(Collectors.toList());
        this.total = commande.getTotal();
        this.nombreArticles = commande.getLignes().stream()
            .mapToInt(LigneCommande::getQuantite)
            .sum();
    }

    // Getters
    public Integer getId() { return id; }
    public UserInfo getUser() { return user; }
    public String getDateCommande() { return dateCommande; }
    public String getDateFormatted() { return dateFormatted; }
    public StatutInfo getStatut() { return statut; }
    public List<LigneInfo> getLignes() { return lignes; }
    public Double getTotal() { return total; }
    public Integer getNombreArticles() { return nombreArticles; }

    // Inner classes for structured response
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

    public static class LigneInfo {
        private Integer id;
        private ProduitInfo produit;
        private Integer quantite;
        private Double prixUnitaire;
        private Double sousTotal;

        public LigneInfo(LigneCommande ligne) {
            this.id = ligne.getId();
            this.produit = new ProduitInfo(ligne.getProduit());
            this.quantite = ligne.getQuantite();
            this.prixUnitaire = ligne.getPrixUnitaire();
            this.sousTotal = ligne.getSousTotal();
        }

        public Integer getId() { return id; }
        public ProduitInfo getProduit() { return produit; }
        public Integer getQuantite() { return quantite; }
        public Double getPrixUnitaire() { return prixUnitaire; }
        public Double getSousTotal() { return sousTotal; }
    }

    public static class ProduitInfo {
        private Integer id;
        private String nom;
        private String region;
        private Integer annee;

        public ProduitInfo(com.example.accessingdatamysql.Produit produit) {
            this.id = produit.getId();
            this.nom = produit.getNom();
            this.region = produit.getRegion();
            this.annee = produit.getAnnee();
        }

        public Integer getId() { return id; }
        public String getNom() { return nom; }
        public String getRegion() { return region; }
        public Integer getAnnee() { return annee; }
    }
}
