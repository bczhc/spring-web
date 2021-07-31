package pers.zhc.web.controller.apps.sometools

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pers.zhc.jni.sqlite.SQLite3
import pers.zhc.web.Global
import pers.zhc.web.utils.Digest

import javax.servlet.http.HttpServletRequest
import java.nio.charset.StandardCharsets

/**
 * @author bczhc
 */
@RestController
class Uploader {
    @Autowired
    private HttpServletRequest request

    private logger = LoggerFactory.getLogger(this.getClass())
    private SQLite3 filesDatabase
    private File uploadDir

    Uploader() {
        init()
    }

    void init() {
        logger.debug("init...")

        uploadDir = new File("./upload")
        if (!uploadDir.exists()) {
            assert uploadDir.mkdir()
        }

        filesDatabase = SQLite3.open("./files.db")
        initFilesDatabase()
    }

    class ReturnMsg {
        def status
        def msg
        def data

        ReturnMsg(int status, String msg, data) {
            this.status = status
            this.msg = msg
            this.data = data
        }

        ReturnMsg(int status, String msg) {
            this(status, msg, null)
        }
    }

    @RequestMapping("/some-tools-app/upload")
    synchronized def uploadRequest() {
        def msg = upload()
        logger.info("File received: {}", msg.properties)
        return msg
    }

    @RequestMapping("/some-tools-app/upload-list")
    synchronized def getList() {
        logger.info("getList()")

        def r = []

        filesDatabase.exec("SELECT filename, digest, upload_time FROM \"file\"") {
            def map = [
                    "filename"            : it[0],
                    "sha1"                : it[1],
                    "uploadTime"          : it[2] as long,
                    "uploadTimeDateString": new Date(it[2] as long).toString()
            ]
            r.add(map)
            return 0
        }

        return r
    }

    /**
     * _____________________________________________________________________________________________
     * | SHA1 digest (filename + content) (160) | Filename length string (96) | filename | content |
     * ---------------------------------------------------------------------------------------------
     */
    def upload() {
        def inputStream = request.inputStream

        def sha1 = new byte[20]
        def read = inputStream.read(sha1)
        if (read != 20) {
            return new ReturnMsg(1, "Unexpected EOF")
        }

        def filenameLenStrBytes = new byte[12]
        if (inputStream.read(filenameLenStrBytes) != 12) {
            return new ReturnMsg(4, "Unexpected EOF")
        }
        def filenameLenStr = new String(filenameLenStrBytes, 0, 12)
        def filenameLength = filenameLenStr as int

        def filenameBytes = new byte[filenameLength]
        if (inputStream.read(filenameBytes) != filenameLength) {
            return new ReturnMsg(5, "Unexpected EOF")
        }
        def filename = new String(filenameBytes, StandardCharsets.UTF_8)

        def arr = new ByteArrayOutputStream()
        writeStream(inputStream, arr)
        def fileBytes = arr.toByteArray()
        def digest = Global.sha1.digest(fileBytes)
        if (!Arrays.equals(digest, sha1)) {
            return new ReturnMsg(2, "Failed to check digest", ["filename": filename])
        }
        inputStream.close()

        def hasRecord = filesDatabase.hasRecord("SELECT * FROM \"file\" WHERE digest IS ?",
                [Digest.toHexString(sha1)] as Object[])
        if (hasRecord) {
            def msg = new ReturnMsg(3, "Already exists")
            return msg
        }

        def digestString = Digest.toHexString(digest)
        if (checkLocalFileExistence(filename)) {
            updateFileRecord(filename, digestString)
        } else {
            addFileRecord(filename, digestString)
        }
        save(filename, fileBytes)

        def fileInfo = [
                "filename": filename,
                "digest"  : digestString
        ]

        return new ReturnMsg(0, "OK", fileInfo)
    }

    def save(String filename, byte[] bytes) {
        def file = new File(uploadDir, filename)

        def os = new FileOutputStream(file)
        os.write(bytes)
        os.close()
    }

    def initFilesDatabase() {
        filesDatabase.exec("""CREATE TABLE IF NOT EXISTS "file"
(
    digest      TEXT NOT NULL PRIMARY KEY,
    filename    TEXT NOT NULL,
    upload_time INTEGER
);""")
    }

    def addFileRecord(String filename, String digest) {
        filesDatabase.execBind("""INSERT INTO "file" (digest, filename, upload_time)
VALUES (?, ?, ?)""", [digest, filename, System.currentTimeMillis()] as Object[])
    }

    def updateFileRecord(String filename, String digest) {
        filesDatabase.execBind("""UPDATE "file"
SET digest=?,
    upload_time=?
WHERE filename IS ?""", [digest, System.currentTimeMillis(), filename] as Object[])
        logger.info("update: $filename")
    }

    static def writeStream(InputStream is, OutputStream os) {
        int readLen
        def buf = new byte[4096]
        while ((readLen = is.read(buf)) != -1) {
            os.write(buf, 0, readLen)
            os.flush()
        }
    }

    private boolean checkLocalFileExistence(String filename) {
        return new File(uploadDir, filename).exists()
    }
}
