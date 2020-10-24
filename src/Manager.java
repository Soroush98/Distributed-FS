import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import sun.misc.IOUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Vector;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Manager {
    static DataInputStream clientData;
    static  InputStream in;
    static  OutputStream out;
    static  DataOutputStream managerData;
    static Vector<FilesFS> exists;
    public static  Vector<Node> refreshnodes(){
        Vector <Node> nodes = new Vector<>();
        nodes.add(new Node(1));
        nodes.add(new Node(2));
        nodes.add(new Node(3));
        nodes.add(new Node(4));
        return nodes;
    }
    public static int checkfile(String fs){
        Vector<Node> nodes = refreshnodes();
        String[] tok = fs.split("\\.");
        int part =1;
        Vector<Integer> parts = new Vector<>();
        for (int i=0;i<nodes.size();i++){
            for (int j=0;j<nodes.get(i).files.size();j++) {
                if (nodes.get(i).files.get(j).name.equals(tok[0]) &&
                        nodes.get(i).files.get(j).extension.equals(tok[1])) {
                    int f = 0;
                    int cur = nodes.get(i).files.get(j).total_partition;
                    for (int x = 0; x < parts.size(); x++) {
                        if (parts.get(x).intValue() == cur)
                            f = 1;
                    }
                    if (f == 0 )
                        parts.add(cur);
                }
            }
        }
        int has =0;
        int index = 0;
        for (int p =0;p<parts.size();p++) {
            int l = 1;
            for (int i = 0; i < nodes.size(); i++) {
                for (int j = 0; j < nodes.get(i).files.size(); j++) {
                    if (nodes.get(i).files.get(j).current_partition == l && nodes.get(i).files.get(j).name .equals( tok[0]) &&
                            nodes.get(i).files.get(j).extension.equals(tok[1])) {
                        if (l==parts.get(p)){
                            has =1;
                            index = p;
                        }
                        l++;
                    }
                }
            }

        }
        if (has==0){
            return -1;
        }
        else
            return parts.get(index);
    }
    public static void list(){
        Vector<Node > nodes = refreshnodes();
        exists = new Vector<>();
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.get(i).files.size(); j++) {
             String fs =   nodes.get(i).files.get(j).name+"."+nodes.get(i).files.get(j).extension;
             if (checkfile(fs) != -1){

                 int s= 0;
                 for (int k=0;k<exists.size();k++){

                     if (exists.get(k).name.equals( nodes.get(i).files.get(j).name) &&
                             exists.get(k).extension.equals( nodes.get(i).files.get(j).extension)){
                         s = 1;

                     }
                 }
                 if (s==0)
                     exists.add(nodes.get(i).files.get(j));
             }
            }
        }

    }
    public static void mix(String fs)  {
        Vector<Node> nodes = refreshnodes();
        String[] tok = fs.split("\\.");
        int index = checkfile(fs) ;
        if (checkfile(fs)==-1)
        {
            System.out.println("File not found or currupted");
        }
        else{
            OutputStream output = null;
            try {
                output = new FileOutputStream("temp"+"/"+fs);
                int cur_p = index;
                System.out.println(cur_p);
                int l=1;
                int finish=0;
                for (int i=0;i<nodes.size();i++){
                    for (int j=0;j<nodes.get(i).files.size();j++)
                    {
                        if (nodes.get(i).files.get(j).current_partition == l && nodes.get(i).files.get(j).name .equals( tok[0]) &&
                                nodes.get(i).files.get(j).extension.equals(tok[1])){
                            File t = new File(nodes.get(i).directory+"/"+nodes.get(i).files.get(j).tfile_name);
                            InputStream x = null;
                            System.out.println(t.getName());
                            x =  new FileInputStream(nodes.get(i).directory+"/"+nodes.get(i).files.get(j).tfile_name);
                            long sz = t.length();
                            byte[] buffer = new byte[(int)sz];
                            int counter = 0;
                            int bytesRead = 0;
                            bytesRead = x.read(buffer, 0, (int)sz);
                            output.write(buffer, 0, bytesRead);
                            x.close();
                            if (l==cur_p) {
                                finish = 1;
                                output.close();
                                break;
                            }
                            l++;



                        }
                    }
                    if (finish ==1 )
                        break;
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    public static void main(String[] args) {
        ServerSocket socket = null;

        try {
            socket = new ServerSocket(20);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                Socket get = socket.accept();
                 in = get.getInputStream();
                 out = get.getOutputStream();
                 clientData = new DataInputStream(in);
                 managerData = new DataOutputStream(out);
                String token =clientData.readUTF();
                String [] split = token.split(" ");
                if (token.equals("Upload")) {
                    upload(get);
                     list();
                }
                else if (token.equals("Refresh"))
                {

                    list();
                    for (int i=0;i<exists.size();i++){
                        managerData.writeUTF(exists.get(i).name);
                        managerData.writeUTF(exists.get(i).extension);
                        managerData.writeUTF(String.valueOf(exists.get(i).total_partition));
                    }
                    managerData.writeUTF("Exit");
                }
                else if (token.equals("Get")){
                    String fn = clientData.readUTF();
                    System.out.println(fn);
                    // mix(fn);
                    String[] tok = fn.split("\\.");
                    Vector<Node> nodes = refreshnodes();
                    int finish = 0;
                    for (int i=0;i<nodes.size();i++){
                        for (int j=0;j<nodes.get(i).files.size();j++) {
                            if (nodes.get(i).files.get(j).name .equals( tok[0]) &&
                                    nodes.get(i).files.get(j).extension.equals(tok[1])) {
                                managerData.writeUTF(nodes.get(i).files.get(j).tfile_name);
                                managerData.writeUTF(nodes.get(i).directory);
                                managerData.writeUTF(String.valueOf(nodes.get(i).files.get(j).size));
                                managerData.writeUTF(nodes.get(i).files.get(j).name);
                                managerData.writeUTF(nodes.get(i).files.get(j).extension);
                                managerData.writeUTF(String.valueOf(nodes.get(i).files.get(j).total_partition));
                                finish = 1;
                                break;
                            }
                        }
                        if (finish ==1 )
                            break;
                    }

                    String dis = "";
                    for (int i=0;i<nodes.size();i++){
                        for (int j=0;j<nodes.get(i).files.size();j++) {
                            if (nodes.get(i).files.get(j).name .equals( tok[0]) &&
                                    nodes.get(i).files.get(j).extension.equals(tok[1])) {
                                 dis = dis + (i+1) +":"+nodes.get(i).files.get(j).current_partition+",";

                            }
                        }
                    }
                    managerData.writeUTF(dis);
                    if (tok[1].equals("jpeg") || tok[1].equals("jpg") ||tok[1].equals("png") || tok[1].equals("txt")){
                      mix(fn);
                    }


                }
                else if (token.equals("Download")){
                    String fn = clientData.readUTF();
                     mix(fn);
                    File myFile = new File("temp/"+fn);
                    byte[] mybytearray = new byte[(int) myFile.length()];
                    FileInputStream fis = new FileInputStream(myFile);
                    BufferedInputStream bis = new BufferedInputStream(fis);
                    DataInputStream dis = new DataInputStream(bis);
                    dis.readFully(mybytearray, 0, mybytearray.length);
                    OutputStream os = out;
                    DataOutputStream dos = new DataOutputStream(os);
                    dos.writeUTF(myFile.getName());
                    dos.writeLong(mybytearray.length);
                    os.write(mybytearray, 0, mybytearray.length);
                    os.flush();

                }

                else if (token.equals("Delete")) {
                    String fn = clientData.readUTF();
                    Vector<Node> nodes = refreshnodes();
                    String[] tok = fn.split("\\.");
                    for (int s = 0; s < nodes.size(); s++) {
                        File dir = new File(nodes.get(s).directory);
                        File[] listOfFiles = dir.listFiles();
                        for (int i = 0; i < listOfFiles.length; i++) {
                            if (listOfFiles[i].isFile()) {
                                String[] tt = listOfFiles[i].getName().split("_");
                                if (tt[0].equals(tok[0]) && tt[1].equals(tok[1]))
                                    if (listOfFiles[i].delete())
                                        System.out.println("deleted ");
                                    else
                                        System.out.println("failed");
                            }
                        }
                    }
                    list();
                }
                else if (token.equals("Rename")) {
                    String fn = clientData.readUTF();
                    String newdata = clientData.readUTF();
                    Vector<Node> nodes = refreshnodes();
                    String[] tok = fn.split("\\.");
                    for (int s = 0; s < nodes.size(); s++) {
                        File dir = new File(nodes.get(s).directory);
                        File[] listOfFiles = dir.listFiles();
                        for (int i = 0; i < listOfFiles.length; i++) {
                            if (listOfFiles[i].isFile()) {
                                String[] tt = listOfFiles[i].getName().split("_");
                                if (tt[0].equals(tok[0]) && tt[1].equals(tok[1])) {
                                    String[] token1 = listOfFiles[i].getName().split("_");
                                    String n = newdata+"_"+tok[1] +"_"+ token1[2] +"_"+token1[3]+"_"+token1[4]+"_"+token1[5];
                                    File newFile = new File(listOfFiles[i].getParent(), n);
                                    Files.move(listOfFiles[i].toPath(), newFile.toPath());
                                        System.out.println("renamed ");

                                }
                            }
                        }
                    }
                    list();
                }


            } catch (IOException e) {

            }
        }
    }
    static void download(String file){
        try {
            byte[] bytes = new byte[16 * 1024];
            InputStream in = new FileInputStream(file);
            int count;
            while ((count = in.read(bytes)) > 0) {
             //   dout.write(bytes, 0, count);
            }
            System.out.println("wrote");
        }catch (IOException e){

        }
    }
    static void upload(Socket get){
        File dir = new File("temp");
        if (!dir.exists())
        dir.mkdir();
        try {
            int bytesRead;
            int current = 0;

             in = get.getInputStream();
             clientData = new DataInputStream(in);
             managerData = new DataOutputStream(out);
            String fileName = clientData.readUTF();
            OutputStream output = new FileOutputStream("temp/"+fileName);

            long size = clientData.readLong();
            long tempsize = size;
            int total = 0;
            byte[] buffer = new byte[1024];
            int counter = 0;
            while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
                total = total +bytesRead;
                float d= total/(float)tempsize;
                counter++;
                if (counter%10000 == 0)
                System.out.println(d*100 + "%");
            }

            in.close();
            clientData.close();
            output.close();
            File in2 = new File("temp/"+fileName);
           shred(refreshnodes(),in2);

        }catch (IOException e){

        }

    }
    static  void shred(Vector<Node> nodes ,File fs ){
        int parts =1 ;
        int psize =1;
        int copy =1;
        if (new File("Settings.txt").exists()){
            Scanner i = null;
            try {
                i = new Scanner(new File("Settings.txt"));
                String [] token = i.nextLine().split(" ");
                String [] token1 = i.nextLine().split(" ");
                String [] token2 = i.nextLine().split(" ");
               copy = Integer.valueOf(token[1]);
                parts = Integer.valueOf(token1[1]);
                psize = Integer.valueOf(token2[1]);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
        int base = 1024*1024;
        if (fs.length() < psize*base){
            for (int c= 1;c<=copy;c++){
                long size = fs.length();
                String dir = nodes.get( 0).directory;

                String[] tok = fs.getName().split("\\.");

                String filename = tok[0] +"_"+tok[1]+"_"+String.valueOf(1)+"_"+String.valueOf(c)+"_"+String.valueOf(1)+"_"+size;
                InputStream x = null;
                try {
                    x = new FileInputStream(fs);
                    DataInputStream fileread = new DataInputStream(x);

                    int total = 0;
                    OutputStream output = new FileOutputStream(dir+"/"+filename);
                    byte[] buffer = new byte[base*psize];
                    int counter = 0;
                    byte[] newbuffer = new byte[(int)fs.length()];
                    int bytesRead = fileread.read(newbuffer, 0,(int) size);
                    output.write(newbuffer, 0, bytesRead);




                    output.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return;
        }
        while (fs.length() < psize * base* parts){
            parts --;
        }
        long size = fs.length();
        for (int p=0;p<parts;p++){

            int bytesRead = 0;
            for (int c= 1;c<=copy;c++){
               String dir = nodes.get( p%nodes.size()).directory;

               String[] tok = fs.getName().split("\\.");

               String filename = tok[0] +"_"+tok[1]+"_"+String.valueOf(p+1)+"_"+String.valueOf(c)+"_"+String.valueOf(parts)+"_"+size;
                InputStream x = null;
                try {
                    x = new FileInputStream(fs);
                    DataInputStream fileread = new DataInputStream(x);

                    int total = 0;
                    OutputStream output = new FileOutputStream(dir+"/"+filename);
                    byte[] buffer = new byte[base*psize];
                    int counter = 0;

                    if (p+1==parts) {

                        byte[] newbuffer = new byte[(int)size];
                        bytesRead = fileread.read(newbuffer, 0,(int) size);
                        output.write(newbuffer, 0, bytesRead);
                    }
                    else{
                        bytesRead = fileread.read(buffer, 0, (int) Math.min(buffer.length, size));
                        output.write(buffer, 0, bytesRead);
                    }


                    output.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            size -= bytesRead;
        }

    }

}
