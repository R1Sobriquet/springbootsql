package com.example.accessingdatamysql.commande.dto;

import com.example.accessingdatamysql.commande.Commande;
import com.example.accessingdatamysql.commande.LigneCommande;
import com.example.accessingdatamysql.commande.StatutCommande;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CommandeResponse {
    private Integer id;
    private Integer userId;
    private String userName;
    private LocalDateTime dateCommande;
    private StatutCommande statut;
    private String statutLabel;
    private String statutColor;
    private List<LigneResponse> lignes;
    private Double total;

    public CommandeResponse(Commande commande) {
        this.id = commande.getId();
        this.userId = commande.getUser().getId();
        this.userName = commande.getUser().getName();
        this.dateCommande = commande.getDateCommande();
        this.statut = commande.getStatut();
        this.statutLabel = commande.getStatut().getLabel();
        this.statutColor = commande.getStatut().getColor();
        this.lignes = commande.getLignes().stream()
                .map(LigneResponse::new)
                .collect(Collectors.toList());
        this.total = this.lignes.stream()
                .mapToDouble(l -> l.getPrixUnitaire() * l.getQuantite())
                .sum();
    }

    public static class LigneResponse {
        private Integer id;
        private Integer produitId;
        private String produitNom;
        private Integer quantite;
        private Double prixUnitaire;

        public LigneResponse(LigneCommande ligne) {
            this.id = ligne.getId();
            this.produitId = ligne.getProduit().getId();
            this.produitNom = ligne.getProduit().getNom();
            this.quantite = ligne.getQuantite();
            this.prixUnitaire = ligne.getPrixUnitaire();
        }

        public Integer getId() {
            return id;
        }

        public Integer getProduitId() {
            return produitId;
        }

        public String getProduitNom() {
            return produitNom;
        }

        public Integer getQuantite() {
            return quantite;
        }

        public Double getPrixUnitaire() {
            return prixUnitaire;
        }
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public StatutCommande getStatut() {
        return statut;
    }

    public String getStatutLabel() {
        return statutLabel;
    }

    public String getStatutColor() {
        return statutColor;
    }

    public List<LigneResponse> getLignes() {
        return lignes;
    }

    public Double getTotal() {
        return total;
    }
}
