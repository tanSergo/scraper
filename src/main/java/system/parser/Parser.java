package parser;

import java.util.List;

/**
 * Created by Sergo on 17.10.2017.
 */
public interface Parser {
    List<String> parseString(String inputLine, List<String> keywords);
}
