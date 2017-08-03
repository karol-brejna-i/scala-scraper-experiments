

## Build
In order to build the project, use sbt:
```
sbt compile
```

To build an executable jar:
```
sbt assembly
```

## Run
Run from sbt:
```
sbt run
```

Or using the jar:
```
java -jar target/scala-2.12/application.jar
```

The code assumes you have you're BAJ login and password stored in envrionment variables:
>  val password = System.getenv().get("BAJ_PASSWORD")
> 
>  val login = System.getenv().get("BAJ_LOGIN")

Set up the variables before you run or supply them while running:
```
BAJ_LOGIN=myuser BAJ_PASSWORD=mypassword java -jar target/scala-2.12/application.jar
```