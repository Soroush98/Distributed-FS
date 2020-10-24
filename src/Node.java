import java.io.File;
import java.util.Vector;

public class Node {
    int number;
    String directory;
    Vector<FilesFS> files = new Vector<>();
    public Node(int number){
        this.number =number;
        this.directory = String.valueOf(number);
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        else{
            File[] listOfFiles = dir.listFiles();

            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                  String [] token =   listOfFiles[i].getName().split("_");
                  FilesFS temp = new FilesFS(token[0],token[1],Integer.valueOf(token[2]),Integer.valueOf(token[4]),listOfFiles[i].getName(),Integer.valueOf(token[5]));
                  files.add(temp);
                }
            }
        }
    }
}
