import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private  ServerSocket server;
    private  Socket socket;
    private  final int PORT = 8189;
    private List<ClientHandler> clients;
    private AuthService authService;

    public AuthService getAuthService() {
        return authService;
    }

    public Server() {
        authService = new SimpleAuthService();
        authService.start();
        clients = new CopyOnWriteArrayList<>();
        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started!");

            while (true){
                socket = server.accept();
                System.out.println("Client connected:"+socket.getRemoteSocketAddress());
                new ClientHandler(this, socket);

            }





        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            System.out.println("Server down");
            try{
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public void subscribe(ClientHandler clientHandler){
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler){
        clients.remove(clientHandler);
    }



    public void broadcastMessage(ClientHandler sender,String msg){
        String message = String.format("[ %s ]: %s", sender.getNickname(),msg);
        if(msg.startsWith("/m ")){
            String  token = msg.split(" ")[1];
            for (ClientHandler client : clients) {
                if(client.getNickname().equals(token)){
                    client.sendMessage(message);
                }
            }
        }else {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

}
