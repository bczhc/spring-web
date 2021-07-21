package pers.zhc;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author bczhc
 */
public class Upload {
    private final Object ctx;
    private final String pathDir;
    private final String baseUrl;

    public static void main(String[] args) {
        System.out.println("Hi!");
    }

    public Upload(Object ctx, String pathDir, String baseUrl) {
        this.ctx = ctx;
        this.pathDir = pathDir;
        this.baseUrl = baseUrl;
    }

    public void run(Object ctx) {
        final File pathDirFile = new File(pathDir);
        final File[] listFiles = pathDirFile.listFiles();
        if (listFiles == null) {
            return;
        }
        new Thread(() -> {
            for (File file : listFiles) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    upload(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void upload(File f) throws IOException {
        final String filename = f.getName();
        // noinspection CharsetObjectCanBeUsed
        final byte[] filenameBytes = filename.getBytes("UTF-8");

        final URL url = new URL(baseUrl + "/some-tools-app/upload");
        final URLConnection connection = url.openConnection();
        connection.setDoOutput(true);

        final OutputStream outputStream = connection.getOutputStream();

        final String filenameLenStr = getCompletedLenStr(filename.getBytes().length);
        if (filenameLenStr.length() != 12) {
            throw new AssertionError();
        }

        final byte[] fileData = read(f);
        final byte[] digest = digest(fileData);

        outputStream.write(digest);
        outputStream.write(filenameLenStr.getBytes());
        outputStream.write(filenameBytes);
        outputStream.write(fileData);
        outputStream.flush();
        outputStream.close();

        final InputStream inputStream = connection.getInputStream();
//        final String response = readIS(inputStream);

        inputStream.close();
    }

    private String getCompletedLenStr(long length) {
        final String lenStr = String.valueOf(length);
        final int strLen = lenStr.length();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (12 - strLen); i++) {
            sb.append('0');
        }
        sb.append(lenStr);

        return sb.toString();
    }

    private byte[] digest(byte[] data) {
        final MessageDigest sha1;
        try {
            sha1 = MessageDigest.getInstance("SHA1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException();
        }
        sha1.update(data);
        return sha1.digest();
    }

    private byte[] read(File f) throws IOException {
        final long length = f.length();
        byte[] data = new byte[((int) length)];
        FileInputStream is = new FileInputStream(f);
        final int readLen = is.read(data);
        if (readLen != length) {
            throw new AssertionError();
        }
        is.close();
        return data;
    }
}
