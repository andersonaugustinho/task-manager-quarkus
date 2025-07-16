package com.example.taskmanager; // Certifique-se de que o pacote está correto

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Task extends PanacheEntity {
    @Column(length = 255, nullable = false)
    public String description;

    @Column(nullable = false)
    public boolean completed;

    // O PanacheEntity já fornece métodos estáticos para CRUD (persist, findById, listAll, deleteById)
    // Não é necessário getters/setters ou construtores padrão para uso básico com Panache.
}