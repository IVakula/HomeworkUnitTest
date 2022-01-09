package homework21;


import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

import java.io.*;
import java.time.LocalDateTime;

public class TestRunner {

    private final SummaryGeneratingListener listener = new SummaryGeneratingListener();

    public <T> void runTestByClassName(Class<T> aClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(aClass))
                .build();

        launchRequest(request);

    }

    public void runTestByClassNames(Class<?>... aClass) {
        for (Class<?> clazz : aClass) {
            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(clazz))
                    .build();

            launchRequest(request);
        }
    }

    public void runTestByClassName(String className) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectClass(className))
                .build();

        launchRequest(request);
    }

    public void runTestByClassNames(String... classNames) {
        for (String className : classNames) {

            LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                    .selectors(DiscoverySelectors.selectClass(className))
                    .build();

            launchRequest(request);
        }
    }

    public void runTestByPackageName(String packagePath) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectPackage(packagePath))
                .build();

        launchRequest(request);
    }

    private void launchRequest(LauncherDiscoveryRequest request) {
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        summary.printTo(new PrintWriter(System.out));
        try {
            PrintWriter printWriter = new PrintWriter(new FileOutputStream("test.log", true));
            printWriter.println("Starting time = " + LocalDateTime.now());
            summary.printTo(printWriter);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
