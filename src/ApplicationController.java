import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Thread.sleep;

public class ApplicationController implements ActionListener, ItemListener {

    private final ApplicationGUI appGui;

    private final int numSets = 4;
    private final int setSize = 8;
    private List<List<Integer>> cache; // Using a List instead of a Map with Queues
    private int memoryAccessCount;
    private int cacheHitCount;
    private int cacheMissCount;

    private final int cacheBlockSize = 64; // Number of words in a cache block
    private Random random; // Random number generator for random replacement

    private int cbCheck = 0;

    private List<Integer> memoryAccesses;

    private String strTextFile;
    private StringBuilder sbTextFile;
    private String strFileName;

    public ApplicationController(ApplicationGUI appGui) {
        this.appGui = appGui;
        this.cache = new ArrayList<>(numSets);

        for (int i = 0; i < numSets; i++) {
            cache.add(new ArrayList<>(setSize));
        }

        this.memoryAccessCount = 0;
        this.cacheHitCount = 0;
        this.cacheMissCount = 0;
        this.strTextFile = "";
        this.strFileName = "";
        this.sbTextFile = new StringBuilder(strTextFile);

        appGui.disableDownload();

        this.random = new Random(); // Initialize the random number generator

        this.appGui.setVisible(true);
        appGui.setActionListener(this);
        appGui.setItemListener(this);
    }

    private void resetSimulation() {
        appGui.disableAll();
        appGui.clearStats();
        appGui.clearCache();
        appGui.setTaLogText("");
        appGui.setDownloadNotificationText("");

        memoryAccesses = null;
        memoryAccessCount = 0;
        cacheHitCount = 0;
        cacheMissCount = 0;
        strFileName = "";
        strTextFile = "";
        sbTextFile = new StringBuilder(strTextFile);

        cache = new ArrayList<>(numSets);

        for (int i = 0; i < numSets; i++) {
            cache.add(new ArrayList<>(setSize));
        }
        appGui.enableAll();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Sequential Sequence")) {
            resetSimulation();
            testCase1();
            appGui.enableAll();
        } else if (e.getActionCommand().equals("Random sequence")) {
            resetSimulation();
            testCase2();
            appGui.enableAll();
        } else if (e.getActionCommand().equals("Mid-repeat blocks")) {
            resetSimulation();
            testCase3();
        } else if (e.getActionCommand().equals("Submit")) {
            if (!appGui.getInputText().isEmpty()) {
                resetSimulation();
                testCase4();
            }
        } else if (e.getActionCommand().equals("Reset")) {
            resetSimulation();
            appGui.clearInput();
            appGui.disableDownload();
        } else if (e.getActionCommand().equals("Download Text Log")) {
            try {
                makeFile();
                appGui.setDownloadNotificationText("Downloaded Text Log as " + strFileName + "!");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        strTextFile = sbTextFile.toString();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        switch (e.getStateChange()) {
            case ItemEvent.DESELECTED -> cbCheck = 0;
            case ItemEvent.SELECTED -> cbCheck = 1;
        }

        System.out.println("checkbox: " + cbCheck);
    }

    private void testCase1() {
        memoryAccesses = generateSequentialSequence(setSize*numSets);
        strFileName = "Sequential_Sequence_Log.txt";

        switch (cbCheck) {
            case 0 -> runSimulation();
            case 1 -> processInBackground();
        }
    }

    private void testCase2() {
        memoryAccesses = generateRandomSequence(setSize*numSets);
        strFileName = "Random_Sequence_Log.txt";

        switch (cbCheck) {
            case 0 -> runSimulation();
            case 1 -> processInBackground();
        }
    }

    private void testCase3() {
        memoryAccesses = generateMidRepeatBlocksTest(setSize*numSets);
        strFileName = "Mid-Repeat_Blocks_Log.txt";

        switch (cbCheck) {
            case 0 -> runSimulation();
            case 1 -> processInBackground();
        }
    }

    private void testCase4() {
        memoryAccesses = generateCustomTest(appGui.getInputText());
        strFileName = "Custom_Sequence.txt";

        switch (cbCheck) {
            case 0 -> runSimulation();
            case 1 -> processInBackground();
        }
    }

    private void processInBackground() {

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                appGui.disableAll();
                System.out.println("Memory Accesses:");
                sbTextFile.append("Memory Accesses:\n");
                for (int memoryAddress : memoryAccesses) {
                    try {
                        sleep(25);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    accessData(memoryAddress);
                }

                System.out.println("\n\nCache Simulation Statistics:");
                sbTextFile.append("\n\nCache Simulation Statistics:\n");
                printCacheStats();
                appGui.enableAll();
                return null;
            }
        };

        worker.execute();
    }

    private void runSimulation() {
        System.out.println("Memory Accesses:");
        sbTextFile.append("Memory Accesses:\n");
        for (int memoryAddress : memoryAccesses) {
            accessData(memoryAddress);
        }

        System.out.println("\nCache Simulation Statistics:");
        sbTextFile.append("\nCache Simulation Statistics:\n");
        printCacheStats();
    }

    public List<Integer> generateSequentialSequence(int n) {
        List<Integer> sequence = new ArrayList<>(n * 2 * 4); // Repeat the sequence four times

        // Generate a sequential sequence up to 2n cache blocks
        for (int i = 0; i < 2 * n; i++) {
            sequence.add(i);
        }

        // Repeat the sequence four times
        for (int i = 0; i < 2; i++) {
            sequence.addAll(new ArrayList<>(sequence));
        }

        return sequence;
    }

    public List<Integer> generateRandomSequence(int size) {
        List<Integer> sequence = new ArrayList<>(size);
        Random random = new Random();

        for (int i = 0; i < size * 4; i++) {
            sequence.add(Math.abs(random.nextInt()) % 128);
        }

        return sequence;
    }

    public List<Integer> generateMidRepeatBlocksTest(int n) {
        List<Integer> sequence = new ArrayList<>(n * 7); // Repeat the sequence four times

        // First sequence: 0 to n-2 (since we want only until 30)
        for (int i = 0; i < n-1; i++) {
            sequence.add(i);
        }

        // Second sequence: 1 to n (30)
        for (int i = 1; i < n-1; i++) {
            sequence.add(i);
        }

        // Third sequence: 31 to 2n - 1
        for (int i = 31; i < 2 * n; i++) {
            sequence.add(i);
        }

        // Repeat the sequence four times
        for (int i = 0; i < 2; i++) {
            sequence.addAll(new ArrayList<>(sequence));
        }

        return sequence;
    }

    public List<Integer> generateCustomTest(String input) {
        List<Integer> sequence = new ArrayList<>();
        String[] strNums = input.split("[,]", 0);

        for (String strNum : strNums) {
            sequence.add(Integer.parseInt(strNum));
        }

        return sequence;
    }

    public void accessData(int memoryAddress) {
        memoryAccessCount++;
        int setIndex = memoryAddress % numSets;
        List<Integer> currentSetCache = cache.get(setIndex);

        if (currentSetCache.contains(memoryAddress)) {
            cacheHitCount++;
            String hit = memoryAddress + " is a HIT! Data found in: " + setIndex + ", " + currentSetCache.indexOf(memoryAddress) + "\n\n";
            System.out.println(hit);
            appGui.appendTaLogText(hit);
            sbTextFile.append(hit);
        } else {
            cacheMissCount++;

            if (currentSetCache.size() >= setSize) {
                // Randomly select an index to replace
                int indexToReplace = random.nextInt(setSize);
                int removedAddress = currentSetCache.set(indexToReplace, memoryAddress);
                String replace = memoryAddress + " is a MISS! Replacing "+ removedAddress + " in: " + setIndex + ", " + indexToReplace + "\n\n";
                System.out.println(replace);
                appGui.appendTaLogText(replace);
                sbTextFile.append(replace);
                appGui.setBlockValue(memoryAddress, setIndex+1, indexToReplace+1);
            } else {
                // Cache set is not full, just add the address
                currentSetCache.add(memoryAddress);
                String miss = memoryAddress + " is a MISS! Storing in: " + setIndex + ", " + (currentSetCache.size() - 1) + "\n\n";
                System.out.println(miss);
                appGui.appendTaLogText(miss);
                sbTextFile.append(miss);
                appGui.setBlockValue(memoryAddress, setIndex+1, currentSetCache.size());
            }
        }
    }

    public void printCacheStats() {
        double cacheHitRate = (double) cacheHitCount / memoryAccessCount * 100;
        double cacheMissRate = (double) cacheMissCount / memoryAccessCount * 100;
        double missPenalty = 11;
        double averageMemoryAccessTime = cacheHitRate * 1 + cacheMissRate * missPenalty; // hitRate*CacheAccessTime + (1-hitRate)*MissPenalty
        double totalMemoryAccessTime = (cacheHitCount*cacheBlockSize) + (cacheMissCount*cacheBlockSize*10) + cacheMissCount;


        System.out.println("1. Memory Access Count: " + memoryAccessCount);
        sbTextFile.append("1. Memory Access Count: " + memoryAccessCount + "\n");
        appGui.updateStat(0, memoryAccessCount);

        System.out.println("2. Cache Hit Count: " + cacheHitCount);
        sbTextFile.append("2. Cache Hit Count: " + cacheHitCount + "\n");
        appGui.updateStat(1, cacheHitCount);

        System.out.println("3. Cache Miss Count: " + cacheMissCount);
        sbTextFile.append("3. Cache Miss Count: " + cacheMissCount + "\n");
        appGui.updateStat(2, cacheMissCount);

        System.out.println("4. Cache Hit Rate: " + cacheHitRate + "%");
        sbTextFile.append("4. Cache Hit Rate: " + cacheHitRate + "%\n");
        appGui.updateStat(3, cacheHitRate);

        System.out.println("5. Cache Miss Rate: " + cacheMissRate + "%");
        sbTextFile.append("5. Cache Miss Rate: " + cacheMissRate + "%\n");
        appGui.updateStat(4, cacheMissRate);

        System.out.println("6. Average Memory Access Time: " + averageMemoryAccessTime + " ns");
        sbTextFile.append("6. Average Memory Access Time: " + averageMemoryAccessTime + " ns\n");
        appGui.updateStat(5, averageMemoryAccessTime);

        System.out.println("7. Total Memory Access Time: " + totalMemoryAccessTime + " ns");
        sbTextFile.append("7. Total Memory Access Time: " + totalMemoryAccessTime + " ns\n\n");
        appGui.updateStat(6, totalMemoryAccessTime);

        printCacheSnapshot();
    }

    private void printCacheSnapshot() {
        System.out.println("\nFinal Snapshot of Cache Memory:");
        strTextFile += "\nFinal Snapshot of Cache Memory:\n";

        for (int setIndex = 0; setIndex < numSets; setIndex++) {
            List<Integer> currentSetCache = cache.get(setIndex);
            System.out.println("Set " + setIndex + ":");
            sbTextFile.append("Set " + setIndex + ":\n");

            int blockNumber = 0;
            for (int i = 0; i < setSize; i++) {
                if (currentSetCache.size() > i) {
                    int block = currentSetCache.get(i);
                    System.out.print("| Block " + blockNumber + ": " + block + " ");
                    sbTextFile.append("| Block " + blockNumber + ": " + block + " ");
                } else {
                    System.out.print("| Block " + blockNumber + ":   ");
                    sbTextFile.append("| Block " + blockNumber + ":   ");
                }
                blockNumber++;
            }

            System.out.println("|\n-----------\n");
            sbTextFile.append("|\n-----------\n");
        }

    }

    private void makeFile() throws IOException {
        File txtFile = new File(strFileName);
        FileWriter fw = new FileWriter(txtFile);
        PrintWriter pw = new PrintWriter(fw);

        pw.println(sbTextFile);

        pw.close();
    }
}
