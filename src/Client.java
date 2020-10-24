import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserPrincipal;
import java.util.Vector;

public class Client extends JFrame {
    JPanel scroll_panel = new JPanel();
    JButton download;
    JLabel current_l = null;
    JPanel prev = null;
    JFrame cur = this;
    JLabel filename=new JLabel();
    JLabel fileext=new JLabel();
    JLabel filesize=new JLabel();
    JLabel partitions=new JLabel();
    JLabel dist=new JLabel();
    JLabel owner=new JLabel();
    JLabel created=new JLabel();
    JLabel lastAccessed=new JLabel();
    JTextField ren = new JTextField();
    Vector <JLabel> labels = new Vector<>();
    public Client(){
        JButton delete=new JButton("Delete");
        JButton rename = new JButton("Rename");
        this.setSize(800,500);
        JPanel panel = new JPanel();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width-800)/2 ,(d.height-500)/2);

        JPanel p1=new JPanel();
        JPanel p2=new JPanel();
        JPanel p3=new JPanel();
        prev = p1;
        p2.setLayout(new GridLayout(8,2,0,0));
        p2.add(new JLabel("File Name:"));
        p2.add(filename);
        p2.add(new JLabel("File Extension:"));
        p2.add(fileext);
        p2.add(new JLabel("File Size:"));
        p2.add(filesize);
        p2.add(new JLabel("Partitions:"));
        p2.add(partitions);
        p2.add(new JLabel("File node distribution:"));
        p2.add(dist);
        p2.add(new JLabel("Owner:"));
        p2.add(owner);
        p2.add(new JLabel("Created:"));
        p2.add(created);
        p2.add(new JLabel("Last accessed :"));
        p2.add(lastAccessed);
        JTabbedPane tp=new JTabbedPane();
        tp.setBounds(220,20,550,350);
        tp.add("Preview",p1);
        tp.add("details",p2);

        //this.add(tp);
        JMenu file, submenu,upload,tools,help;
        JMenuItem i1, i2, i3, i4, i5;
            JMenuBar mb=new JMenuBar();
            file=new JMenu("File");
            upload=new JMenu("Upload");
            tools=new JMenu("Tools");
            help=new JMenu("Help");
            i1=new JMenuItem("Refresh");
            i2=new JMenuItem("Exit");
            i3=new JMenuItem("Settings");
            file.add(i1);
            file.add(i2);
            tools.add(i3);
            mb.add(file);
            mb.add(upload);
            mb.add(tools);
            mb.add(help);
        scroll_panel= new JPanel();
        scroll_panel.setSize(200,400);
        scroll_panel.setLocation(10,20);
        scroll_panel.setLayout(new GridLayout(10,1));

     //   scroll_panel.setBackground(new Color(0x28414958, true));

        download = new JButton("Download");
        download.setSize(100,30);
        download.setLocation(250,380);
        delete.setSize(100,30);
        delete.setLocation(450,380);
        rename.setSize(100,30);
        rename.setLocation(350,380);
        ren.setSize(100,20);
        ren.setLocation(600,380);
        this.add(download);
        this.add(ren);
        this.add(rename);
        this.setLayout(null);
        this.setJMenuBar(mb);
        this.add(panel);
        this.add(tp);
        this.add(delete);
        this.add(scroll_panel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        refresh();
        upload.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    Socket socket  = null;
                    try {
                        socket = new Socket("127.0.0.1",20);

                        String loc=   chooser.getSelectedFile().getAbsolutePath();
                        File myFile = new File(loc);
                        byte[] mybytearray = new byte[(int) myFile.length()];
                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        DataInputStream dis = new DataInputStream(bis);
                        dis.readFully(mybytearray, 0, mybytearray.length);
                        OutputStream os = socket.getOutputStream();
                        DataOutputStream dos = new DataOutputStream(os);
                        dos.writeUTF("Upload");
                        dos.writeUTF(myFile.getName());
                        dos.writeLong(mybytearray.length);


                        //Sending file data to the server
                        os.write(mybytearray, 0, mybytearray.length);
                        os.flush();

                        //Closing socket
                        os.close();
                        dos.close();
                        socket.close();
                        //         dout.writeUTF("Upload "+ loc + " "+ (int)myFile.length())
//                        if (dis.readUTF().equals("Compelete")){
//                            System.out.println("Upload Compeleted");
//
//                       }
                     //   else{
                       //     System.out.println("Failed");
                        //}

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

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
        rename.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Socket socket = null;
                InputStream dis = null;
                try {
                    socket = new Socket("127.0.0.1", 20);
                    dis = socket.getInputStream();
                    OutputStream dout = socket.getOutputStream();
                    DataInputStream di = new DataInputStream(dis);
                    DataOutputStream ou = new DataOutputStream(socket.getOutputStream());
                    if (current_l!=null) {
                        ou.writeUTF("Rename");
                        ou.writeUTF(current_l.getText());
                        ou.writeUTF(ren.getText());

                    }
                    else {
                        System.out.println("select something");
                    }
                    socket.close();
                    dis.close();
                    di.close();
                } catch (IOException e1) {
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
        delete.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Socket socket = null;
                InputStream dis = null;
                try {
                    socket = new Socket("127.0.0.1", 20);
                    dis = socket.getInputStream();
                    OutputStream dout = socket.getOutputStream();
                    DataInputStream di = new DataInputStream(dis);
                    DataOutputStream ou = new DataOutputStream(socket.getOutputStream());
                    if (current_l!=null) {
                        ou.writeUTF("Delete");
                        ou.writeUTF(current_l.getText());

                    }
                    else {
                        System.out.println("select something");
                    }
                    socket.close();
                    dis.close();
                    di.close();
                } catch (IOException e1) {
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
        download.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION && current_l!=null) {
                    try {

                        Socket socket = new Socket("127.0.0.1", 20);
                        InputStream dis = socket.getInputStream();
                        OutputStream dout = socket.getOutputStream();
                        DataInputStream di = new DataInputStream(dis);
                        DataOutputStream ou = new DataOutputStream(socket.getOutputStream());
                        ou.writeUTF("Download");
                        ou.writeUTF(current_l.getText());

                        dout = new DataOutputStream(dout);
                        String fileName = di.readUTF();
                        System.out.println(chooser.getSelectedFile());
                        OutputStream output = new FileOutputStream(chooser.getSelectedFile()+"/"+ fileName);

                        long size = di.readLong();
                        long tempsize = size;
                        int total = 0;
                        byte[] buffer = new byte[1024];
                        int counter = 0;
                        int bytesRead = 0;
                        while (size > 0 && (bytesRead = di.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                            output.write(buffer, 0, bytesRead);
                            size -= bytesRead;
                            total = total + bytesRead;
                            float d = total / (float) tempsize;
                            counter++;
                            if (counter % 10000 == 0)
                                System.out.println(d * 100 + "%");
                        }
                        System.out.println("Compelete");
                        di.close();
                        output.close();
                        socket.close();
                        dis.close();
                        dout.close();

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
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
        i3.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {


            }

            @Override
            public void mousePressed(MouseEvent e) {
                new Settings();
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
        i1.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                try {
                    Socket socket = new Socket("127.0.0.1",20);
                    InputStream is = socket.getInputStream();
                    DataInputStream di = new DataInputStream(is);
                    DataOutputStream ou = new DataOutputStream(socket.getOutputStream());
                    ou.writeUTF("Refresh");
                    labels = new Vector<>();
                    while (true){
                        String name = di.readUTF();
                        if (name.equals("Exit"))
                            break;
                        String ext = di.readUTF();
                      //  System.out.println(name + " " + ext);
                        labels.add(new JLabel(name+"."+ext));
                        String partition = di.readUTF();
                    }
                    scroll_panel.removeAll();
                for (int i=0;i<labels.size();i++) {
                    scroll_panel.add(labels.get(i));


                }
                    cur.revalidate();
                    scroll_panel.revalidate();


                } catch (IOException e1) {
                    e1.printStackTrace();
                }
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

       scroll_panel.addMouseListener(new MouseListener() {
           @Override
           public void mouseClicked(MouseEvent e) {
              for (int i=0;i<labels.size();i++)
              {

                  labels.get(i).addMouseListener(new MouseListener() {
                      @Override
                      public void mouseClicked(MouseEvent e) {

                      }

                      @Override
                      public void mousePressed(MouseEvent e) {
                          prev.removeAll();
                          prev.revalidate();
                          prev.repaint();
                          JLabel current = (JLabel) e.getComponent();
                          if (current_l!=current  ) {

                              current.setBorder(new LineBorder(Color.black, 2));


                          }
                          if (current_l!=null && current_l!=current){
                              current_l.setBorder(null);

                          }
                          current_l = current;
                          scroll_panel.revalidate();
                          Socket socket = null;
                          try {
                              socket = new Socket("127.0.0.1", 20);
                              InputStream is = socket.getInputStream();
                              DataInputStream di = new DataInputStream(is);
                              DataOutputStream ou = new DataOutputStream(socket.getOutputStream());
                              ou.writeUTF("Get");
                              ou.writeUTF(current_l.getText());
                              String tfile = di.readUTF();
                              String dir = di.readUTF();
                              String siz = di.readUTF();
                              String name = di.readUTF();
                              String exten = di.readUTF();
                              String p = di.readUTF();
                              String dis = di.readUTF();
                              filename.setText(name);
                              fileext.setText(exten);
                              partitions.setText(p);
                              dist.setText(dis);
                              filesize.setText(siz);

                              Path path = Paths.get(dir + "/" + tfile);
                              FileOwnerAttributeView foav = Files.getFileAttributeView(path,
                                      FileOwnerAttributeView.class);
                              UserPrincipal own = foav.getOwner();
                              owner.setText(own.getName());
                              BasicFileAttributes attr;

                              try {
                                  attr = Files.readAttributes(path, BasicFileAttributes.class);

                                  created.setText(attr.creationTime().toString());
                                  lastAccessed.setText(attr.lastAccessTime().toString());
                              } catch (IOException e2) {
                                  System.out.println("oops error! ");
                              }
                              if (exten.equals("jpg") || exten.equals("jpeg") || exten.equals("png")) {

                                  BufferedImage myPicture = ImageIO.read(new File("temp/" + filename.getText() + "." + fileext.getText()));
                                  JLabel picLabel = new JLabel(new ImageIcon(myPicture));
                                  prev.add(picLabel);


                              } else if (exten.equals("txt")) {

                                  JTextArea textArea = new JTextArea(3, 20);
                                  prev.add(textArea);
                                  BufferedReader br = new BufferedReader(new FileReader(new File("temp/" + filename.getText() + "." + fileext.getText())));
                                  String text = null;
                                  int lineCount = 0;
                                  while ((text = br.readLine()) != null ) {

                                      textArea.append(text + "\r\n");
                                      lineCount++;
                                  }
                              }




                          } catch (IOException e1) {
                              e1.printStackTrace();
                          }

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
public  void refresh(){
    try {
        Socket socket = new Socket("127.0.0.1",20);
        InputStream is = socket.getInputStream();
        DataInputStream di = new DataInputStream(is);
        DataOutputStream ou = new DataOutputStream(socket.getOutputStream());
        ou.writeUTF("Refresh");
        labels = new Vector<>();
        while (true){
            String name = di.readUTF();
            if (name.equals("Exit"))
                break;
            String ext = di.readUTF();
            //  System.out.println(name + " " + ext);
            labels.add(new JLabel(name+"."+ext));
            String partition = di.readUTF();
        }
        scroll_panel.removeAll();
        for (int i=0;i<labels.size();i++) {
            scroll_panel.add(labels.get(i));


        }
        cur.revalidate();
        scroll_panel.revalidate();


    } catch (IOException e1) {
        e1.printStackTrace();
    }
}



}
