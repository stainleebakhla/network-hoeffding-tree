package in.ac.iitkgp.stan;

import com.yahoo.labs.samoa.instances.InstanceImpl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class TestFileWriter
{
    private String filename;
    private boolean hasWrittenHeaders;

    TestFileWriter(String filename)
    {
        this.filename = filename;
        this.hasWrittenHeaders = false;
    }

    private void writeHeaders(InstanceImpl instance)
    {
        StringBuilder sb = new StringBuilder();
        int n = instance.numAttributes();

        for (int i=0; i<n; i++)
        {
            sb.append(instance.attribute(i).name()).append(",");
        }
        sb.append("result\r\n");

        BufferedWriter bw;

        try
        {
            bw = new BufferedWriter(new FileWriter(filename));
            bw.write(sb.toString());
            bw.close();
            hasWrittenHeaders = true;
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void write(InstanceImpl instance, String result)
    {
        if (!hasWrittenHeaders)
        {
            writeHeaders(instance);
        }

        StringBuilder sb = new StringBuilder();
        int n = instance.numAttributes();

        for (int i=0; i<n; i++)
        {
            sb.append(instance.value(i)).append(",");
        }
        sb.append(result).append("\r\n");

        BufferedWriter bw;

        try
        {
            bw = new BufferedWriter(new FileWriter(filename, true));
            bw.write(sb.toString());
            bw.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }

    public void write(InstanceImpl instance, String result, int total, int correct, double accuracy)
    {
        if (!hasWrittenHeaders)
        {
            writeHeaders(instance);
        }

        StringBuilder sb = new StringBuilder();
        int n = instance.numAttributes();

        for (int i=0; i<n; i++)
        {
            sb.append(instance.value(i)).append(",");
        }
        sb.append(result).append(",");
        sb.append(total).append(",");
        sb.append(correct).append(",");
        sb.append(accuracy).append("\r\n");

        BufferedWriter bw;

        try
        {
            bw = new BufferedWriter(new FileWriter(filename, true));
            bw.write(sb.toString());
            bw.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }
    }
}
