package fileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

public class FileHelper {
    public final int MAX_CHUNK_SIZE = 64000;

    private String filePath;
    private FSChunk[] fullFile;

    public FileHelper(){}

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FSChunk[] getFullFile() {
        return fullFile;
    }

    public void setFullFile(FSChunk[] fullFile) {
        this.fullFile = fullFile;
    }

    public void readLocalFile(String filename){
        this.filePath = filename;
        File file = new File(filename);
        byte[] data;
        try {
            FileInputStream fis = new FileInputStream(file);

            data = new byte[(int) file.length()];
            fis.read(data);

            int nrOfChunks = data.length / MAX_CHUNK_SIZE + 1;
            this.fullFile = new FSChunk[nrOfChunks];

            for (int i = 0; i < nrOfChunks; i++){
                byte[] body;
                int it;
                if (i < nrOfChunks - 1){
                    body = new byte[MAX_CHUNK_SIZE];
                    for (int j = 0; j < MAX_CHUNK_SIZE; j++){
                        it = MAX_CHUNK_SIZE * i + j;
                        body[j] = data[it];
                    }
                }
                else {
                    int rest = data.length - (MAX_CHUNK_SIZE * i);
                    if (rest == 0){
                        body = new byte[0];
                    }
                    else {
                        body = new byte[rest];
                        for (int j = 0; j < rest; j++){
                            it = MAX_CHUNK_SIZE * i + j;
                            body[j] = data[it];
                        }
                    }
                }
                this.fullFile[i] = new FSChunk(filename, i, body);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void writeChunk(FSChunk chunk) throws IOException{
        Path file = Paths.get("/home/gustavo/backupfiles/" + chunk.getFileID() + "-" + chunk.getChunkNo());
        Files.write(file, chunk.getBody());
    }

    public byte[] restichFile(){
        int fileSize = (this.fullFile.length-1) * MAX_CHUNK_SIZE + this.fullFile[this.fullFile.length-1].getBody().length;

        byte[] data = new byte[fileSize];

        for (int i = 0; i < this.fullFile.length; i++){
            for (int j = 0; j < this.fullFile[i].getBody().length; j++){
                int it = MAX_CHUNK_SIZE * i + j;
                data[it] = this.fullFile[i].getBody()[j];
            }
        }

        return data;
    }

    public String generateFileId(String filename) throws IOException, NoSuchAlgorithmException {
        String cwd = System.getProperty("user.dir");

        //"src/server".length = 10 <-------------------|
        //String homedir = cwd.substring(0, cwd.length()-10) + "localfiles/";
        Path file = Paths.get(filename);

        //Getting file metadata
        BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
        String created =  attributes.creationTime().toString();
        String lastAccess = attributes.lastAccessTime().toString();
        String dateModified =  attributes.lastModifiedTime().toString();
        long size = attributes.size();

        //Getting local mac address
        //TODO: find out if there's a better way to do this
        //eth0 if not on VM; enp0s9 otherwise
        Enumeration<InetAddress> netIface = NetworkInterface.getByName("enp0s9").getInetAddresses();

        InetAddress localAddress = netIface.nextElement();
        while(netIface.hasMoreElements())
        {
            localAddress = netIface.nextElement();
            if(localAddress instanceof Inet4Address && !localAddress.isLoopbackAddress())
            {
                break;
            }
        }

        NetworkInterface network = NetworkInterface.getByInetAddress(localAddress);
        byte[] mac = network.getHardwareAddress();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++){
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        String owner = sb.toString();

        //Creating fileId unique string
        String fileId = filename + owner + created + dateModified + size;
        System.out.println(fileId);


        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(fileId.getBytes(StandardCharsets.UTF_8));

        StringBuffer strbuf = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            strbuf.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
        }

        return strbuf.toString();
    }

    public static void main(String args[]){
        FileHelper fh = new FileHelper();
        fh.readLocalFile("spotty.jpg");
        byte[] data = fh.restichFile();

        try {
            String s = new String(data, "ISO-8859-1");
            System.out.println(s);
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }
}
