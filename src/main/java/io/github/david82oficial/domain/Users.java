package io.github.david82oficial.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
@Table(name = "users") //opcional, pois o nome da tabela no banco é o mesmo que o nome da classe
public class Users {
    // O Id pode ser omitido, pois o Panache ja o implementa
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Name is required!")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Age is required!")
    @Column
    private Integer age;
}
