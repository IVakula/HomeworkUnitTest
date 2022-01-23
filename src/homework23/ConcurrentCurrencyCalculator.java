package homework23;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


public class ConcurrentCurrencyCalculator {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    Scanner scanner = new Scanner(System.in);

    public void start() {
        System.out.println("Please enter path to currency data files: (print \"exit\" to exit programme).");
        String path = scanner.nextLine();
        checkExit(path);

        boolean codeFound;
        do {
            codeFound = startProgramme(path);

        } while (!codeFound);
        System.exit(0);
    }

    private boolean startProgramme(String path) {
        System.out.println("Please enter currency code: (print \"exit\" to exit programme).");
        String line = scanner.nextLine();
        checkExit(line);

        boolean skipDate=false;
        boolean correctRange=false;
        LocalDate startDate = null;
        LocalDate endDate = null;
        String startDateString;
        do { //range loop
            do { //start date loop

                System.out.println("Please enter starting date (dd.MM.yyyy): or press enter to skip date range.");
                startDateString = scanner.nextLine();
                checkExit(startDateString);
                if (startDateString.isEmpty()) {
                    skipDate = true;
                } else {
                    startDate = tryToParseDate(startDateString);
                }
            } while (!skipDate && startDate == null);

            String endDateString;
            if (!startDateString.isEmpty()) {
                do { //end date loop

                    System.out.println("Please enter end date (dd.MM.yyyy): ");
                    endDateString = scanner.nextLine();
                    checkExit(endDateString);
                    endDate = tryToParseDate(endDateString);
                } while (endDate == null);
                if(!checkRange(startDate,endDate)){
                    System.out.println("Start date must be less or equal end date");
                } else{
                    correctRange=true;
                }
            }
        } while(!skipDate&&!correctRange);

        AverageResult averageResult = null;
        try {
            averageResult = readFile(path.trim(), line.trim(), startDate, endDate);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(100);
        } catch (CodeNotFoundException e) {
            return false;
        }
        if (averageResult != null) {
            if(!averageResult.isDateRangeFound()){
                System.out.println("No data for selected date range");
                return true;
            }
            if(averageResult.getCurrencyName() == null || averageResult.getCurrencyName().isEmpty()){
                System.out.println("Currency code not found.");
                return false;
            }
            System.out.printf("Average value UAH [Ukrainian Hryvnia] to %1$s [%2$s] -> %3$f%n",
                    line.trim(), averageResult.getCurrencyName(), averageResult.getAverage());
            return true;
        }
        System.out.println("Data files not found.");
        return true;
    }

    private boolean checkRange(LocalDate startDate, LocalDate endDate) {
        return startDate.isBefore(endDate)||startDate.equals(endDate);
    }

    private LocalDate tryToParseDate(String dateString) {
        try {
            return LocalDate.parse(dateString.trim(), formatter);
        } catch(DateTimeParseException e){
            System.out.println("Invalid date format.");
            return null;
        }
    }

    private void checkExit(String line) {
        if (line.equals("exit")) {
            System.exit(0);
        }
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

                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
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
                                    averageResult.setDateRangeFound(true);
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
