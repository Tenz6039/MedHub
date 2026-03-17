package com.medhub.controller.user;

import com.medhub.dto.UserLoginDTO;
import com.medhub.entity.User;
import com.medhub.properties.JwtProperties;
import com.medhub.result.Result;
import com.medhub.service.UserService;
import com.medhub.utils.JwtUtil;
import com.medhub.vo.UserLoginVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user/user")
@Api(tags = "C端用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        //微信登录
        User user = userService.login(userLoginDTO);
        //为用户生成jwt
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        //生成jwt
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
        //返回jwt - 适配前端期望的数据结构
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
                
        return Result.success(data);
    }

    /**
     * 用户退出
     * @return
     */
    @PostMapping("/logout")
    public Result logout() {
        log.info("用户退出");
        userService.logout();
        return Result.success();
    }
}
