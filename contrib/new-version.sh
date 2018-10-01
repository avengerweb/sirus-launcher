PREVIOUS_APP_VERSION="$(mvn -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive exec:exec -q)"
MINOR=$(echo $APP_VERSION | awk -F '.' '{print $1"."$2"."}')
echo $MINOR$TRAVIS_BUILD_NUMBER