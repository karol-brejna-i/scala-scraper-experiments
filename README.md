I wanted to have fun building a mobile client (and learn react-native) for playing [Tzaar](https://boardgamegeek.com/boardgame/31999/tzaar).

The only online server for Tzaar that I now is http://www.boiteajeux.net/ (BAJ). Unfortunately, the server doesn’t provide a proper API.
The actions/moves are controlled by submitting forms. BAJ also seems to use APE (Ajax Push Framework, http://ape-project.org/).

I googled for "scala web scraping" and https://github.com/ruippeixotog/scala-scraper popped out quite a few times.

This project is an experiment of mine on using scala-scraper for harvesting game information from BAJ pages.

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

# Usage
There is no real user interface for this project, yet (no GUI, no CLI). I am focusing on the mechanics.

For now, you can use it by creating App object, similar to this:

```scala
object BajExperiments extends App with DebugUtils {
  val password = System.getenv().get("BAJ_PASSWORD")
  val login = System.getenv().get("BAJ_LOGIN")

  val browser = JsoupBrowser()
  // log to the service
  WebFetcher.loginPost(browser, login, password)
  // get games in progress for logged user
  val doc = WebFetcher.getGamesInProgressDoc(browser)
  val result = ActiveGameListExtractor(doc)
  logResult(result)
}
```

## Implemented features
* user login
* fetch active games list
* extract game details (pieces' positions, etc.)
* make a move

## TODO
* Create a new game
* Accept an invite
* Better (=some) error handling
