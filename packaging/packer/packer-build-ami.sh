#!/bin/bash
VERSION=$1
JAR="../entitydb-app/target/entitydb.jar"
LICENSE="../LICENSE"
README="../README.md"
CONF="../entitydb-app/target/entitydb.conf"
PROPS="../entitydb-app/target/entitydb.properties"
MAPPING="../entitydb-app/target/mapping.json"
TIMESTAMP=`date "+%Y.%m.%d-%H.%M.%S"`
/opt/packer build -only amazon-ebs \
	-var "package=$JAR" \
	-var "license=$LICENSE" \
	-var "readme=$README" \
	-var "conf=$CONF" \
	-var "props=$PROPS" \
	-var "mapping=$MAPPING" \
	-var "ami_name=EntityDB $VERSION $TIMESTAMP" \
	./packer-entitydb.json
