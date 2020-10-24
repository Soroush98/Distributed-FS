public class FilesFS {
    public String name;
    public String extension;
   // public String size;
    public int current_partition;
   // public String distribution;
    public String Created;
    public String LastAccessed;
    public int total_partition;
    public String tfile_name;
    public int  size;
    public FilesFS(String name ,String extension,int current,int total_partition,String tfile_name,int size){
        this.name = name;
        this.extension = extension ;
        this.current_partition = current;
        this.total_partition = total_partition;
        this.tfile_name = tfile_name;
        this.size = size;

    }
}
