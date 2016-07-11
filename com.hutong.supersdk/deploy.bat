echo "Deploy supersdk start --------------------------------------------------"

git checkout master
git pull origin master
set MAVEN_OPTS=-Dfile.encoding=utf-8
echo "build maven"
call mvn clean deploy -U -Dmaven.test.skip=true

echo "Deploy supersdk end --------------------------------------------------"
pause