
git checkout master
git pull origin master
set MAVEN_OPTS=-Dfile.encoding=utf-8
echo "build maven"
call mvn assembly:assembly -U -Dmaven.test.skip=true
pause