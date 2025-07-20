package com.codestorykh.user.controller;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.CreateGroupRequest;
import com.codestorykh.user.dto.request.GroupMemberRequest;
import com.codestorykh.user.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping
    public ResponseEntity<ResponseErrorTemplate> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.createGroup(request));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ResponseErrorTemplate> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateGroupRequest request) {
        return ResponseEntity.ok(groupService.updateGroup(groupId, request));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<ResponseErrorTemplate> addMembersToGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request) {
        return ResponseEntity.ok(groupService.addMembersToGroup(groupId, request));
    }

    @DeleteMapping("/{groupId}/members")
    public ResponseEntity<ResponseErrorTemplate> removeMembersFromGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request) {
        return ResponseEntity.ok(groupService.removeMembersFromGroup(groupId, request));
    }

} 