package in.ac.iitkgp.stan;

import com.yahoo.labs.samoa.instances.InstanceImpl;
import moa.classifiers.trees.HoeffdingTree;
import moa.classifiers.Classifier;

import com.yahoo.labs.samoa.instances.Instance;
import weka.core.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketException;


public class NetworkHoeffdingTree
{
    private ArffNetworkStream trainingStream;
    private ArffNetworkStream testingStream;
    private Classifier learner;
    private int total;
    private int correct;
    private TestFileWriter trainingFile, testingFile;

    NetworkHoeffdingTree(ArffNetworkStream trainingStream, ArffNetworkStream testingStream)
    {
        this.trainingStream = trainingStream;
        this.testingStream = testingStream;

        // finding a folder which does not exist
        String folder;
        for (int i=1; ; i++)
        {
            folder = String.format("/home/stainlee//Desktop/HoeffdingTree-log/test%03d", i);
            File dir = new File(folder);
            if (!dir.exists())
            {
                if (dir.mkdirs()) break;
            }
        }

        this.trainingFile = new TestFileWriter(folder + "/training.csv");
        this.testingFile = new TestFileWriter(folder + "/testing.csv");

        this.trainingStream.prepareForUse();
        this.testingStream.prepareForUse();

        learner = new HoeffdingTree();
        learner.setModelContext(this.trainingStream.getHeader());
        learner.prepareForUse();

        total = 0;
        correct = 0;

        //TimingUtils.enablePreciseTiming();

        // starting threads for training and testing
        Thread trainingThread = new TrainingStreamHandler(this);
        Thread testingThread = new TestingStreamHandler(this);

        trainingThread.start();
        testingThread.start();
    }

    private class TrainingStreamHandler extends Thread
    {
        final NetworkHoeffdingTree networkHoeffdingTree;

        private TrainingStreamHandler(NetworkHoeffdingTree networkHoeffdingTree)
        {
            this.networkHoeffdingTree = networkHoeffdingTree;
        }

        @Override
        public void run()
        {
            while (networkHoeffdingTree.trainingStream.hasMoreInstances())
            {
                try
                {
                    Instance trainInst = networkHoeffdingTree.trainingStream.nextInstance().getData();
                    boolean result;

                    System.out.println("Received Training Data: " + trainInst);

                    synchronized (networkHoeffdingTree)
                    {
                        networkHoeffdingTree.total++;
                        result = networkHoeffdingTree.learner.correctlyClassifies(trainInst);
                        if (result)
                        {
                            networkHoeffdingTree.correct++;
                        }
                        networkHoeffdingTree.learner.trainOnInstance(trainInst);
                    }

                    networkHoeffdingTree.trainingStream.sendBoolean(result);
                    System.out.println("Sent Training Result: " + result);
                    double accuraccy = (double)correct / (double)total * (double) 100;
                    trainingFile.write((InstanceImpl)trainInst, result + "", total, correct, accuraccy);

                }
                catch (SocketException e)
                {
                    System.err.println("Connection Terminated.");
                    System.err.println("Training stream will now close.");
                    break;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    private class TestingStreamHandler extends Thread
    {
        final NetworkHoeffdingTree networkHoeffdingTree;

        private TestingStreamHandler(NetworkHoeffdingTree networkHoeffdingTree)
        {
            this.networkHoeffdingTree = networkHoeffdingTree;
        }

        @Override
        public void run()
        {
            while (networkHoeffdingTree.testingStream.hasMoreInstances())
            {
                try
                {
                    Instance testInst = networkHoeffdingTree.testingStream.nextInstance().getData();
                    int result;

                    System.out.println("\t\t\t\t\t\t\t\t\t\t\tReceived Testing Data: " + testInst);

                    synchronized (networkHoeffdingTree)
                    {
                        result = Utils.maxIndex(networkHoeffdingTree.learner.getVotesForInstance(testInst));
                    }

                    networkHoeffdingTree.testingStream.sendInt(result);
                    System.out.println("\t\t\t\t\t\t\t\t\t\t\tSent Testing Result: " + result);

                    if (result == 0) testingFile.write((InstanceImpl)testInst, "X");
                    else if (result == 1)testingFile.write((InstanceImpl)testInst, "E");
                }
                catch (SocketException e)
                {
                    System.err.println("Connection Terminated.");
                    System.err.println("Testing stream will now close.");
                    break;
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    break;
                }
            }
        }
    }

    public static void main(String args[]) throws InterruptedException, IOException
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        //System.out.println("Enter a file name for printing output (Press enter to skip): ");
        //String filename = br.readLine();
        //if (filename.equals(""))filename = null;

        System.out.println("Specify port for training: ");
        int trainingPort = Integer.parseInt(br.readLine());
        System.out.println("Specify port for testing: ");
        int testingPort = Integer.parseInt(br.readLine());

        System.out.println("Creating streams for training and testing.");

        Thread t1 = new ArffStreamCreator(trainingPort);
        Thread t2 = new ArffStreamCreator(testingPort);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        ArffNetworkStream trainingStream = ((ArffStreamCreator) t1).stream;
        ArffNetworkStream testingStream = ((ArffStreamCreator) t2).stream;

        System.out.println("Staring Classifier.");
        NetworkHoeffdingTree networkHoeffdingTree = new NetworkHoeffdingTree(trainingStream, testingStream);
        System.out.println("Classifier Started.");
    }
}
