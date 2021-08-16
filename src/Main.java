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
        System.gc();
        while (true) {
                Socket connection = serverSocket.accept();
                System.out.println(connection.getRemoteSocketAddress().toString() + "已连接");
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
    byte[] bytes = new byte[1024*512];
    String RemoteIP;
    @Override
    public void run() {
        int len;
        while (true){
            try{
               len = inputStream.read(bytes);
               outputStream.write(bytes,0,len);
            }
            catch (Exception error){
                try {
                    socket.close();
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println();
               
               return;
            }
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
            System.out.print(error.toString());
            System.gc();
            return;
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
            Socket socket = new Socket(ToIP, ToPort);
            ReMessage re = new ReMessage(connection, socket,connection.getRemoteSocketAddress().toString());
            ReMessage send = new ReMessage(socket, connection,"");
            Thread r = new Thread(re);
            Thread s = new Thread(send);
            r.start();
            s.start();
        }
        catch (Exception error){
            System.out.println(connection.getRemoteSocketAddress().toString() +"异常断开");
            try {
                connection.close();
            }
            catch (Exception e){}
           
        }
    }
    public ConnectTo(String TOIP,int TOPort,Socket Connection){
        ToIP = TOIP;
        ToPort = TOPort;
        connection = Connection;
    }
}
