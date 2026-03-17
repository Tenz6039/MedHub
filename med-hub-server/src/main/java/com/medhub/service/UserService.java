package com.medhub.service;

import com.medhub.dto.UserLoginDTO;
import com.medhub.entity.User;

public interface UserService {
    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    User login(UserLoginDTO userLoginDTO);

    /**
     * 用户退出
     */
    void logout();
}
