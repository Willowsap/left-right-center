public class LeftRightCenterMin {
    public static final int NUM_GAMES = 1000000;
    public static final int NUM_PLAYERS = 5;
    public static final int NUM_STARTING_CHIPS = 3;
    public static void main(String[] args) {
        int maxTurns = 0, minTurns = 0, gamesPlayed = 0, numTurns = -1, player;
        double runningAverage = 0.0;
        int[] winners = new int[NUM_PLAYERS], chips = new int[NUM_PLAYERS + 1];
        for (int i = 0; i < NUM_GAMES; i++) {
            numTurns = -1;
            for (int j = 0; j < NUM_PLAYERS; j++) chips[j] = NUM_STARTING_CHIPS;
            chips[NUM_PLAYERS] = 0;
            while (getWinner(chips) == -1) {
                player = ++numTurns % NUM_PLAYERS;
                int receivingPlayer = -1, rolls = chips[player] > 3 ? 3 : chips[player];
                for (int k = 0; k < rolls; k++) {
                    int die = (int) Math.floor(Math.random() * 6);
                    receivingPlayer = die == 0 ? (player + 1) % NUM_PLAYERS
                        : die == 1 ? player - 1 < 0 ? NUM_PLAYERS - 1 : player - 1
                        : die == 2 ? NUM_PLAYERS : -1;
                    if (receivingPlayer != -1) {
                        chips[receivingPlayer]++;
                        chips[player]--;
                    }
                }
            }
            gamesPlayed++;
            winners[getWinner(chips)]++;
            if (runningAverage == 0.0) runningAverage = numTurns;
            else runningAverage = (double)runningAverage
                * ((double)(gamesPlayed - 1) / gamesPlayed)
                + (double)numTurns / gamesPlayed;
            if (maxTurns == 0 || maxTurns < numTurns) maxTurns = numTurns;
            if (minTurns == 0 || minTurns > numTurns) minTurns = numTurns;
        }
        System.out.println("Average #turns: " + runningAverage);
        System.out.println("Max Turns: " + maxTurns);
        System.out.println("Min Turns: " + minTurns);
        for (int i = 0; i < NUM_PLAYERS; i++)
            System.out.printf("Player %d won %d times\n",
                i, winners[i]);
    }
    public static int getWinner(int[] chips) {
        int numZeros = 0, hasChips = -1;
        for (int i = 0; i < NUM_PLAYERS; i++)
            if (chips[i] == 0) numZeros++;
            else hasChips = i;
        if (numZeros == NUM_PLAYERS - 1) return hasChips;
        else return -1;
    }
}
