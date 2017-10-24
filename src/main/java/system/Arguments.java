package system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Arguments {
    private List<String> urls = new ArrayList<String>();
    private List<String> keywords = new ArrayList<String>();
    private Boolean verbose = false;
    private Boolean wordsCount = false;
    private Boolean charactersCount = false;
    private Boolean extractSentences = false;

    public List<String> getUrls() {
        return urls;
    }


    public List<String> getKeywords() {
        return keywords;
    }

    public Boolean getVerbose() {
        return verbose;
    }

    public Boolean getWordsCount() {
        return wordsCount;
    }

    public Boolean getCharactersCount() {
        return charactersCount;
    }

    public Boolean getExtractSentences() {
        return extractSentences;
    }

    @Override
    public String toString() {
        return "Arguments{" +
                "urls=" + urls +
                ", keywords=" + keywords +
                ", verbose=" + verbose +
                ", wordsCount=" + wordsCount +
                ", charactersCount=" + charactersCount +
                ", extractSentences=" + extractSentences +
                '}';
    }

    public Arguments(String[] args) {
        /*Extracting parameters*/
        if (args.length == 0) System.out.println("No args");
        for (int i = 0; i < args.length; i++) {
//            System.out.println(args[i]);
            if (i==0) {
                if (isUrl(args[i])) {
                    urls.add(args[i]);
                } else  {
                    takeLinks(args[i]);
                }
            }
            if (i==1) {
                String[] words = args[i].split(",");
                keywords = Arrays.asList(words);
            }
            if (args[i].equals("-v"))
                verbose = true;
            if (args[i].equals("-w"))
                wordsCount = true;
            if (args[i].equals("-c"))
                charactersCount = true;
            if (args[i].equals("-e"))
                extractSentences = true;
        }

    }


    private void takeLinks(String arg) {
        try {
            File file = new File(arg);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null) {
                urls.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isUrl(String arg) {
        return arg.contains("http");
    }
}
