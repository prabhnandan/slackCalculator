import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Objects;

public class slackCalculationScript {

    public static void main(String[] args) {

        multipleScheduleCalculate("src/");
    }

    private static void multipleScheduleCalculate(String folderPath) {
        // Create filter for gxl files
        FilenameFilter gxl = (f, name) -> name.endsWith("gxl");
        // Get GXL Files in folder
        File[] gxlFiles = Objects.requireNonNull(new File(folderPath).listFiles(gxl));
        // Calculate Slack for each file
        Runtime rt = Runtime.getRuntime();
        int[] numProcessors = new int[]{2, 4, 8, 20, 50};
        String[] priorities = {"blevel", "dls", "etf", "mcp", "cp-tlevel", "cp-critical-parent"};
        String[] placements = {"norm", "latest-children-est", "weighted-children-est", "critical-child-est", "cpop"};
        String[] clusterers = {"dcp", "dsc", "lc"};
        String[] mergers = {"glb", "list"};
        String[] orderings = {"blevel", "est"};
        float totalCombinations = gxlFiles.length * numProcessors.length* priorities.length* placements.length;
        float progress = 0;
        for (File file : gxlFiles) {
            for (int proc : numProcessors) {
                for (String priority : priorities) {
                    for (String placement : placements) {
                        System.out.printf("Progress = %.2f %%\n",progress*100/totalCombinations);
                        progress++;
                        try {
                            Process process = rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f " + file.getAbsolutePath() + " -o out.gxl -p " + proc + " -priority "+priority+" -placement "+placement);

                            process.waitFor();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
//                for(String clusterer: clusterers){
//                    for(String merger: mergers ){
//                        for (String ordering: orderings){
//                            try {
//                                Process process = rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f " + file.getAbsolutePath() + " -o out.gxl -p 5 -priority blevel -placement latest-children-est -no-insertion");
//                                process.waitFor();
//                            } catch (IOException | InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
            }
        }
    }
}
