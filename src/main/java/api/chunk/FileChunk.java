package api.chunk;

/**
 * Created by Murray on 27/02/2018
 */
public abstract class FileChunk {
    private String fileData;

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }
}
