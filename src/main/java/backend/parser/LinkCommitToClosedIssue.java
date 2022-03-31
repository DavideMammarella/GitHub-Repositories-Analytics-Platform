package backend.parser;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkCommitToClosedIssue {

    private LinkCommitToClosedIssue() { }


    /**
     * Match regular expressions in the commit body to identify the commit fixing a closed issue. (Item 3 of the assignment)
     *
     * @param commitMessage commit message body (String)
     * @param repositoryUrl repository url (String)
     * @return numbers of the issues fixed (Set), empty set otherwise
     */
    public static Set<Integer> fixCloseResolveFinder(String commitMessage, String repositoryUrl) {
        final String regex = "(fix(es|ed)?|(close|resolve)[sd]?)\\s+(#\\d+|https://(www\\.)?github\\.com/" + repositoryUrl + "/issues/\\d+)";

        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(commitMessage);

        Set<Integer> issuesNumbers = new HashSet<>();

        while (matcher.find()) {
            String result = matcher.group();
            Pattern numberPattern = Pattern.compile("\\d+");
            Matcher numberMatcher = numberPattern.matcher(result);

            Integer number = null;
            while (numberMatcher.find()) {
                number = Integer.parseInt(numberMatcher.group());
            }

            issuesNumbers.add(number);
        }

        return issuesNumbers;
    }
}
