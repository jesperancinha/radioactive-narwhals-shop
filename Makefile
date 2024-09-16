b: build
build:
	mvn clean install
	cd backend && mvn clean install -Pjar
build-report:
	mvn clean install jacoco:prepare-agent package jacoco:report
run-command-example:
	cd rn-data-reader && java -jar target/rn-data-reader.jar -f ../commons/src/test/resources/narwhals1.xml -d 13
local-pipeline: b run-command-example
deps-plugins-update:
	curl -sL https://raw.githubusercontent.com/jesperancinha/project-signer/master/pluginUpdatesOne.sh | bash -s -- $(PARAMS)
