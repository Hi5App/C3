package NIM;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserManager {

    public static boolean createUser(String username, String nickname, String password) throws Exception{
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String url = "https://api.netease.im/nimserver/user/create.action";
        HttpPost httpPost = new HttpPost(url);

        String appKey = "0fda06baee636802cb441b62e6f65549";
        String appSecret = "3c5c35f26767";
        String nonce =  "12345";
        String curTime = String.valueOf((new Date()).getTime() / 1000L);
        String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

        // 设置请求的header
        httpPost.addHeader("AppKey", appKey);
        httpPost.addHeader("Nonce", nonce);
        httpPost.addHeader("CurTime", curTime);
        httpPost.addHeader("CheckSum", checkSum);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 设置请求的参数
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("accid", username));
        nvps.add(new BasicNameValuePair("name", nickname));
        nvps.add(new BasicNameValuePair("token", password));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

        // 执行请求
        HttpResponse response = httpClient.execute(httpPost);

        String res = EntityUtils.toString(response.getEntity());
        JSONObject jsonObject = null;
        System.out.println(res);

        /*把json字符串转换成json对象*/
        try {
            jsonObject = JSONObject.fromObject(res);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        int code = jsonObject.getInt("code");
        if (code == 200){
            System.out.println("code == 200");
            return true;
        }else {
            System.out.println("fail to create account");
            return false;
        }

    }


}
