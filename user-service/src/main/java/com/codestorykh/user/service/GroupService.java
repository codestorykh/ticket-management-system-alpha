package com.codestorykh.user.service;

import com.codestorykh.common.exception.ResponseErrorTemplate;
import com.codestorykh.user.dto.request.CreateGroupRequest;
import com.codestorykh.user.dto.request.GroupMemberRequest;

public interface GroupService {

    ResponseErrorTemplate createGroup(CreateGroupRequest request);

    ResponseErrorTemplate updateGroup(Long id, CreateGroupRequest request);

    ResponseErrorTemplate addMembersToGroup(Long groupId, GroupMemberRequest groupMemberRequest);

    ResponseErrorTemplate removeMembersFromGroup(Long groupId, GroupMemberRequest groupMemberRequest);
}
