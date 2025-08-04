package com.umitakbulut.reference_manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "station")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Station implements Serializable {

    @Serial
    private static final long serialVersionUID = -525872126807005640L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name ="code", nullable = false, unique = true)
    private String code;

    @Column(name ="name", nullable = false , unique = true)
    private String name;

    @Column(name = "city", nullable = false, unique = true)
    private String city;

    @Column(name = "country", nullable = false, unique = true)
    private String country;
}
