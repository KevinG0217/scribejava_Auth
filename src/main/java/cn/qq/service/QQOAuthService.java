/**
 * @Description: TODO
 * @date 2017��10��29�� ����3:01:47 	
 */
package cn.qq.service;

/**
 * @author lzc
 *
 */
import cn.qq.api.QQApi;
import cn.util.PropUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class QQOAuthService {
    // ��ȡ�û� openid �� URL
    private static final String OPEN_ID_URL = "https://graph.qq.com/oauth2.0/me";
    // ��ȡ�û���Ϣ�� URL��oauth_consumer_key Ϊ apiKey
    private static final String USER_INFO_URL = "https://graph.qq.com/user/get_user_info?oauth_consumer_key=%s&openid=%s";
    // ��������Կ���ͨ�����ö�ȡ
    private String apiKey      = PropUtil.getProp("QQ.apiId");                 // QQ ����Ӧ�ù������ĵ� APP ID
    private String apiSecret   = PropUtil.getProp("QQ.APPKey");                // QQ ����Ӧ�ù������ĵ� APP Key
    private String callbackUrl = PropUtil.getProp("QQ.callbackUrl");             // QQ �ڵ�½�ɹ���ص��� URL����� URL ������ QQ ��������д��
    private String scope       = "get_user_info";                              // QQ ������ API �ӿڣ������û�����
    private OAuth20Service oauthService; // ���� QQ ����� service
    public QQOAuthService() {
        // �������� QQ ����� service
        oauthService = new ServiceBuilder().apiKey(apiKey).apiSecret(apiSecret)
                .scope(scope).callback(callbackUrl).build(QQApi.instance());
    }
    /**
     * ȡ�� QQ ��½ҳ��� URL������
     * https://graph.qq.com/oauth2.0/authorize?response_type=code&client_id=101292272&
     * redirect_uri=http://open.qtdebug.com:8080/oauth/qq/callback&scope=get_user_info
     *
     * @return QQ ��½ҳ��� URL
     */
    public String getLoginUrl() {
        return oauthService.getAuthorizationUrl();
    }
    /**
     * ʹ�� code ��ȡ access token
     *
     * @param code �ɹ���½�� QQ Server ���ظ��ص� URL ���м� code�����ڻ�ȡ access token
     * @return ���ڷ��� QQ ����� token
     * @throws IOException
     */
    public String getAccessToken(String code) throws IOException {
        OAuth2AccessToken token = oauthService.getAccessToken(code); // ʹ�� code ��ȡ accessToken
        String accessToken = token.getAccessToken(); // 5943BF2461ED97237B878BECE78A8744
        return accessToken;
    }
    /**
     * ��ȡ�û��� open id��ÿ���û�����ͬһ�� APP ID �� open id ��һ����
     *
     * @param accessToken ��½ʱ�� QQ ϵͳ�õ��� access token����Ϊ���ʵ�ƾ֤���൱���û������������
     * @return �û��� open id
     * @throws IOException
     */
    public String getOpenId(String accessToken) throws IOException {
        Response oauthResponse = request(oauthService, accessToken, OPEN_ID_URL,Verb.GET);
        String responseBody = oauthResponse.getBody();
        int s = responseBody.indexOf("{");
        int e = responseBody.lastIndexOf("}") + 1;
        String json = responseBody.substring(s, e);
        JSONObject obj = JSON.parseObject(json);
        return obj.getString("openid");
    }
    /**
     * ��ȡ�û�����Ϣ��QQ ���ǳƣ�QQ �ռ��ͷ��ȣ�һ���� 2 �������õ����
     *
     * @param accessToken ��½ʱ�� QQ ϵͳ�õ��� access token����Ϊ���ʵ�ƾ֤���൱���û������������
     * @param openId �û��� open id
     * @return JSONObject ����
     * @throws IOException
     */
    public JSONObject getUserInfo(String accessToken, String openId) throws IOException {
        String url = String.format(USER_INFO_URL, apiKey, openId);
        Response oauthResponse = request(oauthService, accessToken, url,Verb.GET);
        String responseJson = oauthResponse.getBody();
        return JSON.parseObject(responseJson);
    }
    /**
     * ʹ�� OAuth 2.0 �ķ�ʽ�ӷ�������ȡ URL ָ������Ϣ
     *
     * @param accessToken ��½ʱ�� QQ ϵͳ�õ��� access token����Ϊ���ʵ�ƾ֤���൱���û������������
     * @param url ���� OAuth Server ����� URL
     * @return
     */
    public Response request(OAuth20Service service, String accessToken, String url,Verb verb) {
        OAuth2AccessToken token = new OAuth2AccessToken(accessToken);
        OAuthRequest oauthRequest = new OAuthRequest(verb, url, service);
        service.signRequest(token, oauthRequest); // ��� accessToken ��ӵ������У�GET ������ӵ� URL ��
        Response oauthResponse = oauthRequest.send();
        return oauthResponse;
    }
}
