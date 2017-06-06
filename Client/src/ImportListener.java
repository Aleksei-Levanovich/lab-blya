import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Денис on 25.04.2017.
 */
public class ImportListener extends LabListener {
    JProgressBar jpb1;

    ImportListener(JTextField field, TreeSet<Human> col, LabTable colTable, JProgressBar jpb) {
        super(field, col, colTable, jpb);
        jpb1 = jpb;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProgressBarThread jPBarThread = new ProgressBarThread(jpb1);
        jPBarThread.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Matcher m = Pattern.compile("\\{([^}]+)\\}").matcher(getNameField().getText());
                LabCollection ImportCol = new LabCollection();
                while (m.find()) {
                    TreeSet<Human> newCol = new TreeSet<>();
                    //getCollection().addAll(makeCall(m.group().substring(1, m.group().length() - 1)).getUselessData());
                    //getTable().fireTableDataChanged();
                    newCol = ConsoleApp.ImportFrom(m.group().substring(1, m.group().length() - 1)).getUselessData();
                    ImportCol.addUselessData(newCol);
                }
                getNameField().setText("");
                getCollection().clear();
                getCollection().addAll(makeCall("import",ImportCol).getUselessData());
                getTable().fireTableDataChanged();
            }
        }).start();
        try {
            Thread.sleep(1);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        jPBarThread.interrupt();
    }

    protected LabCollection makeCall(String command, LabCollection ImportCollection) {
        try {
            SocketAddress address = new InetSocketAddress(ConsoleApp.HOSTNAME, ConsoleApp.port);
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] sendData;
            byte[] receiveData = new byte[1024];
            byte[] refreshFlag = new byte[1024];
            String sentence = command;
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address);
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            DatagramPacket flagPacket = new DatagramPacket(refreshFlag, refreshFlag.length);
            clientSocket.send(sendPacket);
            clientSocket.send(new DatagramPacket(ImportCollection.serialize(), ImportCollection.serialize().length, address));
            clientSocket.receive(flagPacket);
            clientSocket.receive(receivePacket);
            String flag = new String(refreshFlag);
            if (flag.contains("true")) {
                System.out.print("Вы пытались совершить запрос по неактуальным данным, они были обновлены, поробуйте повторить ваш запрос");
                String path = "src/music/shekh.wav";
                MusicRunnable t1 = new MusicRunnable();
                t1.path = path;
                Thread thread = new Thread(t1);
                thread.start();
            }
            ConsoleApp.timeOut.interrupt();
            LabCollection receivedCollection = LabCollection.deserialize(receivePacket.getData());
            clientSocket.close();
            return receivedCollection;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
