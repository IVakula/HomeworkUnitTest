package homework21;

import java.time.LocalDateTime;


public class TestResult {
    private int totalTestNumber;
    private int successfulTestNumber;
    private int failedTestNumber;
    private LocalDateTime startTestTime;

    public int getTotalTestNumber() {
        return totalTestNumber;
    }

    public void setTotalTestNumber(int totalTestNumber) {
        this.totalTestNumber = totalTestNumber;
    }

    public int getSuccessfulTestNumber() {
        return successfulTestNumber;
    }

    public void setSuccessfulTestNumber(int successfulTestNumber) {
        this.successfulTestNumber = successfulTestNumber;
    }

    public int getFailedTestNumber() {
        return failedTestNumber;
    }

    public void setFailedTestNumber(int failedTestNumber) {
        this.failedTestNumber = failedTestNumber;
    }

    public LocalDateTime getStartTestTime() {
        return startTestTime;
    }

    public void setStartTestTime(LocalDateTime startTestTime) {
        this.startTestTime = startTestTime;
    }

    @Override
    public String toString() {
        return "TestResult{" +
                "totalTestNumber=" + totalTestNumber +
                ", successfulTestNumber=" + successfulTestNumber +
                ", failedTestNumber=" + failedTestNumber +
                ", startTestTime=" + startTestTime +
                '}' + "\n";
    }
}
