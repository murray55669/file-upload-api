### Project Report

##### Requirements

###### 1. Define the API and explain why the set of functions that you provided is sufficient and complete for uploading large files to a server.

The API is designed to allow users to split files into multiple parts (chunks) and uploaded piecemeal, thus enabling large files to be conveniently uploaded without running afoul of network or system limitations.

The API has a grand total of three endpoints, one of which is surplus to requirements. These are detailed below, and the exact functionality and usage of each is explained in `README.md`.

 - `PUT /file/single`: an initial proof-of-concept
 - `PUT file/multipart`: begins a chunked/multi-part upload, and returns a fileId, which is used in conjunction with the next endpoint to upload files in small parts
 - `PUT /file/{fileId}/chunk`: adds a chunk to an existing upload

The onus is placed on the user to send sensible data (although some basic input validation is provided). 

###### 2. Implement it any language that you prefer.

The implementation is in Java, using the Spring framework. 

JSON is used for all incoming and outgoing data, and base64 encoding to transfer the file content. This is less than ideal, as base64 inflates the data by up to a third. A better implementation could use multipart/form methods to directly transfer file bytes. 

Internally, the partially uploaded files are stored in a temporary folder on the server's filesystem. Once a complete set of chunks has been received, the partial files are streamed byte-by-byte (which could benefit from a buffering mechanism) and combined to form the final file. 

Metadata for the partial uploads is kept purely in memory, as is the auto-incrementing fileId. This presents a significant problem if the server is ever restarted (and is a memory leak, besides), and should ideally be moved to more permanent storage, such as a persistent database. The partial files, too, could benefit from a more robust storage/retrieval solution.

The metadata is minimal, simply the fileName and a generated fileId. If more metadata (created by, date last modified, etc.) was required, the input JSON could be expanded to accept this.

The file IDs are stored as integers, which should ideally be switched to longs, to minimise the chances of ID exhaustion.
 
There is no hard restriction on the size of each chunk (although the Spring framework used has a default); this could be configured depending on requirements.

Besides the problems mentioned, the overall implementation is extremely minimal, and as this engineer has no experience building REST APIs (and never having created anything in Spring) could undoubtedly be improved throughout.

###### 3. Explain what should be changed when:
###### -- a. We need authentication for uploading;

A plugin called "Spring Security" is available for the Spring framework, which could be leveraged to cover practically any security needs. Other options are likely available; anything standard and widely regarded would suffice.

###### -- b. We need a set of API to show uploaded files and progress;

A handful of `GET` endpoints could be implemented, for example:
 
 - one to list all files (which should be paginated)
 - one to retrieve a file by ID (potentially returned in chunks)
 - one to retrieve upload progress (chunks received/chunks total) by fileId
  
  Such endpoints would be trivially easy to create were the server using a database for storage, as discussed above.

###### -- c. Resumable uploads.

This is effectively supported, as files are only removed from tracking on server shutdown or when an upload completes. A user can submit chunks as sporadically as they require, and the current implementation should handle it (although there is no endpoint to provide e.g. the list of received chunks for a given fileId, so the user must track what they have sent). 

Holding partially completed files indefinitely is a flaw resulting from the minimal design, but it wouldn't be hard to imagine converting the application to use a persistent database (as detailed above) and periodically flushing abandoned uploads; in this case an endpoint could be provided to flag an incomplete upload so as not to have it cleaned, thus allowing it to be resumed at a later time. A sensible grace period (preferably configurable) would ensure e.g. a temporary network outage would not cause a partially completed upload to be immediately flushed. 