/**
 * @Description: TODO
 * @date 2017��10��29�� ����2:28:02 	
 */
package cn.qq.controller;
import cn.qq.service.QQOAuthService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
@Controller
public class QQOAuthController {
    @Autowired
    private QQOAuthService qqOAuthService;
    // ���ʵ�½ҳ�棬Ȼ����ض��� QQ �ĵ�½ҳ��
    @GetMapping("/oauth/qq.action")
    @ResponseBody
    public String qqLogin() {
        return qqOAuthService.getLoginUrl();
    }
    // QQ �ɹ���½��Ļص�
    @GetMapping("/oauth/qq/callback")
    public String qqLoginCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        String accessToken = qqOAuthService.getAccessToken(code); // 5943BF2461ED97237B878BECE78A8744
        // ���� accessToken �� cookie������ʱ��Ϊ 30 �죬�����Ժ�ʹ��
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setMaxAge(60 * 24 * 30);
        response.addCookie(cookie);
        return "redirect:/success.jsp?type=QQ";
    }
    // ��ȡ QQ �û�����Ϣ
    @GetMapping("/oauth/qq/user")
    @ResponseBody
    public String getUserInfo(@CookieValue(name = "accessToken", required = false) String accessToken) throws IOException {
        if (accessToken == null) {
            return "There is no access token, please login first!";
        }
        String openId = qqOAuthService.getOpenId(accessToken);
        JSONObject json = qqOAuthService.getUserInfo(accessToken, openId);
        return json.toJSONString();
    }
}
