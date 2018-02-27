package api.chunk;

/**
 * Created by Murray on 27/02/2018
 */
public class FileChunkTail extends FileChunk {

    private Integer fileId;
    private Integer chunkIndex;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public Integer getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }
}
