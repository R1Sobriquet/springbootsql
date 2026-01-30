package com.example.accessingdatamysql.dto;

public class LigneCommandeRequest {
    private String nomProduit;
    private Integer quantite;

    public LigneCommandeRequest() {
    }

    public LigneCommandeRequest(String nomProduit, Integer quantite) {
        this.nomProduit = nomProduit;
        this.quantite = quantite;
    }

    public String getNomProduit() {
        return nomProduit;
    }

    public void setNomProduit(String nomProduit) {
        this.nomProduit = nomProduit;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }
}
