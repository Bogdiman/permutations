import com.sun.management.OperatingSystemMXBean;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bogdan on 01-Jun-17.
 */
public class Main {
    private static Logger logger = Logger.getLogger(Main.class);

    private static OperatingSystemMXBean operatingSystemMXBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private static int WORD_SIZE;
    private static final char[] CHARACTERS = {'a', 'b', 'c', 'd', 'e'};
    private static List<String> answers = new ArrayList<>();

    private static long ramUsage = 0;
    private static double cpuUsage = 0;

    private static int iterations = 0;

    private static void process(String word) {
        iterations++;

        cpuUsage += operatingSystemMXBean.getProcessCpuLoad();
        ramUsage += Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

        if (word.length() == WORD_SIZE) {
            answers.add(word);
            return;
        }
        for (char c : CHARACTERS) {
            process(word + c);
        }
    }

    public static void main(String[] args) throws IOException {
        String root = "";

        int port = 9876;

        ServerSocket serverSocket = new ServerSocket(port);
        while (true) {
            Socket clientSocket = serverSocket.accept();

            long startTime = System.currentTimeMillis();

            DataInputStream in = new DataInputStream(clientSocket.getInputStream());
            WORD_SIZE = in.readInt();

            process(root);

            for (String answer : answers) {
                System.out.println(answer);
            }

            in.close();
            clientSocket.close();

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            logger.info("-----------------------------------------");
            logger.info("PERMUTATIONS: This iteration used as avg of " + cpuUsage/iterations * 100 + "% of CPU");
            logger.info("PERMUTATIONS: This iteration used an avg of RAM usage of " + ramUsage/iterations);
            logger.info("PERMUTATIONS: Time needed is " + totalTime + " (msec)");
            logger.info("-----------------------------------------");

            ramUsage = 0;
            cpuUsage = 0;
            iterations = 0;
        }
    }
}
