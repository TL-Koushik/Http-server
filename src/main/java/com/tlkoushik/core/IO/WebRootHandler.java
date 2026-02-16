package com.tlkoushik.core.IO;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLConnection;

public class WebRootHandler {

    private File webRoot;

    public WebRootHandler(String rootDirectoryRelativePath) throws WebRootNotFoundExceptipn {

        webRoot = new File(rootDirectoryRelativePath);

        if (!webRoot.exists() || !webRoot.isDirectory()) {
            throw new WebRootNotFoundExceptipn("web root not found at : " + rootDirectoryRelativePath);
        }

    }

    public String getFileMimeType(String relativePath) throws FileNotFoundException {
        if (checkIfEndsWithSlash(relativePath)) {
            relativePath += "index.html"; // if end with slash or slash request or default request to ip will be
                                          // index.html
        }
        if (!checkIfProvidedPathExists(relativePath)) {
            throw new FileNotFoundException("file does not exists : " + relativePath);
        }
        File file = new File(webRoot, relativePath);
        String mimeType = URLConnection.getFileNameMap().getContentTypeFor(file.getName());

        if (mimeType == null) {
            return "application/octet-stream";
        }

        return mimeType;
    }

    private boolean checkIfProvidedPathExists(String relativePath) {

        File file = new File(webRoot, relativePath);

        if (!file.exists())
            return false;

        try {
            if (file.getCanonicalPath().startsWith(webRoot.getCanonicalPath()))
                return true;
        } catch (IOException e) {
            return false;
        }

        return false;

    }

    public boolean checkIfEndsWithSlash(String relativePath) {
        return relativePath.endsWith("/");
    }

    public byte[] getFileByteArray(String relativePath) throws FileNotFoundException, ReadFileException {
        if (checkIfEndsWithSlash(relativePath)) {
            relativePath += "index.html";
        }

        if (!checkIfProvidedPathExists(relativePath)) {
            throw new FileNotFoundException("file not found at : " + relativePath);
        }

        File file = new File(webRoot, relativePath);

        byte[] fileByteArray = new byte[(int) file.length()];
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            fileInputStream.read(fileByteArray);
            fileInputStream.close();
        } catch (IOException e) {
            throw new ReadFileException(e);
        }
        return fileByteArray;
    }

}
