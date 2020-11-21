package Entity;

public class User {
    private String loginAccount;
    private String loginNickname;
    private String loginEmail;
    private String loginPassword;

    public User(String loginAccount,String loginNickname,String loginEmail,String loginPassword){
        this.loginAccount = loginAccount;
        this.loginNickname = loginNickname;
        this.loginEmail = loginEmail;
        this.loginPassword = loginPassword;
    }

    public User(String loginAccount,String loginPassword){
        this.loginAccount = loginAccount;
        this.loginPassword = loginPassword;
    }

    public User(String loginAccount,String loginNickname,String loginPassword){
        this.loginAccount = loginAccount;
        this.loginNickname = loginNickname;
        this.loginPassword = loginPassword;
    }


    public User(String loginAccount){
        this.loginAccount = loginAccount;
        this.loginEmail = null;
        this.loginPassword = null;
    }

    public String getLoginNickname() {
        return loginNickname;
    }

    public void setLoginNickname(String loginNickname) {
        this.loginNickname = loginNickname;
    }

    public String getLoginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    public String getLoginAccount() {
        return loginAccount;
    }

    public void setLoginAccount(String loginAccount) {
        this.loginAccount = loginAccount;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }
}
