Why a separate repository for unit test?

Short answer: To make dependency management easier.
Longer answer: Default repository has no way of managing dependencies in a way that is not a huge hassle. Hence
this repo was created as a maven project. Default test-dependencies are found in main-branch in pom.xml (JUnit 5 and Mockito).

For a complete guide on how to run the program please read the main repo readme:

https://github.com/LongeMan/EDIM-V2
