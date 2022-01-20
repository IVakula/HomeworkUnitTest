package homework23;

public class APP {
    public static void main(String[] args) {
        ConcurrentCurrencyCalculator concurrentCurrencyCalculator = new ConcurrentCurrencyCalculator();
        concurrentCurrencyCalculator.start();
    }
}
