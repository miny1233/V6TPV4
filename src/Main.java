import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        System.out.println("BindPort");
        int Port = Integer.parseInt(input.nextLine());
        System.out.println("ToIP");
        String ToIP = input.nextLine();
        System.out.println("ToPort");
        int ToPort = Integer.parseInt(input.nextLine());

        ServerSocket serverSocket = new ServerSocket(Port);

        while (true) {
                Socket connection = serverSocket.accept();
                System.out.println(connection.getRemoteSocketAddress().toString() + " 已连接");

                ConnectTo connectTo = new ConnectTo(ToIP,ToPort,connection);
                Thread thread = new Thread(connectTo);
                thread.start();
        }
    }

}

class ReMessage implements Runnable {
    InputStream inputStream;
    Socket socket,server;
    OutputStream outputStream;
    byte[] buffer = new byte[1024 * 512];
    String RemoteIP;
    @Override
    public void run() {
        int len;
        while (true){
            try{
               len = inputStream.read(buffer);
               if(len != -1) {
                   outputStream.write(buffer, 0, len);
               } else {
                   if(!RemoteIP.isEmpty()) System.out.println(RemoteIP + " will be disconnect");
                   break;
               }
            }
            catch (Exception error){
                System.out.println(error.toString());
                break;
            }
        }
        // disconnect
        try {
            if(!socket.isClosed())socket.close();
            if(!socket.isClosed())server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ReMessage(Socket connection,Socket S,String ReIP)  {
        socket = connection;
        server = S;
        RemoteIP = ReIP;
        try {
            outputStream = server.getOutputStream();
            inputStream = socket.getInputStream();
        }
        catch (Exception error){
            error.printStackTrace();
            System.out.println("At: " + ReIP);
        }
    }

}
class ConnectTo implements Runnable{
     String ToIP;
     int ToPort;
     Socket connection;
    @Override
    public void run() {
        try {
            Socket destinationSocket = new Socket(ToIP, ToPort);
            ReMessage re = new ReMessage(connection, destinationSocket,connection.getRemoteSocketAddress().toString());
            ReMessage send = new ReMessage(destinationSocket, connection,"");
            Thread r = new Thread(re);
            Thread s = new Thread(send);
            r.start();
            s.start();
        }
        catch (Exception error){
            System.out.println(connection.getRemoteSocketAddress().toString() + " 异常断开");
            try {
                connection.close();
            }
            catch (Exception e){}
           
        }
    }
    public ConnectTo(String ToIP,int ToPort,Socket Connection){
        this.ToIP = ToIP;
        this.ToPort = ToPort;
        this.connection = Connection;
    }
}
