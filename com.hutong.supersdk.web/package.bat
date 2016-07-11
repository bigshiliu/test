echo "Deploy supersdk.web start --------------------------------------------------"

git checkout master
git pull origin master
git log -1 > .\src\main\webapp\version.txt
set MAVEN_OPTS=-Dfile.encoding=utf-8
echo "build maven offline"
call mvn clean package -U -Dmaven.test.skip=true

echo "Deploy supersdk.web end --------------------------------------------------"
pause