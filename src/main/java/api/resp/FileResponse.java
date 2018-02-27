package api.resp;

/**
 * Created by Murray on 27/02/2018
 */
public class FileResponse {

    private Integer fileId;
    private Integer expectedChunks;
    private Integer receivedChunks;
    private String message;
    private String fileName;

    public FileResponse(Integer fileId, String fileName, Integer expectedChunks, Integer receivedChunks) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.expectedChunks = expectedChunks;
        this.receivedChunks = receivedChunks;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Integer getExpectedChunks() {
        return expectedChunks;
    }

    public void setExpectedChunks(Integer expectedChunks) {
        this.expectedChunks = expectedChunks;
    }

    public Integer getReceivedChunks() {
        return receivedChunks;
    }

    public void setReceivedChunks(Integer receivedChunks) {
        this.receivedChunks = receivedChunks;
    }

    public String getMessage() {
        return message;
    }

    public FileResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
