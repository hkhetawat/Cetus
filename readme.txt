******This is not Cetus as distributed in it’s original form by Purdue University at: http://cetus.ecn.purdue.edu. Modifications have been made to the project.******


-------------------------------------------------------------------------------
RELEASE
-------------------------------------------------------------------------------
Cetus 1.4.2 version (Dec 2014), unlike its predecessor (1.4.1), is not a binary release. 
It is a complete source release for both Cetus command line version and the GUI version.


Cetus is a source-to-source compiler infrastructure for C written in Java, and
can be downloaded from http://cetus.ecn.purdue.edu. This version contains a graphic
user interface (GUI), a client-server mode and contains minor updates and fixes 
in the existing passes.

-------------------------------------------------------------------------------
FEATURES/UPDATES
-------------------------------------------------------------------------------
* New features
	- Translate your C code through Purdue Cetus remote server
	- Compile and run input (sequential) and output (OpenMP) C code and show the charts
		of speedup and efficiency
	- Several demo C codes were added into the binary release, you can add your own C code
		and remove the existing demo C code
	- C code highlight in text area

* Updates

* Bug fixes and improvements
  
* Updates in flags

-------------------------------------------------------------------------------
CONTENTS
-------------------------------------------------------------------------------
This Cetus release has the following contents.

  lib            - Archived classes (jar)
  src            - Cetus source code
  antlr_license.txt	- ANTLR license
  RSyntaxTextArea.License.txt - RSyntaxTextArea license
  cetus_license.txt    - Cetus license
  build.sh       - Command line build script
  build.xml      - Build configuration for Apache Ant
  readme.txt     - This file
  readme_log.txt - Archived release notes
  readme_omp2gpu.txt - readme file for OpenMP-to-CUDA translator

-------------------------------------------------------------------------------
REQUIREMENTS
-------------------------------------------------------------------------------
* JAVA SE 6
* ANTLRv2 
* GCC (Cygwin GCC-4 for Windows OS)

-------------------------------------------------------------------------------
INSTALLATION
-------------------------------------------------------------------------------
* Obtain Cetus distribution
  The latest version of Cetus can be obtained at:
  http://cetus.ecn.purdue.edu/

* Binary Version
  For binary version (.jar) of Cetus, installation is not needed.

* Unpack
  Users need to unpack the distribution before installing Cetus.
  $ cd <directory_where_cetus.tar.gz_exists>
  $ gzip -d cetus.tar.gz | tar xvf -

* Build
  There are several options for building Cetus:
  - For Apache Ant users
    The provided build.xml defines the build targets for Cetus. The available
    targets are "compile", "jar", "gui", "clean" and "javadoc". Users need to edit
    the location of the Antlr tool.
  - For Linux/Unix command line users
    Run the script build.sh after defining system-dependent variables in the
    script.
  - For SDK (Eclipse, Netbeans, etc) users
    First, build the parser with the Antlr tool.
    Then, follow the instructions of each SDK to set up a project.

-------------------------------------------------------------------------------
RUNNING CETUS
-------------------------------------------------------------------------------
Users can run Cetus in the following way:

  $ java -classpath=<user_class_path> cetus.exec.Driver <options> <C files>

The "user_class_path" should include the class paths of Antlr, rsyntaxtextarea and Cetus.
"build.sh" and "build.xml" provides a target that generates a wrapper script
for Cetus users. 

- Like previous versions, you can still run command line version of Cetus by running "cetus" or "java -jar 
cetusgui.jar" plus flags (options) and input C file, e.g. "cetus foo.c" or "java -jar cetusgui.jar foo.c" for 
processing C file with the default options.

  - You can start Cetus GUI by double-clicking cetusgui.jar if your OS supports it. You can also start Cetus GUI by 
running "java -jar cetusgui.jar" or "java -jar cetusgui.jar -gui" in command line. Starting Cetus GUI through command 
line should work on all Windows, Linux and Mac. Previous script "cetus" is still working and "cetus gui" starts GUI 
too.
  - If you want to process your C code by Cetus on Windows, a preprocessor, i.e. Cygwin gcc-4.exe and cpp-4.exe), must be 
installed. However, compiling C code by Cetus with Cygwin on Windows has not been fully tested and is not guaranteed to 
work. Also, after installing Cygwin on Windows, the path to gcc-4.exe and cpp-4.exe (e.g. C:\cygwin\bin) must be set in Environment 
Variables on Windows.
  - The binary version is only for personal and academic use, not for commercial use. The license files of ANTLR and 
Cetus are also included in cetusgui.jar. We will regularly update the binary version. Please always visit Cetus website 
and download the latest version.

-------------------------------------------------------------------------------
KNOWN ISSUES
-------------------------------------------------------------------------------
Starting 1.4.0, the automatic loop parallelization was turned ON by default (level 1). We have seen that this
may lead to translation failures in certain cases. One such case is of usage of typedef'ed variables in the 
potential parallel loops. We are working towards fixing this issue.


-------------------------------------------------------------------------------
TESTING
-------------------------------------------------------------------------------
We have tested Cetus successfully using the following benchmark suites:

* SPEC CPU2006
  More information about this suite is available at http://www.spec.org

* SPEC OMP2001
  More information about this suite is available at http://www.spec.org

* NPB 2.3 written in C
  More information about this suite is available at
  http://www.hpcs.cs.tsukuba.ac.jp/omni-openmp/

May, 2013
The Cetus Team

URL: http://cetus.ecn.purdue.edu
EMAIL: cetus@ecn.purdue.edu

