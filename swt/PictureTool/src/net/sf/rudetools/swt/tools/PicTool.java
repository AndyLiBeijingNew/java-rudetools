package net.sf.rudetools.swt.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.action.ToolBarManager;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class PicTool extends ApplicationWindow {

    private static final Logger LOG = LoggerFactory.getLogger(PicTool.class);

    private Text txtSrcDir;
    private Text txtLog;
    private ProgressBar progressBar;
    private Text txtTgtDir;

    protected Map<String, Integer> fileCount = new HashMap<String, Integer>();
    private Label lblFileDone;
    private Label lblFileCount;
    private Label lblSizeDone;
    private Label lblSizeCount;
    private String[] sourceFiles;
    private String targetDirStr;
    protected Thread picThread;

    /**
     * Create the application window.
     */
    public PicTool() {
        super(null);
        createActions();
        addToolBar(SWT.FLAT | SWT.WRAP);
        addMenuBar();
        addStatusLine();
    }

    /**
     * Create contents of the application window.
     * 
     * @param parent
     */
    @Override
    protected Control createContents(final Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));

        Composite composite = new Composite(container, SWT.NONE);
        composite.setLayout(new GridLayout(3, false));
        createSourceRow(composite);
        createTargetRow(composite);

        txtLog = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        txtLog.setTextLimit(8000);
        txtLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));

        createProgressRow(composite);

        final Button btnStart = new Button(composite, SWT.NONE);
        btnStart.setText("Start");
        btnStart.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 3, 1));
        btnStart.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (sourceFiles != null && sourceFiles.length > 0) {

                    picThread = new Thread(new Runnable() {

                        @Override
                        public void run() {
                            for (String filename : sourceFiles) {
                                File file = new File(filename);
                                handleJpgFiles(file);
                            }
                            LOG.info("Completed Job !!!");

                            Display.getDefault().syncExec(new Runnable() {

                                @Override
                                public void run() {
                                    btnStart.setEnabled(true);
                                }
                            });
                        }
                    });
                    picThread.setDaemon(true);
                    picThread.start();
                    btnStart.setEnabled(false);
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });

        // register dnd listener.
        createDndSupportForSrc();
        createDndSupportForTarget();

        return container;
    }

    private void createProgressRow(Composite parent) {
        Composite progressRow = new Composite(parent, SWT.NONE);
        progressRow.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 4, 1));
        progressRow.setLayout(new GridLayout(4, true));

        Label lblFile = new Label(progressRow, SWT.NONE);
        lblFile.setText("File:");
        lblFile.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        lblFileDone = new Label(progressRow, SWT.NONE);
        lblFileDone.setText("0");
        lblFileDone.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));

        Label lbldash = new Label(progressRow, SWT.NONE);
        lbldash.setText(" / ");
        lbldash.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));

        lblFileCount = new Label(progressRow, SWT.NONE);
        lblFileCount.setText("0");
        lblFileCount.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        Label lblSize = new Label(progressRow, SWT.NONE);
        lblSize.setText("Size:");
        lblSize.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        lblSizeDone = new Label(progressRow, SWT.NONE);
        lblSizeDone.setText("0");
        lblSizeDone.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));

        Label lbldash2 = new Label(progressRow, SWT.NONE);
        lbldash2.setText(" / ");
        lbldash2.setLayoutData(new GridData(SWT.CENTER, SWT.BOTTOM, false, false, 1, 1));

        lblSizeCount = new Label(progressRow, SWT.NONE);
        lblSizeCount.setText("0");
        lblSizeCount.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 1, 1));

        progressBar = new ProgressBar(progressRow, SWT.NONE);
        progressBar.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false, 4, 1));
    }

    private void createSourceRow(final Composite parent) {
        Label lblSrcDir = new Label(parent, SWT.NONE);
        lblSrcDir.setText("Source:");
        lblSrcDir.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        txtSrcDir = new Text(parent, SWT.BORDER);
        txtSrcDir.setEditable(false);
        txtSrcDir.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Button btnSrcDir = new Button(parent, SWT.NONE);
        btnSrcDir.setText("Select");
        btnSrcDir.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        btnSrcDir.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dirDialog = new DirectoryDialog(parent.getShell());
                dirDialog.setText("Select your Picture Source directory");
                String selectedDir = dirDialog.open();
                if (selectedDir != null) {
                    txtSrcDir.setText(selectedDir);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    private void createTargetRow(final Composite parent) {
        Label lblTargetDir = new Label(parent, SWT.NONE);
        lblTargetDir.setText("Target:");
        lblTargetDir.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

        txtTgtDir = new Text(parent, SWT.BORDER);
        txtTgtDir.setEditable(false);
        txtTgtDir.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

        Button btnTgtDir = new Button(parent, SWT.NONE);
        btnTgtDir.setText("Select");
        btnTgtDir.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
        btnTgtDir.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog dirDialog = new DirectoryDialog(parent.getShell());
                dirDialog.setText("Select your Picture Target directory");
                String selectedDir = dirDialog.open();
                if (selectedDir != null) {
                    targetDirStr = selectedDir;
                    txtTgtDir.setText(targetDirStr);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    /**
     * Create the actions.
     */
    private void createActions() {

    }

    private void createDndSupportForSrc() {

        int operations = DND.DROP_DEFAULT | DND.DROP_COPY;

        DropTarget dropTarget = new DropTarget(txtSrcDir, operations);
        dropTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetListener() {

            @Override
            public void dropAccept(DropTargetEvent event) {

            }

            @Override
            public void drop(DropTargetEvent event) {

                if (txtTgtDir.getText().length() > 0) {
                    if (FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
                        String[] files = (String[]) event.data;
                        if (files != null && files.length > 0) {
                            txtLog.setText("");
                            if (files.length == 1) {
                                txtSrcDir.setText(files[0]);
                            } else {
                                txtSrcDir.setText(files[0] + " ... etc " + files.length + " folders");
                            }

                            long sum = 0;
                            for (String filename : files) {
                                File file = new File(filename);
                                sum += file.getTotalSpace();
                            }
                            lblFileCount.setText(String.format("%,9d", sum));
                            lblFileCount.redraw();
                            sourceFiles = files;

                            File singleFile = new File(files[0]);
                            if (singleFile.isFile()) {
                                Date date = getEXIFDate(singleFile);
                                printJpgFile(singleFile, date);
                                txtLog.setText(" S i n g l e   F i l e   !!!!!!");
                            }
                        }
                    }
                } else {
                    txtLog.setText(" P l e a s e   F i l l   i n   T a r g e t   F i r s t  !!!!!!");
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

    private void createDndSupportForTarget() {

        int operations = DND.DROP_DEFAULT | DND.DROP_COPY;

        DropTarget dropTarget = new DropTarget(txtTgtDir, operations);
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
                                txtTgtDir.setText(targetDirStr);
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

    protected Date getEXIFDate(final File jpegFile) {
        Date date = null;

        if (jpegFile.exists() && jpegFile.isFile()) {
            Metadata metadata;
            try {
                metadata = JpegMetadataReader.readMetadata(jpegFile);
            } catch (final Exception e) {
                Display.getDefault().asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        txtLog.setText("File:\t" + jpegFile.getAbsolutePath());
                        txtLog.append("\nException:\t" + e.getMessage());
                        txtLog.append("\n\n");
                    }
                });

                return null;
            }
            ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
            if (exifSubIFDDirectory != null) {
                date = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
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
        return new Point(600, 400);
    }

    private void printJpgFile(final File file, final Date date) {
        LOG.info("\tFile:\t{}", file.getAbsolutePath());
        LOG.info("\t\tEXIFDate:\t{}\n", date);
        if (date != null && file.isFile()) {
            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    txtLog.setText(">>>>>>>> File:\t" + file);
                    txtLog.append("\nDir  Name:\t" + getDateStr(date));
                    txtLog.append("\nFile Name:\t" + getDateTimeStr(date));
                    txtLog.append("\n\n");
                }
            });
        }
    }

    /**
     * Nested handle the files in the directory.
     * 
     * @param file
     */
    private void handleJpgFiles(File file) {

        if (file == null || targetDirStr == null || "Thumbs.db".equalsIgnoreCase(file.getName())) {
            return;
        }

        // Deep first loop
        if (file.isDirectory()) {
            LOG.info("\nDir:\t{}\n", file.getAbsolutePath());
            for (File subFile : file.listFiles()) {
                handleJpgFiles(subFile);
            }
        }

        if (file.isFile()) {

            Date date = getEXIFDate(file);
            printJpgFile(file, date);

            File targetFolder;
            String targetFileName;
            if (date != null) {
                // the pictures has own EXIFData
                String dateStr = getDateStr(date);
                String dateTimeStr = getDateTimeStr(date);

                // step 1. create target folder
                targetFolder = new File(targetDirStr + File.separator + dateStr);
                int fileNo = 1;
                if (targetFolder.exists()) {
                    fileNo = fileCount.get(dateStr);
                } else {
                    // fileNo = 1;
                    targetFolder.mkdir();
                }
                fileCount.put(dateStr, fileNo + 1);

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
            copyFile(file, targetFolder, targetFileName);
        }
    }

    protected long copyFile(File srcFile, File destDir, String newFileName) {
        long copySize = 0;
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
            long size = fcin.size();
            fcin.transferTo(0, fcin.size(), fcout);
            fcin.close();
            fis.close();
            fcout.close();
            fos.close();
            copySize = size;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copySize;
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
