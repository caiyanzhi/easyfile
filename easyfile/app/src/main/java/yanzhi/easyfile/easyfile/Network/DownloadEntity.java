package yanzhi.easyfile.easyfile.Network;

/**
 * 记录断点续传的方位，添加header：Range:bytes=startPoint-(endPoint-1)
 * @desc Created by yanzhi on 2016-02-29.
 */
public class DownloadEntity {
    private boolean supportResume;
    private long startPoint;
    private long endPoint;
    public DownloadEntity(boolean supportResume, long startPoint, long endPoint) {
        this.supportResume = supportResume;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public DownloadEntity() {
    }

    public boolean isSupportResume() {
        return supportResume;
    }

    public void setSupportResume(boolean supportResume) {
        this.supportResume = supportResume;
    }

    public long getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(long startPoint) {
        this.startPoint = startPoint;
    }

    public long getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(long endPoint) {
        this.endPoint = endPoint;
    }
}
