# Application Protocol

## General

- Authors: Fabien Léger & Samuel Dos Santos
- Course: DAI, HEIG-VD

## Overview

The Nokemon Internet Protocol (NOKENET) is an interaction protocol for the video game Nokemon.
It allows users to send actions to the server to interact with the game in a 1 player vs 1 player environment.

A user can also be called player or trainer.

Each player has one single Nokemon. A Nokemon is an animal players use to fight each other. When fighting each other,
a trainer can either ask its Nokemon to attack the other Nokemon or to heal itself. The game is turn-based.

## Transport protocol

The NOKENET protocol is an action based message protocol. It must use the TCP (Transmission Control Protocol) to have
reliability over the network but also have a more connection based protocol. It must use the port 7270.

Each action must be encoded in UTF-8 and delimited by a newline character (`\n`). Each action must be read as a command
like one in a command-line application or an old RPG video game.

The server must be online for NOKENET to work.

The client will establish the initial connection. In case there are already two players on the server, the
server will refuse the connection.

Once the client has established the connection, it will be asked to choose a trainer name. This name will be unique. In
case the name is already taken, the server will send and error.

After that, the server will confirm the name and let the client go through. The client can now either:

- Start a new game
- Join an existing game
- Quit

If the player starts a new game, the server will wait for someone else to join the game. If there is already an existing
game, the client cannot create a new one.

Joining a game will join automatically if a game already exists.

Quitting will close the connection between the client and the server.

Once someone joins the game, the player that will start the game will be chosen randomly.

A player has two possibilities while in a game:

- Attack the other player's Nokemon, reducing his health points
- Heal his own Nokemon, rising his health points (up to the max number of health points he started with)

Each one of those actions will be sent to the server. The server will do the calculations then send the results to the
players.

If it's not a player's turn but a request is sent by him, an error will be sent by the server.

A game ends once all of a player's Nokemons are defeated.

Players can then again either start a new game, join an existing one or quit.

## Messages

### Username

Used by the client to send his username to the server.

#### Command

```
USERNAME <username>
```
- `username`: String of characters between 3 and 15 inclusive

#### Response

- `OK`: username was accepted by the server
- `ERROR <code>`: username was not accepted. There are 3 error codes possible
  - `8`: command doesn't exist
  - `42`: command is wrongly formatted
  - `117`: username is already taken


### Create

The client creates a game if there is no existing one already.

#### Command

```
CREATE
```

#### Response

- `OK`: server successfully created the game and joined it
- `ERROR <code>`: create was not accepted. There are 3 error codes possible
  - `8`: command doesn't exist
  - `42`: command is wrongly formatted
  - `118`: there is already an existing lobby

### Join

The client joins the existing game if there is one already created.

#### Command

```
JOIN
```

#### Response

- `OK`: server successfully joined the game
- `ERROR <code>`: join was not accepted. There are 3 error codes possible
  - `8`: command doesn't exist
  - `42`: command is wrongly formatted
  - `119`: there is no lobby to join

### Quit

The client quits the applications and ends the connection.

#### Command

```
QUIT
```

#### Response

- `OK`: server successfully closed the connection
- `ERROR <code>`: quit was not accepted. There are 2 error codes possible
  - `8`: command doesn't exist
  - `42`: command is wrongly formatted

### Attack

During a game, the client sends an attack action for his Nokemon.

#### Command

```
ATTACK
```

#### Response

- `HIT <username> <nb>`: username was accepted by the server
  - `username`: username of the trainer who's nokemon got hit
  - `nb`: number of health points deducted
- `ERROR <code>`: attack was not accepted. There are 3 error codes possible
  - `8`: command doesn't exist
  - `42`: command is wrongly formatted
  - `120`: can't attack outside a game

### Heal

During a game, the client sends a heal action for his Nokemon.

#### Command

```
HEAL
```

#### Response

- `HEALED <username> <nb>`: username was accepted by the server
    - `username`: username of the trainer who's nokemon got healed
    - `nb`: number of health points healed
- `ERROR <code>`: heal was not accepted. There are 3 error codes possible
  - `8`: command doesn't exist
  - `42`: command is wrongly formatted
  - `121`: can't heal outside a game

## Examples