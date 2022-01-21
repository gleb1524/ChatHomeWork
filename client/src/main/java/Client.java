import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Client implements Initializable {
    private static Socket socket;
    private static final int PORT = 8189;
    private static final String HOST = "localhost";
    private static DataInputStream in;
    private static DataOutputStream out;
    public HBox sendMessageBar;
    private boolean authenticated;
    private String nickname;
    private Stage stage;
    @FXML
    public javafx.scene.control.TextArea textArea;
    @FXML
    public javafx.scene.control.TextField textField;
    @FXML
    public TextField textFieldLogin;
    @FXML
    public PasswordField textFieldPassword;
    @FXML
    public Button sigIn;
    @FXML
    public HBox authBar;


    @FXML
    public void pressToClose(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            try {
                out.writeUTF("/end");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Stage scene = (Stage) textField.getScene().getWindow();
            scene.close();
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            stage = (Stage) textField.getScene().getWindow();
        });
        setAuthenticated(false);
    }

    @FXML
    protected void enterBtn(ActionEvent actionEvent) throws IOException {
        if (textField.getText().length() > 0) {
            if(textField.getText().startsWith("/m ")){
                textArea.appendText(textField.getText()+"\n");
            }
            out.writeUTF(textField.getText());
            textField.clear();
            textField.requestFocus();
        }
    }

    @FXML
    public void pressToAuth(ActionEvent actionEvent) {
        if(socket == null || socket.isClosed()){
            connect();
        }

        try{
            String str = String.format("/auth %s %s",
                    textFieldLogin.getText().trim(), textFieldPassword.getText().trim());
           out.writeUTF(str);
           textFieldPassword.clear();
        } catch (IOException e) {
            System.out.println(e+"ошибка в отправке пары пароль/логин");
        }
    }
    public void connect(){
        try {

            socket = new Socket(HOST, PORT);
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());


            /* Thread inputStream =*/
            new Thread(() -> {
                try {
                    while (true) {
                    //цикл аутентификации
                        String str = in.readUTF();

                            if (str.equals("/end")) {
                            break;
                            }
                            if(str.startsWith("/authok")){
                                nickname = str.split(" ")[1];
                                setAuthenticated(true);
                                break;
                            }
                        else {
                            textArea.appendText((str + "\n"));
                            break;
                        }
                    }
                    //цикл работы
                    while (authenticated) {
                        String str = in.readUTF();
                        if(str.startsWith("/m")){
                            String[] token = str.split(" ");
                            for (int i = 2; i < token.length; i++) {
                                str=token[i]+" ";
                            }
                        }
                        textArea.appendText(( str + "\n"));
                        if (str.equals("Server down")) {
                            break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                        Platform.runLater(() -> {
                            Stage scene = (Stage) textField.getScene().getWindow();
                            scene.close();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        authBar.setVisible(!authenticated);
        authBar.setManaged(!authenticated);
        sendMessageBar.setVisible(authenticated);
        sendMessageBar.setVisible(authenticated);

        if(!authenticated){
            nickname="";
        }
        setTitle(nickname);
    }
    private void setTitle(String nickname){
        String title;
        if(nickname.equals("")){
            title = "MyChat";
        }else {
            title = String.format("Magic chat - %s", nickname);
        }
        Platform.runLater(() -> {
            stage.setTitle(title);
        });
    }
}
