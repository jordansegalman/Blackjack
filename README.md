# Blackjack

This is a multiplayer Blackjack card game with a server and client. The server acts as the house and creates new tables for players to join through the client. Every player starts with a certain amount of money and must make a minimum bet each round. Players can leave after each round or will be kicked out when they do not have enough money to place the minimum bet. The dealer hits on a soft 17, a shoe of six decks is used, and the shoe is shuffled when a new round starts if there is less than one and a half decks worth of cards remaining in the shoe. All other rules are standard Blackjack rules.

## How to Play

Follow these instructions to run Blackjack on your computer.

### Prerequisites

* Java Runtime Environment

### Running

To run the server and client, navigate to the directory containing the JAR files and enter the following commands.

```
java -jar BlackjackServer.jar

java -jar BlackjackClient.jar
```

## License

This project is licensed under the MIT License. See the [LICENSE.md](LICENSE.md) file for details.
