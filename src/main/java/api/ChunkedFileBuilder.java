package api;

import api.resp.FileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Murray on 27/02/2018
 */
public class ChunkedFileBuilder {

    private static final File TEMP_DIR = new File("temp");
    private static final File OUTPUT_DIR = new File("uploads");
    private static final AtomicInteger FILE_ID = new AtomicInteger(1);

    private final int fileId;
    private final String fileName;
    private final Integer numChunks;

    private Set<Integer> receivedChunks;
    private File tempDir;

    public ChunkedFileBuilder(String fileName, Integer numChunks) {
        fileId = FILE_ID.getAndIncrement();
        this.fileName = fileName;
        this.numChunks = numChunks;

        receivedChunks = new HashSet<>();
    }

    public int getFileId() {
        return fileId;
    }

    public Integer getNumChunks() {
        return numChunks;
    }

    public int getReceivedChunks() {
        return receivedChunks.size();
    }

    public synchronized ResponseEntity<FileResponse> handleReceivedChunk(Integer chunkIndex, String base64Content) {
        if (numChunks == null) return new ResponseEntity<>(new FileResponse(fileId, fileName, numChunks, receivedChunks.size()).setMessage("Missing required field: numChunks"), HttpStatus.BAD_REQUEST);
        if (chunkIndex == null) return new ResponseEntity<>(new FileResponse(fileId, fileName, numChunks, receivedChunks.size()).setMessage("Missing required field: chunkIndex"), HttpStatus.BAD_REQUEST);
        if (chunkIndex >= numChunks) return new ResponseEntity<>(new FileResponse(fileId, fileName, numChunks, receivedChunks.size()).setMessage(String.format("Invalid chunkIndex, was %d, expected number of chunks was %d", chunkIndex, numChunks)), HttpStatus.BAD_REQUEST);
        FileResponse resp = new FileResponse(fileId, fileName, numChunks, receivedChunks.size());
        if (fileName == null) return new ResponseEntity<>(resp.setMessage("Missing required field: fileName"), HttpStatus.BAD_REQUEST);
        if (base64Content == null) return new ResponseEntity<>(resp.setMessage("Missing required field: fileName"), HttpStatus.BAD_REQUEST);
        try {
            if (numChunks == 1) {
                doSaveFile(base64Content);
                return new ResponseEntity<>(resp.setMessage("Single-chunk file uploaded successfully"), HttpStatus.OK);
            } else {
                doSaveChunk(chunkIndex, base64Content);
                resp.setReceivedChunks(receivedChunks.size());
                if (numChunks == receivedChunks.size()) {
                    doAssembleChunks();
                    return new ResponseEntity<>(resp.setMessage("Multi-chunk file uploaded successfully"), HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(resp.setMessage("Chunk uploaded successfully"), HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(resp.setMessage("Unknown internal error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void doSaveChunk(int chunkIndex, String base64Content) throws IOException {
        if (!receivedChunks.contains(chunkIndex)) {
            receivedChunks.add(chunkIndex);
            tempDir = TEMP_DIR.toPath().resolve("file-" + fileId).toFile();
            if (!tempDir.exists() && !tempDir.mkdir()) throw new IOException("Failed to initialise file directory");
            doSaveToPath(getChunkPath(chunkIndex), base64Content);
        }
    }

    private void doSaveFile(String base64Content) throws IOException {
        doSaveToPath(OUTPUT_DIR.toPath().resolve(getNewFilename()), base64Content);
    }

    private String getNewFilename() {
        return String.format("id_%d_%s", fileId, fileName);
    }

    private Path getChunkPath(int chunkIndex) {
        return tempDir.toPath().resolve("chunk-" + chunkIndex);
    }

    private void doSaveToPath(Path path, String base64Content) throws IOException {
        File out = path.toFile();
        byte[] decodedString = Base64.getDecoder().decode(base64Content);
        try (FileOutputStream stream = new FileOutputStream(out)) {
            stream.write(decodedString);
        }
    }

    private void doAssembleChunks() throws IOException {
        File out = OUTPUT_DIR.toPath().resolve(getNewFilename()).toFile();
        List<Integer> orderedChunks = new ArrayList<>(receivedChunks);
        try (FileOutputStream outStream = new FileOutputStream(out)) {
            for (Integer index : orderedChunks) {
                try (FileInputStream inStream = new FileInputStream(getChunkPath(index).toFile())) {
                    int c;
                    while ((c = inStream.read()) != -1) {
                        outStream.write(c);
                    }
                }
            }
        } finally {
            FileSystemUtils.deleteRecursively(tempDir);
        }
    }

    public static void bootstrap() {
        if (!OUTPUT_DIR.exists() && !OUTPUT_DIR.mkdir()) throw new RuntimeException("Failed to create output directory");
        if (!TEMP_DIR.exists() && !TEMP_DIR.mkdir()) throw new RuntimeException("Failed to create temp directory");
    }
}
