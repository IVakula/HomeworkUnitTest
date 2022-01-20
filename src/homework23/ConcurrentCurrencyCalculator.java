package homework23;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class ConcurrentCurrencyCalculator {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public void start() {
        boolean codeFound;
        do {
            codeFound = startProgramme();

        } while (!codeFound);
        System.exit(0);
    }

    private boolean startProgramme() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter path to currency data files: ");
        String path = scanner.nextLine();

        System.out.println("Please enter currency code: (print \"exit\" to exit programme).");

        String line = scanner.nextLine();
        if (line.equals("exit")) {
            System.exit(0);
        }
        System.out.println("Please enter starting date (dd.MM.yyyy): or press enter to skip date range.");
        String startDateString = scanner.nextLine();
        String endDateString;
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (!startDateString.isEmpty()) {
            System.out.println("Please enter end date (dd.MM.yyyy): ");
            endDateString = scanner.nextLine();
            startDate = LocalDate.parse(startDateString.trim(), formatter);
            endDate = LocalDate.parse(endDateString.trim(), formatter);
        }

        AverageResult averageResult = null;
        try {
            averageResult = readFile(path.trim(),line.trim(), startDate, endDate);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(100);
        } catch (CodeNotFoundException e) {
            return false;
        }
        if (averageResult != null) {
            System.out.printf("Average value UAH [Ukrainian Hryvnia] to %1$s [%2$s] -> %3$f%n",
                    line.trim(), averageResult.getCurrencyName(), averageResult.getAverage());
            return true;
        }
        return false;
    }

    private AverageResult readFile(String path, String currencyCode, LocalDate startDate, LocalDate endDate) throws IOException, CodeNotFoundException {
        List<List<String>> filesToProcess = getFilesToProcess(path);
        AverageResult result = null;
        if (!filesToProcess.isEmpty()) {
            AtomicReference<String> currencyName = new AtomicReference<>();
            ExecutorService executorService = Executors.newFixedThreadPool(3);
            List<Callable<AverageResult>> callables = new ArrayList<>();
            for (List<String> toProcess : filesToProcess) {
                callables.add(createTask(toProcess, currencyName, currencyCode, startDate, endDate));
            }
            List<Future<AverageResult>> futures = new ArrayList<>();
            try {
                futures.addAll(executorService.invokeAll(callables));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<AverageResult> averages = new ArrayList<>();
            for (Future<AverageResult> f : futures) {
                try {
                    averages.add(f.get());
                } catch (ExecutionException e) {
                    throw new CodeNotFoundException();
                } catch (InterruptedException e) {
                    // Ignore exception
                }
            }
            result = averages.stream().filter(Objects::nonNull).reduce((a, b) -> {
                b.setAverage(b.getAverage() + a.getAverage());
                return b;
            }).orElse(new AverageResult());

            if (futures.size() != 0) {
                result.setAverage(result.getAverage() / (double) futures.size());
            }
        }
        return result;
    }

    private List<List<String>> getFilesToProcess(String path) {
        List<List<String>> result = new ArrayList<>();
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(pathname -> pathname.getName().endsWith(".csv"));
            if (files != null) {
                int potionNumber = files.length / 30;
                if (files.length % 30 > 0) {
                    potionNumber++;
                }
                for (int i = 0; i < potionNumber; i++) {
                    File[] subArray = Arrays.copyOfRange(files, i * 30, i * 30 + 30);

                    result.add(Arrays.stream(subArray)
                                    .filter(Objects::nonNull)
                            .map(File::getAbsolutePath).collect(Collectors.toList()));
                }
            }
        }
        return result;
    }

    private Callable<AverageResult> createTask(List<String> files,
                                               AtomicReference<String> currencyName,
                                               String currencyCode,
                                               LocalDate startDate,
                                               LocalDate endDate) {

        return () -> {
            double sum = 0d;
            int fileCounter = 0;
            AverageResult averageResult = new AverageResult();
            for (String file : files) {

                boolean fileContainsCode = false;

                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    bufferedReader.readLine();

                    String line;
                    boolean dateInRange = false;
                    LocalDate currentDate = null;
                    do {
                        line = bufferedReader.readLine();
                        if (line != null) {
                            String[] lineData = line.split("\\|");
                            if (lineData.length == 7) {
                                if (currentDate == null) {
                                    currentDate = LocalDate.parse(lineData[0].trim(), formatter);
                                    dateInRange = isDateInRange(currentDate, startDate, endDate);
                                }
                                if (dateInRange) {
                                    if (lineData[3].equals(currencyCode)) {
                                        sum += Double.parseDouble(lineData[6]);
                                        fileContainsCode = true;
                                        fileCounter++;
                                        if (currencyName != null && currencyName.get() == null) {
                                            currencyName.set(lineData[5]);
                                        }
                                    }
                                }
                            }
                        }
                    } while (dateInRange && line != null && !fileContainsCode);
                }
            }
            averageResult.setAverage(sum / (double) fileCounter);
            if (currencyName != null) {
                averageResult.setCurrencyName(currencyName.get());
            }
            return averageResult;
        };
    }

    private boolean isDateInRange(LocalDate currentDate, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return true;
        }
        return currentDate.equals(startDate) || currentDate.equals(endDate)
                || (currentDate.isAfter(startDate) && currentDate.isBefore(endDate));
    }
}
