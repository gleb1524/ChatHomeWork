import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
    private  Socket socket;
    private Server server;
    private  DataInputStream in;
    private  DataOutputStream out;
    private boolean authenticated;

    public String getNickname() {
        return nickname;
    }

    public String nickname;
    private List<ClientHandler> clients;

    public ClientHandler(Server server, Socket socket) {
        this.socket = socket;
        this.server = server;
        try{
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    //цикл аутентификации
                    while (true) {

                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            sendMessage("Server down");
                            break;
                        }
                        if(str.startsWith("/auth")){
                            String[] token = str.split(" ",3);
                            if(token.length<3){
                                continue;
                            }
                            String newNick = server.getAuthService().getNickname(token[1], token[2]);
                            if (newNick != null){
                                authenticated = true;
                                nickname = newNick;
                                sendMessage("/authok " + nickname);
                                server.subscribe(this);
                                System.out.println("Client " + nickname +" authenticated");
                                break;
                            }else{
                                sendMessage("Неверный логин/пароль");
                            }
                        }
                    }
                //цикл работы
                while (authenticated) {
                    String str = in.readUTF();
                        System.out.println("Client: " + str);
                        if(str.startsWith("/")){
                            if (str.equals("/end")) {
                                sendMessage("Server down");
                                break;
                            }

                        }
                        server.broadcastMessage(   this,str + "\n");

                }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        System.out.println("Client disconnected");
                        try {
                            socket.close();
                            server.unsubscribe(this);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}