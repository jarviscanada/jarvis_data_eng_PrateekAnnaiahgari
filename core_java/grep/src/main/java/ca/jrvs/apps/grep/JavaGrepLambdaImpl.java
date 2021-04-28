package ca.jrvs.apps.grep;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JavaGrepLambdaImpl extends JavaGrepImpl {

  final Logger logger = LoggerFactory.getLogger(JavaGrep.class);

  @Override
  public List<File> listFiles(String rootDir) {
    List<File> filesList = new ArrayList<>();
    try (Stream<Path> walk = Files.walk(Paths.get(rootDir))) {
      filesList = walk.filter(Files::isRegularFile)
          .map(x -> x.toFile()).collect(Collectors.toList());
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
    return filesList;
  }

  @Override
  public List<String> readLines(File inputFile) {
    List<String> lines = new ArrayList<>();
    try (Stream<String> stream = Files.lines(Paths.get(inputFile.toURI()))) {
      lines = stream.collect(Collectors.toList());
    } catch (IOException e) {
      logger.error(e.getMessage(), e);
    }
    return lines;
  }

  @Override
  public void writeToFile(List<String> lines) throws IOException {
    FileWriter writer = new FileWriter(getOutFile());
    lines.stream().forEach(line -> {
      try {
        writer.write(line + System.lineSeparator());
      } catch (IOException e) {
        logger.error(e.getMessage(), e);
      }
    });
    writer.close();
  }

  @Override
  public void process() throws IOException {
    List<String> matchedLines = new ArrayList<>();
    matchedLines = listFiles(getRootPath()).stream()
        .flatMap(file -> readLines(file).stream()
            .filter(line -> containsPattern(line))).collect(Collectors.toList());
    writeToFile(matchedLines);
  }

  public static void main(String[] args) {
    BasicConfigurator.configure();
    if (args.length != 3) {
      throw new IllegalArgumentException("USAGE: JavaGrep regex rootPath outFile");
    }

    JavaGrepLambdaImpl javaGrepLambdaImpl = new JavaGrepLambdaImpl();
    javaGrepLambdaImpl.setRegex(args[0]);
    javaGrepLambdaImpl.setRootPath(args[1]);
    javaGrepLambdaImpl.setOutFile(args[2]);

    try {
      javaGrepLambdaImpl.process();
    } catch (Exception ex) {
      javaGrepLambdaImpl.logger.error(ex.getMessage(), ex);
    }
  }

}
