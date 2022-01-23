package homework23;

public class AverageResult {
    private double average;
    private  String currencyName;
    private boolean dateRangeFound;

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public boolean isDateRangeFound() {
        return dateRangeFound;
    }

    public void setDateRangeFound(boolean dateRangeNotFound) {
        this.dateRangeFound = dateRangeNotFound;
    }
}
