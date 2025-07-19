package com.codestorykh.user.entity;

import com.codestorykh.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "tt_permission")
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    private String status;

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    private Set<Group> groups = new HashSet<>();

    // Helper methods
    public void addRole(Role role) {
        this.roles.add(role);
        role.getPermissions().add(this);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
        role.getPermissions().remove(this);
    }

    public void addGroup(Group group) {
        this.groups.add(group);
        group.getPermissions().add(this);
    }

    public void removeGroup(Group group) {
        this.groups.remove(group);
        group.getPermissions().remove(this);
    }
}
