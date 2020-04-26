package org.ops4j.krabbl.core.crawl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class NumberFixerTest {

    private Set<String> numbers = new HashSet<>();

    @Test
    public void findBrokenNumbers() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("/home/hwellmann/git/folkies/oneill/1850/abc/numbers.txt"));
        for (String line : lines) {
            checkLine(line);
        }
    }

    private void checkLine(String line) {
        String fileNumber = line.substring(0, 4).replaceFirst("^0+", "");
        int colon = line.lastIndexOf(':');
        String abcNumber = line.substring(colon + 1).trim();
        if (!fileNumber.equals(abcNumber)) {
            System.out.println(fileNumber + " " + abcNumber);
        }
    }

    @Test
    public void moveDuplicates() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("/home/hwellmann/git/folkies/oneill/1850/abc/numbers.txt"));
        for (String line : lines) {
            checkDuplicate(line);
        }
    }

    private void checkDuplicate(String line) {
        String number = line.substring(0, 4);
        int colon = line.indexOf(':');
        String fileName = line.substring(0, colon);
        if (numbers.contains(number)) {
            System.out.println("git mv " + fileName + " ../duplicates");
        } else {
            numbers.add(number);
        }
    }



}
