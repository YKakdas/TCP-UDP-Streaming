package data;

import java.io.Serial;
import java.io.Serializable;

public class FrameInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private byte[] data;
    private int frameNum;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getFrameNum() {
        return frameNum;
    }

    public void setFrameNum(int frameNum) {
        this.frameNum = frameNum;
    }

    public FrameInfo(byte[] data, int frameNum) {
        this.data = data;
        this.frameNum = frameNum;
    }
}