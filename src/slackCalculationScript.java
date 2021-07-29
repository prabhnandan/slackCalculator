import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
        int[] numProcessors = new int[]{2, 4, 8, 20};
        String[] priorities = {"blevel", "etf"};
        String[] placements = {"norm", "weighted-children-est"};
        String[] clusterers = {"dcp", "dsc"};
        String[] mergers = {"glb", "list"};
        String[] orderings = {"blevel", "est"};
        int progress = 1;

        for (File file : gxlFiles) {
            for (int proc : numProcessors) {
                for (String priority : priorities) {
                    for (String placement : placements) {
                        try {
                            rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f " + file.getAbsolutePath() + " -o " + file.getParent() + "/output/" + file.getName() + "_" + proc + "_" + priority + "_" + placement + ".gxl -p " + proc + " -priority " + priority + " -placement " + placement).waitFor();
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                for (String clusterer : clusterers) {
                    for (String merger : mergers) {
                        for (String ordering : orderings) {
                            try {
                                rt.exec("java -jar parcschedule-1.0-jar-with-dependencies.jar -f " + file.getAbsolutePath() + " -o " + file.getParent() + "/output/" + file.getName() + "_" + proc + "_" + clusterer + "_" + merger + "_" + ordering + ".gxl -p " + proc + " -clusterer " + clusterer + " -merger " + merger + " -ordering " + ordering).waitFor();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            System.out.println("Progress = "+progress +"/"+gxlFiles.size());
            progress++;
        }
    }
}
