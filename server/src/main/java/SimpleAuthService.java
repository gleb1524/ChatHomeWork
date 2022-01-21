import java.util.ArrayList;
import java.util.List;

public class SimpleAuthService implements AuthService{
    @Override
    public void start() {
        System.out.println("Auth service start");
    }


    @Override
    public void stop() {
        System.out.println("Auth service stopped");
    }

    private class ClientsDate {
        private String login;
        private String password;
        private String nickname;

        public ClientsDate(String login, String password, String nickname) {
            this.login = login;
            this.password = password;
            this.nickname = nickname;
        }
    }
    public List<ClientsDate> clientsDate;


    public  SimpleAuthService(){
        clientsDate = new ArrayList<>();
        clientsDate.add(new ClientsDate("Login1", "Password1", "Login1"));
        clientsDate.add(new ClientsDate("Login2", "Password2", "Login2"));
        clientsDate.add(new ClientsDate("Login3", "Password3", "Login3"));
    }


    @Override
    public String getNickname(String login, String password) {
        for (ClientsDate date : clientsDate) {
            if(date.login.equals(login)&&date.password.equals(password)){
                return date.nickname;
            }
        }
        return null;
    }
}
