package ca.jrvs.apps.grep;

import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class JavaGrepImpl implements JavaGrep {

  final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

  private String regex;
  private String rootPath;
  private String outFile;

  public static void main(String[] args) {
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }

    //Use default logger config
  }


  @Override
  public void process() throws IOException {
    List<String> matchedLines = new ArrayList<>();
    for (File file : listFiles(getRootPath())) {
      for (String line : readLines(file)) {
        if (containsPattern(getRegex())) {
          matchedLines.add(line);
        }
      }
    }
  writeToFile(matchedLines);
  }

  @Override
  public List<File> listFiles(String rootDir) {
    List<File> files = new ArrayList<File>();
    File folder = new File(rootDir);
    File[] AllFiles = folder.listFiles();
    for (File f : AllFiles) {
      if (f.isFile()) {
        files.add(f);
      }
    }
    return files;
  }

  @Override
  public List<String> readLines(File inputFile) {
    Path path = Paths.get(inputFile.toURI());
    List<String> lines = new ArrayList<>();
    try {
      lines = Files.readAllLines(path);
    } catch (IOException e) {
      logger.error("Unable to read lines from file", e);
    }

    return lines;
  }

  @Override
  public boolean containsPattern(String line) {
    return Pattern.matches(getRegex(), line);
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    FileWriter writer = new FileWriter(getOutFile());
    for (String line : lines) {
      writer.write(line + System.lineSeparator());
    }
    writer.close();
  }

  @Override
  public String getRootPath() {
    return this.rootPath;
  }

  @Override
  public void setRootPath(String rootPath) {
    this.rootPath = rootPath;
  }

  @Override
  public String getRegex() {
    return this.regex;
  }

  @Override
  public void setRegex(String regex) {
    this.regex = regex;
  }

  @Override
  public String getOutFile() {
    return this.outFile;
  }

  @Override
  public void setOutFile(String outFile) {
    this.outFile = outFile;
  }
}
