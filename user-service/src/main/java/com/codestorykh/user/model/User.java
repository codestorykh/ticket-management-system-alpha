package com.codestorykh.user.model;

import com.codestorykh.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "tt_user")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;
}
