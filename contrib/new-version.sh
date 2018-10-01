PREVIOUS_APP_VERSION="$(grep -o "<version>.*</version>" pom.xml | head -1 | grep -o "[[:digit:]]*\.[[:digit:]]*\.[[:digit:]]*")"
MINOR=$(echo $PREVIOUS_APP_VERSION | awk -F '.' '{print $1"."$2"."}')
echo $MINOR$TRAVIS_BUILD_NUMBER