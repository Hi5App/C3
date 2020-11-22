package Model;

import Entity.User;
import NIM.UserManager;

import java.sql.*;

public class LoginModel {
    private static String DBUNAME = "root";
    private static String DBUPWD = "~Qq1122SEU";
//    private static String DBUPWD = "111qqq2341xjh";
    private static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String URL = "jdbc:mysql://localhost:3306/userinfo?&serverTimezone=UTC&useSSL=false";

    public static String login(User user){
        String loginAccount = user.getLoginAccount();
        String loginPassword = user.getLoginPassword();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try{
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL,DBUNAME,DBUPWD);
            pstmt = con.prepareStatement("select count(*)from useraccount where (account=? and password=?) or (email=? and password=?)");
            pstmt.setString(1,loginAccount);
            pstmt.setString(2,loginPassword);
            pstmt.setString(3,loginAccount);
            pstmt.setString(4,loginPassword);
            rs = pstmt.executeQuery();
            if (rs.next()){
                count = rs.getInt(1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                if (rs!=null){
                    rs.close();
                }
                if (pstmt!=null){
                    pstmt.close();
                }
                if (con!=null){
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (count==1){
                return "true";
            }else{
                return "false";
            }
        }
    }

    public static String register(User user){
        String loginAccount = user.getLoginAccount();
        String loginNickname = user.getLoginNickname();
        String loginPassword = user.getLoginPassword();
        String loginEmail= user.getLoginEmail();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL,DBUNAME,DBUPWD);
            pstmt = con.prepareStatement("select count(*)from useraccount where account=?");
            pstmt.setString(1,loginAccount);
            rs = pstmt.executeQuery();
            if (rs.next()){
                count = rs.getInt(1);
            }
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                if (rs!=null){
                    rs.close();
                }
                if (pstmt!=null){
                    pstmt.close();
                }
                if (con!=null){
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (count>0) {
                return " Account Already exist";
            }
        }


        try {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL,DBUNAME,DBUPWD);
            pstmt = con.prepareStatement("select count(*)from useraccount where email=?");
            pstmt.setString(1,loginEmail);
            rs = pstmt.executeQuery();
            if (rs.next()){
                count = rs.getInt(1);
            }
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                if (rs!=null){
                    rs.close();
                }
                if (pstmt!=null){
                    pstmt.close();
                }
                if (con!=null){
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (count>0) {
                return " Email Already exist";
            }
        }

        try{
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL,DBUNAME,DBUPWD);
            pstmt = con.prepareStatement("insert into useraccount (account, password, email, nickname) values (?,?,?,?)");
            pstmt.setString(1,loginAccount);
            pstmt.setString(2,loginPassword);
            pstmt.setString(3,loginEmail);
            pstmt.setString(4,loginNickname);
            count = pstmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try{
                if (rs!=null){
                    rs.close();
                }
                if (pstmt!=null){
                    pstmt.close();
                }
                if (con!=null){
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (count==1){
                boolean result;
                try {
                    result = UserManager.createUser(loginAccount, loginNickname, loginPassword);
                }catch (Exception e){
                    e.printStackTrace();
                    return "CreateUser: Something Wrong when create user";
                }
                if (result){
                    return "true";
                }else {
                    System.out.println("Something wrong when create user");
                    return "Something wrong when create user";
                }
            }else{
                return "false";
            }
        }
    }

}
