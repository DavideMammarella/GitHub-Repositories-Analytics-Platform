package backend.metrics;

import backend.model.Commit;
import com.github.mauricioaniche.ck.CK;
import liquibase.util.file.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MetricsComputer {

    private MetricsComputer() { }

    private static final String RESOURCES = "src/main/resources";

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsComputer.class);

    public static void computeMetricsDifference(Commit commit) {
        try {
            createTmpDir();
            Set<String> modifiedFiles = commit.getModifiedFiles()
                    .stream()
                    .filter(x -> FilenameUtils.getExtension(x).equals("java"))
                    .collect(Collectors.toSet());

            gitCheckout(modifiedFiles, commit.getHash());
            copyFiles(modifiedFiles);

            Map<String, Double> commitMetrics = computeMetrics();

            gitCheckout(modifiedFiles, commit.getHash() + "^");
            copyFiles(modifiedFiles);

            Map<String, Double> commitBeforeMetrics = computeMetrics();
            Map<String, Double> commitDiffMetrics = computeDiffMetrics(commitBeforeMetrics, commitMetrics);

            commit.setAverageCboDifference(commitDiffMetrics.get("cbo"));
            commit.setAverageLocDifference(commitDiffMetrics.get("loc"));
            commit.setAverageWmcDifference(commitDiffMetrics.get("wmc"));
            commit.setAverageLcomDifference(commitDiffMetrics.get("lcom"));

            cleanTmpDir();
        } catch (InterruptedException | IOException e) {
            Thread.currentThread().interrupt();
            LOGGER.error(e.getMessage());
        }
    }

    private static void gitCheckout(Set<String> files, String commitHash) throws IOException, InterruptedException {
        for (String file : files) {
            String postCommit = "git checkout " + commitHash + " " + file;
            Process pr = Runtime.getRuntime()
                    .exec(postCommit, null, new File(RESOURCES + "/repositories"));
            pr.waitFor();
        }
    }

    private static void copyFiles(Set<String> files) throws IOException, InterruptedException {
        for (String file : files) {
            String mkdir = "mkdir -p tmp/" + file;
            String cp = "cp " + file + " ../tmp/" + file;

            Process mkdirProcess = Runtime.getRuntime()
                    .exec(mkdir, null, new File(RESOURCES));
            mkdirProcess.waitFor();

            Process cpProcess = Runtime.getRuntime()
                    .exec(cp, null, new File(RESOURCES + "/repositories"));
            cpProcess.waitFor();
        }
    }

    private static Map<String, Double> computeMetrics() throws IOException {
        String inputDir = RESOURCES + "/tmp";

        Map<String, Double> results = new HashMap<>();
        AtomicInteger counter = new AtomicInteger();

        new CK().calculate(inputDir, result -> {
            results.merge("cbo", (double) result.getCbo(), Double::sum);
            results.merge("loc", (double) result.getLoc(), Double::sum);
            results.merge("wmc", (double) result.getWmc(), Double::sum);
            results.merge("lcom", (double) result.getLcom(), Double::sum);

            counter.incrementAndGet();
        });

        results.put("cbo", results.getOrDefault("cbo", 0D) / counter.doubleValue());
        results.put("loc", results.getOrDefault("loc", 0D) / counter.doubleValue());
        results.put("wmc", results.getOrDefault("wmc", 0D) / counter.doubleValue());
        results.put("lcom", results.getOrDefault("lcom", 0D) / counter.doubleValue());

        return results;
    }

    private static Map<String, Double> computeDiffMetrics(Map<String, Double> before, Map<String, Double> after) {
        Map<String, Double> diff = new HashMap<>();

        diff.put("cbo", (after.get("cbo") - before.get("cbo")) / before.get("cbo"));
        diff.put("loc", (after.get("loc") - before.get("loc")) / before.get("loc"));
        diff.put("wmc", (after.get("wmc") - before.get("wmc")) / before.get("wmc"));
        diff.put("lcom", (after.get("lcom") - before.get("lcom")) / before.get("lcom"));

        return diff;
    }

    private static void createTmpDir() throws IOException, InterruptedException {
        String rm = "mkdir tmp";

        Process pr = Runtime.getRuntime().exec(rm, null, new File(RESOURCES));
        pr.waitFor();
    }

    private static void cleanTmpDir() throws IOException, InterruptedException {
        String rm = "rm -rf tmp";

        Process pr = Runtime.getRuntime().exec(rm, null, new File(RESOURCES));
        pr.waitFor();
    }
}