#!/bin/sh
#
# build.sh - build script for Cetus.
#

# Modify these variables for your system.
ANTLR="$PWD/lib/antlr.jar"            #antlr location

if [ -z "$ANTLR" ]; then
  echo "Please define ANTLR in $0"
  exit 1
fi

# Check for java/javac/jar.
for tool in java javac jar; do
  which $tool >/dev/null
  if [ $? -ne 0 ]; then
    echo $tool is not found.
    exit 1
  fi
done

# No change is required for these variables.
CETUSROOT=$PWD
SRC="$CETUSROOT/src/*/*/*.java $CETUSROOT/src/*/*/*/*.java"
PARSER="$CETUSROOT/src/cetus/base/grammars"
# Source files for parser construction.
parser_src="
  CetusCParser.java
  CToken.java
  LineObject.java
  NewCParser.g
  Pragma.java
  Pre.g
  PreprocessorInfoChannel.java
"
# Internal files to be removed from a release package.
# Maintainer SHOULD ALWAYS keep an up-to-date list of these files.
# internal_src: list of internal source code. these files should be removed
#     before generating API/Manual for release.
# internal_files: list of other internal files. these files will be removed
#     in the release package. antlr.jar 2.7.7 can be redistributed, so kept
internal_src="
"

internal_files="
    build_maintainer.xml
    build_maintainer.sh
    buildcetus.xml
    cetus_checks.xml
    readme_maintainer.txt
    manual
    lib/checkstyle.jar
    test
"

case "$1" in
  parser)
  echo "Compiling the parser using ANTLR..."
  #Build the parser
  cd $PARSER
  java -cp $ANTLR:class antlr.Tool $PARSER/Pre.g
  java -cp $ANTLR:class antlr.Tool $PARSER/NewCParser.g
  cd -
  ;;
  compile)
  echo "Compiling the source files..."
  if [ ! -f $PARSER/NewCParser.java ]; then
    $0 parser
  fi
  [ -d class ] || mkdir class
  javac -g -cp $ANTLR:class -d class $SRC
  ;;
  jar)
  $0 compile
  echo "Archiving the class files..."
  [ -d lib ] || mkdir lib
  jar cfm lib/cetus.jar MANIFEST-ADD.MF -C class .
  ;;
  javadoc)
  echo "Generating JAVA documents..."
  javadoc -d api cetus $SRC
  ;;
  manual)
  echo "Generating Cetus manual..."
  cd manual
  xmlto --skip-validation html manual.xml
  xmlto --skip-validation html-nochunks manual.xml
  xmlto --skip-validation pdf manual.xml
  cd -
  ;;
  clean)
  echo "Cleaning up..."
  rm -rf class bin lib/cetus.jar
  ;;
  purge)
  $0 clean
  echo "Purging classes and documents..."
  # removes files under grammars directory except for the source files.
  cd $PARSER; mv $parser_src ..; rm -f *
  cd ..; mv $parser_src grammars/
  cd $CETUSROOT
  rm -rf api manual/*.html manual/*.pdf
  ;;
  bin)
  $0 jar
  echo "Generating a wrapper..."
  [ -d bin ] || mkdir bin
  classpath="$PWD/lib/cetus.jar"
  cat > bin/cetus << EOF
#!/bin/sh
# This file was generated automatically by build.sh.
java -cp $classpath -Xms500m -Xmx500m cetus.exec.Driver \$*
EOF
  chmod 755 bin/cetus
  ;;
  package)
  # quit if the current copy is the main branch (trunk).
  svn info | grep URL | grep trunk
  [ $? -eq 0 ] && echo "Do not invoke this target for main branch!!!" && exit 1
  # removes any source code which should not be in the release.
  for f in $internal_src; do svn rm $f; done
  svn commit -m "Removed internal source code for release"
  # creates cetus.jar and add it to the branch.
  $0 jar
  svn add lib/cetus.jar
  svn commit lib/cetus.jar -m "Added cetus.jar for release"
  # creates manual and API reference.
  $0 javadoc
  $0 manual
  tar cvf documents.tar api manual/*.html
  $0 purge
  # removes internal files.
  for f in $internal_files; do svn rm $f; done
  svn commit -m "Removed internal files for release"
  svn update
  svn export . ./cetus-version
  ;;
  *)
  echo "Usage: $0 <target>"
  echo "  <target> is one of the followings:"
  echo "  bin     - compile, jar, and generate a wrapper script."
  echo "  compile - compile the source files."
  echo "  clean   - remove classes, jar, and the wrapper."
  echo "  jar     - archive the class files."
  echo "  javadoc - generate api documents."
  echo "  manual  - generate manual."
  echo "  parser  - rebuild the parser."
  echo "  purge   - remove classes, jar, wrapper, and documents."
  echo "  package - create release package (do not run this in main branch)."
  exit 1
  ;;
esac
