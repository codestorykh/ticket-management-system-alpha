package com.codestorykh.user.service;

import com.codestorykh.common.criteria.BaseSearchCriteria;
import com.codestorykh.common.dto.PageableRequestVO;
import com.codestorykh.common.dto.PageableResponseVO;
import com.codestorykh.user.dto.request.UserFilterRequest;
import com.codestorykh.user.entity.User;

public interface UserSearchService {
    PageableResponseVO<User> searchUsers(UserFilterRequest filterRequest);

    PageableResponseVO<User> searchUsersWithCriteria(BaseSearchCriteria searchCriteria, PageableRequestVO pageable);
} 