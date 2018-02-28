/**
 * @Description: TODO
 * @date 2017��10��29�� ����2:28:02 	
 */
package cn.weibo.controller;
import cn.weibo.service.WeiBoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
@Controller
public class WeiBoCtroller {
    @Autowired
    private WeiBoService weiBoService;
    // ���ʵ�½ҳ�棬Ȼ����ض��� WeiBo �ĵ�½ҳ��
    @RequestMapping("/oauth/weibo.action")
    @ResponseBody
    public String WeiBoLogin(HttpServletRequest request,HttpServletResponse response) {
    	return weiBoService.getLoginUrl();
    }
    // WeiBo �ɹ���½��Ļص�
    @RequestMapping("/oauth/weibo/callback.action")
    public String WeiBoLoginCallback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        String accessToken = weiBoService.getAccessToken(code); // 5943BF2461ED97237B878BECE78A8744
        // ���� accessToken �� cookie������ʱ��Ϊ 30 �죬�����Ժ�ʹ��
        Cookie cookie = new Cookie("accessToken", accessToken);
        cookie.setMaxAge(60 * 24 * 30);
        response.addCookie(cookie);
        return "redirect:/success.jsp?type=weibo";
    }
    // ��ȡ WeiBo �û�����Ϣ
    @RequestMapping("/oauth/weibo/user.action")
    @ResponseBody
    public String getUserInfo(@CookieValue(name = "accessToken", required = false) String accessToken) throws IOException {
        if (accessToken == null) {
            return  "There is no access token, please login first!";
        }
        String uid  = weiBoService.getUserid(accessToken);
        System.out.println(uid);
        String result=weiBoService.getUserInfo(accessToken,uid);
        System.out.println(result);
        return result;
    }
    
    
    //��������¼�
    @RequestMapping("/oauth/weibo/share.action")
    @ResponseBody
    public String WeiBoShare(@CookieValue(name = "accessToken", required = false) String accessToken){
    	try {
    		//�÷������ݱ������㱾��վ�����ӣ���Ҫ��weiboƽ̨����д
        	String context = URLEncoder.encode("����΢�����������,http://lzclzc.tunnel.qydev.com/scribejava_Auth/success.jsp","UTF-8");
        	if(accessToken == null){
        		return "accessToken  null";
        	}
        	String aa = weiBoService.UserShare(accessToken, context);
        	System.out.println(aa);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
    }
    
}
