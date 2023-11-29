import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import static java.lang.Math.round;

class ApplicationGUI extends JFrame {

    JPanel panelCacheSnap = new JPanel(); // panel A in design
    JPanel panelLog = new JPanel(); // panel B in design
    JPanel panelTestCases = new JPanel(); // panel C in design
    JPanel panelStats = new JPanel(); // panel D in design
    JPanel panelMisc = new JPanel(); // panel E in design

    JTextArea taLog = new JTextArea();

    JTextArea tfInput = new JTextArea();

    JButton[] btnTestCases = new JButton[3]; // btn for each test case
    JButton btnReset = new JButton(); // btn to reset the gui
    JButton btnDownloadLog = new JButton(); // btn to download text log
    JButton btnSubmit = new JButton();

    JCheckBox cbTracing = new JCheckBox(); // checkbox to enable animated tracing

    JLabel lblInput = new JLabel();
    JLabel lblSeqLog = new JLabel();
    JLabel lblTestCases = new JLabel();
    JLabel lblStatsTitle = new JLabel();
    JLabel lblMisc = new JLabel();
    JLabel lblDownloadNotif = new JLabel();

    JLabel[] lblStats = new JLabel[7]; // text label for each stat
    JLabel[][] lblCacheBSA = new JLabel[5][9]; // 2D representation of the Cache including the labels for sets/blocks

    public ApplicationGUI() {
        super("Cache Simulation");
        setLayout(new BorderLayout());

        setSize(1500, 1300);

        screen();

        setResizable(true);
        setAlwaysOnTop(false);
        setVisible(true);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void screen() {
        initCacheSnap();

        initLogPanel();

        JPanel panelBottom = new JPanel();

        panelBottom.setLayout(new GridLayout(1,3));
        panelBottom.setBackground(Color.decode("#FFEBC1"));
        panelBottom.setBorder(new MatteBorder(0,2,2,2,Color.BLACK));
        panelBottom.setPreferredSize(new Dimension(1500, 300));

        initTestCasesPanel();
        initStatsPanel();
        initMiscPanel();

        panelBottom.add(panelTestCases);
        panelBottom.add(panelStats);
        panelBottom.add(panelMisc);

        this.add(panelBottom, BorderLayout.SOUTH);
    }



    private void initCacheSnap() {
        panelCacheSnap.setLayout(new GridLayout(5, 9));
        panelCacheSnap.setBackground(Color.decode("#FFEBC1"));
        panelCacheSnap.setBorder(new MatteBorder(2, 2, 2, 2, Color.BLACK));
        panelCacheSnap.setPreferredSize(new Dimension(950, 500));
        initCacheBlocks();

        this.add(panelCacheSnap, BorderLayout.CENTER);
    }

    private void initLogPanel() {
        panelLog.setLayout(new BorderLayout());
        panelLog.setBackground(Color.decode("#FFEBC1"));
        panelLog.setBorder(new MatteBorder(2, 0, 2, 2, Color.BLACK));
        panelLog.setPreferredSize(new Dimension(550, 500));

        lblSeqLog.setFont(new Font(lblSeqLog.getFont().getName(), Font.BOLD, 40));
        lblSeqLog.setBorder(new EmptyBorder(50,1,25,1));
        lblSeqLog.setHorizontalAlignment(SwingConstants.CENTER);
        lblSeqLog.setText("LOG");

        panelLog.add(lblSeqLog, BorderLayout.NORTH);

        taLog.setFont(new Font(taLog.getFont().getName(), Font.BOLD, 20));
        taLog.setLineWrap(true);
        taLog.setWrapStyleWord(true);
        taLog.setText("");
        taLog.setOpaque(false);
        taLog.setBorder(new EmptyBorder(50,100,50,100));
        taLog.setAutoscrolls(true);
        taLog.setEditable(false);
        taLog.setFocusable(false);

        DefaultCaret caret = (DefaultCaret)taLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane spTextLog = new JScrollPane(taLog);
        spTextLog.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spTextLog.setBorder(new EmptyBorder(0,0,0,0));
        spTextLog.setAutoscrolls(true);
        spTextLog.setBackground(Color.decode("#ffecb8"));
        spTextLog.setOpaque(false);
        spTextLog.getViewport().setOpaque(false);

        panelLog.add(spTextLog, BorderLayout.CENTER);

        this.add(panelLog, BorderLayout.EAST);
    }

    private void highlightLog(String text) {
        Highlighter highlighter = taLog.getHighlighter();
        Highlighter.HighlightPainter painter;

        if (text.contains("MISS!")) {
            painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);
            int p0 = taLog.getText().lastIndexOf("MISS!");
            int p1 = p0 + "MISS!".length();
            try {
                highlighter.addHighlight(p0, p1, painter);
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        } else if (text.contains("HIT!")) {
            painter = new DefaultHighlighter.DefaultHighlightPainter(Color.decode("#9CD3B1"));
            int p0 = taLog.getText().lastIndexOf("HIT!");
            int p1 = p0 + "HIT!".length();
            try {
                highlighter.addHighlight(p0, p1, painter);
            } catch (BadLocationException e) {
                throw new RuntimeException(e);
            }
        }


    }

    private void initTestCasesPanel() {

        panelTestCases.setLayout(new BorderLayout());
        panelTestCases.setBackground(Color.decode("#FFEBC1"));
        panelTestCases.setBorder(new MatteBorder(0,0,0,2,Color.BLACK));
        panelTestCases.setPreferredSize(new Dimension(750, 300));

        lblTestCases.setFont(new Font(lblTestCases.getFont().getName(), Font.BOLD, 40));
        lblTestCases.setBorder(new EmptyBorder(25, 1, 25, 1));
        lblTestCases.setHorizontalAlignment(SwingConstants.CENTER);
        lblTestCases.setText("TEST CASES");

        panelTestCases.add(lblTestCases, BorderLayout.NORTH);
        panelTestCases.add(initTestCaseBtns(), BorderLayout.CENTER);
        initInputPanel();
    }

    private void initInputPanel() {
        JPanel panelInput = new JPanel();
        panelInput.setLayout(new FlowLayout());
        panelInput.setBackground(Color.decode("#FFEBC1"));
        panelInput.setPreferredSize(new Dimension(750, 100));

        lblInput.setBorder(new EmptyBorder(15, 1, 15, 1));
        lblInput.setVisible(true);
        lblInput.setHorizontalAlignment(SwingConstants.LEFT);
        lblInput.setText("Custom Input: ");

        tfInput.setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
        tfInput.setVisible(true);
        tfInput.setFont(new Font(tfInput.getFont().getName(), Font.BOLD, 15));
        tfInput.setColumns(20);
        tfInput.setRows(3);
        tfInput.setLineWrap(true);
        tfInput.setWrapStyleWord(true);
        tfInput.setAutoscrolls(true);
        tfInput.setToolTipText("separate inputs via comma with no space");

        DefaultCaret caret = (DefaultCaret)tfInput.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane spInput = new JScrollPane(tfInput);
        spInput.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        spInput.setAutoscrolls(true);
        spInput.setBackground(Color.decode("#ffecb8"));
        spInput.setOpaque(false);
        spInput.getViewport().setOpaque(false);

        btnSubmit.setText("Submit");
        btnSubmit.setBorder(new EmptyBorder(10,10,10,10));
        btnSubmit.setBackground(Color.decode("#9CADD3"));

        panelInput.add(lblInput);
        panelInput.add(spInput);
        panelInput.add(btnSubmit);

        panelTestCases.add(panelInput, BorderLayout.SOUTH);
    }

    private void initStatsPanel() {
        panelStats.setLayout(new BorderLayout());
        panelStats.setBackground(Color.decode("#FFEBC1"));
        panelStats.setBorder(new MatteBorder(0,0,0,2,Color.BLACK));
        panelStats.setPreferredSize(new Dimension(800, 300));

        lblStatsTitle.setFont(new Font(lblTestCases.getFont().getName(), Font.BOLD, 40));
        lblStatsTitle.setBorder(new EmptyBorder(25, 1, 25, 1));
        lblStatsTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatsTitle.setText("CACHE STATS");

        panelStats.add(lblStatsTitle, BorderLayout.NORTH);

        initStatsLabels();
    }

    private void initMiscPanel() {
        panelMisc.setLayout(new BorderLayout());
        panelMisc.setBackground(Color.decode("#FFEBC1"));
        panelMisc.setBorder(new EmptyBorder(0,0,0,0));
        panelMisc.setPreferredSize(new Dimension(700, 300));

        lblMisc.setFont(new Font(lblTestCases.getFont().getName(), Font.BOLD, 40));
        lblMisc.setBorder(new EmptyBorder(25, 1, 25, 1));
        lblMisc.setHorizontalAlignment(SwingConstants.CENTER);
        lblMisc.setText("MISCELLANEOUS");

        panelMisc.add(lblMisc, BorderLayout.NORTH);
        panelMisc.add(lblDownloadNotif, BorderLayout.SOUTH);

        initMiscBtns();
    }

    private void initCacheBlocks() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                if (i != 0 && j != 0) {
                    lblCacheBSA[i][j] = new JLabel();
                    lblCacheBSA[i][j].setBorder(new MatteBorder(1,1,1,1, Color.BLACK));
                    lblCacheBSA[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                    lblCacheBSA[i][j].setText("");
                    lblCacheBSA[i][j].setOpaque(false);
                    panelCacheSnap.add(lblCacheBSA[i][j]);
                } else if (i == 0 && j != 0){
                    lblCacheBSA[i][j] = new JLabel();
                    lblCacheBSA[i][j].setBorder(new MatteBorder(1,1,1,1, Color.BLACK));
                    lblCacheBSA[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                    lblCacheBSA[i][j].setText("BLOCK " + Integer.toString(j-1));
                    lblCacheBSA[i][j].setOpaque(false);
                    panelCacheSnap.add(lblCacheBSA[i][j]);
                } else {
                    lblCacheBSA[i][j] = new JLabel();
                    lblCacheBSA[i][j].setBorder(new MatteBorder(1,1,1,1, Color.BLACK));
                    lblCacheBSA[i][j].setHorizontalAlignment(SwingConstants.CENTER);
                    if (i == 0) {
                        lblCacheBSA[i][j].setText("CACHE");
                    } else {
                        lblCacheBSA[i][j].setText("SET " + Integer.toString(i-1));
                    }

                    lblCacheBSA[i][j].setOpaque(false);
                    panelCacheSnap.add(lblCacheBSA[i][j]);
                }
                lblCacheBSA[i][j].setFont(new Font(lblCacheBSA[i][j].getFont().getName(), Font.BOLD, 20));
            }
        }
    }

    private JPanel initTestCaseBtns() {
        JPanel panelTCaseBtns = new JPanel();

        panelTCaseBtns.setLayout(new FlowLayout());
        panelTCaseBtns.setBackground(Color.decode("#FFEBC1"));

        for (int i = 0; i < 3; i++) {
            btnTestCases[i] = new JButton();
            switch (i) {
                case 0 -> btnTestCases[i].setText("Sequential Sequence");
                case 1 -> btnTestCases[i].setText("Random sequence");
                case 2 -> btnTestCases[i].setText("Mid-repeat blocks");
            }
            btnTestCases[i].setBorder(new EmptyBorder(10,10,10,10));
            btnTestCases[i].setBackground(Color.decode("#9CADD3"));
            panelTCaseBtns.add(btnTestCases[i]);
        }

        return panelTCaseBtns;
    }

    private void initStatsLabels() {
        JPanel panelStatsGrid = new JPanel();

        panelStatsGrid.setLayout(new GridLayout(4, 2));
        panelStatsGrid.setBackground(Color.decode("#FFEBC1"));
        panelStatsGrid.setBorder(new EmptyBorder(0,0,50,0));

        for (int i = 0; i < 7; i++) {
            lblStats[i] = new JLabel();
            switch (i) {
                case 0 -> lblStats[i].setText("Memory Access Count: ");
                case 1 -> lblStats[i].setText("Cache Hit Count: ");
                case 2 -> lblStats[i].setText("Cache Miss Count: ");
                case 3 -> lblStats[i].setText("Cache Hit Rate: ");
                case 4 -> lblStats[i].setText("Cache Miss Rate: ");
                case 5 -> lblStats[i].setText("Average Memory Access Time: ");
                case 6 -> lblStats[i].setText("Total Memory Access Time: ");
            }
            lblStats[i].setHorizontalAlignment(SwingConstants.CENTER);
            panelStatsGrid.add(lblStats[i]);
        }

        panelStats.add(panelStatsGrid, BorderLayout.CENTER);
    }

    private void initMiscBtns() {
        JPanel panelMiscBtns = new JPanel();

        panelMiscBtns.setLayout(new FlowLayout());
        panelMiscBtns.setBackground(Color.decode("#FFEBC1"));

        btnReset.setText("Reset");
        btnReset.setBorder(new EmptyBorder(10,10,10,10));
        btnReset.setBackground(Color.decode("#9CADD3"));
        panelMiscBtns.add(btnReset);

        btnDownloadLog.setText("Download Text Log");
        btnDownloadLog.setBorder(new EmptyBorder(10,10,10,10));
        btnDownloadLog.setBackground(Color.decode("#9CADD3"));
        panelMiscBtns.add(btnDownloadLog);

        cbTracing.setText("Tracing");
        cbTracing.setHorizontalAlignment(SwingConstants.CENTER);
        cbTracing.setOpaque(false);
        cbTracing.setBorder(new EmptyBorder(10,10,10,10));
        panelMiscBtns.add(cbTracing);

        panelMisc.add(panelMiscBtns, BorderLayout.CENTER);
    }

    public void setTaLogText(String text) {
        taLog.setText(text);
        revalidate();
        repaint();
    }

    public void appendTaLogText(String text) {
        taLog.append(text);
        highlightLog(text);
        revalidate();
        repaint();
    }

    public void setBlockValue(int value, int set, int block) {
//        System.out.println("value: " + value + "\nset: " + set + "\nblock: " + block);
        lblCacheBSA[set][block].setText(Integer.toString(value));
        revalidate();
        repaint();
    }

    public void clearCache() {
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 9; j++) {
                if (i != 0 && j != 0) {
                    lblCacheBSA[i][j].setText(" ");
                } else if (i == 0 && j != 0){
                    lblCacheBSA[i][j].setText("BLOCK " + Integer.toString(j-1));
                } else {
                    if (i == 0) {
                        lblCacheBSA[i][j].setText("CACHE");
                    } else {
                        lblCacheBSA[i][j].setText("SET " + Integer.toString(i-1));
                    }
                }
            }
        }
    }

    public void clearStats() {
        for (int i = 0; i < 7; i++) {
            switch (i) {
                case 0 -> lblStats[i].setText("Memory Access Count: ");
                case 1 -> lblStats[i].setText("Cache Hit Count: ");
                case 2 -> lblStats[i].setText("Cache Miss Count: ");
                case 3 -> lblStats[i].setText("Cache Hit Rate: ");
                case 4 -> lblStats[i].setText("Cache Miss Rate: ");
                case 5 -> lblStats[i].setText("Average Memory Access Time: ");
                case 6 -> lblStats[i].setText("Total Memory Access Time: ");
            }
        }
    }

    public void clearInput() {
        tfInput.setText("");
    }

    public String getInputText() {
        return tfInput.getText();
    }

    public void updateStat(int statIndex, double value) {
        switch (statIndex) {
            case 0 -> lblStats[statIndex].setText("Memory Access Count: " + round(value));
            case 1 -> lblStats[statIndex].setText("Cache Hit Count: " + round(value));
            case 2 -> lblStats[statIndex].setText("Cache Miss Count: " + round(value));
            case 3 -> lblStats[statIndex].setText("Cache Hit Rate: " + value + "%");
            case 4 -> lblStats[statIndex].setText("Cache Miss Rate: " + value + "%");
            case 5 -> lblStats[statIndex].setText("Average Memory Access Time: " + value + "ns");
            case 6 -> lblStats[statIndex].setText("Total Memory Access Time: " + value + "ns");
        }
        revalidate();
        repaint();
    }

    public void disableAll() {
        for (int cnt = 0; cnt < 3; cnt++) {
            btnTestCases[cnt].setEnabled(false);
        }

        btnSubmit.setEnabled(false);
        btnReset.setEnabled(false);
        disableDownload();

        tfInput.setEnabled(false);

        cbTracing.setEnabled(false);
    }

    public void enableAll() {
        for (int cnt = 0; cnt < 3; cnt++) {
            btnTestCases[cnt].setEnabled(true);
        }

        btnSubmit.setEnabled(true);
        btnReset.setEnabled(true);
        enableDownload();

        tfInput.setEnabled(true);

        cbTracing.setEnabled(true);
    }

    public void disableDownload() {
        btnDownloadLog.setEnabled(false);
    }

    public void enableDownload() {
        btnDownloadLog.setEnabled(true);
    }

    public void setDownloadNotificationText(String text) {
        lblDownloadNotif.setText(text);
    }

    public void setActionListener(ActionListener listener) {
        for (int cnt = 0; cnt < 3; cnt++) {
            btnTestCases[cnt].addActionListener(listener);
        }

        btnReset.addActionListener(listener);
        btnDownloadLog.addActionListener(listener);
        btnSubmit.addActionListener(listener);
    }

    public void setItemListener(ItemListener listener) {
        cbTracing.addItemListener(listener);
    }

}
