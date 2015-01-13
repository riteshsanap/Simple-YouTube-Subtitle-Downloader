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

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    private List<Video> videos;                             // List of videos
    private Settings appSettings;                           // Application settings
    private GUI gui;                                        // GUI
    private List<List<NetSubtitle>> lSubsWithTranslations;  // Tracks (item 0) + Targets (item 1)
    
    protected void addTracks(List<NetSubtitle> subtitles) {
        lSubsWithTranslations.get(0).addAll(subtitles);
    }
    
    protected void addTargets(List<NetSubtitle> subtitles) {
        lSubsWithTranslations.get(1).addAll(subtitles);
    }
    
    protected List<NetSubtitle> getTracks() {
        return lSubsWithTranslations.get(0);
    }
    
    protected List<NetSubtitle> getTargets() {
        return lSubsWithTranslations.get(1);
    }
    
    protected GUI getGUI() {
        return gui;
    }
    
    public Controller(GUI gui, Settings settings) {
        this.appSettings = settings;
        this.gui = gui;
        
        initSubtitlesDataStructure();
        
    }
    
    // Data structure initialisation
    protected final void initSubtitlesDataStructure() {
        lSubsWithTranslations = new ArrayList<List<NetSubtitle>>();
        lSubsWithTranslations.add(new ArrayList<NetSubtitle>());
        lSubsWithTranslations.add(new ArrayList<NetSubtitle>());
    }
    
    // Parses a text file and returns subtitles for each video URL found
    protected void processURLListFile(InputStreamReader isr) {
        videos = Converter.parseURLListFile(isr);
        retrieveSubtitles();
    }
    
    // Returns subtitles for one video URL
    protected void processInputURL() {
        videos = new ArrayList<Video>();
        videos.add(new Video(appSettings.getURLInput()));
        retrieveSubtitles();
    }
    
    // Returns true if there is not at least 1 track and 1 target
    protected boolean islSubsWithTranslationsNull() {
        List<List<NetSubtitle>> swt = lSubsWithTranslations;
        return (swt == null || swt.size() < 2 ||
                swt.get(0) == null ||  swt.get(0).isEmpty() ||
                swt.get(1) == null ||  swt.get(1).isEmpty());
    }


    // Retrieves LIST of subtitles from the network
    public void retrieveSubtitles() {
        List<List<NetSubtitle>> al;
        List<Video> invalidVideos;
        int videoCount, videoTotalCount;
        
        initSubtitlesDataStructure();
        videoTotalCount = this.videos.size();
        
        invalidVideos = new ArrayList<Video>();

        videoCount = 0;
       // Check if URL is valid
        for (Video v : this.videos) {
            try {
                gui.appStatusConnecting(++videoCount, videoTotalCount);
                al = v.getSubtitlesWithTranslations();
                addTracks(al.get(0)); // lSubsWithTranslations.get(0).addAll(al.get(0));
                if (getTargets().isEmpty()) // Only add targets of the *first video with targets* - technically wrong, it makes sense in practice
                    addTargets(al.get(1)); // lSubsWithTranslations.get(1).addAll(al.get(1));
                gui.appStatusClear();
            } catch (Video.HostNoGV e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.url.unknown.host");
                else gui.appLogsBundleMessage("msg.url.unknown.host", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (Video.NoDocId e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.url.parameter.docid.not.found");
                else gui.appLogsBundleMessage("msg.url.parameter.docid.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (Video.NoQuery e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.url.parameter.not.found");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (Video.InvalidDocId e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.url.parameter.docid.invalid");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (Video.NoSubs e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.infile.no.subtitles.found");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (MalformedURLException e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.url.invalid.format");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (org.jdom.input.JDOMParseException e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.url.unexpected.format");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (java.net.UnknownHostException e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.net.unknown.host");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (FileNotFoundException e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.url.does.not.exist");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (Video.NoYouTubeParamV e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.net.missing.video.param");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            } catch (Exception e) {
                if (videoTotalCount == 1) gui.appErrorBundleMessage("msg.unknown.error");
                else gui.appLogsBundleMessage("msg.url.parameter.not.found", v.getURL());
                invalidVideos.add(v);
                continue;
            }

            gui.tmSubtitlesLists_populate();

        }
        
        // Removing invalid "videos" (text lines not containing a URL or a video without subtitles)
        for (Video v : invalidVideos)
            this.videos.remove(v);
        
        gui.prepareNewConversion();

    }
    
    // Converts an XML file to SRT
    protected void convertSubtitlesXML() {
        String fileName;
        Converter conv;
        InputStreamReader isr = null;
       
        try {
            isr = new InputStreamReader(new FileInputStream(appSettings.getFileInput()), "UTF-8");
        } catch (FileNotFoundException ex) {
            gui.setMsgIOException();
            gui.prepareNewConversion();
            return;
        } catch (java.io.UnsupportedEncodingException ex) {
            if (Settings.DEBUG) System.out.println("(DEBUG) encoding not supported");
        }
        
        fileName = Common.removeExtension((new File(appSettings.getFileInput())).getName()) + ".srt";

        conv = new Converter(
                gui,
                isr,
                Common.returnDirectory(appSettings.getOutput()) + fileName,
                gui.getDelay());
        conv.run();
        gui.prepareNewConversion();
        gui.appErrorBundleMessage("msg.conversion.finished");
    }
    
    // Downloads multiple tracks from the network and converts them to SRT
    protected void convertSubtitlesTracks() {
        Converter conv;
        Video v;

        Object dataTracks[][];
        String fileName, s;
        List<NetSubtitle> lTracks;
        int i, selectedCountTotalSubtitles, selectedCountTracks;
        boolean fewSubsSkipped = false;
        
        InputStreamReader isr;
        
        dataTracks = gui.getTableModelTracksData();
        lTracks = this.getTracks();

        selectedCountTotalSubtitles = gui.tmSubtitlesLists_getNumberSelectedTracks();

        // For each selected track: convert it to SRT
        for (i = 0, selectedCountTracks = 0; i < dataTracks.length; i++) {
            if (((Boolean) dataTracks[i][0]).booleanValue()) {

                gui.appStatusConverting(selectedCountTracks+1, selectedCountTotalSubtitles);

                v = getTracks().get(i).getVideo();

                try {
                    // When handling tracks, it is worth to try signature method FOR EACH track,
                    // even when a previous track retrieval via signature method failed
                    if (v.getMagicURL().isEmpty()) throw new Exception("No *Magic* URL!");
                    v.setMethod(NetSubtitle.Method.YouTubeSignature);
                    isr = v.readURL(lTracks.get(i).getTrackURL());
                } catch (Exception ex1) {
                    if (Settings.DEBUG) {
                        System.out.println("(DEBUG) URL could not be read via Signature method...");
                        System.out.println(
                            String.format("(DEBUG) Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s, Exception message='%s'",
                                v.getMethod(),
                                lTracks.get(i).getType(),
                                lTracks.get(i).getId(),
                                lTracks.get(i).getIdXML(),
                                lTracks.get(i).getLang(),
                                lTracks.get(i).getName(),
                                ex1.getMessage()));
                    }

                    if (lTracks.get(i).getType() == NetSubtitle.Tipus.YouTubeASRTrack)
                    {
                        // YouTube ASR cannot be retrieved by using Legacy method.
                        if (Settings.DEBUG) {
                            System.out.println("(DEBUG) YouTube ASR cannot be retrieved via Legacy method. Operation partially aborted.");
                            System.out.println(
                                String.format("(DEBUG) Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s",
                                    v.getMethod(),
                                    lTracks.get(i).getType(),
                                    lTracks.get(i).getId(),
                                    lTracks.get(i).getIdXML(),
                                    lTracks.get(i).getLang(),
                                    lTracks.get(i).getName()));
                        }
                        fewSubsSkipped = true;
                        if (selectedCountTotalSubtitles == 1) gui.appErrorBundleMessage("msg.io.cc.unreadable");
                        else gui.appLogsBundleMessage("msg.io.cc.unreadable", "[" + v.getId() + "," + lTracks.get(i).getLang() + "," + lTracks.get(i).getIdXML() + "]");
                        continue;
                    } else if (lTracks.get(i).getType() == NetSubtitle.Tipus.YouTubeTrack)
                    {
                        // A YouTube track/target can be retrieved by using legacy method.
                        // However, GUI should not reach this point with a target
                        if (Settings.DEBUG) System.out.println("(DEBUG) Switching to YouTube Legacy mode and retrying...");
                        v.setMethod(NetSubtitle.Method.YouTubeLegacy);

                        try
                        {
                            isr = v.readURL(lTracks.get(i).getTrackURL(NetSubtitle.Method.YouTubeLegacy));
                        } catch (Exception ex2) {
                            if (Settings.DEBUG) {
                                System.out.println("(DEBUG) URL could not be read with Legacy method. Operation partially aborted");
                                System.out.println(
                                    String.format("(DEBUG) Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s, Exception message='%s'",
                                        v.getMethod(),
                                        lTracks.get(i).getType(),
                                        lTracks.get(i).getId(),
                                        lTracks.get(i).getIdXML(),
                                        lTracks.get(i).getLang(),
                                        lTracks.get(i).getName(),
                                        ex2.getMessage()));
                            }
                            fewSubsSkipped = true;
                            if (selectedCountTotalSubtitles == 1) gui.appErrorBundleMessage("msg.io.cc.unreadable");
                            else gui.appLogsBundleMessage("msg.io.cc.unreadable", "[" + v.getId() + "," + lTracks.get(i).getLang() + "," + lTracks.get(i).getIdXML() + "]");
                            continue;
                        }
                    } else 
                    {
                        // YouTube Target should not reach this point due to GUI.
                        // Google Track should not reach this point.
                        if (Settings.DEBUG) System.out.println("(DEBUG) Entered wrong section of code. Unexpected result.");
                        fewSubsSkipped = true;                        
                        if (selectedCountTotalSubtitles == 1) gui.appErrorBundleMessage("msg.io.cc.unreadable");
                        else gui.appLogsBundleMessage("msg.io.cc.unreadable", "[" + v.getId() + "," + lTracks.get(i).getLang() + "," + lTracks.get(i).getIdXML() + "]");
                        continue;
                    }
                }

                fileName = "";
                if (appSettings.getIncludeTitleInFilename()) {
                    s = Common.removaInvalidFileNameChars(v.getTitle());
                    if (s != null)
                    fileName += s + "_";
                }

                fileName += lTracks.get(i).getId();
                fileName += "_" + lTracks.get(i).getIdXML();

                if (appSettings.getIncludeTrackNameInFilename()) {
                    s = lTracks.get(i).getName();
                    if (s != null)
                        fileName += "_" + s;
                }

                s = lTracks.get(i).getLang();
                if (s != null)
                    fileName += "_" + s;

                fileName += ".srt";


                conv = new Converter(
                    gui,
                    isr,
                    Common.returnDirectory(appSettings.getOutput()) + fileName,
                    gui.getDelay());
                
                if (!conv.run()) 
                {
                    // Conversion failed
                    // If Signature method was used and type is Track, let's retry
                    // Otherwise, operation is partially aborted
                    if (v.getMethod() == NetSubtitle.Method.YouTubeSignature && 
                            lTracks.get(i).getType() == NetSubtitle.Tipus.YouTubeTrack)
                    {
                        // A YouTube track/target can be retrieved by using legacy method.
                        if (Settings.DEBUG) System.out.println("(DEBUG) Switching to YouTube Legacy mode and retrying...");
                        v.setMethod(NetSubtitle.Method.YouTubeLegacy);

                        try
                        {
                            isr = v.readURL(lTracks.get(i).getTrackURL(NetSubtitle.Method.YouTubeLegacy));
                        } catch (Exception ex1) {
                            if (Settings.DEBUG) {
                                System.out.println("(DEBUG) URL could not be read with Legacy method. Operation partially aborted");
                                System.out.println(
                                    String.format("(DEBUG) Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s, Exception message='%s'",
                                        v.getMethod(),
                                        lTracks.get(i).getType(),
                                        lTracks.get(i).getId(),
                                        lTracks.get(i).getIdXML(),
                                        lTracks.get(i).getLang(),
                                        lTracks.get(i).getName(),
                                        ex1.getMessage()));
                            }
                            fewSubsSkipped = true;
                            continue;
                        }

                        conv = new Converter(
                            gui,
                            isr,
                            Common.returnDirectory(appSettings.getOutput()) + fileName, //Common.removeExtension(appSettings.getOutput()) + fileName, // jtfOutput.getText()
                            gui.getDelay());

                        conv.run();
                    }
                }
                selectedCountTracks++;
            }
        }

        gui.prepareNewConversion();
        if (selectedCountTotalSubtitles == 0) { // there is no selection
            gui.appErrorBundleMessage("msg.sublist.none.selected");
        } else { // there is selection and the process ended (either successfully or not)
            if (! fewSubsSkipped) gui.appErrorBundleMessage("msg.conversion.finished");
        }
    }

    // Downloads multiple targets (translated tracks) from the network and converts them to SRT
    protected void convertSubtitlesTargets() {
        Converter conv;
        Video v;
        
        Object dataTracks[][], dataTargets[][];
        String fileName, s;
        List<NetSubtitle> lTracks, lTargets;
        int i, j,
                selectedCountTotalTracks, selectedCountTracks,
                selectedCountTotalTargets, selectedCountTargets,
                selectedCountTotalSubtitles;
        boolean fewSubsSkipped = false;
        InputStreamReader isr;
        
        NetSubtitle srcLang;
        
        dataTracks = gui.getTableModelTracksData();
        dataTargets = gui.getTableModelTargetsData();
        lTracks = getTracks();
        lTargets = getTargets();

        selectedCountTotalTracks = gui.tmSubtitlesLists_getNumberSelectedTracks();
        selectedCountTotalTargets = gui.tmSubtitlesLists_getNumberSelectedTargets();
        selectedCountTotalSubtitles = selectedCountTotalTracks * selectedCountTotalTargets;
        
        // For each selected track
        for (i = 0, selectedCountTracks = 0; i < dataTracks.length; i++) {
            if (((Boolean) dataTracks[i][0]).booleanValue()) {
                srcLang = lTracks.get(i);
                v = srcLang.getVideo();

                // For each selected target
                for (j = 0, selectedCountTargets = 0; j < dataTargets.length; j++) {
                    if (((Boolean) dataTargets[j][0]).booleanValue()) {
                        
                        gui.appStatusConverting(selectedCountTracks * selectedCountTotalTargets + selectedCountTargets + 1, selectedCountTotalSubtitles);
                        
                        // If the source is an ASR track and we cannot use Signature method, operation must be completely aborted
                        if (srcLang.getType() == NetSubtitle.Tipus.YouTubeASRTrack && v.getMagicURL().isEmpty()) {
                            if (Settings.DEBUG) {
                                System.out.println("(DEBUG) YouTube ASR cannot be retrieved via Legacy method. Operation completely aborted.");
                                System.out.println(
                                    String.format("(DEBUG) [SOURCE] Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s",
                                        v.getMethod(),
                                        srcLang.getType(),
                                        srcLang.getId(),
                                        srcLang.getIdXML(),
                                        srcLang.getLang(),
                                        srcLang.getName()));
                            }
                            
                            // gui.prepareNewConversion();
                            if (selectedCountTotalSubtitles == 1) gui.appErrorBundleMessage("msg.io.asr.unreadable");
                            else gui.appLogsBundleMessage("msg.io.asr.unreadable", "[" + v.getId() + "," + srcLang.getLang() + "," + srcLang.getIdXML() + "]");
                            
                            break; // ASR as source and no ASR available: ALL targets from this ASR must be ABORTED
                        }

                        try {
                            // When handling targets, it is worth to try signature method FOR EACH target,
                            // even when a previous track retrieval via signature method failed
                            // ASR source has already been discarded if *Magic* URL was not available

                            if (v.getMagicURL().isEmpty()) throw new Exception("No *Magic* URL!");
                            v.setMethod(NetSubtitle.Method.YouTubeSignature);
                            isr = v.readURL(lTargets.get(j).getTargetURL(srcLang));
                        } catch (Exception ex1) {
                            if (Settings.DEBUG) {
                                System.out.println("(DEBUG) URL could not be read... ");
                                System.out.println(
                                    String.format("(DEBUG) [TARGET] Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s, Exception message='%s'",
                                        v.getMethod(),
                                        lTargets.get(j).getType(),
                                        lTargets.get(j).getId(),
                                        lTargets.get(j).getIdXML(),
                                        lTargets.get(j).getLang(),
                                        lTargets.get(j).getName(),
                                        ex1.getMessage()));
                            }

                            if (srcLang.getType() == NetSubtitle.Tipus.YouTubeASRTrack)
                            {
                                // YouTube ASR targets cannot be retrieved by using Legacy method.
                                if (Settings.DEBUG) {
                                    System.out.println("(DEBUG) YouTube targets translated from ASR cannot be retrieved via Legacy method. Operation partially aborted.");
                                    System.out.println(
                                        String.format("(DEBUG) [TARGET] Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s",
                                            v.getMethod(),
                                            lTargets.get(j).getType(),
                                            lTargets.get(j).getId(),
                                            lTargets.get(j).getIdXML(),
                                            lTargets.get(j).getLang(),
                                            lTargets.get(j).getName()));
                                }
                                fewSubsSkipped = true;
                                if (selectedCountTotalSubtitles == 1) gui.appErrorBundleMessage("msg.io.cc.unreadable");
                                else gui.appLogsBundleMessage("msg.io.cc.unreadable", "[" + v.getId() + "," + lTargets.get(j).getLang() + "," + lTargets.get(j).getIdXML() + "]");
                                continue;
                            } else if (srcLang.getType() == NetSubtitle.Tipus.YouTubeTrack)
                            {
                                // NOTE: In order if it is worth to use legacy mode, we check the SOURCE (ASR or normal track)
                                // A YouTube track/target can be retrieved by using legacy method.
                                if (Settings.DEBUG) System.out.println("(DEBUG) Switching to YouTube Legacy mode and retrying...");
                                v.setMethod(NetSubtitle.Method.YouTubeLegacy);

                                try
                                {
                                    isr = v.readURL(lTargets.get(j).getTargetURL(NetSubtitle.Method.YouTubeLegacy, srcLang));
                                } catch (Exception ex2) {
                                    if (Settings.DEBUG) {
                                        System.out.println("(DEBUG) URL could not be read with legacy method. Operation partially aborted");
                                        System.out.println(
                                            String.format("(DEBUG) [TARGET] Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s, Exception message='%s'",
                                                v.getMethod(),
                                                lTargets.get(j).getType(),
                                                lTargets.get(j).getId(),
                                                lTargets.get(j).getIdXML(),
                                                lTargets.get(j).getLang(),
                                                lTargets.get(j).getName(),
                                                ex2.getMessage()));
                                    }
                                    fewSubsSkipped = true;
                                    if (selectedCountTotalSubtitles == 1) gui.appErrorBundleMessage("msg.io.cc.unreadable");
                                    else gui.appLogsBundleMessage("msg.io.cc.unreadable", "[" + v.getId() + "," + lTargets.get(j).getLang() + "," + lTargets.get(j).getIdXML() + "]");
                                    continue;
                                }
                            } else
                            {
                                // YouTube Track should not reach this point due to GUI.
                                // Google Track should not reach this point.
                                if (Settings.DEBUG) System.out.println("(DEBUG) Entered wrong section of code. Unexpected result.");
                                fewSubsSkipped = true;
                                if (selectedCountTotalSubtitles == 1) gui.appErrorBundleMessage("msg.io.cc.unreadable");
                                else gui.appLogsBundleMessage("msg.io.cc.unreadable", "[" + v.getId() + "," + lTargets.get(j).getLang() + "," + lTargets.get(j).getIdXML() + "]");
                                continue;
                            }
                        }

                        fileName = "";
                        if (appSettings.getIncludeTitleInFilename()) {
                            s = Common.removaInvalidFileNameChars(v.getTitle());
                            if (s != null)
                            fileName += s + "_";
                        }

                        fileName += srcLang.getId();
                        fileName += "_" + lTargets.get(j).getIdXML();

                        s = lTargets.get(j).getLang();
                        if (s != null)
                            fileName += "_" + s;


                        fileName += "_" + srcLang.getIdXML();

                        if (appSettings.getIncludeTrackNameInFilename()) {
                            s = srcLang.getName();
                            if (s != null)
                                fileName += "_" + s;
                        }

                        s = srcLang.getLang();
                        if (s != null)
                            fileName += "_" + s;

                        fileName += ".srt";

                        conv = new Converter(
                            gui,
                            isr,
                            Common.returnDirectory(appSettings.getOutput()) + fileName, //Common.removeExtension(appSettings.getOutput()) + fileName, // jtfOutput.getText()
                            gui.getDelay());
                        
                        if (!conv.run())
                        {
                            // Conversion failed
                            // If Signature method was used and SOURCE type is Track, let's retry
                            // Otherwise, operation is partially aborted
                            if (v.getMethod() == NetSubtitle.Method.YouTubeSignature && 
                                    srcLang.getType() == NetSubtitle.Tipus.YouTubeTrack)
                            {
                                // A YouTube track/target can be retrieved by using legacy method.
                                if (Settings.DEBUG) System.out.println("(DEBUG) Switching to YouTube Legacy mode and retrying...");
                                v.setMethod(NetSubtitle.Method.YouTubeLegacy);

                                try
                                {
                                    isr = v.readURL(lTargets.get(j).getTargetURL(NetSubtitle.Method.YouTubeLegacy, srcLang));
                                } catch (Exception ex1) {
                                    if (Settings.DEBUG) {
                                        System.out.println("(DEBUG) URL could not be read with Legacy method. Operation partially aborted");
                                        System.out.println(
                                            String.format("(DEBUG) Method=%s, Type=%s, ID=%s, IDXML=%s, Lang=%s, Name=%s, Exception message='%s'",
                                                v.getMethod(),
                                                lTargets.get(j).getType(),
                                                lTargets.get(j).getId(),
                                                lTargets.get(j).getIdXML(),
                                                lTargets.get(j).getLang(),
                                                lTargets.get(j).getName(),
                                                ex1.getMessage()));
                                    }
                                    fewSubsSkipped = true;
                                    if (selectedCountTotalSubtitles == 1) gui.appErrorBundleMessage("msg.io.cc.unreadable");
                                    else gui.appLogsBundleMessage("msg.io.cc.unreadable", "[" + v.getId() + "," + lTargets.get(j).getLang() + "," + lTargets.get(j).getIdXML() + "]");
                                    continue;
                                }

                                conv = new Converter(
                                    gui,
                                    isr,
                                    Common.returnDirectory(appSettings.getOutput()) + fileName, //Common.removeExtension(appSettings.getOutput()) + fileName, // jtfOutput.getText()
                                    gui.getDelay());

                                conv.run();
                            }
                        }
                        selectedCountTargets++;
                    }
                }
                selectedCountTracks++;
            }
        }

        gui.prepareNewConversion();
        if (selectedCountTotalTracks == 0) { // there is no selection
            gui.appErrorBundleMessage("msg.sublist.none.selected");
        } else { // there is selection and the process ended (successfully or not)
            if (!fewSubsSkipped) gui.appErrorBundleMessage("msg.conversion.finished");
        }
    }
    
    protected void showLocalHelp() {
        String path = null;
        File pwd;
        
        try {
            // Opening localised local help (Catalan, Spanish...)
            // Examples: "doc/ca/help.html", "doc\\es\\help.html" (without single backslashes)
            pwd = new File(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent());
            path = pwd.getCanonicalPath() +
                    File.separator +
                    Settings.PROJECT_HELP_NON_LOCALISED_PATH +
                    File.separator +
                    appSettings.getLocaleLanguage().replace("_", "-").toLowerCase() +
                    File.separator +
                    Settings.PROJECT_HELP_FILE_NAME;
            
            if (Settings.DEBUG) System.out.println("(DEBUG) Opening " + path + "...");
            Desktop.getDesktop().open(new File(path));
        } catch (Exception ex) {
            try {
                // It failed. Attempting default language (English)
                if (Settings.DEBUG) System.out.println("(DEBUG) File " + path + " could not be opened");
                
                // Examples: "doc/en/help.html", "doc\\en\\help.html" (without single backslashes)
                pwd = new File(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent());
                path = pwd.getCanonicalPath() +
                        File.separator +
                        Settings.PROJECT_HELP_NON_LOCALISED_PATH +
                        File.separator +
                        "en" +
                        File.separator +
                        Settings.PROJECT_HELP_FILE_NAME;

                if (Settings.DEBUG) System.out.println("(DEBUG) Opening " + path + "...");
                Desktop.getDesktop().open(new File(path));
            } catch (Exception ex2) {
                // It failed again. Sending user to project website
                if (Settings.DEBUG) System.out.println("(DEBUG) File " + path + " could not be opened");
                visitWebsite();
            }
        }
    }
    
    protected void showAbout() {
        String path = null;
        File pwd;
        
        try {
            pwd = new File(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent());
            path = pwd.getCanonicalPath() +
                    File.separator +
                    Settings.PROJECT_README;
            
            if (Settings.DEBUG) System.out.println("(DEBUG) Opening " + path + "...");
            Desktop.getDesktop().open(new File(path));
        } catch (Exception ex) {
            // It failed. Sending user to project website
            if (Settings.DEBUG) System.out.println("(DEBUG) File " + path + " could not be opened");
            visitWebsite();
        }
    }
    
    protected void visitWebsite() {
        try {
            if (Settings.DEBUG) System.out.println("(DEBUG) Browsing to " + Settings.PROJECT_URL + "...");
            Desktop.getDesktop().browse(java.net.URI.create(Settings.PROJECT_URL));
        } catch (Exception ex) {
            // It failed. Nothing to be done.
            if (Settings.DEBUG) System.out.println("(DEBUG) Browsing to " + Settings.PROJECT_URL + " FAILED");
        }
    }
}