package com.examen.productos.entitys;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "derechohabientes")
public class Derechohabiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String nombre;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal importe;

    @Column(nullable = false)
    private Long cuenta;

    public Derechohabiente() {}

    public Derechohabiente(Long id, String nombre, BigDecimal importe, Long cuenta) {
        this.id = id;
        this.nombre = nombre;
        this.importe = importe;
        this.cuenta = cuenta;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getImporte() { return importe; }
    public void setImporte(BigDecimal importe) { this.importe = importe; }

    public Long getCuenta() { return cuenta; }
    public void setCuenta(Long cuenta) { this.cuenta = cuenta; }
}
