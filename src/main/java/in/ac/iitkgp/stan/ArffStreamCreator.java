package in.ac.iitkgp.stan;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ArffStreamCreator extends Thread
{
    ArffNetworkStream stream;
    private int port;

    ArffStreamCreator(int port)
    {
        this.stream = null;
        this.port = port;
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("Waiting for connection in port " + port + "...");
            ServerSocket serverSocket = new ServerSocket(port);
            Socket socket = serverSocket.accept();
            System.out.println("Connection accepted in port " + port + ".");
            this.stream = new ArffNetworkStream(socket);
            System.out.println("Stream created in port " + port + ".");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
