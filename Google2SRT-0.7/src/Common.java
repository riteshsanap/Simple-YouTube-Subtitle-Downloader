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

public class Common {
    
    enum tLanguage {ca, de, en, es, fr, it, pl, pt_BR, ru, zh_HanS, zh_HanT};
    
    public static boolean isSupportedLanguage(String language) {
        boolean isSupported = false;
        
        try {
            if (tLanguage.valueOf(language) != null) {
                isSupported = true;
            }
        } catch (Exception e) {
            isSupported = false;
        }
        
        return isSupported;
    }
    
    public static String getExtension(String filename) {
        String extension;
        int i;
        
        extension = null;
        if (filename == null)
            return null;
        i = filename.lastIndexOf(".");
        if ((i > 0) && (i < filename.length() - 1))
            extension = filename.substring(i+1).toLowerCase();
        
        return extension;
    }

    public static String removeExtension(String filename) {
        String noExtension;
        int i;
        
        noExtension = filename;
        i = filename.lastIndexOf(".");
        if ((i > 0) && (i < filename.length() - 1))
            noExtension = filename.substring(0, i);
        
        return noExtension;
    }
    
    public static String returnDirectory(String path) {
        return (path.endsWith(File.separator)) ? path : path + File.separator;
    }
    
    public static String removaInvalidFileNameChars(String filenameCandidate) {
        return (filenameCandidate != null) ?
                filenameCandidate.
                    replace("\\", "_").
                    replace("/", "_").
                    replace(":", "_").
                    replace("*", "_").
                    replace("?", "_").
                    replace("\"", "_").
                    replace("<", "_").
                    replace(">", "_").
                    replace("|", "_") : null;
    }
}
