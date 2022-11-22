package data;

import java.io.Serial;
import java.io.Serializable;

public class UDPDatagramInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;

    private int size;

    private int numberOfFragments;

    public UDPDatagramInfo(int size, int numberOfFragments) {
        this.size = size;
        this.numberOfFragments = numberOfFragments;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getNumberOfFragments() {
        return numberOfFragments;
    }

    public void setNumberOfFragments(int numberOfFragments) {
        this.numberOfFragments = numberOfFragments;
    }
}
