package com.codestorykh.user.service;

import com.codestorykh.user.dto.request.CreateGroupRequestDTO;
import com.codestorykh.user.dto.request.GroupMemberRequest;
import com.codestorykh.user.dto.response.GroupResponseDTO;

public interface GroupService {

    GroupResponseDTO createGroup(CreateGroupRequestDTO request);

    GroupResponseDTO updateGroup(Long id, CreateGroupRequestDTO request);

    GroupResponseDTO addMembersToGroup(Long groupId, GroupMemberRequest groupMemberRequest);

    GroupResponseDTO removeMembersFromGroup(Long groupId, GroupMemberRequest groupMemberRequest);
}
