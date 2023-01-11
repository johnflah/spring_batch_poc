package com.jof.batch.entity;

import lombok.Data;

import javax.persistence.*;
@Entity
@Table
@Data
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "word")
    private String word;

    @Column(name = "flipped")
    private String flipped;
}
