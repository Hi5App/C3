package Servlet;

import Entity.User;
import Model.FriendsModel;
import Model.LoginModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AddFriendsServlet extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String loginAccount_1 = req.getParameter("Username");
        String loginAccount_2 = req.getParameter("Peer");
        User user_1 = new User(loginAccount_1);
        User user_2 = new User(loginAccount_2);
        String result = FriendsModel.addFriends(user_1, user_2);
        System.out.println("申请账号："+ user_1.getLoginAccount() +",添加账号：" + user_2.getLoginAccount() + ",添加结果" + result);
        resp.setCharacterEncoding("UTF-8");
        //通过PrintWriter返回给客户端操作结果
        PrintWriter writer = resp.getWriter();
        writer.print(result);
        writer.flush();
    }
}
