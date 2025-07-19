package com.codestorykh.user.entity;

import com.codestorykh.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tt_user")
public class User extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_image")
    private String userImage;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "login_attempt")
    private Integer loginAttempt = 0;

    @Column(name = "max_attempt")
    private Integer maxAttempt = 3;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "user_type", nullable = false)
    private String userType;

    private String gender;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "status")
    private String status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tt_user_role",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tt_user_group",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "group_id", referencedColumnName = "id")
    )
    private Set<Group> groups = new HashSet<>();

    // Helper method to add a role to the user
    public void addRole(Role role) {
        this.roles.add(role);
    }

    // Helper method to add a group to the user
    public void addGroup(Group group) {
        this.groups.add(group);
    }

}
