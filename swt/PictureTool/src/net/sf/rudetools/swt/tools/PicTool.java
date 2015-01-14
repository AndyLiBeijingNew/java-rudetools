package net.sf.rudetools.swt.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class PicTool extends ApplicationWindow {

    private static final Logger LOG = LoggerFactory.getLogger(PicTool.class);

    private Text textSrcDir;
    private String sourceDirStr;

    private Text textTgtDir;
    private String targetDirStr;

    private Text textLog;
    private ProgressBar progressBar;

    protected Thread workerThread;
    protected boolean isWorking = false;
    protected long sizeSum = 0;

    /*
     * (non-Javadoc)
     * @see org.eclipse.jface.window.ApplicationWindow#close()
     */
    @Override
    public boolean close() {
        if (MessageDialog.openConfirm(PicTool.this.getShell(), "Exit Confirm", "Are you sure to Exit?")) {
            super.close();
        }
        return false;
    }

    /**
     * @return the isWorking
     */
    protected synchronized boolean isWorking() {
        return isWorking;
    }

    /**
     * @param isWorking
     *            the isWorking to set
     */
    protected synchronized void setWorking(boolean isWorking) {
        this.isWorking = isWorking;
    }

    protected Thread refreshThread;
    protected int refreshInterval = 3;
    protected boolean isRefreshing = true;

    protected synchronized int getRefreshInterval() {
        return 3;
    }

    protected synchronized void setRefreshInterval(int refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    protected synchronized boolean isRefreshing() {
        return isRefreshing;
    }

    protected synchronized void setRefreshing(boolean isRefreshing) {
        this.isRefreshing = isRefreshing;
    }

    protected List<FileTypeInf> listData = new ArrayList<>();
    protected FileTypeInf fileCount = new FileTypeInf(" File ");
    protected FileTypeInf sizeCount = new FileTypeInf(" Size ");

    private Map<String, Integer> fileExtCount;;

    private TableViewer viewer;

    /**
     * Create the application window.
     */
    public PicTool() {
        super(null);
        setShellStyle(SWT.CLOSE | SWT.RESIZE);
        // createActions();
        // addToolBar(SWT.FLAT | SWT.WRAP);
        // addMenuBar();
        addStatusLine();
    }

    /**
     * Create contents of the application window.
     * 
     * @param parent
     */
    @Override
    protected Control createContents(Composite parent) {
        // parent.setLayoutData(new GridData(S WT.FILL, SWT.FILL, true, true));
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(new GridLayout(1, false));

        // the folder part with source row and target row
        Group folderGroup = new Group(composite, SWT.NONE);
        folderGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
        folderGroup.setLayout(new GridLayout(3, false));

        createSourceRow(folderGroup);
        createTargetRow(folderGroup);

        // action
        Group actionGroup = new Group(composite, SWT.NONE);
        actionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        actionGroup.setText("Action");
        actionGroup.setLayout(new GridLayout(2, false));
        createActionGroup(actionGroup);

        Composite tblComposite = new Composite(composite, SWT.NONE);
        tblComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        tblComposite.setLayout(new FillLayout());

        viewer = new TableViewer(tblComposite, SWT.FULL_SELECTION);
        FileTypeTableProvider tableProvider = new FileTypeTableProvider();
        viewer.setContentProvider(tableProvider);
        viewer.setLabelProvider(tableProvider);
        listData = new ArrayList<FileTypeInf>();
        fileCount = new FileTypeInf(" File ");
        sizeCount = new FileTypeInf(" Size ");
        listData.add(fileCount);
        listData.add(sizeCount);

        viewer.setInput(listData);
        Table table = viewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setAlignment(SWT.CENTER);
        column.setText("Type");
        column.setWidth(80);

        // status
        // Group statusGroup = new Group(composite, SWT.NONE);
        // statusGroup.setText("Status");
        // statusGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
        // false, 1, 1));
        // statusGroup.setLayout(new GridLayout(4, false));
        // createStatusGroup(statusGroup);

        // progress bar
        Composite progressComposite = new Composite(composite, SWT.NONE);
        progressComposite.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 1, 1));
        progressComposite.setLayout(new GridLayout(1, false));
        createProgressRow(progressComposite);

        // register dnd listener.
        createDndSupportForSrc();
        createDndSupportForTarget();

        return parent;
    }

    private void createActions(Composite parent) {
        final Button btnStart = new Button(parent, SWT.NONE);
        btnStart.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        btnStart.setText("Start");

        btnStart.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (isWorking()) {
                    if (workerThread != null) {
                        // pause logic
                        try {
                            workerThread.wait();
                            btnStart.setText("Continue");
                            setWorking(false);
                            setRefreshing(false);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    if ((sourceDirStr != null && sourceDirStr.length() > 0)
                            && (targetDirStr != null && targetDirStr.length() > 0)) {

                        if (workerThread != null && workerThread.isAlive()) {
                            workerThread.notify();
                            btnStart.setText("pause");
                            setWorking(true);
                            setRefreshing(true);
                        } else {
                            // new action
                            workerThread = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    File file = new File(sourceDirStr);
                                    try {
                                        fileExtCount = new HashMap<String, Integer>();
                                        updateFileInfTable();
                                        handleJpgFiles(file);
                                        LOG.info("Completed Job !!!");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        LOG.info("Broken Job !!!");
                                        LOG.error(e.getMessage());
                                    }

                                    Display.getDefault().syncExec(new Runnable() {

                                        @Override
                                        public void run() {
                                            btnStart.setText("Start");
                                            setWorking(false);
                                            setRefreshing(false);
                                        }
                                    });
                                }
                            });
                            workerThread.setDaemon(true);
                            workerThread.start();
                            btnStart.setText("Pause");
                        }
                    } else {
                        textLog.setText(" P l e a s e   F i l l   i n   N e c e s s a r y   I t e m   F i r s t  !!!!!!");
                    }
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });

        Button btnExit = new Button(parent, SWT.NONE);
        btnExit.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        btnExit.setText("Exit");

        btnExit.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                PicTool.this.close();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    private void createActionGroup(Composite actionGroup) {
        textLog = new Text(actionGroup, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        textLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
        textLog.setTextLimit(8000);

        // mode group
        Group modeGroup = new Group(actionGroup, SWT.NONE);
        modeGroup.setText("Mode");
        modeGroup.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        modeGroup.setLayout(new GridLayout(1, false));
        createModeGroup(modeGroup);

        // action
        ToolBar toolBar = new ToolBar(actionGroup, SWT.VERTICAL);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
        toolBar.setLayout(new GridLayout(1, false));
        createActions(toolBar);
    }

    private void createModeGroup(Composite modeGroup) {
        Button copyMode = new Button(modeGroup, SWT.RADIO);
        copyMode.setText("Copy");
        copyMode.setSelection(true);

        Button moveMode = new Button(modeGroup, SWT.RADIO);
        moveMode.setText("Move");
        moveMode.setSelection(false);
    }

    private void createProgressRow(Composite parent) {
        progressBar = new ProgressBar(parent, SWT.NONE);
        progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        progressBar.setMinimum(0);
        progressBar.setMinimum(100);
    }

    private void createSourceRow(final Composite parent) {
        Label lblSrcDir = new Label(parent, SWT.NONE);
        lblSrcDir.setText("Source");
        lblSrcDir.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        textSrcDir = new Text(parent, SWT.BORDER);
        textSrcDir.setEditable(false);
        textSrcDir.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Button btnSrcDir = new Button(parent, SWT.NONE);
        btnSrcDir.setText(" ... ");
        btnSrcDir.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        btnSrcDir.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dirDialog = new DirectoryDialog(parent.getShell());
                dirDialog.setText("Select your Picture Source directory");
                String selectedDir = dirDialog.open();
                if (selectedDir != null) {
                    textSrcDir.setText(selectedDir);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    private void createTargetRow(final Composite parent) {
        Label lblTargetDir = new Label(parent, SWT.NONE);
        lblTargetDir.setText("Target");
        lblTargetDir.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        textTgtDir = new Text(parent, SWT.BORDER);
        textTgtDir.setEditable(false);
        textTgtDir.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Button btnTgtDir = new Button(parent, SWT.NONE);
        btnTgtDir.setText(" ... ");
        btnTgtDir.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        btnTgtDir.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dirDialog = new DirectoryDialog(parent.getShell());
                dirDialog.setText("Select your Picture Target directory");
                String selectedDir = dirDialog.open();
                if (selectedDir != null) {
                    targetDirStr = selectedDir;
                    textTgtDir.setText(targetDirStr);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    private void createDndSupportForSrc() {

        int operations = DND.DROP_DEFAULT | DND.DROP_COPY;

        DropTarget dropTarget = new DropTarget(textSrcDir, operations);
        dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetListener() {

            @Override
            public void dropAccept(DropTargetEvent event) {

            }

            @Override
            public void drop(DropTargetEvent event) {

                if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    String[] files = (String[]) event.data;
                    if (files != null && files.length > 0) {
                        // Only handle the first folder.
                        sourceDirStr = files[0];
                        textSrcDir.setText(sourceDirStr);

                        File sourceDir = new File(sourceDirStr);

                        initFileInfTable(sourceDir);

                        if (sourceDir.isFile()) {
                            try {
                                Date date = getEXIFDate(sourceDir);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            textLog.append(" S i n g l e   F i l e   !!!!!!");
                        }
                    }
                }
                event.detail = DND.DROP_NONE;
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                event.detail = DND.DROP_COPY;
            }

            @Override
            public void dragOperationChanged(DropTargetEvent event) {

            }

            @Override
            public void dragLeave(DropTargetEvent event) {
            }

            @Override
            public void dragEnter(DropTargetEvent event) {
            }
        });
    }

    protected void updateFileInfTable() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (isRefreshing()) {
                    try {
                        Thread.sleep(getRefreshInterval() * 1000);
                        Display.getDefault().asyncExec(new Runnable() {

                            @Override
                            public void run() {
                                viewer.refresh();
                                progressBar.setSelection(sizeCount.getPercent());
                            }
                        });
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    protected void initFileInfTable(final File sourceDir) {
        new Thread(new Runnable() {

            @Override
            public void run() {

                fileCount.clear();
                sizeCount.clear();
                getFolderInf(sourceDir);

                // update table
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {

                        Table table = viewer.getTable();
                        for (TableColumn column : table.getColumns()) {
                            if (!"Type".equals(column.getText())) {
                                column.dispose();
                            }
                        }
                        table.removeAll();

                        List<String> fileTypeList = fileCount.getFileTypeList();

                        for (String fileType : fileTypeList) {
                            TableColumn column = new TableColumn(table, SWT.NONE);
                            column.setAlignment(SWT.CENTER);
                            column.setText(fileType);
                            column.setWidth(150);
                        }
                        viewer.setInput(listData);
                        viewer.refresh();
                    }
                });
            }
        }).start();
    }

    private void createDndSupportForTarget() {

        int operations = DND.DROP_DEFAULT | DND.DROP_COPY;

        DropTarget dropTarget = new DropTarget(textTgtDir, operations);
        dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetListener() {

            @Override
            public void dropAccept(DropTargetEvent event) {

            }

            @Override
            public void drop(DropTargetEvent event) {
                if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    String[] fileNames = (String[]) event.data;
                    if (fileNames != null && fileNames.length > 0) {
                        String filename = fileNames[0];
                        File file = new File(filename);
                        if (file != null && file.isDirectory()) {
                            File targetFile = new File(file.getAbsolutePath() + File.separator
                                    + System.currentTimeMillis());
                            if (targetFile.mkdir()) {
                                targetDirStr = targetFile.getAbsolutePath();
                                textTgtDir.setText(targetDirStr);

                                initFileInfDone();
                            }
                        }
                    }
                }
                event.detail = DND.DROP_NONE;
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                event.detail = DND.DROP_COPY;
            }

            @Override
            public void dragOperationChanged(DropTargetEvent event) {

            }

            @Override
            public void dragLeave(DropTargetEvent event) {
            }

            @Override
            public void dragEnter(DropTargetEvent event) {
            }
        });
    }

    protected void initFileInfDone() {
        fileCount.clearDone();
        sizeCount.clearDone();
    }

    protected Date getEXIFDate(File file) throws IOException {
        Date date = null;

        if (file != null && file.exists() && file.isFile()) {
            // would throw out exception
            Metadata metadata;
            try {
                metadata = JpegMetadataReader.readMetadata(file);
                ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
                if (exifSubIFDDirectory != null) {
                    date = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                }
            } catch (JpegProcessingException e) {
                // ignore the jpeg file without JPEG information
            }
        }
        return date;
    }

    /**
     * Create the menu manager.
     * 
     * @return the menu manager
     */
    @Override
    protected MenuManager createMenuManager() {
        MenuManager menuManager = new MenuManager("menu");
        return menuManager;
    }

    /**
     * Create the toolbar manager.
     * 
     * @return the toolbar manager
     */
    @Override
    protected ToolBarManager createToolBarManager(int style) {
        ToolBarManager toolBarManager = new ToolBarManager(style);
        return toolBarManager;
    }

    /**
     * Create the status line manager.
     * 
     * @return the status line manager
     */
    @Override
    protected StatusLineManager createStatusLineManager() {
        StatusLineManager statusLineManager = new StatusLineManager();
        return statusLineManager;
    }

    /**
     * Launch the application.
     * 
     * @param args
     */
    public static void main(String args[]) {
        try {
            PicTool window = new PicTool();
            window.setBlockOnOpen(true);
            window.open();
            Display.getCurrent().dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Configure the shell.
     * 
     * @param newShell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("JPG File Distributor");
    }

    /**
     * Return the initial size of the window.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(600, 480);
    }

    private void printWorkingFile(final File srcFile, final File destDir, final String newFileName) {
        LOG.info("Source File:\t{}", srcFile.getAbsolutePath());
        LOG.info("\n         ->:\t{}\\\\{}", destDir.getAbsolutePath(), newFileName);
        LOG.info("\n size:\t{}", srcFile.length());

        Display.getDefault().asyncExec(new Runnable() {

            @Override
            public void run() {
                textLog.setText("Source File:\t" + srcFile.getAbsolutePath());
                textLog.append("\n         ->:\t" + destDir.getAbsolutePath() + "\\\\" + newFileName);
                textLog.append("\n       size:\t " + String.format("%,d K", (int) (srcFile.length() / 1024)));
            }
        });
    }

    /**
     * Nested handle the files in the directory.
     * 
     * @param file
     * @throws IOException
     */
    private void handleJpgFiles(File file) throws IOException {

        if (file == null || targetDirStr == null || "Thumbs.db".equalsIgnoreCase(file.getName())) {
            return;
        }

        // Deep first loop
        if (file.isDirectory()) {
            LOG.info("\nDir:\t{}\n", file.getAbsolutePath());
            for (File subFile : file.listFiles()) {
                handleJpgFiles(subFile);
            }
        } else

        if (file.isFile()) {

            Date date = getEXIFDate(file);
            File targetFolder;
            String targetFileName;
            if (date != null) {
                // the pictures has own EXIFData
                String dateStr = getDateStr(date);
                String dateTimeStr = getDateTimeStr(date);

                // step 1. create target folder
                targetFolder = new File(targetDirStr + File.separator + dateStr);
                int fileNo = 1;
                synchronized (fileExtCount) {

                    if (targetFolder.exists()) {
                        fileNo = fileExtCount.get(dateStr);
                    } else {
                        // fileNo = 1;
                        targetFolder.mkdir();
                    }
                    fileExtCount.put(dateStr, fileNo + 1);
                }
                
                // step 2. get the new file name
                String extNo = String.format("%03d", fileNo);
                targetFileName = dateTimeStr + "." + extNo + ".jpg";
            } else {
                // handle unknown pictures or mov files
                targetFileName = file.getName();
                String parentName = file.getParentFile().getName();
                targetFolder = new File(targetDirStr, parentName);
                if (!targetFolder.exists()) {
                    targetFolder.mkdirs();
                }
            }

            // step 3. copy the file to the target
            if (copyFile(file, targetFolder, targetFileName)) {
                addDoneFile(file);
            }

        }
    }

    protected boolean copyFile(File srcFile, File destDir, String newFileName) {
        boolean isDone = false;
        printWorkingFile(srcFile, destDir, newFileName);
        try {
            FileInputStream fis = new FileInputStream(srcFile);
            FileChannel fcin = fis.getChannel();
            File newFile = new File(destDir, newFileName);
            int duplicatedNo = 1;
            while (newFile.exists()) {
                String extNo = "Copy (" + duplicatedNo + ")";
                String dupName = newFileName.replace(".jpg", extNo + ".jpg");
                duplicatedNo++;
                newFile = new File(destDir, dupName);
            }

            FileOutputStream fos = new FileOutputStream(newFile);
            FileChannel fcout = fos.getChannel();
            fcin.transferTo(0, fcin.size(), fcout);
            fcin.close();
            fis.close();
            fcout.close();
            fos.close();
            isDone = true;
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error("Copy file Error:\t{}", e.getMessage());
        }
        return isDone;
    }

    protected void getFolderInf(File file) {
        if (file == null) {
            return;
        }

        if (file.isFile()) {
            // do the real things
            addCountFile(file);
        } else if (file.isDirectory()) {
            for (File subFile : file.listFiles()) {
                getFolderInf(subFile);
            }
        }
    }

    protected synchronized void addDoneFile(File file) {
        String ext = getFileExtName(file);
        if (ext != null && ext.length() > 0) {
            fileCount.addDone(ext, 1L);
            sizeCount.addDone(ext, file.length());
        }
    }

    protected synchronized void addCountFile(File file) {
        String ext = getFileExtName(file);
        if (ext != null) {
            fileCount.addAll(ext, 1L);
            sizeCount.addAll(ext, file.length());
        }
    }

    protected String getFileExtName(File file) {
        if (file != null && file.exists() && file.isFile()) {
            String fileName = file.getName();
            int index = fileName.lastIndexOf(".");
            if (index >= 0) {
                return fileName.substring(index + 1).toLowerCase();
            }
        }

        return "";
    }

    public static String getDateTimeStr(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh.mm.ss");
        return df.format(date);
    }

    public static String getDateStr(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }
}
