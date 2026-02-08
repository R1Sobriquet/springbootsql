package com.example.accessingdatamysql.commande.dto;

import java.util.List;

public class CommandeRequest {
    private Integer userId;
    private List<LigneCommandeRequest> lignes;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<LigneCommandeRequest> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommandeRequest> lignes) {
        this.lignes = lignes;
    }
}
