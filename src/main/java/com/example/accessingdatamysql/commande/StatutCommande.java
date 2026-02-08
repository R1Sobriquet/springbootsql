package com.example.accessingdatamysql.commande;

public enum StatutCommande {
    EN_ATTENTE("En attente", "#FFA500"),
    VALIDEE("Validée", "#3B82F6"),
    EN_PREPARATION("En préparation", "#8B5CF6"),
    EXPEDIEE("Expédiée", "#06B6D4"),
    LIVREE("Livrée", "#10B981"),
    ANNULEE("Annulée", "#EF4444");

    private final String label;
    private final String color;

    StatutCommande(String label, String color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public String getColor() {
        return color;
    }
}
