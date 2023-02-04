install-go-linux:
	curl https://dl.google.com/go/go1.20.linux-amd64.tar.gz -o go.tar.gz
	sudo rm -rf /usr/local/go && sudo tar -C /usr/local -xzf go.tar.gz
	export PATH=$PATH:/usr/local/go/bin
run:
	cd hello && go run .
run-quote:
	cd quote && go get rsc.io/quote && go run .
run-list:
	cd  list && go run .
