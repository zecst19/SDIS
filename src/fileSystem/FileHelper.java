package fileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {
    public final int MAX_CHUNK_SIZE = 64000;

    private FSChunk[] fullFile;

    public FileHelper(){}

    public void readLocalFile(String filename){
        File file = new File(filename);
        byte[] data;
        try {
            FileInputStream fis = new FileInputStream(file);

            data = new byte[(int) file.length()];
            fis.read(data);

            int nrOfChunks = data.length / MAX_CHUNK_SIZE + 1;

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
                        body = null;
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
        Path file = Paths.get("/home/gustavo/backupfiles/" + chunk.getFileID());
        Files.write(file, chunk.getBody());

        //TODO: static class for logging this
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
}
