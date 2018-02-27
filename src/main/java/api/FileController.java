package api;

import api.chunk.FileChunkHead;
import api.chunk.FileChunkTail;
import api.resp.FileResponse;
import api.resp.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Created by Murray on 27/02/2018
 */

@RestController
public class FileController {

    private final Map<Integer, ChunkedFileBuilder> builderMap = new ConcurrentHashMap<>();

    @RequestMapping(value="/file/single", method=PUT)
    public ResponseEntity<FileResponse> greeting(@RequestBody SingleFile singleFile) {
        ChunkedFileBuilder cbf = new ChunkedFileBuilder(singleFile.getFileName(), 1);
        return cbf.handleReceivedChunk(0, singleFile.getFileData());
    }

    @RequestMapping(value="/file/multipart", method=PUT)
    public ResponseEntity<FileResponse> greeting(@RequestBody FileChunkHead fileChunk) {
        ChunkedFileBuilder cbf = new ChunkedFileBuilder(fileChunk.getFileName(), fileChunk.getNumChunks());
        ResponseEntity<FileResponse> resp = cbf.handleReceivedChunk(0, fileChunk.getFileData());
        if (resp.getStatusCode() != HttpStatus.OK) return resp;
        if (fileChunk.getNumChunks() != 1) {
            builderMap.put(cbf.getFileId(), cbf);
        }
        return resp;
    }

    @RequestMapping(value="/file/{fileId}/chunk", method=PUT)
    public ResponseEntity greeting(@PathVariable("fileId") Integer fileId, @RequestBody FileChunkTail fileChunk) {
        if (fileId == null) return new ResponseEntity<>(new MessageResponse("Badly formatted request"), HttpStatus.BAD_REQUEST);
        ChunkedFileBuilder cbf = builderMap.get(fileId);
        if (cbf == null) return new ResponseEntity<>(new MessageResponse(String.format("No file found with id %d", fileId)), HttpStatus.BAD_REQUEST);
        ResponseEntity<FileResponse> resp = cbf.handleReceivedChunk(fileChunk.getChunkIndex(), fileChunk.getFileData());
        if (cbf.getNumChunks() != null && cbf.getReceivedChunks() == cbf.getNumChunks()) builderMap.remove(fileId);
        return resp;
    }
}
