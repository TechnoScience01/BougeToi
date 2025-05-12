package com.example.bougetoi;


public class UserInfo {

    private String prenom;
    private String nom;
    private String dateNaissance;
    private String genre;
    private float objectif_calorique;

    private String objectif_performance;
    private float taille;
    private String objectif;
    private String objectif_poids;

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public String getObjectif_performance() {
        return objectif_performance;
    }
    public void setObjectif_performance(String objectif_performance) {
        this.objectif_performance = objectif_performance;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public float getObjectif_calorique() {
        return objectif_calorique;
    }

    public void setObjectif_calorique(float objectif_calorique) {
        this.objectif_calorique = objectif_calorique;
    }

    public float getTaille() {
        return taille;
    }

    public void setTaille(float taille) {
        this.taille = taille;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public String getObjectif_poids() {
        return objectif_poids;
    }

    public void setObjectif_poids(String objectif_poids) {
        this.objectif_poids = objectif_poids;
    }

}
