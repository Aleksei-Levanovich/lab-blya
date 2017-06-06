import javafx.scene.media.MediaPlayer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Created by Денис on 25.04.2017.
 */
public class RemoveListener extends LabListener {
    public static boolean status=true;
    Integer i=0;
    private JLabel backGround;
    RemoveListener(JTextField nameField, JSpinner ageSpinner, JTextField locField, TreeSet<Human> col, LabTable colTable, JProgressBar jpb,JLabel background) {
        super(nameField, ageSpinner, locField, col, colTable, jpb);
        backGround=background;
    }

    @Override
    public void actionPerformed(ActionEvent e){
        if (getNameField().getText().equals("kebab") && (int)getAgeSpinner().getValue()==0 && getLocField().getText().equals("")) {
            String path = "src/music/kebab.wav";
            String path2="src/music/shum2.wav";
            try {
                backGround.setIcon(new ImageIcon(ImageIO.read(new File("src/Backgrounds/kebab.jpg"))));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            if (i<1){
                MusicRunnable t1 = new MusicRunnable();
                t1.path = path;
                Thread thread = new Thread(t1);
                if (status==true){thread.start(); i = i+1;}
                status = false;
            }
            else{
                MusicRunnable t2 = new MusicRunnable();
                t2.path = path2;
                Thread thread2 = new Thread(t2);
                thread2.start();
                i = i+1;
            }
        }else
        if ((int) getAgeSpinner().getValue() >= 0 && (int) getAgeSpinner().getValue() <= 120) {
            if (Pattern.compile("[A-zА-я']+").matcher(getNameField().getText()).matches()) {
                if (Pattern.compile("[A-zА-я0-9\\-_]+").matcher(getLocField().getText()).matches()) {
                    if(ConsoleApp.timedOut){
                        System.out.print("Вы не подавали признаков жизни более двух минут, будет осуществлено переподключение к серверу");
                        ConsoleApp.tryToConnect();}
                    Human consoleArgument = new Human(getNameField().getText(), (int) getAgeSpinner().getValue(), getLocField().getText());
                    /*Iterator iter = getCollection().iterator();
                    boolean removed = false;
                    while (iter.hasNext()) {
                        Human currentElement = (Human) iter.next();
                        if (currentElement.equals(consoleArgument)) {
                            iter.remove();
                            System.out.print(consoleArgument.toString() + " успешно удалён из коллекции");
                            removed = true;
                            getTable().fireTableDataChanged();
                            break;
                        }
                    }
                    if (!removed) {
                        System.out.print("Данного элемента нет в коллекции");
                    }*/
                    getNameField().setText("");
                    getAgeSpinner().setValue(0);
                    getLocField().setText("");

                    getCollection().clear();
                    getCollection().addAll(makeCall("remove",consoleArgument).getUselessData());
                    getTable().fireTableDataChanged();
                } else {
                    System.out.print("Поле \"Локация\" не может являться пустым. В локации могут содержаться лишь символы кириллицы, латинского алфавита, цифры, \"-\" и \"_\"");
                    String path = "src/music/shekh.wav";
                    MusicRunnable t1 = new MusicRunnable();
                    t1.path = path;
                    Thread thread = new Thread(t1);
                    thread.start();
                }
            } else {
                System.out.print("Поле \"Имя\" не может являться пустым.В имени могут содержаться только символы кириллицы и латинского алфавита");
                String path = "src/music/shekh.wav";
                MusicRunnable t1 = new MusicRunnable();
                t1.path = path;
                Thread thread = new Thread(t1);
                thread.start();
            }
        } else {
            System.out.print("Возраст может быть только в пределах от 0 до 120 лет");
            String path = "src/music/shekh.wav";
            MusicRunnable t1 = new MusicRunnable();
            t1.path = path;
            Thread thread = new Thread(t1);
            thread.start();
        }
    }
}
