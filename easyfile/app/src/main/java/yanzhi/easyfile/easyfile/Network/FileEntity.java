package yanzhi.easyfile.easyfile.Network;

import java.io.File;

/**
 * @desc Created by yanzhi on 2016-02-29.
 */
public class FileEntity{

    private final String fileNameString;
    private final File file;
    private final String contentType;
    public FileEntity(File file, String contentType, String fileNameString) {
        this.file = file;
        this.contentType = contentType;
        if (fileNameString == null || fileNameString.length() == 0) {
            fileNameString = file.getName();
        }
        this.fileNameString = fileNameString;
    }

    public File getFile() {
        return file;
    }

    public String getContentType() {
        return contentType;
    }


    public String getFileNameString() {
        return fileNameString;
    }
}
