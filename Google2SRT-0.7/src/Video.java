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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import org.apache.commons.io.IOUtils;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class Video {
    public static class HostNoGV extends Exception {};
    public static class NoDocId extends Exception {};
    public static class NoQuery extends Exception {};
    public static class InvalidDocId extends Exception {};
    public static class NoSubs extends Exception {};
    public static class NoYouTubeParamV extends Exception {};
    
    private String _id = null;                // "docid" (Google Video) o "v" (YouTube)
    private String _magicURL = "";
    private String _title = "";
    private HashMap<String, String> _params;
    private NetSubtitle.Method _method;
    private String YouTubeWebSource;
    private String _URL;
    private List<List<NetSubtitle>> _subsWT;
    
    public Video(String URL) {
        _subsWT = new ArrayList<List<NetSubtitle>>();
        _URL = URL;
    }
    
    public String getMagicURL() {
        return _magicURL;
    }
    
    public String getURL() {
        return _URL;
    }
    
    public String getTitle() {
        return _title;
    }
    
    public String getId() {
        return _id;
    }
    
    public HashMap<String, String> getParams() {
        return _params;
    }
    
    public NetSubtitle.Method getMethod() {
        return _method;
    }
    
    public void setMethod(NetSubtitle.Method method) {
        _method = method;
    }
    
    
    public List<NetSubtitle> getSubtitles() throws MalformedURLException, HostNoGV, NoQuery, NoDocId, InvalidDocId, UnsupportedEncodingException, JDOMException, IOException, NoSubs, NoYouTubeParamV {
        if (_subsWT.isEmpty())
            getSubtitlesWithTranslations(); // _subsWT = getSubtitlesWithTranslations(URL)
        
        return _subsWT.get(0);
    }
    
    public List<List<NetSubtitle>> getSubtitlesWithTranslations() throws MalformedURLException, HostNoGV, NoQuery, NoDocId, InvalidDocId, UnsupportedEncodingException, JDOMException, IOException, NoSubs, NoYouTubeParamV {
        String urlList;
        URL url;
        Document xmlDoc;
        List<NetSubtitle> lTracks;
        List<List<NetSubtitle>> result;
        
        // Already retrieved
        if (! _subsWT.isEmpty()) return _subsWT;
            
        url = new URL(_URL);
        result = new ArrayList<List<NetSubtitle>>();
            
        if (url.getHost() == null) {
            throw new HostNoGV();
        } else if (url.getHost().indexOf("video.google.com") != -1) {
            _params = getURLParams(_URL);
            setMethod(NetSubtitle.Method.Google);
            urlList = NetSubtitle.getListURL(getMethod(), getParams());
            xmlDoc = readListURL(urlList);
            lTracks = getListSubs(xmlDoc, getParams());
            result = new ArrayList<List<NetSubtitle>>();
            result.add(lTracks);
            result.add(new ArrayList<NetSubtitle>());
        } else if (url.getHost().indexOf("youtube.com") != -1
                || url.getHost().indexOf("youtu.be") != -1) {
            
            if (url.getHost().indexOf("youtu.be") != -1) {
                // http://youtu.be/c8RGPpcenZY => https://www.youtube.com/watch?v=c8RGPpcenZY
                
                String s;
                try { s = url.getFile(); }
                catch (Exception e) { s = " "; }
                
                url = new URL("https://www.youtube.com/watch?v=" + s.substring(1, s.length()));
                _URL = url.toString();
            } else 
            {
                // http://www.youtube.com/watch?v=c8RGPpcenZY => https://www.youtube.com/watch?v=c8RGPpcenZY

                url = new URL(url.toString().replace("http://", "https://"));
                _URL = url.toString();
            }
            
            if (Settings.DEBUG) System.out.println("(DEBUG) Final video URL: " + _URL);
            
            try {
                _magicURL = retrieveMagicURL(_URL);
                _title = retrieveVideoTitle();
                _params = getURLParams(getMagicURL());
                setMethod(NetSubtitle.Method.YouTubeSignature);
                urlList = NetSubtitle.getListURL(getMethod(), getParams());
                xmlDoc = readListURL(urlList);
                result = getListSubsWithTranslations(xmlDoc, getParams(), getMethod());
            }
            catch (Exception ex) {
                if (Settings.DEBUG) System.out.println("(DEBUG) Exception reading via Signature mode. Switching to Legacy mode...");
                _magicURL = "";
                _title = "";
                _params = getURLParams(_URL);
                setMethod(NetSubtitle.Method.YouTubeLegacy);
                urlList = NetSubtitle.getListURL(getMethod(), getParams());
                xmlDoc = readListURL(urlList);
                result = getListSubsWithTranslations(xmlDoc, getParams(), getMethod());
            }
            
        } else {
            throw new HostNoGV();
        }

        _subsWT = result;
        return result;
    }
    
    public String retrieveMagicURL(String YouTubeURL) throws MalformedURLException, IOException {
            
            String magicURL;
            InputStreamReader isr;
            
            isr = readURL(YouTubeURL);
            YouTubeWebSource = readURL(isr);   
            magicURL = NetSubtitle.getMagicURL(YouTubeWebSource);
            
            
            if (Settings.DEBUG) System.out.println("(DEBUG) *Magic* URL: " + magicURL);
            
            return magicURL;
    }
    
    public String retrieveVideoTitle() {
        return (YouTubeWebSource != null) ?
                NetSubtitle.getVideoTitleFromSource(YouTubeWebSource) : "";
    }
    
    
    public HashMap<String, String> getURLParams(String URL) throws MalformedURLException {
        URL url;
        String[] sparams;
        HashMap<String, String> mparams;
        String name, value;
        String[] as;
        
        url = new URL(URL);
        sparams = url.getQuery().split("&");
        mparams = new HashMap<String, String>();
        for (String param : sparams)
        {
            as = param.split("=");
            if (as.length > 1)
            {
                name = as[0];
                value = as[1];
            }
            else if (as.length > 0)
            {
                name = as[0];
                value = "";
            } else
            {
                name = "";
                value = "";
            }
                
            mparams.put(name, value);
        }
        
        return mparams;
    }
    
    private Document readListURL(String url) throws MalformedURLException, JDOMException, IOException {
        SAXBuilder parser = new SAXBuilder();
        InputStreamReader isr;

        isr = readURL(url);
        return parser.build(isr);
    }
    
    private List<NetSubtitle> getListSubs(Document xml,
            HashMap<String, String> params)
            throws NoSubs, UnsupportedEncodingException {
        return getListSubsWithTranslations(xml, params, NetSubtitle.Method.Google).get(0);
    }
    
    private List<List<NetSubtitle>> getListSubsWithTranslations(Document xml,
            HashMap<String, String> params,
            NetSubtitle.Method method)
            throws NoSubs, UnsupportedEncodingException {
        Element arrel, track;
        List<Element> tracks;
        int tam, i, tmpInt;
        Attribute tmpAtt;
        String tmpS, sName, sLang, sLangOrig, sLangTrans;
        List<NetSubtitle> lTracks = new ArrayList<NetSubtitle>();
        List<NetSubtitle> lTargets = new ArrayList<NetSubtitle>();
        List<List<NetSubtitle>> resultat;
        NetSubtitle tNS;
        
        if (xml == null)
            throw new NoSubs();
        
        arrel = xml.getRootElement();
        tmpAtt = arrel.getAttribute("docid");
        if (tmpAtt == null)
            throw new NoSubs();

        tracks = arrel.getChildren();
        tam = tracks.size();
        if (tam == 0)
            return null;
        i = 0;
        while (i < tam) {
            track = tracks.get(i);
            if (track != null) {
                tmpAtt = track.getAttribute("id");
                if (tmpAtt != null) {
                    tmpS = tmpAtt.getValue();
                    tmpInt = Integer.valueOf(tmpS);

                    //<track id="0" name="" lang_code="ca" lang_original="Català" lang_translated="Catalan" cantran="true"/>
                    //<target id="42" lang_code="ca" lang_original="Català" lang_translated="Catalan"/>

                    tmpAtt = track.getAttribute("lang_code");
                    sLang = tmpAtt.getValue();
                    tmpAtt = track.getAttribute("lang_original");
                    sLangOrig = tmpAtt.getValue();
                    tmpAtt = track.getAttribute("lang_translated");
                    sLangTrans = tmpAtt.getValue();

                    tNS = new NetSubtitle(this);
                    switch (method)
                    {
                        case Google:
                            _id = params.get("docid"); //tNS.setId(params.get("docid"));
                            break;
                        case YouTubeLegacy:
                        case YouTubeSignature:
                            _id = params.get("v"); // tNS.setId(params.get("v"));
                            break;
                        default:
                            _id = ""; // tNS.setId("");
                    }
                    tNS.setIdXML(tmpInt);
                    tNS.setLang(sLang);
                    tNS.setLangOriginal(sLangOrig);
                    tNS.setLangTranslated(sLangTrans);

                    tmpS = track.getName();
                    if ("track".equals(tmpS))
                    {
                        if ((tmpAtt = track.getAttribute("kind")) != null &&
                            (tmpS = tmpAtt.getValue()) != null &&
                            "asr".equals(tmpS))
                        {
                            tNS.setType(NetSubtitle.Tipus.YouTubeASRTrack);
                        } else
                        {
                            tmpAtt = track.getAttribute("name");
                            sName = tmpAtt.getValue();
                            tNS.setName(sName);
                            tNS.setType(NetSubtitle.Tipus.YouTubeTrack);
                        }
                        tNS.setTrack(true);
                        lTracks.add(tNS);
                    } else if ("target".equals(tmpS))
                    {
                        tNS.setType(NetSubtitle.Tipus.YouTubeTarget);
                        lTargets.add(tNS);
                    }
                }
            }
            i++;
        }
        
        resultat = new ArrayList<List<NetSubtitle>>();
        resultat.add(lTracks);
        resultat.add(lTargets);
            
        return resultat;
    }
    
    
    public InputStreamReader readURL(String s) throws MalformedURLException, IOException {
        URL url;
        InputStreamReader isr;
        String appName, appVersion;

        appName = java.util.ResourceBundle.getBundle("Bundle").getString("app.name");
        appVersion = java.util.ResourceBundle.getBundle("Bundle").getString("app.version");
        
        url = new URL(s);
        URLConnection urlconn = url.openConnection();
        urlconn.setRequestProperty("Accept",
            "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        urlconn.setRequestProperty("Accept-Charset",
            "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
        urlconn.setRequestProperty("User-Agent",
            "Mozilla/5.0 (compatible; " + appName + "/" + appVersion + ")");
        urlconn.connect();

        isr = new InputStreamReader(urlconn.getInputStream(), "UTF-8");
        
        return isr;
    }
    
    public String readURL(InputStreamReader isr) throws IOException {
        String s;
        
        StringWriter writer = new StringWriter();
        IOUtils.copy(isr, writer);
        s = writer.toString();
        
        return s;
    }

}
