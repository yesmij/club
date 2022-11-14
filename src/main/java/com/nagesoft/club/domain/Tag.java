package com.nagesoft.club.domain;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor @AllArgsConstructor @Builder
@Data @EqualsAndHashCode(of = "id")
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull @Column(unique = true)
    private String title;
}
