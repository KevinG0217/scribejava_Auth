/**
 * @Description: TODO
 * @date 2017��10��29�� ����6:19:38 	
 */
package cn.weibo.service;

import cn.util.PropUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.scribejava.apis.SinaWeiboApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class WeiBoService {
	// ��ȡ�û�uid �� URL
    private static final String U_ID_URL = "https://api.weibo.com/2/account/get_uid.json";
    // ��ȡ�û���Ϣ�� URL
    private static final String USER_INFO_URL = "https://api.weibo.com/2/users/show.json?uid=%s";
    // �û������url
    private static final String USER_SHARE_URL = "https://api.weibo.com/2/statuses/share.json?status=%s";
    
    // ��������Կ���ͨ�����ö�ȡ
    //ע�� �м�callbackUrl����������΢������ƽ̨����д�Ļص�ҳ�棬�˵�ַһ��Ҫ�����������ø���͸���߾��С����ص�ַ    http://download.csdn.net/download/liu976180578/10139479
    private String scope       = "all";                                           // WeiBo ������ API �ӿڣ������û�����
    private OAuth20Service oauthService; // ���� WeiBo ����� service
    public WeiBoService() {
        // �������� WeiBo ����� service
        oauthService = new ServiceBuilder().apiKey(PropUtil.getProp("WeiBo.apiKey")).apiSecret(PropUtil.getProp("WeiBo.apiSecret"))
                .scope(scope).callback(PropUtil.getProp("WeiBo.callbackUrl")).build(SinaWeiboApi20.instance());
    }
    /**
     * ȡ�� WeiBo ��½ҳ��� URL������
     * https://graph.WeiBo.com/oauth2.0/authorize?response_type=code&client_id=101292272&
     * redirect_uri=http://open.qtdebug.com:8080/oauth/WeiBo/callback&scope=get_user_info
     *
     * @return WeiBo ��½ҳ��� URL
     */
    public String getLoginUrl() {
        return oauthService.getAuthorizationUrl();
    }
    /**
     * ʹ�� code ��ȡ access token
     *
     * @param code �ɹ���½�� WeiBo Server ���ظ��ص� URL ���м� code�����ڻ�ȡ access token
     * @return ���ڷ��� WeiBo ����� token
     * @throws IOException
     */
    public String getAccessToken(String code) throws IOException {
        OAuth2AccessToken token = oauthService.getAccessToken(code); // ʹ�� code ��ȡ accessToken
        String accessToken = token.getAccessToken(); // 5943BF2461ED97237B878BECE78A8744
        return accessToken;
    }
    
    /**
     * ��ȡ�û���uid��WeiBo ���ǳƣ�WeiBo �ռ��ͷ��ȣ�һ���� 2 �������õ����
     *
     * @param accessToken ��½ʱ�� WeiBo ϵͳ�õ��� access token����Ϊ���ʵ�ƾ֤���൱���û������������
     * @throws IOException
     */
    public String getUserid(String accessToken) throws IOException {
        String url = String.format(U_ID_URL);
        Response oauthResponse = request(oauthService, accessToken, url,Verb.GET);
        String responseJson = oauthResponse.getBody();
        String uid=JSON.parseObject(responseJson).getString("uid");
        return uid;
    }
    
    /**
     * ��ȡ�û�����Ϣ��WeiBo ���ǳƣ�WeiBo �ռ��ͷ��ȣ�һ���� 2 �������õ����
     *
     * @param accessToken ��½ʱ�� WeiBo ϵͳ�õ��� access token����Ϊ���ʵ�ƾ֤���൱���û������������
     * @param Uid �û���uid
     * @return JSONObject ����
     * @throws IOException
     */
    public String getUserInfo(String accessToken,String uid) throws IOException {
        String url = String.format(USER_INFO_URL,uid);
        Response oauthResponse = request(oauthService, accessToken, url,Verb.GET);
        String responseJson = oauthResponse.getBody();
        return responseJson;
    }
    
    
    
    
    /**
     * ����WeiBo ���ǳƣ�
     *
     * @param accessToken ��½ʱ�� WeiBo ϵͳ�õ��� access token����Ϊ���ʵ�ƾ֤���൱���û������������
     * @param Uid �û���uid
     * @return JSONObject ����
     * @throws IOException
     */
    public String UserShare(String accessToken,String status) throws IOException {
        String url = String.format(USER_SHARE_URL,status);
        Response oauthResponse = request(oauthService, accessToken, url,Verb.POST);
        String responseJson = oauthResponse.getBody();
        return responseJson;
    }
    /**
     * ʹ�� OAuth 2.0 �ķ�ʽ�ӷ�������ȡ URL ָ������Ϣ
     *
     * @param accessToken ��½ʱ�� WeiBo ϵͳ�õ��� access token����Ϊ���ʵ�ƾ֤���൱���û������������
     * @param url ���� OAuth Server ����� URL
     * @return
     */
    public Response request(OAuth20Service service, String accessToken, String url,Verb verb) {
        OAuth2AccessToken token = new OAuth2AccessToken(accessToken);
        OAuthRequest oauthRequest = new OAuthRequest(verb, url, service);//verb���ύ��ʽpost/get/..
        service.signRequest(token, oauthRequest); // ��� accessToken ��ӵ������У�GET ������ӵ� URL ��
        Response oauthResponse = oauthRequest.send();
        return oauthResponse;
    }
}
