package in.ac.iitkgp.stan;

import com.yahoo.labs.samoa.instances.Instances;
import moa.core.InputStreamProgressMonitor;
import moa.streams.ArffFileStream;
import moa.streams.clustering.ClusterEvent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ArffNetworkStream extends ArffFileStream
{
    private Socket socket;
    private DataOutputStream out;
    private char lineEnding;

    ArffNetworkStream(Socket socket)
    {
        this.socket = socket;
        this.classIndexOption.setValue(-1);
        this.lineEnding = (char)10;     // LF character '\n'
        //restart();
    }

    @Override
    public void restart()
    {
        try
        {
            if (this.fileReader != null)
            {
                this.fileReader.close();
            }

            InputStream fileStream = socket.getInputStream();
            out = new DataOutputStream(socket.getOutputStream());
            this.fileProgressMonitor = new InputStreamProgressMonitor(fileStream);
            this.fileReader = new BufferedReader(new InputStreamReader(this.fileProgressMonitor));
            //this.fileReader = new BufferedReader(new InputStreamReader(fileStream));
            int classIndex = this.classIndexOption.getValue();
            this.instances = new Instances(this.fileReader, 1, classIndex);
            if (classIndex < 0)
            {
                this.instances.setClassIndex(this.instances.numAttributes() - 1);
            }
            else if (this.classIndexOption.getValue() > 0)
            {
                this.instances.setClassIndex(this.classIndexOption.getValue() - 1);
            }
            this.numInstancesRead = 0;
            this.lastInstanceRead = null;
            this.hitEndOfFile = !readNextInstanceFromFile();
        }
        catch (IOException ioe)
        {
            throw new RuntimeException("ArffNetworkStream restart failed.", ioe);
        }
        this.clusterEvents = new ArrayList<ClusterEvent>();
    }

    public void sendBoolean(boolean value)throws IOException
    {
        StringBuilder sb = new StringBuilder();
        if (value)
        {
            sb.append('1');
        }
        else
        {
            sb.append('0');
        }

        sb.append(lineEnding);
        sb.append(lineEnding);

        out.writeBytes(sb.toString());
    }

    public void sendInt(int value)throws IOException
    {
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        sb.append(lineEnding);
        sb.append(lineEnding);

        out.writeBytes(sb.toString());
    }
}
