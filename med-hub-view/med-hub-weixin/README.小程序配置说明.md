# 微信小程序开发配置说明

## 配置步骤

1. **复制配置文件**
   ```bash
   cp project.config.json.example project.config.json
   ```

2. **获取微信小程序 AppID**
   - 登录 [微信公众平台](https://mp.weixin.qq.com/)
   - 进入你的小程序管理后台
   - 在 "开发" -> "开发管理" -> "开发设置" 中找到 AppID

3. **修改配置文件**
   
   打开 `project.config.json`，将 `appid` 字段修改为你自己的小程序 AppID：
   ```json
   {
     "appid": "your-wechat-appid-here"  // 替换为你的真实 AppID
   }
   ```

4. **修改 manifest.json**
   
   打开 `manifest.json`，找到 `mp-weixin` 配置部分，同样修改 `appid`：
   ```json
   "mp-weixin" : {
     "appid" : "your-wechat-appid-here"  // 替换为你的真实 AppID
   }
   ```

## 注意事项

- ⚠️ **不要提交** `project.config.json` 和 `project.private.config.json` 到 Git 仓库
- ⚠️ 每个开发者应该使用自己的小程序 AppID 进行开发
- ⚠️ 如果要提交配置修改，请确保只提交 `project.config.json.example` 示例文件

## 常见问题

### Q: 我没有小程序 AppID 怎么办？
A: 可以在微信公众平台注册一个小程序账号，注册后会获得测试 AppID。也可以使用微信提供的 [测试账号](https://developers.weixin.qq.com/miniprogram/dev/devtools/sandbox.html) 进行开发。

### Q: 为什么要使用占位符？
A: 避免将真实的 AppID 提交到公共仓库，虽然 AppID 不是敏感密钥，但保持配置文件的整洁和可维护性是良好的开发习惯。

---

**最后更新**: 2026-03-18
