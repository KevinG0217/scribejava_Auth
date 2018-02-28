/**
 * @Description: TODO
 * @date 2017��10��30�� ����4:42:52 	
 */
package cn.weixin.service;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.github.scribejava.apis.SinaWeiboApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

/**
 * @author lzc
 *
 */
public class WeiXinService {

	// ��ȡ�û�uid �� URL
    private static final String U_ID_URL = "https://api.weibo.com/2/account/get_uid.json";
    // ��ȡ�û���Ϣ�� URL
    private static final String USER_INFO_URL = "https://api.weibo.com/2/users/show.json?uid=%s";
    // ��������Կ���ͨ�����ö�ȡ
    //ע�� �м�callbackUrl����������΢������ƽ̨����д�Ļص�ҳ�棬�˵�ַһ��Ҫ�����������ø���͸���߾��С����ص�ַ    http://download.csdn.net/download/liu976180578/10139479
    private String callbackUrl = "http://lzclzc.tunnel.qydev.com/scribejava_Auth/oauth/weibo/callback.action"; // WeiBo �ڵ�½�ɹ���ص��� URL����� URL ������ WeiBo ��������д��
    private String apiKey      = "3703387386";                                      // WeiBo ����Ӧ�ù������ĵ� APP ID
    private String apiSecret   = "f525745eaa5fbaee169b23dae552049f";               // WeiBo ����Ӧ�ù������ĵ� APP Key
    private String scope       = "all";                                           // WeiBo ������ API �ӿڣ������û�����
    private OAuth20Service oauthService; // ���� WeiBo ����� service
    public WeiXinService() {
        // �������� WeiBo ����� service
        oauthService = new ServiceBuilder().apiKey(apiKey).apiSecret(apiSecret)
                .scope(scope).callback(callbackUrl).build(SinaWeiboApi20.instance());
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
        Response oauthResponse = request(oauthService, accessToken, url);
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
        Response oauthResponse = request(oauthService, accessToken, url);
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
    public Response request(OAuth20Service service, String accessToken, String url) {
        OAuth2AccessToken token = new OAuth2AccessToken(accessToken);
        OAuthRequest oauthRequest = new OAuthRequest(Verb.GET, url, service);
        service.signRequest(token, oauthRequest); // ��� accessToken ��ӵ������У�GET ������ӵ� URL ��
        Response oauthResponse = oauthRequest.send();
        return oauthResponse;
    }

}
