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

import java.util.List;
import javax.swing.table.AbstractTableModel;

public class TableModel extends AbstractTableModel {
        private final String[] columnNamesWithTrackNames = {"Convert?", "Language code", "Language name", "Track name", "Video", "Title"};
        private final String[] columnNamesWithoutTrackNames = {"Convert?", "Language code", "Language name"};
        private java.util.ResourceBundle bundle;
        private boolean isWithTrackNames;


        private Object data[][];
        List<NetSubtitle> list;

        public TableModel(java.util.ResourceBundle bundle, boolean withTrackNames) {
            this.list = null;
            this.data = null;
            this.bundle = bundle;
            this.isWithTrackNames = withTrackNames;
        }

        public Object[][] getData() {
            return this.data;
        }

        public void setData(Object[][] dades) {
            this.data = dades;
            fireTableDataChanged();
        }

        public void setBundle(java.util.ResourceBundle bundle, javax.swing.JTable jt) {
            this.bundle = bundle;
            updateColumnNames(jt);
        }
        
        private void updateColumnNames(javax.swing.JTable jt) {
           javax.swing.table.TableColumnModel tcm;
           javax.swing.table.TableColumn tc;
           
           tcm = jt.getColumnModel();
           
           for (int i = 0; i < tcm.getColumnCount(); i++) {
               tc = tcm.getColumn(i);
               tc.setHeaderValue(getColumnName(i));
           }
        }

        public void init(List<NetSubtitle> subList) {
            int i;
            NetSubtitle ns;
            String s;
            NetSubtitle.Tipus t;

            this.list = subList;
            
            if (this.isWithTrackNames)
                this.data = new Object[subList.size()][columnNamesWithTrackNames.length];
            else
                this.data = new Object[subList.size()][columnNamesWithoutTrackNames.length];

            
            if (this.list.size() == 1) {
                ns = this.list.get(0);
                this.data[0][0] = true;                 // checkbox
                this.data[0][1] = ns.getLang();         // lang code
                this.data[0][2] = ns.getLangOriginal(); // lang name

                
                if (this.isWithTrackNames) {
                    s = ns.getName();              // track name (optional)
                    if (! s.isEmpty())
                        this.data[0][3] = ns.getName();
                    else
                    {
                        t = ns.getType();
                        switch(t)
                        {
                            case YouTubeASRTrack:
                                this.data[0][3] = "[ASR]";
                                break;
                            case YouTubeTarget:
                                this.data[0][3] = "[TARGET]";
                                break;
                            default:
                                this.data[0][3] = s;
                        }
                    }
                    this.data[0][4] = ns.getId();           // video ID
                    this.data[0][5] = ns.getTitle();        // video title
                }
            } else
                for (i = 0; i < this.list.size(); i++) {
                    ns = this.list.get(i);

                    this.data[i][0] = false;                // checkbox
                    this.data[i][1] = ns.getLang();         // lang code
                    this.data[i][2] = ns.getLangOriginal(); // lang name
                    
                    if (this.isWithTrackNames) {
                        s = ns.getName();              // track name (optional)
                        if (! s.isEmpty())
                            this.data[i][3] = ns.getName();
                        else
                        {
                            t = ns.getType();
                            switch(t)
                            {
                                case YouTubeASRTrack:
                                    this.data[i][3] = "[ASR]";
                                    break;
                                case YouTubeTarget:
                                    this.data[i][3] = "[TARGET]";
                                    break;
                                default:
                                    this.data[i][3] = s;
                            }
                        }

                        this.data[i][4] = ns.getId();           // video ID
                        this.data[i][5] = ns.getTitle();        // video title
                    }
                }
            
            fireTableDataChanged();
        }

        public void init(List<NetSubtitle> list, Object[][] data) {
            this.list = list;
            this.data = data;
            
            fireTableDataChanged();
        }
        
        public void clear() {
            init(new java.util.ArrayList<NetSubtitle>());
        }

      
        @Override
        public Class getColumnClass(int columnIndex) {
            if (columnIndex == 0) return java.lang.Boolean.class;
            else return java.lang.String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 0) return true;
            else return false;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        @Override
         public String getColumnName(int col) {
            String s;
            try { s = bundle.getString("GUI.jtTrackList.columnName." + col); }
            catch (Exception e) { s = ""; }
            return s;
         }


        public int getRowCount() {
            if (data == null) return 0;
            return data.length;
        }

        public int getColumnCount() {
            if (this.isWithTrackNames)
                return columnNamesWithTrackNames.length;
            else
                return columnNamesWithoutTrackNames.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return data[rowIndex][columnIndex];
        }
    }