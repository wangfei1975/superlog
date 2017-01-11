# Superlog
A log view tool that works for Android and BB10. My work used to read and analyze a huge amount of logs for bug fixing and issue locating. However, I could not find a handy log tool for this, so I created my own one:

[![solarized dualmode](https://github.com/wangfei1975/superlog/raw/master/screenshots/superlog.png)](#features)

# Main features
 * Fast. Works on huge logs files without UI latency. 
 * Connect to devices and view live logs
 * Open and view log files. 
 * Detect and parse all Android log formats (brief, tag, raw, time, threadtime, long).
 * Powerful filters. Create filter on top of other filters.
 * Mutli-tab view for one log source.
 * Save, restory and short-cuts for customized filters.
 * Other useful tools(show time difference between logs)

# How to use it
Run class feiw.Slogmain

## Build instructions using Gradle
If you have problems running the class mentioned above, you can use Gradle to build the project:

Build the project by either executing
  * gradlew (eg if you don't have Gradle installed)
  * or the Gradle build file (build.gradle)

Execute the resulting Jar:

```
   java -jar superlog-1.0-standalone.jar
```

## MacOS
Run with JVM option -XstartOnFirstThread (useful to run it from IntelliJ):

```
   java  -XstartOnFirstThread  -jar superlog-1.0-standalone.jar
```
