package data;

import java.io.Serial;
import java.io.Serializable;

public class FrameInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private byte[] data;
    private int size;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public FrameInfo(byte[] data, int size) {
        this.data = data;
        this.size = size;
    }
}