# AZTEC WASH for CS3733 at WPI

## Project Guidelines
### General
- You should use a Java 8 SDK
- This uses Gradle
    - Java code for the main app goes in src/main/java
    - Resources for the main app go in src/main/resources
    - Java code for the test cases goes in src/test/java
    - Resources for the test cases go in src/test/resources
- Work in a branch with your subgroup. Only merge branches back into master once you're confident you aren't merging in broken stuff
- Don't commit things with lots of errors
- New big features should get their own branch
- Write a lot of test cases

### Style Guidelines
- This stuff all gets enforces by Gradle's StyleChecker
    - Most stuff should show up as a warning, not an error. You should still fix them if at all possible.
    - You can read more about the config in `config/checkstyle/checkstyle.xml`
- Use spaces instead of tab characters (your IDE or text editor should be able to handle this)
- Class names are camel case with a capital first letter
    - ex. `ExampleClassName`
- Method names are camel case with a lowercase first letter
    - ex. `thisIsAMethod()`
- Variables are camel case with a lowercase first letter
    - ex. `exampleVariable`
- Constants (static final variable) are all caps with underscores
    - ex. `CONSTANT_VALUE`
- Comments on classes and methods are javadoc style
    - Comments on other stuff can be normal `//` style ones
    - ex:
    
            /**
             * This is a Javadoc style comment
             * @param arg the argument for our method
             */
            public void myMethod(int arg) {
                //Do something...
            }    
- `if`, `for`, `while`, etc. all need to have a space after them
- As a general rule, curly braces should take up as little space as possible
    - Braces after a statement should be on the same line
    - An `else` statement (or `do while` or `catch` and probably more...) should be on the same line as the brace the closed the previous block
    - ex:
        
            if (a == b) {
                //Do something...
            } else {
                //Do something else...
            }

## Project Setup
- You need to have git installed. Here's a download link: https://git-scm.com/downloads

### If you are using IntelliJ IDEA:
- When installing, make sure the Gradle plugin gets installed (it should happen by default)
- Use the "Checkout from Version Control" option to clone the project.
    - IntelliJ might complain that it couldn't find git.exe. 
        - Go to settings->Version Control->git
        - Set the path to the executable (probably C:\Program Files (x86)\Git or something similar, if you're on Windows)
        - Now try again, and it should work.
    - This is probably the URL you want to use: https://github.com/williamahartman/CS3733Team2Project.git
    - If you want to use SSH instead: git@github.com:williamahartman/CS3733Team2Project.git
    - IntelliJ should ask you if you want to open the gradle project file right after it finishes cloning the project.
        - IntelliJ comes with a bundled version of gradle. This should work automatically. If it doesn't for some reason, you can download gradle [here](http://gradle.org/gradle-download/) or with your package manager of choice.
- If all goes well, the project should open up.
    - Setting up running the project:
        - Click View -> Tool Windows -> Gradle
        - In the tool window, click on run, which is inside the "other" folder.
        - At this point, you should see the project start and the initial window appear.
    - Setting up testing the project:
        - Click View -> Tool Windows -> Gradle
        - In the tool window, click on test, which is inside the "verification" folder.
        - At this point, you should see a window indicating that all tests passed.
    - Setting up testing the project:
        - Click View -> Tool Windows -> Gradle
        - In the tool window, click on check, which is inside the "verification" folder.
        - At this point, you should see a window indicating that all style checks passed.
    - From now on:
        - To run, make sure "CS3733Team2Project[run]" is selected in the dropdown menu near the top right corner. Run with the green arrow or Shift+f10
        - To run tests only, select "CS3733Team2Project[test]" in the drop down.
        - To run style checks and tests only, select "CS3733Team2Project[check]" in the drop down.
- When you run the program, all tests and checks will automatically occur as well.

### If you just wanna build stuff on the command line:
- Install Gradle [here](http://gradle.org/gradle-download/) or with your package manager of choice.
    - On Windows, make sure Gradle is on your PATH and that your JAVA_HOME environment variable is set.
- In the root of the project:
    - To run: `gradle run`
    - To run tests only: `gradle test`
    - To run style checks and tests only: `gradle check`
