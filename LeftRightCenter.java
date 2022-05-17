import java.util.Arrays;

/**
 * Simulates the game "Left, Right, Center".
 * 
 * @author Willow Sapphire
 * @version 05/16/2022
 */
public class LeftRightCenter {
    /**
     * These constants are used to set simulation parameters.
     * Would be better to convert them to command line arguments.
     */
    public static final int NUM_GAMES = 100;
    public static final int NUM_PLAYERS = 4;
    public static final int[] STRATEGIES = {3, 3, 3, 3, 3, 3};
    public static final int NUM_STARTING_CHIPS = 3;

    /**
     * The players in the game.
     */
    private Player[] players;

    /**
     * Current number of turns taken.
     * Could overflow if many games are played.
     */
    private int numTurns;

    /**
     * Keeps track of total turns taken across games.
     */
    private long runningTotal;

    /**
     * Keeps track of the current average number of turns.
     */
    private double runningAverage;

    /**
     * Tracks number of games played.
     */
    private int gamesPlayed;

    /**
     * Tracks who won.
     * Must be the same length as players.
     */
    private int[] winners;

    /**
     * The strategy for each player.
     * Must be the same length as players.
     */
    private int[] strategies;

    /**
     * Runs a set number of simulations for a set nunber of players.
     * 
     * @param args - unused currently
     */
    public static void main(String[] args) {
        LeftRightCenter game = new LeftRightCenter(NUM_PLAYERS, STRATEGIES);
        for (int i = 0; i < NUM_GAMES; i++) game.playGame();
        System.out.println("Average #turns: " + game.getRunningAverageTurns());
        System.out.println("Winners");
        for (int i = 0; i < NUM_PLAYERS; i++)
            System.out.printf("Player %d won %d times\n",
                i, game.getWinners()[i]);
    }

    /**
     * Constructor for LeftRightCenter.
     * 
     * @param numPlayers - the number of players in the game.
     * @param strategies - the strategies for each player.
     */
    public LeftRightCenter(int numPlayers, int[] strategies) {
        this.numTurns = -1;
        this.gamesPlayed = 0;
        this.runningAverage = 0;
        this.runningTotal = 0;
        this.winners = new int[numPlayers];
        this.strategies = Arrays.copyOf(strategies, strategies.length);
        setNumPlayers(numPlayers);
    }

    /**
     * Plays one game of Left, Right, Center.
     */
    public void playGame() {
        numTurns = 0;
        resetPlayers();
        while (getWinner() == -1)
            players[++numTurns % players.length].takeTurn(players);
        winners[getWinner()]++;
        runningTotal += numTurns;
        gamesPlayed++;
        updateRunningAverage();
    }

    /**
     * Updates the running average of turns per game.
     */
    public void updateRunningAverage() {
        if (runningAverage == 0)
            runningAverage = numTurns;
        else
            runningAverage = (double)runningAverage
                * ((double)(gamesPlayed - 1) / gamesPlayed)
                + (double)numTurns / gamesPlayed;
    }

    /**
     * Finds the winner in the players array.
     * 
     * @return the index of the winner in the players array
     *      or -1 if there is no winner.
     */
    public int getWinner() {
        for (int i = 0; i < players.length; i++)
            if (players[i].getChips() == players.length * NUM_STARTING_CHIPS)
                return i;
        return -1;
    }

    /**
     * Resets each players chips to the starting value.
     */
    public void resetPlayers() {
        for (Player p : players) p.setChips(NUM_STARTING_CHIPS);
    }

    /**
     * Getter for the number of turns the previous game took.
     * 
     * @return numTurns.
     */
    public int getNumTurns() {
        return this.numTurns;
    }

    /**
     * Getter for the running average total turns.
     * 
     * @return runningTotal.
     */
    public long getRunningTotalTurns() {
        return this.runningTotal;
    }

    /**
     * Getter for the running average turn count.
     * 
     * @return runningAverage.
     */
    public double getRunningAverageTurns() {
        return this.runningAverage;
    }

    /**
     * Getter for the winners array.
     * 
     * @return a copy of the array storing how much each player won.
     */
    public int[] getWinners() {
        return Arrays.copyOf(winners, winners.length);
    }

    /**
     * Getter for the strategies array.
     * 
     * @return a copy of the player strategies currently used.
     */
    public int[] getStrategies() {
        return Arrays.copyOf(strategies, strategies.length);
    }

    /**
     * Resets the number of players and makes new players.
     * 
     * @param numPlayers - the new number of players.
     */
    public void setNumPlayers(int numPlayers) {
        this.players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++)
            players[i] = new Player(i, strategies[i]);
    }

    /**
     * Sets the strategy of each player.
     * 
     * @param strategies - the strategy for each player.
     *      must be equal length or longer than the player array.
     */
    public void setStrategies(int[] strategies) {
        for (int i = 0; i < players.length; i++)
            players[i].setStrategy(strategies[i]);
    }

    /**
     * Private class to simulate a player in the game.
     */
    private class Player {
        /**
         * Current number of chips.
         */
        private int chips;

        /**
         * The player's id.
         */
        private int playerNum;

        /**
         * 0 = random
         * 1 = left of center
         * 2 = right of center
         * 3 = player with most
         * 4 = player with least
         */
        private int strategy;

        /**
         * Player constructor.
         * 
         * @param playerNum - the player's unique id (index in players array).
         * @param strategy - the player's strategy for getting center
         *      when there is an odd number of players.
         */
        public Player(int playerNum, int strategy) {
            this.playerNum = playerNum;
            this.chips = NUM_STARTING_CHIPS;
            this.strategy = strategy;
        }

        /**
         * The player takes one turn.
         * They roll the dice up to 3 times depending on players chips.
         * 1 chips = 1 roll
         * 2 chips = 2 rolls
         * 3+ chips = 3 rolls
         * Then they give chips to the appropriate players.
         * 
         * @param players - the array of players in the game. 
         *      Necessary so this player can give chips to other players.
         */
        public void takeTurn(Player[] players) {
            int rolls = chips > 3 ? 3 : chips;
            int receivingPlayer = -1;
            for (int i = 0; i < rolls; i++) {
                char die = roll();
                receivingPlayer =
                      die == 'r' ? (playerNum + 1) % players.length
                    : die == 'l' ? playerNum - 1 < 0
                        ? players.length - 1 : playerNum - 1
                    : die == 'c' ? getPlayerAcross() : -1;
                if (receivingPlayer != -1) {
                    players[receivingPlayer].addChips(1);
                    chips--;
                    // these two print statements can be used to verify th
                    // players are following the rules.
                    // uncomment if you want to check.
                    //System.out.printf(
                    //    "Player %d rolled %c, gives Player %d a chip",
                    //    playerNum, die, receivingPlayer);
                } else {
                    //System.out.printf("Player %d rolled %c, gives nothing",
                    //    playerNum, die);
                }
                
            }
        }

        /**
         * Adds a certain number of chips to the player's pile.
         * Not required due to setChips method, 
         * but makes adding chips slightly simpler.
         * 
         * @param chipsToAdd - number of chips to add.
         */
        public void addChips(int chipsToAdd) {
            chips += chipsToAdd;
        }

        /**
         * Performs one roll of the die.
         * 
         * @return 'l' means player to the left.
         *         'r' means player to the right.
         *         'c' means player across.
         *         '.' means no player.
         */
        public char roll() {
            int roll = (int) Math.floor(Math.random() * 6);
            return roll == 0 ? 'l' :  roll == 1 ? 'r' : roll == 2 ? 'c' : '.';
        }

        /**
         * Getter method for this player's chip count.
         * 
         * @return chips.
         */
        public int getChips() {
            return chips;
        }

        /**
         * Setter method for this player's strategy.
         * Allows the strategy to change without making a new player.
         * 
         * @param strategy - the strategy for this player.
         *      See javadoc comment at the top for each strategy.
         */
        public void setStrategy(int strategy) {
            this.strategy = strategy;
        }

        /**
         * Setter method for the players number of chips.
         * Primarily used to reset the nuimber back to starting amount.
         * 
         * @param chips - the number of chips to set the player's pile to.
         */
        public void setChips(int chips) {
            this.chips = chips;
        }

        /**
         * Gets the index of the player across from this player.
         * When there is an odd number of players,
         * this player's strategy is used.
         * 
         * @return the index of the player across.
         */
        private int getPlayerAcross() {
            // don't use strategy algorithm with an even number of players.
            if (players.length % 2 == 0)
                return (playerNum + players.length / 2) % players.length;
            // use strategy for an odd number of players
            return strategy == 0 ? getLeftOrRightRandomly()
                 : strategy == 1 ? getLeftOfCenter()
                 : strategy == 2 ? getRightOfCenter()
                 : strategy == 3 ? getPlayerWithMore()
                 : strategy == 4 ? getPlayerWithLess()
                 : -1;
        }

        /**
         * Gets the player to the left of center.
         * 
         * @return the index of the player left of center
         *      in relation to this player.
         */
        private int getLeftOfCenter() {
            return (players.length + (int) Math.floor(players.length / 2))
                % players.length;
        }

        /**
         * Gets the player to the right of center.
         * 
         * @return the index of the player right of center
         *      in relation to this player.
         */
        private int getRightOfCenter() {
            return (players.length + (int) Math.ceil(players.length / 2))
                % players.length;
        }

        /**
         * Gets the player to left or right of center randomly.
         * 
         * @return the player to left or right of center
         *      in relation to this player.
         */
        private int getLeftOrRightRandomly() {
            return Math.random() >= 0.5 ? getLeftOfCenter()
                : getRightOfCenter();
        }

        /**
         * Gets the player across from this player who has more chips.
         * 
         * @return the player to left or right of center
         *      who has more chips than the other.
         */
        private int getPlayerWithMore() {
            int r = getRightOfCenter();
            int l = getLeftOfCenter();
            return (players[l].getChips() > players[r].getChips()) ? l : r;
        }

        /**
         * Gets the player across from this player who has fewer chips.
         * 
         * @return the player to left or right of center
         *      who has fewer chips than the other.
         */
        private int getPlayerWithLess() {
            int r = getRightOfCenter();
            int l = getLeftOfCenter();
            return (players[l].getChips() < players[r].getChips()) ? l : r;
        }
    }
}