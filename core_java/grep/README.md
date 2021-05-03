# Introduction
Unix-like operating systems have a great command-line utility called `grep` for searching plain-text files for lines that match a given regular expression. 
This java application mimics the `grep` command. The application was built using Maven using IntelliJ IDEA and packaged using Maven and Docker for easy deployment.

# Quick Start
To use the application, follow these steps:  
    * pull the docker image from DockerHub using:        
    ```$ docker pull saiprateekreddy96/grep ```  
    * Run the container from the downloaded image with three required parameters:  
    ```$ docker run saiprateekreddy96/grep regex rootpath outFile```

#Implementation
## Pseudocode
``` matchedLines = []
    for file in listFiles(rootDir)
        for line in readLines(file)
            if containsPattern(line)
                matchedLines.add(line)
    writeToFile(matchedLines)
```

## Performance Issue
The application uses Lists to store intermediate values. Lists occupy a lot of space in heap memory when its size becomes arbitrarily large and hence throwing 
an `OutOfMemoryError` exception.   
To overcome this error, Streams were used instead of lists for the implementation enabling the application
to process huge data with a small heap memory.

# Test
Testing of the application was done using a sample text file input with different regex and verifying the output generated in the output file.
# Deployment
For hassle-free and easy deployment of the application to the user, a Docker image was created from the maven packaged uber JAR with the main program as the entry point. 
The image was then uploaded to Docker Hub.

# Improvement
1. Modify interface to use either Streams of BufferedReader.
2. Matching Subdirectories : Expand the project to match lines recursively from all the files in subdirectories.
3. Display Line Number: Expand the application to display the matched line number along with the line in the output file.  