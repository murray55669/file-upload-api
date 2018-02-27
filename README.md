A demo REST API designed to upload large files to a server.

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

The first request should be to `/file/multipart/``, and contain the `fileName` and `numChunks` (as well as the first chunk of `fileData`), and will return an ID. 

Subsequent chunk `PUT`s for the same file should be to `/file/{fileId}/chunk/` contain a `chunkIndex` (zero indexed), and the relevant chunk of `fileData`.
First request:
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
    "expectedChunks": 3,
    "receivedChunks": 1
}
```
Further requests:
```json
{
    "chunkIndex": 2,
    "fileData": "ZW5jb2RlZA=="
}
```
And corresponding response:
```json
{
    "fileId": 123,
    "message": "Chunk uploaded successfully",
    "expectedChunks": 3,
    "receivedChunks": 2
}
```