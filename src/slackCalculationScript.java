import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class slackCalculationScript {

    public static void main(String[] args) {

        multipleScheduleCalculate("src/graphs/");
    }

    private static void multipleScheduleCalculate(String folderPath) {
        // Get GXL Files in folder
        File[] graphTypes = new File(folderPath).listFiles();
        ArrayList<File> gxlFiles = new ArrayList<>();
        for (File file : graphTypes) {
            gxlFiles.addAll(Arrays.asList(file.listFiles()));
        }
        // Calculate Slack for each file
        Runtime rt = Runtime.getRuntime();
        int[] numProcessors = new int[]{2, 4, 8, 20, 50};
        String[] priorities = {"blevel", "dls", "etf", "mcp", "cp-tlevel", "cp-critical-parent"};
        String[] placements = {"norm", "latest-children-est", "weighted-children-est", "critical-child-est", "cpop"};
        String[] clusterers = {"dcp", "dsc", "lc"};
        String[] mergers = {"glb", "list"};
        String[] orderings = {"blevel", "est"};
        float totalCombinations = gxlFiles.size() * numProcessors.length * priorities.length * placements.length;
        float progress = 0;
        for (File file : gxlFiles) {
            for (int proc : numProcessors) {
                for (String priority : priorities) {
                    for (String placement : placements) {
                        System.out.printf("Progress = %.6f %%\n", progress * 100 / totalCombinations);
                        progress++;
                        try {
                            Process process = rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f " + file.getAbsolutePath() + " -o " + file.getParent() + "/output/" + file.getName() + "_" + proc + "_" + priority + "_" + placement + ".gxl -p " + proc + " -priority " + priority + " -placement " + placement);

                            process.waitFor();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                for (String clusterer : clusterers) {
                    for (String merger : mergers) {
                        for (String ordering : orderings) {
                            try {
                                Process process = rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f " + file.getAbsolutePath() + " -o " + file.getParent() + "/output/" + file.getName() + "_" + proc + "_" + clusterer + "_" + merger + "_" + ordering + ".gxl -p " + proc + " -clusterer " + clusterer + " -merger " + merger + " -ordering " + ordering);
                                process.waitFor();
                            } catch (IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }
}
