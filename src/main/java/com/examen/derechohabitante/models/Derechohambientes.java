package com.examen.derechohabitante.models;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "derechohambientes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Derechohambientes {
    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String ciudad;

    @Column(precision = 19, scale = 2)
    private BigDecimal importe;

    @Column(nullable = false, length = 16)
    private String cuenta;
}
