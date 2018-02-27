package api.chunk;

/**
 * Created by Murray on 27/02/2018
 */
public class FileChunkHead extends FileChunk {

    private String fileName;
    private Integer numChunks;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getNumChunks() {
        return numChunks;
    }

    public void setNumChunks(Integer numChunks) {
        this.numChunks = numChunks;
    }
}
