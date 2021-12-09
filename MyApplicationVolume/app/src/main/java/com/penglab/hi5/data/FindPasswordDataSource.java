package com.penglab.hi5.data;

import com.penglab.hi5.core.net.HttpUtilsUser;
import com.penglab.hi5.data.model.user.FindPasswordUser;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by Jackiexing on 12/7/21
 */
public class FindPasswordDataSource {

    private String responseData;

    public Result<FindPasswordUser> findPassword(String username, String email) {
        try {
            findPasswordWithOkHttp(username, email);
            if (responseData != null) {
                if (responseData.equals("true")) {
                    FindPasswordUser user = new FindPasswordUser(username, email);
                    return new Result.Success<FindPasswordUser>(user);
                } else {
                    return new Result.Error(new Exception(responseData));
                }
            } else {
                return new Result.Error(new Exception("Response from server is empty !"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Check the network please !"));
        }
    }

    private void findPasswordWithOkHttp(String username, String email) throws IOException {

        Response response = HttpUtilsUser.findPasswordWithOkHttp(username, email);
        if (response != null) {
            responseData = response.body().string();
        } else {
            responseData = "Connect Failed When Find Back Password !";
        }
    }
}
