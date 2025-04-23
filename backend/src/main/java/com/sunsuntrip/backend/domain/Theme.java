package com.sunsuntrip.backend.domain;

import jakarta.persistence.*;

@Entity
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // 예: "자연", "역사", "휴식", "온천", "엔터테인먼트"
}

