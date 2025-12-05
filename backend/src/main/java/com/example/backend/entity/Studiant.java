package com.example.backend.entity;

import com.example.backend.enumeration.Level;
import com.example.backend.enumeration.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Setter
@Getter
public class Studiant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Role role = Role.STUDIANT;

    public Studiant() {}

    public Studiant( String username, Level level) {
        this.username=username;
        this.level=level;
    }
    // Méthode otoString

    @Override
    public String toString() {
        return "Studiant{" +
                "id=" + this.id +
                ", username='" + this.username +
                ", level='" + this.level +
                '}';
    }
}
