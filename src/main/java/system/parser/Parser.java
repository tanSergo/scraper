package system.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public interface Parser {
    List<String> parseUrl(BufferedReader in, Parser parser, List<String> keywords) throws IOException;
}
