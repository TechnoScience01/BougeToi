package com.example.bougetoi;

import java.util.List;

public class Seance {
    private String nom;
    private String date;
    private int duree;
    private String description;
    private String type;
    private List<String> exercices;

    public Seance(String nom, String date, int duree, String description, String type, List<String> exercices) {
        this.nom = nom;
        this.date = date;
        this.duree = duree;
        this.description = description;
        this.type = type;
        this.exercices = exercices;
    }

    // Getters and setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getExercices() {
        return exercices;
    }

    public void setExercices(List<String> exercices) {
        this.exercices = exercices;
    }
}