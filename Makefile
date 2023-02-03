b: build
build:
	mvn clean install
	cd backend && mvn clean install -Pjar
run-command-example:
	cd rn-data-reader && java -jar target/rn-data-reader.jar -f ../commons/src/test/resources/narwhals1.xml -d 13
local-pipeline: b run-command-example
