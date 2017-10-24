package parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FullTextParser implements Parser {
    @Override
    public List<String> parseString(String inputLine, List<String> keywords) {
        return null;
    }

    @Override
    public List<String> parseUrl(BufferedReader in, Parser parser, List<String> keywords) throws IOException {
        List<String> sequences = new ArrayList<>();
        List<String> regularExpressions = new ArrayList<>();
        String inputLine;
        String text = "";
        while ((inputLine = in.readLine()) != null) {
            text = text.concat(inputLine);
        }
        System.out.println(text);
        String code_pattern = "\\s//:((//:~){0}|.|\\s)*//:~\\s";
//        text = text.replaceAll(code_pattern, "");

//        System.out.println("\n----- Code removed -----\n");
//        System.out.println(text);
        String regex = "[A-Z][[\\w|,|']*| ]*[.!?]";
        String regex1 = "(^|(?<=[.!?]\\s))(\\d+\\.\\s?)*[А-ЯA-Z][^!?]*?[.!?](?=\\s*(\\d+\\.\\s)*[А-ЯA-Z]|$)";
        Pattern p = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher m = p.matcher(text);
        System.out.println("\n----- Parsed -----\n");
        while (m.find()) {
//            System.out.println(m.group());
            String s = m.group();
            sequences.add(s);
        }
        return sequences;
    }
}
