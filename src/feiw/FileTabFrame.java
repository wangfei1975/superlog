package feiw;

import org.eclipse.swt.custom.CTabFolder;

import feiw.LogSource.LogFilter;

public class FileTabFrame extends SlogTabFrame {

    public FileTabFrame(CTabFolder parent, String txt, int style, String fname) {
        super(parent, txt, style, new FileLogSource(fname), null);
        setImage(Resources.file_16);
    }

}
