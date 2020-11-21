package Servlet;

import Entity.User;
import Model.LoginModel;
import javax.servlet.ServletException;
//import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//@WebServlet(name = "RegisterServlet",value = "/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        String registerAccount = request.getParameter("registerUsername");
        String registerNickname = request.getParameter("registerNickname");
        String registerEmail = request.getParameter("registerEmail");
        String registerPassword = request.getParameter("registerPassword");
        User registerUser = new User(registerAccount,registerNickname,registerEmail,registerPassword);
        String rs = LoginModel.register(registerUser);
        System.out.println("注册账号："+registerAccount+"注册用户名："+registerNickname+",注册邮箱："+registerEmail+",注册密码："+registerPassword+",注册结果"+rs);
        //通过PrintWriter返回给客户端操作结果
        PrintWriter writer = response.getWriter();
        writer.print(rs);
        writer.flush();
    }
}
