Code Names game implementation in Java with a modular package structure.
Game parameters are externalized via XML, allowing seamless customization of gameplay. The system supports distinct roles for administrators and players.
### Technology: 
- Built in Java with a modular architecture.
- Game settings and parameters are loaded from an external XML file in the Resources folder.
### User Functionality:
#### Admin Controls:
- Load new game configurations (number of cards, black cards, groups, word sets) from XML file.
- Monitor ongoing games.
- Exclusive single-admin access.
#### Player Interaction:
- View all available games (both in-progress and open for registration).
- Check real-time status of player registration for any game.
- Register as a guesser or spymaster.
- Observe other groups' gameplay if their team loses.
