/**
 * 
 */
package net.sf.rudetools.swt.tools;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTypeInf {

    private static final Logger LOG = LoggerFactory.getLogger(FileTypeInf.class);

    private String infType;
    private List<String> typeList = new ArrayList<String>();
    private List<Long> numAll = new ArrayList<Long>();
    private List<Long> numDone = new ArrayList<Long>();

    public FileTypeInf(String infType) {
        this.infType = infType;
    }

    public synchronized void addAll(String fileType, Long newAll) {
        int index = typeList.indexOf(fileType);
        if (index >= 0) {
            long oldAll = numAll.get(index);
            numAll.set(index, oldAll + newAll);
        } else {
            typeList.add(fileType);
            numAll.add(newAll);
            numDone.add(0L);
        }
    }

    public synchronized void addDone(String fileType, Long newDone) {
        int index = typeList.indexOf(fileType);
        if (index >= 0) {
            long oldDone = numDone.get(index);
            numDone.set(index, oldDone + newDone);
        } else {
            LOG.error("XXXXXXXXXXXXXXX new done type:\t{} with done:{}", fileType, newDone);
        }
    }

    public synchronized List<String> getFileTypeList() {
        return typeList;
    }

    public String getInfType() {
        return infType;
    }

    public synchronized String getText(String fileType) {
        int index = typeList.indexOf(fileType);
        if (index >= 0) {
            return format(numDone.get(index)) + " / " + format(numAll.get(index));
        }
        return " - ";
    }

    public synchronized String getText(int columnIndex) {
        String text = " - ";
        if (columnIndex == 0) {
            text = infType;
        } else if (columnIndex <= numAll.size()) {
            text = format(numDone.get(columnIndex - 1)) + " / " + format(numAll.get(columnIndex - 1));
        }
        return text;
    }

    public synchronized void clear() {
        typeList.clear();
        numAll.clear();
        numDone.clear();
    }

    protected String format(long num) {
        return String.format("%,9l", num);
    }
}
