package com.codestorykh.user.service.impl;

import com.codestorykh.user.constant.Constant;
import com.codestorykh.user.dto.request.CreateGroupRequestDTO;
import com.codestorykh.user.dto.request.GroupMemberRequest;
import com.codestorykh.user.dto.response.GroupResponseDTO;
import com.codestorykh.user.entity.Group;
import com.codestorykh.user.entity.Permission;
import com.codestorykh.user.entity.Role;
import com.codestorykh.user.entity.User;
import com.codestorykh.user.repository.GroupRepository;
import com.codestorykh.user.repository.RoleRepository;
import com.codestorykh.user.repository.UserRepository;
import com.codestorykh.user.service.GroupService;
import com.codestorykh.user.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final PermissionService permissionService;


    @Override
    public GroupResponseDTO createGroup(CreateGroupRequestDTO request) {
        if (groupRepository.existsByName(request.getName())) {
            throw new RuntimeException("Group with name '" + request.getName() + "' already exists");
        }

        Set<Role> roles = request.getRoles() != null
                ? new HashSet<>(roleRepository.findByNameIn(request.getRoles()))
                : Set.of();
        Set<Permission> permissions = request.getPermissions() != null
                ? new HashSet<>(permissionService.getPermissionsByNameIn(request.getPermissions()))
                : Set.of();

        Group group = mapToGroup(request);
        group.setRoles(roles);
        group.setPermissions(permissions);

        groupRepository.save(group);
        return mapToDto(group);
    }

    @Override
    public GroupResponseDTO updateGroup(Long groupId, CreateGroupRequestDTO request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        if (request.getName() != null && !request.getName().equals(group.getName())) {
            if (groupRepository.existsByName(request.getName())) {
                throw new RuntimeException("Group with name '" + request.getName() + "' already exists");
            }
            group.setName(request.getName());
        }

        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }

        if (request.getRoles() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findByNameIn(request.getRoles()));
            group.setRoles(roles);
        }

        if (request.getPermissions() != null) {
            Set<Permission> permissions = new HashSet<>(permissionService.getPermissionsByNameIn(request.getPermissions()));
            group.setPermissions(permissions);
        }

        if (StringUtils.hasText(request.getStatus())) {
            group.setStatus(request.getStatus());
        }
        groupRepository.save(group);

        return mapToDto(group);
    }

    @Override
    public GroupResponseDTO addMembersToGroup(Long groupId, GroupMemberRequest groupMemberRequest) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        List<User> users = userRepository.findAllById(groupMemberRequest.getUserIds());

        for (User user : users) {
            if (!group.getUsers().contains(user)) {
                group.addUser(user);
            }
        }

        groupRepository.save(group);

        return mapToDto(group);
    }

    @Override
    public GroupResponseDTO removeMembersFromGroup(Long groupId, GroupMemberRequest groupMemberRequest) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));

        List<User> users = userRepository.findAllById(groupMemberRequest.getUserIds());

        for (User user : users) {
            group.removeUser(user);
        }

        Group updatedGroup = groupRepository.save(group);
        return mapToDto(updatedGroup);
    }


    private GroupResponseDTO mapToDto(Group group) {
        return GroupResponseDTO.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .roles(group.getRoles().stream().map(Role::getName).collect(java.util.stream.Collectors.toSet()))
                .permissions(group.getPermissions().stream().map(Permission::getName).collect(java.util.stream.Collectors.toSet()))
                .status(group.getStatus())
                .createdBy(group.getCreatedBy())
                .updatedBy(group.getUpdatedBy())
                .createdAt(group.getCreatedAt())
                .updatedAt(group.getUpdatedAt())
                .memberCount(group.getUsers().size())
                .build();
    }

    private Group mapToGroup(CreateGroupRequestDTO request) {
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setStatus(Constant.ACTIVE);

        return group;
    }

}
