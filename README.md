### Readme

A demo REST API designed to upload large files to a server. Build and run using standard Maven methods. 

---

### API endpoints:

`PUT /file/single/` accepts JSON of the form:
```json
{
    "fileName": "myFileName.txt",
    "fileData": "YmFzZSA2NCBlbmNvZGVkIGZpbGU="
}
``` 
where `fileData` is base64-encoded file contents.

---

`PUT /file/multipart/` and `PUT /file/{fileId}/chunk/` combined allow a file to be sent in chunks. 

The first request should be to `/file/multipart/`, and contain the `fileName` and `numChunks`, as well as the first chunk of `fileData`, and will return a fileId. 

Subsequent chunk `PUT`s for the same file should be to `/file/{fileId}/chunk/`, and contain a `chunkIndex` (zero indexed), and the relevant chunk of `fileData`. Example flow shown below.

First request (via `PUT /file/multipart/`):
```json
{
    "fileName": "myFileName.txt",
    "fileData": "YmFzZSA2NA==",
    "numChunks": 3
}
```
Example response:
```json
{
    "fileId": 123,
    "message": "Chunk uploaded successfully",
    "expectedChunks": 3,
    "receivedChunks": 1,
    "fileName": "myFileName.txt"
}
```
Further requests... (via `PUT /file/123/chunk`):
```json
{
    "chunkIndex": 2,
    "fileData": "ZW5jb2RlZA=="
}
```
...And corresponding responses..:
```json
{
    "fileId": 123,
    "message": "Chunk uploaded successfully",
    "expectedChunks": 3,
    "receivedChunks": 2,
    "fileName": "myFileName.txt"
}
```
...until finally:
```json
{
    "fileId": 123,
    "message": "Multi-chunk file uploaded successfully",
    "expectedChunks": 3,
    "receivedChunks": 3,
    "fileName": "myFileName.txt"
}
```