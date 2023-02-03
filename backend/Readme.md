# Radioactive Narwhals Web-shop Backend

## How to run

Use Intellij or run via the command line

1. Compilation

```shell
mvn clean install  package
```

2. Run

```shell
java -jar target/backend.jar
```

## Test commands
```shell
curl -i -X POST http://localhost:8080/rnarwhals-shop/load -H 'Content-Type: application/xml' --data-binary "@../commons/src/test/resources/narwhals1.xml"
```

```shell
curl -i http://localhost:8080/rnarwhals-shop/stock/13 -H 'Content-Type: application/json'
```

```shell
curl -i http://localhost:8080/rnarwhals-shop/narwhals/13 -H 'Content-Type: application/json'
```

```shell
curl -i -X POST http://localhost:8080/rnarwhals-shop/load -H 'Content-Type: application/xml' -d "<narwhals>
    <narwhal name=\"SonicDJ-1\" age=\"4\" sex=\"f\" />
    <narwhal name=\"SonicDJ-2\" age=\"8\" sex=\"f\" />
    <narwhal name=\"SonicDJ-3\" age=\"9.5\" sex=\"f\" />
</narwhals>
"
```

```shell
curl -i -X POST http://localhost:8080/rnarwhals-shop/order/200 -H 'Content-Type: application/json' -d "{
\"customer\": \"PinkOgre\",
\"order\": { \"seaCabbage\": 1100, \"tusks\": \"3\" }
}
"
```

```shell
curl -i -X POST http://localhost:8080/rnarwhals-shop/order/90 -H 'Content-Type: application/json' -d "{
\"customer\": \"PinkOgre\",
\"order\": { \"seaCabbage\": 1200, \"tusks\": \"3\" }
}
"
````

```shell
curl -i -X POST http://localhost:8080/rnarwhals-shop/order/5 -H 'Content-Type: application/json' -d "{
\"customer\": \"PinkOgre\",
\"order\": { \"seaCabbage\": 20, \"tusks\": \"3\" }
}
"
```

```shell
curl -i -X POST http://localhost:8080/rnarwhals-shop/order/6 -H 'Content-Type: application/json' -d "{
\"customer\": \"PinkOgre\",
\"order\": { \"seaCabbage\": 20, \"tusks\": \"3\" }
}
"
```
