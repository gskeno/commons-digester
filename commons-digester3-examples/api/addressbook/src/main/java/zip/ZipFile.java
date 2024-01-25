package zip;

import java.util.ArrayList;
import java.util.List;

public class ZipFile {
    private String type;

    private String interval;

    private String delay;

    private String srcdir;

    private List<String> destdirs;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay;
    }

    public String getSrcdir() {
        return srcdir;
    }

    public void setSrcdir(String srcdir) {
        this.srcdir = srcdir;
    }

    public List<String> getDestdirs() {
        return destdirs;
    }

    public void setDestdirs(List<String> destdirs) {
        this.destdirs = destdirs;
    }

    public void addDestdir(String destdir){
        if (destdirs == null){
            destdirs = new ArrayList<>();
        }
        destdirs.add(destdir);
    }

    @Override
    public String toString() {
        return "ZipFile{" +
                "type='" + type + '\'' +
                ", interval='" + interval + '\'' +
                ", delay='" + delay + '\'' +
                ", srcdir='" + srcdir + '\'' +
                ", destdirs=" + destdirs +
                '}';
    }
}
