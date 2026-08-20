package co.edu.uniajc.estudiante.opemay.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "compradores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comprador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 60)
    private String telefono;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false)
    private Boolean activo = true;
}
