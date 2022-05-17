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
    public static final int NUM_GAMES = 1000000;
    public static final int NUM_PLAYERS = 5;
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
     * Runs a set number of simulations for a set nunber of players.
     * 
     * @param args - unused currently
     */
    public static void main(String[] args) {
        LeftRightCenter game = new LeftRightCenter(NUM_PLAYERS);
        int maxTurns = 0, minTurns = 0;
        for (int i = 0; i < NUM_GAMES; i++) {
            game.playGame();
            if (maxTurns == 0 || maxTurns < game.getNumTurns()) maxTurns = game.getNumTurns();
            if (minTurns == 0 || minTurns > game.getNumTurns()) minTurns = game.getNumTurns();
        }
        System.out.println("Average #turns: " + game.getRunningAverageTurns());
        System.out.println("Max Turns: " + maxTurns);
        System.out.println("Min Turns: " + minTurns);
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
    public LeftRightCenter(int numPlayers) {
        this.numTurns = -1;
        this.gamesPlayed = 0;
        this.runningAverage = 0;
        this.runningTotal = 0;
        this.winners = new int[numPlayers];
        setNumPlayers(numPlayers);
    }

    /**
     * Plays one game of Left, Right, Center.
     */
    public void playGame() {
        numTurns = 0;
        resetPlayers();
        while (getWinner() == -1)
            players[++numTurns % NUM_PLAYERS].takeTurn(players);
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
        int numZeros = 0, hasChips = -1;
        for (int i = 0; i < NUM_PLAYERS; i++) {
            if (players[i].getChips() == 0) numZeros++;
            else hasChips = i;
        }
        if (numZeros == NUM_PLAYERS - 1) return hasChips;
        else return -1;
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
     * Resets the number of players and makes new players.
     * 
     * @param numPlayers - the new number of players.
     */
    public void setNumPlayers(int numPlayers) {
        this.players = new Player[numPlayers + 1];
        for (int i = 0; i < players.length; i++)
            players[i] = new Player(i);
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
         * Player constructor.
         * 
         * @param playerNum - the player's unique id (index in players array).
         * @param strategy - the player's strategy for getting center
         *      when there is an odd number of players.
         */
        public Player(int playerNum) {
            this.playerNum = playerNum;
            this.chips = NUM_STARTING_CHIPS;
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
                      die == 'r' ? (playerNum + 1) % NUM_PLAYERS
                    : die == 'l' ? playerNum - 1 < 0
                        ? NUM_PLAYERS - 1 : playerNum - 1
                    : die == 'c' ? NUM_PLAYERS : -1;
                if (receivingPlayer != -1) {
                    players[receivingPlayer].addChips(1);
                    chips--;
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
         * Setter method for the players number of chips.
         * Primarily used to reset the nuimber back to starting amount.
         * 
         * @param chips - the number of chips to set the player's pile to.
         */
        public void setChips(int chips) {
            this.chips = chips;
        }
    }
}