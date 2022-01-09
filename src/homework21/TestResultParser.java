package homework21;

import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestResultParser {
    public static List<TestResult> parse(String fileName) {
        File file = new File(fileName);
        return parse(file);
    }

    public static List<TestResult> parse(File file) {
        List<TestResult> testResults = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            TestResult result = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Starting time")) {
                    result = new TestResult();
                    testResults.add(result);
                    result.setStartTestTime(LocalDateTime.parse(line.split("=")[1].trim()));
                } else if (result != null) {
                    if (line.contains("tests found")) {
                        result.setTotalTestNumber(Integer.parseInt(line.replaceAll("[^0-9]", "")));
                    } else if (line.contains("tests successful")) {
                        result.setSuccessfulTestNumber(Integer.parseInt(line.replaceAll("[^0-9]", "")));
                    } else if (line.contains("tests failed")) {
                        result.setFailedTestNumber(Integer.parseInt(line.replaceAll("[^0-9]", "")));
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return testResults;
    }

    public static List<TestResult> parse(Path path) {
        return parse(path.toFile());
    }
}
