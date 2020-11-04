package Servlet;

import Entity.User;
import Model.FriendsModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class QueryFriendsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String loginAccount_1 = req.getParameter("Username");
        User user = new User(loginAccount_1);
        String result = FriendsModel.queryFriends(user);
        System.out.println("查询账号："+ user.getLoginAccount() +", 好友列表：" + result);
        resp.setCharacterEncoding("UTF-8");
        //通过PrintWriter返回给客户端操作结果
        PrintWriter writer = resp.getWriter();
        writer.print(result);
        writer.flush();
    }
}
