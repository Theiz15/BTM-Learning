package com.learning.btmlearning.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Category {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id ;

    String name ;

    String slug ;

    String description ;

    String iconUrl ;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "category")
    @Builder.Default
    List<Course> courses = new ArrayList<>();
}
