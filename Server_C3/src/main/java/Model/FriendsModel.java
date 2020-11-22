package Model;

import Entity.User;

import java.sql.*;

public class FriendsModel {
    private static String DBUNAME = "root";
        private static String DBUPWD = "~Qq1122SEU";
//    private static String DBUPWD = "111qqq2341xjh";
    private static String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static String URL = "jdbc:mysql://localhost:3306/userinfo?&serverTimezone=UTC&useSSL=false";

    public static String addFriends(User user_1, User user_2){

        String loginAccount_1 = user_1.getLoginAccount();
        String loginAccount_2 = user_2.getLoginAccount();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int count = 0;
        try {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL,DBUNAME,DBUPWD);
            pstmt = con.prepareStatement("select * from useraccount where account=?");
            pstmt.setString(1,loginAccount_2);
            rs = pstmt.executeQuery();
            if (rs.next()){
                count++;
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
            if (count<=0) {
                return " Account not exist";
            }
        }


        try {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL,DBUNAME,DBUPWD);
            pstmt = con.prepareStatement("select * from relationship where ( user1 = ? and user2 = ?) or ( user1 = ? and user2 = ?)");
            pstmt.setString(1,loginAccount_1);
            pstmt.setString(2,loginAccount_2);
            pstmt.setString(3,loginAccount_2);
            pstmt.setString(4,loginAccount_1);
            rs = pstmt.executeQuery();
            count = 0;
            if (rs.next()){
                count++;
                System.out.println("count: " + count);
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
                return " Friends Already exist";
            }
        }

        try{
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL,DBUNAME,DBUPWD);
            pstmt = con.prepareStatement("insert into relationship (user1, user2) values (?,?)");
            pstmt.setString(1,loginAccount_1);
            pstmt.setString(2,loginAccount_2);
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
                return "true";
            }else{
                return "false";
            }
        }
    }

    public static String queryFriends(User username){

        String loginAccount = username.getLoginAccount();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String friendslist = "";
        StringBuilder fl = new StringBuilder();
        System.out.println(loginAccount);
        int count = 0;
        try {
            Class.forName(DRIVER);
            con = DriverManager.getConnection(URL,DBUNAME,DBUPWD);
            pstmt = con.prepareStatement("select * from relationship where user1 = ? or user2 = ?");
            pstmt.setString(1,loginAccount);
            pstmt.setString(2,loginAccount);
            rs = pstmt.executeQuery();
            while (rs.next()){
                System.out.println(count);
                count++;
                System.out.println(rs.getString(1));
                System.out.println(rs.getString(2));
                System.out.println(rs.getString(3));
                String user_1 = rs.getString(2);
                String user_2 = rs.getString(3);
                if (!user_1.equals(loginAccount)){
                    System.out.println(user_1);
                    fl.append(user_1)
                            .append(";");
//                    friendslist = friendslist + user_1 + ";";
                }
                if (!user_2.equals(loginAccount)){
                    System.out.println(user_2);
                    fl.append(user_2)
                            .append(";");
//                    friendslist = friendslist + user_2 + ";";
                }
            }
            friendslist = fl.toString();
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
                return "";
            }
            if (count>0) {
                return friendslist.substring(0,friendslist.length()-1);
            }else {
                return "";
            }
        }
    }
}
