package zip;

import java.util.ArrayList;
import java.util.List;

public class ZipFiles {
    private List<ZipFile> zipFileList;

    public List<ZipFile> getZipFileList() {
        return zipFileList;
    }

    public void setZipFileList(List<ZipFile> zipFileList) {
        this.zipFileList = zipFileList;
    }

    public void addZipFile(ZipFile zipFile) {
        if (this.zipFileList == null){
            zipFileList = new ArrayList<>();
        }
        zipFileList.add(zipFile);
    }

    @Override
    public String toString() {
        return "ZipFiles{" +
                "zipFileList=" + zipFileList +
                '}';
    }
}
