package com.codestorykh.user.controller;

import com.codestorykh.common.dto.ApiResponse;
import com.codestorykh.user.dto.request.CreateGroupRequestDTO;
import com.codestorykh.user.dto.request.GroupMemberRequest;
import com.codestorykh.user.dto.response.GroupResponseDTO;
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
    public ResponseEntity<ApiResponse<GroupResponseDTO>> createGroup(@Valid @RequestBody CreateGroupRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success("Group created successfully", groupService.createGroup(request)));
    }

    @PutMapping("/{groupId}")
    public ResponseEntity<ApiResponse<GroupResponseDTO>> updateGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateGroupRequestDTO request) {
        return ResponseEntity.ok(ApiResponse.success("Group updated successfully",
                groupService.updateGroup(groupId, request)));
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<GroupResponseDTO>> addMembersToGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Members added to group successfully",
                groupService.addMembersToGroup(groupId, request)));
    }

    @DeleteMapping("/{groupId}/members")
    public ResponseEntity<ApiResponse<GroupResponseDTO>> removeMembersFromGroup(
            @PathVariable Long groupId,
            @Valid @RequestBody GroupMemberRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Members removed from group successfully",
                groupService.removeMembersFromGroup(groupId, request)));
    }

} 