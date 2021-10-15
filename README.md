# Get Shop Signs

## Purpose

This mod is designed to take the guesswork and confusion out of servers with
multiple player-run chest/sign shops. I'm constantly running around hunting for
the right shop that has the price I remember, or finding something cheaper
after buying for a higher price.

## Planned functionality

To help mitigate this, I'm trying my hand at my first Minecraft mod, with the
following goals:

- Player runs a command which queries all blocks within a cube around him
- Loop gets the text of the sign, if it's a chest shop, parse out the values
for seller, item, quantity, buy and sell prices.
- Store those values in a persistent way. To start with, I'm going to use a
JSON file, but I may move it to a SQL database so it can be indexed and queried
directly.

## General notes

This is my first attempt at a mod, and I'm not a Java guy, so I am sure the code
will be horrible. My intent is to make this as simple and useful as possible, and
if other people want to extend it, I'll gladly take PRs.

## TODOs

[ ] Load JSON data into list at connection
[ ] Store blocks indexed by world and server, as there is currently only one list which is not great.
[ ] Allow searching at arbitrary points, to do updates of shop districts from any location.
[ ] Create search function to find best buy/sell prices for items.
[ ] Allow comparison of complex items, i.e. "Piston" = recipe components, "Iron Block" = 9 Iron Ingots, etc.
[ ] Configurable backend to allow SQLite/MySQL/Postgres exporting natively.
