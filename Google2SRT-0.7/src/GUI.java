/*
    This file is part of Google2SRT.

    Google2SRT is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    Google2SRT is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Google2SRT.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 * @author kom
 * @version "0.7, 10/27/14"
 */


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.UIManager;
import javax.swing.table.TableRowSorter;



public class GUI extends javax.swing.JFrame {
    TableModel tablemodelTracks, tablemodelTargets; // graphical subtitles containers
    
    java.util.ResourceBundle bundle = GUI.getBundle();
    
    private final JFileChooser fc1 = new JFileChooser(),
                               fc2 = new JFileChooser();
     
    private boolean msgInfileInvalidFormat, msgIOException; // Errors generated deep inside
    
    private static Settings appSettings;    // Application settings
    private static Controller _controller;  // Application controller
    
    private boolean _selectAllTracks = false; // Select All if "true". Clear selection if "false"
    private boolean _selectAllTargets = false; // Select All if "true". Clear selection if "false"
    
    
    /** Creates new form GUI */
    public GUI() {
        initComponents();

        // Icon setup
        setIconImage((new javax.swing.ImageIcon(getClass().getResource("/logo.png"))).getImage());
       
        // Load settings
        appSettings = new Settings(bundle);
        appSettings.loadSettings();
        
        _controller = new Controller(this, appSettings);

        setLanguage(appSettings.getLocaleLanguage());
        jtfInput.setText(appSettings.getURLInput());
        jtfOutput.setText(appSettings.getOutput());
        jcbTitle.setSelected(appSettings.getIncludeTitleInFilename());
        jcbTrackName.setSelected(appSettings.getIncludeTrackNameInFilename());
        
        fc1.setAcceptAllFileFilterUsed(false);
        fc1.addChoosableFileFilter(new SupportedFileTypesFilter());
        fc1.addChoosableFileFilter(new XMLFilter());
        fc1.addChoosableFileFilter(new TXTFilter());
        fc1.setCurrentDirectory(new java.io.File(appSettings.getFileInput()));
        
        fc2.setCurrentDirectory(new java.io.File(appSettings.getOutput()));
        fc2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        tmSubtitlesLists_init();
        
        
        jtTrackList.setAutoCreateRowSorter(true);        
        jtTargetList.setAutoCreateRowSorter(true);
        
        // *** TRANSLATION PENDING ***
        this.jbutSetLangDe.setVisible(false);
        this.jbutSetLangFr.setVisible(false);
        this.jbutSetLangPl.setVisible(false);
        this.jbutSetLangRu.setVisible(false);
        
        // Window is centered
        pack();
        setLocationRelativeTo(null);
    }
    
    public static void main(String args[]) {
        
        // Set System Look&Feel
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception ex) { }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               new GUI().setVisible(true);
            }
        });
        
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                // Save settings before exiting
                appSettings.saveSettings();
            }
         }));
    }
    
    protected void appErrorBundleMessage(String bundleString) {
        String msg;
        
        msg = bundle.getString(bundleString);
        javax.swing.JOptionPane.showMessageDialog(null, msg);
    }
    
    protected boolean appManySelectedWarning(int numSelected) {
        String msg;
        
        msg = MessageFormat.format(bundle.getString("msg.conversion.many.subtitles"), numSelected);
        return JOptionPane.showConfirmDialog(null, msg, "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }
    
    protected void appLogsBundleMessage(String bundleString, String detail) {
        String msg;
        
        msg = bundle.getString(bundleString);
        this.jtaLogs.append(detail + ": " + msg + "\n");
    }
    
    protected void appLogsClear() {
        this.jtaLogs.setText("");
    }
    
    protected void appStatusBundleMessage(String bundleString) {
        String msg;
        
        msg = bundle.getString(bundleString);
        this.jlStatus.setText(msg);
    }
    
    protected void appStatusClear() {
        this.jlStatus.setText("");
    }
    
    protected void appStatusConverting(int progress, int total) {
        String msg;
        
        msg = bundle.getString("msg.status.converting") + " " + progress + "/" + total;
        this.jlStatus.setText(msg);
    }
    
    protected void appStatusConnecting(int progress, int total) {
        String msg;
        
        msg = bundle.getString("msg.status.connecting") + " " + progress + "/" + total;
        this.jlStatus.setText(msg);
    }
   
    public void setMsgInfileInvalidFormat() {
        this.msgInfileInvalidFormat = true;
    }
    
    public void setMsgIOException() {
        this.msgIOException = true;
    }
    
    public void prepareNewConversion() {
        this.enableControls(true);
        this.jlStatus.setText("");
        
        if (msgIOException) appErrorBundleMessage("msg.io.error");
        if (msgInfileInvalidFormat) appErrorBundleMessage("msg.infile.invalid.format");
        
        msgIOException = false;
        msgInfileInvalidFormat = false;
        
    }
    
    public void enableControls(boolean enable)
    {
        this.jbutConvert.setEnabled(enable);
        this.jbutInput.setEnabled(enable);
        this.jbutOutput.setEnabled(enable);
        this.jrbURL.setEnabled(enable);
        this.jrbURLListXML.setEnabled(enable);
        this.jcbAll.setEnabled(enable);
        this.jcbInvert.setEnabled(enable);
        this.jcbTitle.setEnabled(enable);
        this.jcbTrackName.setEnabled(enable);
        this.jtfInput.setEnabled(enable);
        this.jtfOutput.setEnabled(enable);
        this.jspinnerDelay.setEnabled(enable);
        this.jtTrackList.setEnabled(enable);
        this.jtTrackList.getTableHeader().setEnabled(enable);
        this.jtTargetList.setEnabled(enable);
        this.jtTargetList.getTableHeader().setEnabled(enable);
    }

    public static java.util.ResourceBundle getBundle() {
        java.util.ResourceBundle b;
        try {
            b = java.util.ResourceBundle.getBundle("Bundle");
        } catch (MissingResourceException e) {
            b = java.util.ResourceBundle.getBundle("Bundle", new java.util.Locale("en"));
        }
        return b;
    }
    
    protected Double getDelay() {
        return ((Double)jspinnerDelay.getValue()).doubleValue();
    }
    
    protected Object[][] getTableModelTracksData() {
        return this.tablemodelTracks.getData();
    }
    
    protected Object[][] getTableModelTargetsData() {
        return this.tablemodelTargets.getData();
    }
    
    private void setLanguage (String s) {  
        
        if (s == null) {
            bundle = java.util.ResourceBundle.getBundle("Bundle");
            appSettings.setLocaleLanguage("en");
        } else
            try {
                bundle = java.util.ResourceBundle.getBundle("Bundle_" + s);
                appSettings.setLocaleLanguage(s);
            }
            catch (Exception e) {
                bundle = java.util.ResourceBundle.getBundle("Bundle");
                appSettings.setLocaleLanguage("en");
            }

        if (this.jrbURL.isSelected()) {
            this.jbutInput.setText(bundle.getString("GUI.jbutInput.text"));
            this.jbutInput.setToolTipText(bundle.getString("GUI.jbutInput.toolTipText"));
        } else {
            this.jbutInput.setText(bundle.getString("GUI.jbutInput.xmlfile.text"));
            this.jbutInput.setToolTipText(bundle.getString("GUI.jbutInput.xmlfile.toolTipText"));
        }

        this.jlSubIn.setText(bundle.getString("GUI.jlSubIn.text"));
        this.jlSubOut.setText(bundle.getString("GUI.jlSubOut.text"));
        this.jlDelay.setText(bundle.getString("GUI.jlDelay.text"));
        this.jbutOutput.setText(bundle.getString("GUI.jbutOutput.text"));
        this.jbutOutput.setToolTipText(bundle.getString("GUI.jbutOutput.toolTipText"));
        this.jbutConvert.setText(bundle.getString("GUI.jbutConvert.text"));
        this.jbutConvert.setToolTipText(bundle.getString("GUI.jbutConvert.toolTipText"));
        this.jtfInput.setToolTipText(bundle.getString("GUI.jtfInput.toolTipText"));
        this.jtfOutput.setToolTipText(bundle.getString("GUI.jtfOutput.toolTipText"));
        this.jrbURLListXML.setText(bundle.getString("GUI.jrbURLListXML.text"));
        this.jrbURLListXML.setToolTipText(bundle.getString("GUI.jrbURLListXML.toolTipText"));
        this.jrbURL.setText(bundle.getString("GUI.jrbURL.text"));
        this.jrbURL.setToolTipText(bundle.getString("GUI.jrbURL.toolTipText"));
        this.jspinnerDelay.setToolTipText(bundle.getString("GUI.jspinnerDelay.toolTipText"));
        this.jcbInvert.setText(bundle.getString("GUI.jcbInvert.text"));
        this.jcbInvert.setToolTipText(bundle.getString("GUI.jcbInvert.toolTipText"));
        this.jcbAll.setText(bundle.getString("GUI.jcbAll.text"));
        this.jcbAll.setToolTipText(bundle.getString("GUI.jcbAll.toolTipText"));
        this.jcbTitle.setText(bundle.getString("GUI.jcbTitle.text"));
        this.jcbTitle.setToolTipText(bundle.getString("GUI.jcbTitle.toolTipText"));
        this.jcbTrackName.setText(bundle.getString("GUI.jcbTrackName.text"));
        this.jcbTrackName.setToolTipText(bundle.getString("GUI.jcbTrackName.toolTipText"));
        this.jmiCut.setText(bundle.getString("GUI.jmiCut.text"));
        this.jmiCopy.setText(bundle.getString("GUI.jmiCopy.text"));
        this.jmiPaste.setText(bundle.getString("GUI.jmiPaste.text"));
        this.jTabbedPane.setTitleAt(0, bundle.getString("GUI.jspTracks.TabConstraints.tabTitle"));
        this.jTabbedPane.setTitleAt(1, bundle.getString("GUI.jspTargets.TabConstraints.tabTitle"));

        if (tablemodelTracks != null) tablemodelTracks.setBundle(bundle, jtTrackList);
        if (tablemodelTargets != null) tablemodelTargets.setBundle(bundle, jtTargetList);
        
        tmSubtitlesLists_width();
    }
    
    private void tmSubtitlesLists_init() {
        this.tablemodelTracks = new TableModel(bundle, true);
        this.jtTrackList.setModel(tablemodelTracks);

        this.tablemodelTargets = new TableModel(bundle, false);
        this.jtTargetList.setModel(tablemodelTargets);
        tmSubtitlesLists_width();
    }
    
    protected void tmSubtitlesLists_populate() {
        TableRowSorter<TableModel> jtTrackListSorter;
        List <RowSorter.SortKey> sortKeys;
        
        jtTrackListSorter = new TableRowSorter<TableModel>(tablemodelTracks);
        jtTrackList.setRowSorter(jtTrackListSorter);
        sortKeys  = new ArrayList<RowSorter.SortKey>();
        sortKeys.add(new RowSorter.SortKey(5, SortOrder.ASCENDING)); // Title
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING)); // Language code
        jtTrackListSorter.setSortKeys(sortKeys);
        
        jtTargetList.setRowSorter(new TableRowSorter<TableModel>(tablemodelTargets));
        
        jtTargetList.getRowSorter().toggleSortOrder(1);
        
        _selectAllTracks = false;
        _selectAllTargets = false;

        if (!_controller.islSubsWithTranslationsNull()) {
            tablemodelTracks.init(_controller.getTracks());
            tablemodelTargets.init(_controller.getTargets());
        }
        
        tmSubtitlesLists_width();
    }
    
    protected void tmSubtitlesLists_clear() {
        if (tablemodelTracks != null) { tablemodelTracks.clear(); }
        if (tablemodelTargets != null) { tablemodelTargets.clear(); }
    }
    
    private void tmSubtitlesLists_width() {
        javax.swing.table.TableColumnModel tcm;
        
        tcm = jtTrackList.getColumnModel();
        tcm.getColumn(0).setMaxWidth(80); tcm.getColumn(0).setPreferredWidth(80);       // Convert
        tcm.getColumn(1).setMaxWidth(120); tcm.getColumn(1).setPreferredWidth(120);     // Language code
        tcm.getColumn(2).setMaxWidth(125); tcm.getColumn(2).setPreferredWidth(125);     // Language name
        tcm.getColumn(3).setMaxWidth(150); tcm.getColumn(3).setPreferredWidth(115);     // Track name (reasonable max)
        tcm.getColumn(4).setMaxWidth(110); tcm.getColumn(4).setPreferredWidth(110);     // Video
                                                                                        // Title (max available)
        
        tcm = jtTargetList.getColumnModel();
        tcm.getColumn(0).setMaxWidth(80); tcm.getColumn(0).setPreferredWidth(80);       // Convert
        tcm.getColumn(1).setMaxWidth(120); tcm.getColumn(1).setPreferredWidth(120);     // Language code
                                                                                        // Language name (max avaialble)
    }
    
    // Count how many tracks are selected
    protected int tmSubtitlesLists_getNumberSelectedTracks() {
        Object[][] dataTracks;
        int i, selectedCountTotalTracks;
        
        dataTracks = getTableModelTracksData();
        
        for (i = 0, selectedCountTotalTracks = 0; i < dataTracks.length; i++)
            if (((Boolean) dataTracks[i][0]).booleanValue())
                selectedCountTotalTracks++;
        
        return selectedCountTotalTracks;
    }
    
    // Count how many targets are selected
    protected int tmSubtitlesLists_getNumberSelectedTargets() {
        Object[][] dataTargets;
        int i, selectedCountTotalTargets;
        
        dataTargets = getTableModelTargetsData();
        
        for (i = 0, selectedCountTotalTargets = 0; i < dataTargets.length; i++)
            if (((Boolean) dataTargets[i][0]).booleanValue())
                selectedCountTotalTargets++;
        
        return selectedCountTotalTargets;
    }

    protected void loadInputFile(String path) {
        final InputStreamReader final_isr; // workaround to pass "isr" to the thread
        InputStreamReader isr = null;
        
        if ("".equals(path)) {
            appErrorBundleMessage("msg.infile.not.specified");
            prepareNewConversion();
            return;
        }

        // Loading input file
        try {
            isr = new InputStreamReader(new FileInputStream(path), "UTF-8");
        } catch (FileNotFoundException ex) {
            this.setMsgIOException();
            prepareNewConversion();
            return;
        } catch (java.io.UnsupportedEncodingException ex) {
            if (Settings.DEBUG) System.out.println("(DEBUG) encoding not supported");
        }

        _controller.initSubtitlesDataStructure();
        this.tmSubtitlesLists_clear();
        
        // Is it an XML file? => If so we are done
        if (!Converter.isXML(isr)) {

            // Converter.isXML() usage requires reloading the file
            try {
                isr = new InputStreamReader(new FileInputStream(path), "UTF-8");
            } catch (FileNotFoundException ex) {
                this.setMsgIOException();
                prepareNewConversion();
                return;
            } catch (java.io.UnsupportedEncodingException ex) {
                if (Settings.DEBUG) System.out.println("(DEBUG) encoding not supported");
            }

            // Getting list of URL from text file
            this.enableControls(false);

            final_isr = isr; // workaround to pass "isr" to the thread

            new Thread(new Runnable() {
                @Override
                public void run() {
                    _controller.processURLListFile(final_isr);
                }
            }).start();
        }
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        subsSource = new javax.swing.ButtonGroup();
        jpmContextual = new javax.swing.JPopupMenu();
        jmiCut = new javax.swing.JMenuItem();
        jmiCopy = new javax.swing.JMenuItem();
        jmiPaste = new javax.swing.JMenuItem();
        jrbURL = new javax.swing.JRadioButton();
        jrbURLListXML = new javax.swing.JRadioButton();
        jtfInput = new javax.swing.JTextField();
        jtfOutput = new javax.swing.JTextField();
        jbutInput = new javax.swing.JButton();
        jbutOutput = new javax.swing.JButton();
        jbutConvert = new javax.swing.JButton();
        jspinnerDelay = new javax.swing.JSpinner();
        jlSubIn = new javax.swing.JLabel();
        jlSubOut = new javax.swing.JLabel();
        jlDelay = new javax.swing.JLabel();
        jbutSetLangCa = new javax.swing.JButton();
        jbutSetLangDe = new javax.swing.JButton();
        jbutSetLangEn = new javax.swing.JButton();
        jbutSetLangEs = new javax.swing.JButton();
        jbutSetLangFr = new javax.swing.JButton();
        jbutSetLangIt = new javax.swing.JButton();
        jbutSetLangPl = new javax.swing.JButton();
        jbutSetLangPtBr = new javax.swing.JButton();
        jbutSetLangRu = new javax.swing.JButton();
        jbutSetLangZhHanS = new javax.swing.JButton();
        jbutSetLangZhHansT = new javax.swing.JButton();
        jlStatus = new javax.swing.JLabel();
        jcbAll = new javax.swing.JCheckBox();
        jcbInvert = new javax.swing.JCheckBox();
        jcbTrackName = new javax.swing.JCheckBox();
        jcbTitle = new javax.swing.JCheckBox();
        jTabbedPane = new javax.swing.JTabbedPane();
        jspTracks = new javax.swing.JScrollPane();
        jtTrackList = new javax.swing.JTable();
        jspTargets = new javax.swing.JScrollPane();
        jtTargetList = new javax.swing.JTable();
        jspLogs = new javax.swing.JScrollPane();
        jtaLogs = new javax.swing.JTextArea();
        jbutHelp = new javax.swing.JButton();
        jbutWeb = new javax.swing.JButton();
        jbutAbout = new javax.swing.JButton();

        jmiCut.setAction(new javax.swing.text.DefaultEditorKit.CutAction());
        jpmContextual.add(jmiCut);

        jmiCopy.setAction(new javax.swing.text.DefaultEditorKit.CopyAction());
        jpmContextual.add(jmiCopy);

        jmiPaste.setAction(new javax.swing.text.DefaultEditorKit.PasteAction());
        jpmContextual.add(jmiPaste);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(java.util.ResourceBundle.getBundle("Bundle").getString("app.name") + " " + java.util.ResourceBundle.getBundle("Bundle").getString("app.version"));
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setMinimumSize(new java.awt.Dimension(635, 420));

        subsSource.add(jrbURL);
        jrbURL.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("Bundle"); // NOI18N
        jrbURL.setText(bundle.getString("GUI.jrbURL.text")); // NOI18N
        jrbURL.setToolTipText(bundle.getString("GUI.jrbURL.toolTipText")); // NOI18N
        jrbURL.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jrbURLItemStateChanged(evt);
            }
        });

        subsSource.add(jrbURLListXML);
        jrbURLListXML.setText(bundle.getString("GUI.jrbURLListXML.text")); // NOI18N
        jrbURLListXML.setToolTipText(bundle.getString("GUI.jrbURLListXML.toolTipText")); // NOI18N
        jrbURLListXML.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jrbURLListXMLItemStateChanged(evt);
            }
        });

        jtfInput.setText(bundle.getString("GUI.jtfInput.text")); // NOI18N
        jtfInput.setToolTipText(bundle.getString("GUI.jtfInput.toolTipText")); // NOI18N
        jtfInput.setComponentPopupMenu(jpmContextual);
        jtfInput.setName("Input"); // NOI18N
        jtfInput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jtfInputMousePressed(evt);
            }
        });
        jtfInput.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jtfInputFocusGained(evt);
            }
        });

        jtfOutput.setText(bundle.getString("GUI.jtfOutput.text")); // NOI18N
        jtfOutput.setToolTipText(bundle.getString("GUI.jtfOutput.toolTipText")); // NOI18N
        jtfOutput.setComponentPopupMenu(jpmContextual);
        jtfOutput.setName("FSortida"); // NOI18N
        jtfOutput.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jtfOutputMousePressed(evt);
            }
        });

        jbutInput.setText(bundle.getString("GUI.jbutInput.text")); // NOI18N
        jbutInput.setToolTipText(bundle.getString("GUI.jbutInput.toolTipText")); // NOI18N
        jbutInput.setName("bTriaEntrada"); // NOI18N
        jbutInput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutInputActionPerformed(evt);
            }
        });

        jbutOutput.setText(bundle.getString("GUI.jbutOutput.text")); // NOI18N
        jbutOutput.setToolTipText(bundle.getString("GUI.jbutOutput.toolTipText")); // NOI18N
        jbutOutput.setName("bTriaSortida"); // NOI18N
        jbutOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutOutputActionPerformed(evt);
            }
        });

        jbutConvert.setText(bundle.getString("GUI.jbutConvert.text")); // NOI18N
        jbutConvert.setToolTipText(bundle.getString("GUI.jbutConvert.toolTipText")); // NOI18N
        jbutConvert.setName("Convertir"); // NOI18N
        jbutConvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutConvertActionPerformed(evt);
            }
        });

        jspinnerDelay.setModel(new javax.swing.SpinnerNumberModel(Double.valueOf(0.0d), null, null, Double.valueOf(0.1d)));
        jspinnerDelay.setToolTipText(bundle.getString("GUI.jspinnerDelay.toolTipText")); // NOI18N
        jspinnerDelay.setName("Retard"); // NOI18N

        jlSubIn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlSubIn.setLabelFor(jbutInput);
        jlSubIn.setText(bundle.getString("GUI.jlSubIn.text")); // NOI18N
        jlSubIn.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jlSubOut.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jlSubOut.setLabelFor(jbutOutput);
        jlSubOut.setText(bundle.getString("GUI.jlSubOut.text")); // NOI18N
        jlSubOut.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        jlDelay.setText(bundle.getString("GUI.jlDelay.text")); // NOI18N

        jbutSetLangCa.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangCa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ca.jpg"))); // NOI18N
        jbutSetLangCa.setToolTipText(bundle.getString("GUI.jbutSetLangCa.toolTipText")); // NOI18N
        jbutSetLangCa.setBorder(null);
        jbutSetLangCa.setBorderPainted(false);
        jbutSetLangCa.setFocusPainted(false);
        jbutSetLangCa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangCaActionPerformed(evt);
            }
        });

        jbutSetLangDe.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangDe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/de.jpg"))); // NOI18N
        jbutSetLangDe.setToolTipText(bundle.getString("GUI.jbutSetLangDe.toolTipText")); // NOI18N
        jbutSetLangDe.setBorder(null);
        jbutSetLangDe.setBorderPainted(false);
        jbutSetLangDe.setFocusPainted(false);
        jbutSetLangDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangDeActionPerformed(evt);
            }
        });

        jbutSetLangEn.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangEn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/en.jpg"))); // NOI18N
        jbutSetLangEn.setToolTipText(bundle.getString("GUI.jbutSetLangEn.toolTipText")); // NOI18N
        jbutSetLangEn.setBorder(null);
        jbutSetLangEn.setBorderPainted(false);
        jbutSetLangEn.setFocusPainted(false);
        jbutSetLangEn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangEnActionPerformed(evt);
            }
        });

        jbutSetLangEs.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangEs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/es.jpg"))); // NOI18N
        jbutSetLangEs.setToolTipText(bundle.getString("GUI.jbutSetLangEs.toolTipText")); // NOI18N
        jbutSetLangEs.setBorder(null);
        jbutSetLangEs.setBorderPainted(false);
        jbutSetLangEs.setFocusPainted(false);
        jbutSetLangEs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangEsActionPerformed(evt);
            }
        });

        jbutSetLangFr.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangFr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/fr.jpg"))); // NOI18N
        jbutSetLangFr.setToolTipText(bundle.getString("GUI.jbutSetLangFr.toolTipText")); // NOI18N
        jbutSetLangFr.setBorder(null);
        jbutSetLangFr.setBorderPainted(false);
        jbutSetLangFr.setFocusPainted(false);
        jbutSetLangFr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangFrActionPerformed(evt);
            }
        });

        jbutSetLangIt.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangIt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/it.jpg"))); // NOI18N
        jbutSetLangIt.setToolTipText(bundle.getString("GUI.jbutSetLangIt.toolTipText")); // NOI18N
        jbutSetLangIt.setBorder(null);
        jbutSetLangIt.setBorderPainted(false);
        jbutSetLangIt.setFocusPainted(false);
        jbutSetLangIt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangItActionPerformed(evt);
            }
        });

        jbutSetLangPl.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangPl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pl.jpg"))); // NOI18N
        jbutSetLangPl.setToolTipText(bundle.getString("GUI.jbutSetLangPl.toolTipText")); // NOI18N
        jbutSetLangPl.setBorder(null);
        jbutSetLangPl.setBorderPainted(false);
        jbutSetLangPl.setFocusPainted(false);
        jbutSetLangPl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangPlActionPerformed(evt);
            }
        });

        jbutSetLangPtBr.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangPtBr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pt_BR.jpg"))); // NOI18N
        jbutSetLangPtBr.setToolTipText(bundle.getString("GUI.jbutSetLangPtBr.toolTipText")); // NOI18N
        jbutSetLangPtBr.setBorder(null);
        jbutSetLangPtBr.setBorderPainted(false);
        jbutSetLangPtBr.setFocusPainted(false);
        jbutSetLangPtBr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangPtBrActionPerformed(evt);
            }
        });

        jbutSetLangRu.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangRu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ru.jpg"))); // NOI18N
        jbutSetLangRu.setToolTipText(bundle.getString("GUI.jbutSetLangRu.toolTipText")); // NOI18N
        jbutSetLangRu.setBorder(null);
        jbutSetLangRu.setBorderPainted(false);
        jbutSetLangRu.setFocusPainted(false);
        jbutSetLangRu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangRuActionPerformed(evt);
            }
        });

        jbutSetLangZhHanS.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangZhHanS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zh_HanS.jpg"))); // NOI18N
        jbutSetLangZhHanS.setToolTipText(bundle.getString("GUI.jbutSetLangZhHanS.toolTipText")); // NOI18N
        jbutSetLangZhHanS.setBorder(null);
        jbutSetLangZhHanS.setBorderPainted(false);
        jbutSetLangZhHanS.setFocusPainted(false);
        jbutSetLangZhHanS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangZhHanSActionPerformed(evt);
            }
        });

        jbutSetLangZhHansT.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jbutSetLangZhHansT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/zh_HanT.jpg"))); // NOI18N
        jbutSetLangZhHansT.setToolTipText(bundle.getString("GUI.jbutSetLangZhHansT.toolTipText")); // NOI18N
        jbutSetLangZhHansT.setBorder(null);
        jbutSetLangZhHansT.setBorderPainted(false);
        jbutSetLangZhHansT.setFocusPainted(false);
        jbutSetLangZhHansT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutSetLangZhHansTActionPerformed(evt);
            }
        });

        jcbAll.setText(bundle.getString("GUI.jcbAll.text")); // NOI18N
        jcbAll.setToolTipText(bundle.getString("GUI.jcbAll.toolTipText")); // NOI18N
        jcbAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbAllActionPerformed(evt);
            }
        });

        jcbInvert.setText(bundle.getString("GUI.jcbInvert.text")); // NOI18N
        jcbInvert.setToolTipText(bundle.getString("GUI.jcbInvert.toolTipText")); // NOI18N
        jcbInvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbInvertActionPerformed(evt);
            }
        });

        jcbTrackName.setText(bundle.getString("GUI.jcbTrackName.text")); // NOI18N
        jcbTrackName.setToolTipText(bundle.getString("GUI.jcbTrackName.toolTipText")); // NOI18N
        jcbTrackName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTrackNameActionPerformed(evt);
            }
        });

        jcbTitle.setText(bundle.getString("GUI.jcbTitle.text")); // NOI18N
        jcbTitle.setToolTipText(bundle.getString("GUI.jcbTitle.toolTipText")); // NOI18N
        jcbTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbTitleActionPerformed(evt);
            }
        });

        jtTrackList.setModel(new TableModel(java.util.ResourceBundle.getBundle("Bundle"), true));
        jspTracks.setViewportView(jtTrackList);

        jTabbedPane.addTab(bundle.getString("GUI.jspTracks.TabConstraints.tabTitle"), jspTracks); // NOI18N
        jspTracks.getAccessibleContext().setAccessibleName("");

        jtTargetList.setModel(new TableModel(java.util.ResourceBundle.getBundle("Bundle"), false));
        jspTargets.setViewportView(jtTargetList);

        jTabbedPane.addTab(bundle.getString("GUI.jspTargets.TabConstraints.tabTitle"), jspTargets); // NOI18N

        jtaLogs.setEditable(false);
        jtaLogs.setColumns(20);
        jtaLogs.setRows(5);
        jspLogs.setViewportView(jtaLogs);

        jbutHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/help.png"))); // NOI18N
        jbutHelp.setText(bundle.getString("GUI.jbutHelp.text")); // NOI18N
        jbutHelp.setBorder(null);
        jbutHelp.setBorderPainted(false);
        jbutHelp.setContentAreaFilled(false);
        jbutHelp.setFocusPainted(false);
        jbutHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutHelpActionPerformed(evt);
            }
        });

        jbutWeb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/web.png"))); // NOI18N
        jbutWeb.setText(bundle.getString("GUI.jbutWeb.text")); // NOI18N
        jbutWeb.setBorder(null);
        jbutWeb.setBorderPainted(false);
        jbutWeb.setContentAreaFilled(false);
        jbutWeb.setFocusPainted(false);
        jbutWeb.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutWebActionPerformed(evt);
            }
        });

        jbutAbout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/about.png"))); // NOI18N
        jbutAbout.setText(bundle.getString("GUI.jbutAbout.text")); // NOI18N
        jbutAbout.setBorder(null);
        jbutAbout.setBorderPainted(false);
        jbutAbout.setContentAreaFilled(false);
        jbutAbout.setFocusPainted(false);
        jbutAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbutAboutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jlSubOut)
                                    .addComponent(jlSubIn))
                                .addGap(63, 63, 63)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jtfInput, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jtfOutput, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jrbURL, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jrbURLListXML, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jcbAll)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbInvert)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jcbTrackName)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jcbTitle))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jbutSetLangCa, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangDe, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangEn, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangEs, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangFr, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangIt, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangPl, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangPtBr, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangRu, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(4, 4, 4)
                                .addComponent(jbutSetLangZhHanS, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutSetLangZhHansT, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jlDelay)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jspinnerDelay, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jbutOutput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbutInput, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jbutConvert, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jbutHelp)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutAbout)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jbutWeb)))
                        .addGap(0, 64, Short.MAX_VALUE))
                    .addComponent(jlStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane)
                    .addComponent(jspLogs))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jrbURL)
                        .addComponent(jrbURLListXML))
                    .addComponent(jbutHelp)
                    .addComponent(jbutAbout)
                    .addComponent(jbutWeb))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jlSubIn)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jtfInput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbutInput)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfOutput, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jbutOutput)
                    .addComponent(jlSubOut))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jlDelay)
                        .addComponent(jspinnerDelay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jbutConvert))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jbutSetLangCa, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jbutSetLangIt, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addComponent(jbutSetLangZhHanS, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutSetLangZhHansT, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutSetLangPtBr, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutSetLangPl, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutSetLangFr, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutSetLangEs, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutSetLangDe, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutSetLangEn, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jbutSetLangRu, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jcbAll)
                            .addComponent(jcbInvert)
                            .addComponent(jcbTrackName)
                            .addComponent(jcbTitle))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jspLogs, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jbutSetLangCa.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangCa.toolTipText")); // NOI18N
        jbutSetLangDe.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangDe.toolTipText")); // NOI18N
        jbutSetLangFr.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangFr.toolTipText")); // NOI18N
        jbutSetLangIt.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangIt.toolTipText")); // NOI18N
        jbutSetLangPl.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangPl.toolTipText")); // NOI18N
        jbutSetLangPtBr.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangPtBr.toolTipText")); // NOI18N
        jbutSetLangRu.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangRu.toolTipText")); // NOI18N
        jbutSetLangZhHanS.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangZhHanS.toolTipText")); // NOI18N
        jbutSetLangZhHansT.getAccessibleContext().setAccessibleDescription(bundle.getString("GUI.jbutSetLangZhHansT.toolTipText")); // NOI18N
        jTabbedPane.getAccessibleContext().setAccessibleName("");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbutConvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutConvertActionPerformed
        int totalSubtitlesSelected;
        
        if (!_controller.islSubsWithTranslationsNull()) {  // Tracks or targets
            if (jTabbedPane.getSelectedIndex() == 0) { // Tracks
                totalSubtitlesSelected = tmSubtitlesLists_getNumberSelectedTracks();
                if (totalSubtitlesSelected > appSettings.getLargeNumberSelectedSubtitlesWarning()
                        && !appManySelectedWarning(totalSubtitlesSelected))
                    return;
            } else if (jTabbedPane.getSelectedIndex() == 1) { // Targets
                totalSubtitlesSelected = tmSubtitlesLists_getNumberSelectedTracks() * tmSubtitlesLists_getNumberSelectedTargets();
                if (totalSubtitlesSelected > appSettings.getLargeNumberSelectedSubtitlesWarning()
                        && !appManySelectedWarning(totalSubtitlesSelected))
                    return;
            }
        }

        // Disable certain controls to avoid user interaction
        this.enableControls(false);
        this.jcbAll.setSelected(false);
        
        appSettings.setOutput(jtfOutput.getText());
        
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (_controller.getGUI().jrbURL.isSelected()) { // Source: Single URL (network)
                    if (_controller.islSubsWithTranslationsNull()) {
                        _controller.processInputURL();
                    }
                }
                
                if ("".equals(appSettings.getOutput())) {
                    appErrorBundleMessage("msg.outfile.not.specified");
                    prepareNewConversion();
                    return;
                }

                appStatusBundleMessage("msg.status.converting");
                
                if (_controller.islSubsWithTranslationsNull()) { // Source: XML file (no tracks / targets)
                    _controller.convertSubtitlesXML();
                } else {
                    if (jTabbedPane.getSelectedIndex() == 0) { // Normal tracks
                        _controller.convertSubtitlesTracks();
                    } else if (jTabbedPane.getSelectedIndex() == 1) { // Translated tracks (target)
                        _controller.convertSubtitlesTargets();
                    }
                }
            }
        }).start();
        
    }//GEN-LAST:event_jbutConvertActionPerformed

    private void jbutInputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutInputActionPerformed
        int returnVal;
        String path;
        
        if (this.jrbURLListXML.isSelected()) {  // Method: URL List / XML file
            returnVal = fc1.showOpenDialog(this.getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                path = fc1.getSelectedFile().getAbsolutePath();
                appSettings.setFileInput(path);
                jtfInput.setText(path);
                loadInputFile(path);
            }
        } else { // Method: Single URL
            appSettings.setURLInput(jtfInput.getText());

            this.enableControls(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    _controller.processInputURL();
                }
            }).start();
        }           
    }//GEN-LAST:event_jbutInputActionPerformed
    
    private void jbutOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutOutputActionPerformed
        int returnVal;
        String path;
                
        returnVal = fc2.showSaveDialog(this.getParent());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path = fc2.getSelectedFile().getAbsolutePath();
            jtfOutput.setText(path);
            appSettings.setOutput(path);
        }
    }//GEN-LAST:event_jbutOutputActionPerformed

    private void jbutSetLangCaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangCaActionPerformed
        this.setLanguage("ca");
}//GEN-LAST:event_jbutSetLangCaActionPerformed

    private void jbutSetLangEsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangEsActionPerformed
        this.setLanguage("es");
    }//GEN-LAST:event_jbutSetLangEsActionPerformed

    private void jbutSetLangEnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangEnActionPerformed
        this.setLanguage("en");
    }//GEN-LAST:event_jbutSetLangEnActionPerformed

private void jtfInputFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jtfInputFocusGained
    this.jtfInput.selectAll();
}//GEN-LAST:event_jtfInputFocusGained

private void jcbInvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbInvertActionPerformed
    int i;
    Object data[][];
    
    if (this.jTabbedPane.getSelectedIndex() == 0) // Normal tracks
    {
        this.jcbInvert.setSelected(false);
        data = this.tablemodelTracks.getData();
        if (data == null) return;
        for (i = 0; i < data.length; i++)
            data[i][0] = !((Boolean) data[i][0]).booleanValue();
        this.tablemodelTracks.init(_controller.getTracks(), data);
    }
    else if (this.jTabbedPane.getSelectedIndex() == 1) // Translated tracks (targets)
    {
        this.jcbInvert.setSelected(false);
        data = this.tablemodelTargets.getData();
        if (data == null) return;
        for (i = 0; i < data.length; i++)
            data[i][0] = !((Boolean) data[i][0]).booleanValue();
        this.tablemodelTargets.init(_controller.getTargets(), data);
    }
    this.tmSubtitlesLists_width();
}//GEN-LAST:event_jcbInvertActionPerformed

private void jcbAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbAllActionPerformed
    int i;
    Object data[][];

    if (this.jTabbedPane.getSelectedIndex() == 0) // Normal tracks
    {
        this.jcbAll.setSelected(false);
        _selectAllTracks = ! _selectAllTracks;
        data = this.tablemodelTracks.getData();
        if (data == null) return;
        for (i = 0; i < data.length; i++)
            data[i][0] = _selectAllTracks;
        this.tablemodelTracks.init(_controller.getTracks(), data);
    }
    else if (this.jTabbedPane.getSelectedIndex() == 1) // Translated tracks (targets)
    {
        this.jcbAll.setSelected(false);
        _selectAllTargets = ! _selectAllTargets;
        data = this.tablemodelTargets.getData();
        if (data == null) return;
        for (i = 0; i < data.length; i++)
            data[i][0] = _selectAllTargets;
        this.tablemodelTargets.init(_controller.getTargets(), data);
    }
    this.tmSubtitlesLists_width();
}//GEN-LAST:event_jcbAllActionPerformed

private void jrbURLItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jrbURLItemStateChanged

    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
        appSettings.setFileInput(jtfInput.getText());
        jtfInput.setText(appSettings.getURLInput());
        jtfInput.setEditable(true);

        jbutInput.setText(bundle.getString("GUI.jbutInput.text"));
        jbutInput.setToolTipText(bundle.getString("GUI.jbutInput.toolTipText"));
        
        if (! appSettings.getURLInput().isEmpty()) {
            this.enableControls(false);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    _controller.processInputURL();
                }
            }).start();
        }
    }
}//GEN-LAST:event_jrbURLItemStateChanged

private void jrbURLListXMLItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jrbURLListXMLItemStateChanged

    if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
//        _controller.initSubtitlesDataStructure();
//        this.tmSubtitlesLists_clear();
//        this.tmSubtitlesLists_populate();
        
        appSettings.setURLInput(jtfInput.getText());
        jtfInput.setText(appSettings.getFileInput());
        jtfInput.setEditable(false);

        jbutInput.setText(bundle.getString("GUI.jbutInput.xmlfile.text"));
        jbutInput.setToolTipText(bundle.getString("GUI.jbutInput.xmlfile.toolTipText"));
        
        if (! appSettings.getFileInput().isEmpty()) {
            loadInputFile(appSettings.getFileInput());
        }
    }
}//GEN-LAST:event_jrbURLListXMLItemStateChanged

private void jtfInputMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfInputMousePressed

    if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
        this.jtfInput.requestFocusInWindow();
    }

}//GEN-LAST:event_jtfInputMousePressed

private void jtfOutputMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtfOutputMousePressed

    if (evt.getButton() == java.awt.event.MouseEvent.BUTTON3) {
        this.jtfOutput.requestFocusInWindow();
    }
    
}//GEN-LAST:event_jtfOutputMousePressed

private void jbutSetLangPtBrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangPtBrActionPerformed
    this.setLanguage("pt_BR");
}//GEN-LAST:event_jbutSetLangPtBrActionPerformed

    private void jbutSetLangItActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangItActionPerformed
        this.setLanguage("it");
    }//GEN-LAST:event_jbutSetLangItActionPerformed

    private void jbutSetLangZhHanSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangZhHanSActionPerformed
        this.setLanguage("zh_HanS");
    }//GEN-LAST:event_jbutSetLangZhHanSActionPerformed

    private void jbutSetLangZhHansTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangZhHansTActionPerformed
        this.setLanguage("zh_HanT");
    }//GEN-LAST:event_jbutSetLangZhHansTActionPerformed

    private void jbutSetLangPlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangPlActionPerformed
        this.setLanguage("pl");
    }//GEN-LAST:event_jbutSetLangPlActionPerformed

    private void jbutSetLangFrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangFrActionPerformed
        this.setLanguage("fr");
    }//GEN-LAST:event_jbutSetLangFrActionPerformed

    private void jbutSetLangDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangDeActionPerformed
        this.setLanguage("de");
    }//GEN-LAST:event_jbutSetLangDeActionPerformed

    private void jbutSetLangRuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutSetLangRuActionPerformed
        this.setLanguage("ru");
    }//GEN-LAST:event_jbutSetLangRuActionPerformed

    private void jcbTrackNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTrackNameActionPerformed
        appSettings.setIncludeTrackNameInFilename(jcbTrackName.isSelected());
    }//GEN-LAST:event_jcbTrackNameActionPerformed

    private void jcbTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbTitleActionPerformed
        appSettings.setIncludeTitleInFilename(jcbTitle.isSelected());
    }//GEN-LAST:event_jcbTitleActionPerformed

    private void jbutAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutAboutActionPerformed
        _controller.showAbout();
    }//GEN-LAST:event_jbutAboutActionPerformed

    private void jbutHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutHelpActionPerformed
        _controller.showLocalHelp();
    }//GEN-LAST:event_jbutHelpActionPerformed

    private void jbutWebActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbutWebActionPerformed
        _controller.visitWebsite();
    }//GEN-LAST:event_jbutWebActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JButton jbutAbout;
    private javax.swing.JButton jbutConvert;
    private javax.swing.JButton jbutHelp;
    private javax.swing.JButton jbutInput;
    private javax.swing.JButton jbutOutput;
    private javax.swing.JButton jbutSetLangCa;
    private javax.swing.JButton jbutSetLangDe;
    private javax.swing.JButton jbutSetLangEn;
    private javax.swing.JButton jbutSetLangEs;
    private javax.swing.JButton jbutSetLangFr;
    private javax.swing.JButton jbutSetLangIt;
    private javax.swing.JButton jbutSetLangPl;
    private javax.swing.JButton jbutSetLangPtBr;
    private javax.swing.JButton jbutSetLangRu;
    private javax.swing.JButton jbutSetLangZhHanS;
    private javax.swing.JButton jbutSetLangZhHansT;
    private javax.swing.JButton jbutWeb;
    private javax.swing.JCheckBox jcbAll;
    private javax.swing.JCheckBox jcbInvert;
    private javax.swing.JCheckBox jcbTitle;
    private javax.swing.JCheckBox jcbTrackName;
    private javax.swing.JLabel jlDelay;
    private javax.swing.JLabel jlStatus;
    private javax.swing.JLabel jlSubIn;
    private javax.swing.JLabel jlSubOut;
    private javax.swing.JMenuItem jmiCopy;
    private javax.swing.JMenuItem jmiCut;
    private javax.swing.JMenuItem jmiPaste;
    private javax.swing.JPopupMenu jpmContextual;
    private javax.swing.JRadioButton jrbURL;
    private javax.swing.JRadioButton jrbURLListXML;
    private javax.swing.JScrollPane jspLogs;
    private javax.swing.JScrollPane jspTargets;
    private javax.swing.JScrollPane jspTracks;
    private javax.swing.JSpinner jspinnerDelay;
    private javax.swing.JTable jtTargetList;
    private javax.swing.JTable jtTrackList;
    private javax.swing.JTextArea jtaLogs;
    private javax.swing.JTextField jtfInput;
    private javax.swing.JTextField jtfOutput;
    private javax.swing.ButtonGroup subsSource;
    // End of variables declaration//GEN-END:variables
    
}
