if [ -z "$JDK6" ]; then
        JDK6="/opt/taobao/install/jdk1.6.0_21"
fi

if [ ! -d "$JDK6" ]; then
	echo ÕÒ²»µ½JDK6Ä¿Â¼: $JDK6
	exit 1;
fi

JAVA_HOME=$JDK6
export JAVA_HOME

rm -rf build/jar
rm -rf packages
mkdir packages

BASE_DIR="`pwd`"


echo ===========package ctumile client=========
sleep 2
cd $BASE_DIR/mergeserver
mvn clean package -Dmaven.test.skip=true
echo =====Copy Released Jars To $BASE_DIR/packages=====
sleep 1
cp $BASE_DIR/mergeserver/client/target/*.jar $BASE_DIR/packages
cp $BASE_DIR/mergeserver/common/target/*.jar $BASE_DIR/packages
cd ..
