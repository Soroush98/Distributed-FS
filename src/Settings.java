import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.util.Scanner;

public class Settings extends JFrame{
    JLabel copies = new JLabel("Number of copies");
    JLabel parts = new JLabel("Number of Parts") ;
    JLabel size =new JLabel("Minimum size of each part(MB)");
    String[] cps ={"1","2","3"};
    String[] prts = {"1","2","4","8"};
    String[] szs = {"1","2","5","10","20","50"};
    JComboBox<String> cp = new JComboBox<>(cps);
    JComboBox<String> prt = new JComboBox<>(prts);
    JComboBox<String> sz = new JComboBox<>(szs);
    JButton  save = new JButton("Save");
    public Settings(){
        JFrame cur = this;
        System.out.println("sf");
        this.setSize(500,500);
        JPanel panel = new JPanel();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width-500)/2 ,(d.height-500)/2);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        File set = new File("Settings.txt");
        cp.setSelectedIndex(0);
        prt.setSelectedIndex(0);
        sz.setSelectedIndex(0);
        if (!set.exists()){
            try {
                set.createNewFile();
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter("Settings.txt", "UTF-8");
                    writer.println("Copies "+cp.getSelectedItem());
                    writer.println("Parts "+prt.getSelectedItem());
                    writer.println("Size "+sz.getSelectedItem());
                    writer.close();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Scanner i = new Scanner(set);
                String [] token = i.nextLine().split(" ");
                String [] token1 = i.nextLine().split(" ");
                String [] token2 = i.nextLine().split(" ");
                i.close();

                cp.setSelectedItem(token[1]);
                prt.setSelectedItem(token1[1]);
                sz.setSelectedItem(token2[1]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        this.add(copies);
        this.add(cp);
        this.add(parts);
        this.add(prt);
        this.add(size);
        this.add(sz);
        this.add(save);
        this.setLayout(new GridLayout(4,2,10,20));
        this.setVisible(true);
        save.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter("Settings.txt", "UTF-8");
                    writer.println("Copies "+cp.getSelectedItem());
                    writer.println("Parts "+prt.getSelectedItem());
                    writer.println("Size "+sz.getSelectedItem());
                    writer.close();
                    cur.dispose();
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
}
