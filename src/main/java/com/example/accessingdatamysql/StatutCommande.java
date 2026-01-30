package com.example.accessingdatamysql;

public enum StatutCommande {
    EN_ATTENTE("En attente", "#FFA500"),      // Orange
    VALIDEE("Validée", "#3B82F6"),            // Blue
    EN_PREPARATION("En préparation", "#8B5CF6"), // Purple
    EXPEDIEE("Expédiée", "#06B6D4"),          // Cyan
    LIVREE("Livrée", "#10B981"),              // Green
    ANNULEE("Annulée", "#EF4444");            // Red

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
