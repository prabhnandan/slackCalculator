import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.apache.commons.io.FileUtils.listFiles;

public class slackCalculationScript {

    public static void main(String[] args) {

        multipleScheduleCalculate("src/graphs/BenchmarkSet");
        multipleScheduleCalculate("src/graphs/listSchedVSclusterSched");
    }

    private static void multipleScheduleCalculate(String folderPath) {
        // Get GXL Files in folder
        ArrayList<File> gxlFiles;
        gxlFiles = (ArrayList<File>) listFiles(new File(folderPath), new String[] {"gxl"},true);

        // Calculate Slack for each file
        Runtime rt = Runtime.getRuntime();
        int[] numProcessors = new int[]{2, 4, 8, 20};
        String[] priorities = {"blevel", "etf"};
        String[] placements = {"norm", "weighted-children-est"};
        String[] clusterers = {"dcp", "dsc"};
        String[] mergers = {"glb", "list"};
        String[] orderings = {"blevel", "est"};
        int progress = 1;

        for (File file : gxlFiles) {
            long startTime = System.currentTimeMillis();
            for (int proc : numProcessors) {
                for (String priority : priorities) {
                    for (String placement : placements) {
                        String outputFileName = "output/" + file.getName() + proc + priority + placement + ".gxl";
                        if (!new File(outputFileName).exists()) {
                            try {
                                rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f " + file.getAbsolutePath() + " -o " + outputFileName + " -p " + proc + " -priority " + priority + " -placement " + placement).waitFor();
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                for (String clusterer : clusterers) {
                    for (String merger : mergers) {
                        for (String ordering : orderings) {
                            String outputFileName = "output/" + file.getName() + proc + clusterer + merger + ordering + ".gxl";
                            if (!new File(outputFileName).exists()) {
                                try {
                                    rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f " + file.getAbsolutePath() + " -o " + outputFileName + " -p " + proc + " -clusterer " + clusterer + " -merger " + merger + " -ordering " + ordering).waitFor();
                                } catch (InterruptedException | IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
            System.out.println("Progress = "+progress +"/"+gxlFiles.size() + " Time Elapsed: " + (System.currentTimeMillis() - startTime)/1000);
            progress++;
        }
    }
}
