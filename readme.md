#Note:
-Assignment 2 contains files from Assignment 1. However the file src/mutantInjector.java
performs the tasks required for Assignment 2. Please run the main method in src/mutantInjector.java to inject mutants. 

-If resources/library.txt does not exist, please run the main method in src/createLibrary.java.

-src/mutantInjector.java performs the task of creating a copy of the software under test. This copy is saved
as resources/copiedSUT.java. It also injects one mutant per line from the mutant fault list. The mutant
is only injected on one line in the code. This task is done for each line in the mutant fault list. 
A new file is generated as resources/mutantSUT-line'number' for each line with an injected mutant. All other lines of 
code remain the same as the original. 