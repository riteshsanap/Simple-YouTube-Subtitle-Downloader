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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ResourceBundle;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;


public class Settings {
    protected static final boolean DEBUG = false;
    protected static final String PROJECT_URL = "http://google2srt.sourceforge.net";
    protected static final String PROJECT_README = "README.TXT";
    protected static final String PROJECT_HELP_NON_LOCALISED_PATH = "doc";
    protected static final String PROJECT_HELP_FILE_NAME = "help.html";
    
    
    private ResourceBundle bundle;
    private SAXBuilder xmlparser;
    private XMLOutputter xmlwriter;
    
    // Default input file (manually downloaded XML file)
    private static final String DEFAULT_XML_FILE_INPUT = "";
    // Example: System.getProperty("user.home") +  System.getProperty("file.separator") + "timedtext.xml";
    
    private String fileInput = DEFAULT_XML_FILE_INPUT;
    public String getFileInput() { return this.fileInput; }
    public void setFileInput(String xmlFileInput) { this.fileInput = xmlFileInput; }
    
    
    // Default output file/path
    private static final String DEFAULT_OUTPUT = System.getProperty("user.home");
    
    private String srtOutput = DEFAULT_OUTPUT;
    public String getOutput() { return this.srtOutput; }
    public void setOutput(String srtOutput) { this.srtOutput = srtOutput; }
    
    // Default input URL
    private static final String DEFAULT_URL_INPUT = "";
    // Some samples (debug)
    // http://www.youtube.com/watch?v=c8RGPpcenZY (4 real tracks)
    // http://www.youtube.com/watch?v=XraeBDMm2PM (5 real tracks with names)
    // http://www.youtube.com/watch?v=IElqf-FCMs8 (EN ASR + EN track)
    // http://www.youtube.com/watch?v=UOfn1cTARrY (ES ASR)
    // http://www.youtube.com/watch?v=PH8JuizIXw8 (EN ASR + many real tracks)
    
    private String urlInput = DEFAULT_URL_INPUT;
    public String getURLInput() { return this.urlInput; }
    public void setURLInput(String URLInput) { this.urlInput = URLInput; }
    
 
    // Locale (language)
    private String localeLanguage;
    public String getLocaleLanguage() { return this.localeLanguage; }
    public void setLocaleLanguage(String localeLanguage) { this.localeLanguage = localeLanguage; }
    
    
    // Default large number of selected subtitles warning
    private static final int DEFAULT_LARGE_NUMBER_SELECTED_SUBTITLES_WARNING = 100;
    
    private int largeNumberSelectedSubtitlesWarning = DEFAULT_LARGE_NUMBER_SELECTED_SUBTITLES_WARNING;
    public int getLargeNumberSelectedSubtitlesWarning() { return this.largeNumberSelectedSubtitlesWarning; }
    
    // Include video title in output file name?
    private static final boolean DEFAULT_INCLUDE_TITLE_IN_FILENAME = false;
    private boolean includeTitleInFilename = DEFAULT_INCLUDE_TITLE_IN_FILENAME;
    protected boolean getIncludeTitleInFilename() { return includeTitleInFilename; }
    protected void setIncludeTitleInFilename(boolean include) { includeTitleInFilename = include; }
    
    // Include track name in output file name?
    private static final boolean DEFAULT_INCLUDE_TRACKNAME_IN_FILENAME = false;
    private boolean includeTrackNameInFilename = DEFAULT_INCLUDE_TRACKNAME_IN_FILENAME;
    protected boolean getIncludeTrackNameInFilename() { return includeTrackNameInFilename; }
    protected void setIncludeTrackNameInFilename(boolean include) { includeTrackNameInFilename = include; }
    
    // Configuration file
    // Windows: %APPDATA%\Google2SRT\settings.xml
    // Other: ~/.google2srt/settings.xml
    private String appSettingsFile;
    
    public Settings(ResourceBundle bundle) 
    {
        this.bundle = bundle;
        
        // System language
        localeLanguage = bundle.getLocale().getLanguage();
        
        // Initializing XML tools
        xmlparser = new SAXBuilder();
        xmlwriter = new XMLOutputter();

    }
    
    // Loads settings from file
    public boolean loadSettings() {
        appSettingsFile = initSettingsFile();
        return loadSettingsFromFile(appSettingsFile);
    }
    
    // Save settings to file
    public boolean saveSettings() {
        return saveSettingsToFile(appSettingsFile);
    }
    
    private String initSettingsFile() {
            String osname;
            String settingsPath;
            String settingsFile;
            File f, fdir;

            osname = System.getProperty("os.name");

            if (osname != null && osname.startsWith("Windows")) {
                settingsPath = System.getenv("APPDATA") +
                        System.getProperty("file.separator") +
                        "Google2SRT";
            } else {
                settingsPath = System.getProperty("user.home") + 
                        System.getProperty("file.separator") +
                        ".google2srt";
            }

            try {
                fdir = new File(settingsPath);
                if (fdir.exists()) {
                    if (! fdir.isDirectory()) // NON-directory already exists: ABORTING
                        return null;
                } else {
                    // create directory
                    if (! fdir.mkdir()) // directory could not be created: ABORTING
                        return null;
                }
            } catch (Exception e) {
                // Exiting without touching the system
                return null;
            }

            // directory already existed or it was successfully created

            settingsFile = settingsPath +
                    System.getProperty("file.separator") +
                    "settings.xml";
            try {                        
                f = new File(settingsFile);

                if (f.exists())
                {
                    if (! f.isFile()) // NON-normal file already exists: ABORTING
                        return null;
                    else if (f.canRead())
                        return settingsFile; // normal file exists AND is readable
                } else // file does not exist yet: to be created via JDOM SAX
                    return settingsFile; 
            } catch (Exception e) {
                return null;
            }

            return settingsFile;
        }
    
    private boolean loadSettingsFromFile(String appSettingsFile) {
        InputStreamReader isr;
        
        try {
            isr = new InputStreamReader(new FileInputStream(appSettingsFile), "UTF-8");
        } catch (FileNotFoundException ex_isr_FNF) {
            // Settings file does not exist or cannot be read
            
            if ((new File(appSettingsFile)).exists()) // File exists and could not be read: ABORTING
                return false;
            
            // Creating file
            return saveSettingsToFile(appSettingsFile);
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
        
        return parseSettingsFromXMLFile(isr);
    }
    
    private boolean saveSettingsToFile(String appSettingsFile) {
        OutputStreamWriter osw;
        
        try {
            if (Settings.DEBUG) System.out.println("(DEBUG) Writing to settings file: " + appSettingsFile);
            osw = new OutputStreamWriter(new FileOutputStream(appSettingsFile), "UTF-8");
            return saveSettingsToXMLFile(osw);
        } catch (FileNotFoundException ex) {
            if (Settings.DEBUG) System.out.println("(DEBUG) Failed writing to settings file: " + appSettingsFile);
            return false;
        } catch (UnsupportedEncodingException ex) {
            return false;
        }
    }
    
    private boolean saveSettingsToXMLFile(OutputStreamWriter osw) {
        Document doc;
        Element el;
        
        // <settings />
        doc = new Document();
        el = new Element("settings");
        doc.setRootElement(el);

        /* <settings
         *      language="en"
         *      output="/home/user/subtitles/"
         *      xmlfileinput="/home/user/timedtext.xml"
         *      titleinfilename="0"
         *      tracknameinfilename="0"
         *      lnsswarning="100"
         * />
         * 
         */
        el.setAttribute(new Attribute("language", localeLanguage));
        el.setAttribute(new Attribute("output", srtOutput));
        el.setAttribute(new Attribute("xmlfileinput", fileInput));
        el.setAttribute(new Attribute("titleinfilename", includeTitleInFilename ? "1" : "0"));
        el.setAttribute(new Attribute("tracknameinfilename", includeTrackNameInFilename ? "1" : "0"));        
        el.setAttribute(new Attribute("lnsswarning", Integer.toString(largeNumberSelectedSubtitlesWarning)));
           
        try {
            xmlwriter.output(doc, osw);
        } catch (IOException ex) {
            if (Settings.DEBUG) System.out.println("(DEBUG) XML file could not be written");
            return false;
        }

        if (Settings.DEBUG) System.out.println("(DEBUG) Settings written to XML file successfully");
        return true;
    }

    private boolean parseSettingsFromXMLFile(InputStreamReader isr) {
        /*
         * <settings language="language" output="path" xmlfileinput="path" />
         * 
         * language:        Language in ISO 639 format or special value "*SYSTEM*" (system-detected).
         *                  Examples: "en", "pt_BR", "zh_HanS", "*SYSTEM"*
         * 
         * output:          Valid path for SRT output.
         *                  Examples: "/home/user/SRT/", "C:\\Users\\user\\SRT"(*)
         * 
         * xmlfileinput:    Valid path for XML file input.
         *                  Examples: "/home/user/timedtext.xml", "C:\\Users\\user\\timedtext.xml"(*)
         * 
         * titleinfilename: Include video title in output file name.
         *                  Valid values: Yes: "1"; No: "0" 
         * 
         * tracknameinfilename: Include track name in output file name.
         *                      Valid values: Yes: "1"; No: "0" 
         * 
         * lnsswarning:     Large number of selected subtitles warning. Application will ask for user confirmation.
         *                  Example: 100
         *
         * (*) with SINGLE backslashes
         * 
         */
        Document doc;
        Element el;
        Attribute at;
        String val;
        
        try {
            doc = xmlparser.build(isr);
        } catch (JDOMException ex) {
            if (Settings.DEBUG) System.out.println("(DEBUG) Exception parsing XML settings: " + ex.getMessage());
            return false;
        } catch (IOException ex) {
            if (Settings.DEBUG) System.out.println("(DEBUG) Settings could not be read from XML file");
            return false;
        }
        
        el = doc.getRootElement();

        // Set language detected by the system or saved by the user
        // - User-defined: If 'language' has a valid value different from "*SYSTEM*"
        // - System-detected: In any other case
        at = el.getAttribute("language");
        if (at != null) // language is defined
        {
            val = at.getValue();
            
            if (! "*SYSTEM*".equals(val) && Common.isSupportedLanguage(val)) {
                localeLanguage = val; // Set language saved by the user
            } else {
                localeLanguage = bundle.getLocale().getLanguage(); // Set language detected by the system (locale)
            }
        } else { // language is NOT defined
            localeLanguage = bundle.getLocale().getLanguage(); // Set language detected by the system (locale)
        }
        
        // Set output location
        at = el.getAttribute("output");
        if (at != null)
            srtOutput = at.getValue();
        else
            srtOutput = DEFAULT_OUTPUT;
        
        // Set XML file input location
        at = el.getAttribute("xmlfileinput");
        if (at != null)
            fileInput = at.getValue();
        else
            fileInput = DEFAULT_XML_FILE_INPUT;
        
        // Include video title in output file name?
        at = el.getAttribute("titleinfilename");
        if (at != null)
            includeTitleInFilename = ! "0".equals(at.getValue());
        else
            includeTitleInFilename = DEFAULT_INCLUDE_TITLE_IN_FILENAME;
        
        // Include track name in output file name?
        at = el.getAttribute("tracknameinfilename");
        if (at != null)
            includeTrackNameInFilename = ! "0".equals(at.getValue());
        else
            includeTrackNameInFilename = DEFAULT_INCLUDE_TRACKNAME_IN_FILENAME;
        
        // Set XML file input location
        at = el.getAttribute("lnsswarning");
        if (at != null) {
            Integer iTmp;
            try {
                iTmp = new Integer(at.getValue());
                largeNumberSelectedSubtitlesWarning = iTmp.intValue();
            } catch (NumberFormatException ex) {
                largeNumberSelectedSubtitlesWarning = DEFAULT_LARGE_NUMBER_SELECTED_SUBTITLES_WARNING;
            }
        } else
            largeNumberSelectedSubtitlesWarning = DEFAULT_LARGE_NUMBER_SELECTED_SUBTITLES_WARNING;
        
        
        if (Settings.DEBUG) System.out.println("(DEBUG) Settings parsed from XML file successfully");
        return true;
    }
}
