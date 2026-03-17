package com.medhub.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.medhub.constant.MessageConstant;
import com.medhub.context.BaseContext;
import com.medhub.dto.UserLoginDTO;
import com.medhub.entity.User;
import com.medhub.exception.LoginFailedException;
import com.medhub.mapper.UserMapper;
import com.medhub.properties.WeChatProperties;
import com.medhub.service.UserService;
import com.medhub.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    //微信登录接口
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;
    
    @Autowired
    private UserMapper userMapper;
    
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User login(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获得当前微信用户的openid
        String openid = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空，如果为空表示登录失败，业务异常
        if (openid == null) {
            log.info("用户登录失败，openid为空");
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        
        //判断当前用户是否为新用户
        User user = userMapper.getByOpenid(openid);
        if (user == null) {
            //新用户，自动注册
            user = User.builder()
                    .openid(openid)
                    .createTime(java.time.LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回用户对象
        return user;
    }

    /**
     * 调用微信接口服务，获取微信用户的openid
     * @param code
     * @return
     */
    private String getOpenid(String code) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");

        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        return jsonObject.getString("openid");
    }

    /**
     * 用户退出
     */
    @Override
    public void logout() {
        Long userId = BaseContext.getCurrentId();
        log.info("用户退出登录：userId={}", userId);
        BaseContext.setCurrentId(null);
    }
}
