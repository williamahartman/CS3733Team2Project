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
- New big features should get their own branch
- Write a lot of test case

### Style Guidelines
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
        ```
            /**
             * This is a Javadoc style comment
             * @param arg the argument for our method
             */
            public void myMethod(int arg) {
                //Do something...
            }
        ```
- I'll clarify stuff further...

## Project Setup
### If you are using IntelliJ IDEA:
- When installing, make sure the Gradle plugin gets installed (it should happen by default)
- Use the "Checkout from Version Control" option to clone the project.
    - This is probably the URL you want to use: https://github.com/williamahartman/CS3733Team2Project.git
    - If you want to use SSH instead: git@github.com:williamahartman/CS3733Team2Project.git
    - IntelliJ should ask you if you want to open the gradle project file right after it finishes cloning the project.
        - IntelliJ comes with a bundled version of gradle. This should work automatically. If it doesn't for some reason, you can download gradle [here](http://gradle.org/gradle-download/) or with your package manager of choice.
- If all goes well, the project should open up.
    - Setting up running the project:
        - Click View -> Tool Windows -> Gradle
        - In the tool window, click on run, which is inside the "other" folder.
        - At this point, you should see the project start and the initial window appear.
    - Setting up running the project:
        - Click View -> Tool Windows -> Gradle
        - In the tool window, click on test, which is inside the "verification" folder.
        - At this point, you should see a window indicating that all tests passed.
    - From now on:
        - To run, make sure "CS3733Team2Project[run]" is selected in the dropdown menu near the top right corner. Run with the green arrow or Shift+f10
        - To run tests, select "CS3733Team2Project[test]" in the drop down.

### If you just wanna build stuff on the command line:
- Install Gradle [here](http://gradle.org/gradle-download/) or with your package manager of choice.
    - On Windows, make sure Gradle is on your PATH and that your JAVA_HOME environment variable is set.
- In the root of the project:
    - To run: `gradle run`
    - To run tests: `gradle test`