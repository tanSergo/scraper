package system.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullTextParser implements Parser {

    @Override
    public List<String> parseUrl(BufferedReader in, Parser parser, List<String> keywords) throws IOException {
        List<String> sentences = new ArrayList<>();
        String inputLine;
        String text = "";
        while ((inputLine = in.readLine()) != null) {
            text = text.concat(inputLine);
        }

        String regex = "[A-Z][[\\w|,|']*| ]*[.!?] ";
        Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher m = p.matcher(text);
        while (m.find()) {
            String s = m.group();
            for (String keyword : keywords) {
                if (s.contains(" " + keyword + " ")) {
                    sentences.add(s);
                }
            }
        }
        return sentences;
    }
}
