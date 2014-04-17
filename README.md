# Noto

**Noto** is a note-taking application based on the principle that transcribing information should be easy. The concept is a plain-text note-taking application that interprets Markdown-esque markup into HTML with a syntax for simple diagramming. Both the markup and syntax are based around symbols and structures that try to clearly relate spoken information with writing.

The output of building these notes will be formatted webpages that provide easy to read and accessible means of reviewing and distributing notes.

## Compilation From Source

There are two steps to compiling Noto into an executable. First you need to generate the class files:

    javac src/*.java

At this point you can run the application from the terminal with:

    java Noto

But if you want an executable file, then you need to build the JAR for it:

    jar cvfm Noto.jar manifest.txt *.class

After creating the JAR I recommend cleaning up the class files:

    rm *.class

## Brief Background

The idea for **Noto** came from my experience in college classrooms where I often had trouble both paying attention while trying to take nicely formatted and organized notes. So when it became time to come up with ideas for my senior project (class CIS 4914), I thought "why not?" and here we are now.

I am a huge supporter of helping people learn and keeping things free and available, so you can download and check out the pure source here while I am still in development. After the project is completed I plan to post a downloadable executable on my website as well as provide fairly well commented source code for anyone who is interested in such things.

Cheers! And happy coding :)
