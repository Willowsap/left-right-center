public class LeftRightCenter {
    public static final int NUM_GAMES = 1000;
    public static final int NUM_PLAYERS = 5;
    public static final int[] STRATEGIES = {4, 3, 3, 3, 3};
    public static void main(String[] args) {
        LeftRightCenter game = new LeftRightCenter();
        int[] winners = new int[NUM_PLAYERS];
        long turns = 0;
        for (int i = 0; i < NUM_GAMES; i++) {
            turns += game.playGame(NUM_PLAYERS, STRATEGIES, winners);
        }
        System.out.println("Average #turns: " + turns / NUM_GAMES);
        System.out.println("Winners");
        for (int i = 0; i < winners.length; i++) {
            System.out.printf("Player %d won %d times\n", i, winners[i]);
        }
    }

    Player[] players;

    public int playGame(int numPlayers, int[] strategies, int[] winners) {
        players = new Player[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            players[i] = new Player(i, strategies[i]);
        }
        int turn = -1;
        while (!gameOver()) {
            players[++turn % players.length].takeTurn(players);
        }
        //printResults(turn, true);
        winners[getWinner()]++;
        return turn;
    }

    public int getWinner() {
        if (gameOver()) {
            for (int i = 0; i < players.length; i++) {
                if (players[i].getChips() == players.length * 3) return i;
            }
        }
        return -1;
    }

    public void printResults(int numTurns, boolean fullPrint) {
        System.out.println("Game over after " + numTurns + " turns.");
        if (fullPrint) {
            for (Player p : players) {
                System.out.println("Player " + p.getPlayerNum() + ": " + p.getChips() + " chips");
            }
        }
    }

    public boolean gameOver() {
        int numZero = 0;
        for (Player p : players) {
            if (p.getChips() == 0) {
                numZero++;
            }
        }
        return numZero == players.length - 1;
    }

    private class Player {
        private int chips;
        private int playerNum;

        /**
         * 0 = random
         * 1 = left of center
         * 2 = right of center
         * 3 = player with most
         * 4 = player with least
         */
        private int strategy;

        public Player(int playerNum, int strategy) {
            this.playerNum = playerNum;
            this.chips = 3;
            this.strategy = strategy;
        }

        public void takeTurn(Player[] players) {
            int rolls = chips > 3 ? 3 : chips;
            int receivingPlayer = -1;
            for (int i = 0; i < rolls; i++) {
                char die = roll();
                receivingPlayer = die == 'r' ? (playerNum + 1) % players.length
                    : die == 'l' ? playerNum - 1 < 0 ? players.length - 1 : playerNum - 1
                    : die == 'c' ? getPlayerAcross(players.length) : -1;
                if (receivingPlayer != -1) {
                    players[receivingPlayer].addChips(1);
                    chips--;
                    //System.out.printf("Player %d rolled %c, gives Player %d a chip", playerNum, die, receivingPlayer);
                } else {
                    //System.out.printf("Player %d rolled %c, gives nothing", playerNum, die);
                }
                
            }
        }
        public int getPlayerNum() {
            return playerNum;
        }

        public int getChips() {
            return chips;
        }

        public void addChips(int chipsToAdd) {
            chips += chipsToAdd;
        }

        public char roll() {
            int roll = (int) Math.floor(Math.random() * 6);
            return roll == 0 ? 'l' :  roll == 1 ? 'r' : roll == 2 ? 'c' : '.';
        }

        public int getPlayerAcross(int numPlayers) {
            if (numPlayers % 2 == 0) {
                return (playerNum + numPlayers / 2) % numPlayers;
            }
            switch(strategy) {
                case 0:
                    return Math.random() >= 0.5 ? getLeftOfCenter(numPlayers) : getRightOfCenter(numPlayers);
                case 1:
                    return getLeftOfCenter(numPlayers);
                case 2:
                    return getRightOfCenter(numPlayers);
                case 3:
                    return getPlayerWithMore(numPlayers);
                case 4:
                    return getPlayerWithLess(numPlayers);
                default:
                    System.out.println("Error in strategy");
                    return -1;
            }
        }

        private int getLeftOfCenter(int numPlayers) {
            return (playerNum + (int) Math.floor(numPlayers / 2)) % numPlayers;
        }

        private int getRightOfCenter(int numPlayers) {
            return (playerNum + (int) Math.ceil(numPlayers / 2)) % numPlayers;
        }

        private int getPlayerWithMore(int numPlayers) {
            int playerToRight = getRightOfCenter(numPlayers);
            int playerToLeft = getLeftOfCenter(numPlayers);
            return (players[playerToLeft].getChips() > players[playerToRight].getChips()) ? playerToLeft : playerToRight;
        }

        private int getPlayerWithLess(int numPlayers) {
            int playerToRight = getRightOfCenter(numPlayers);
            int playerToLeft = getLeftOfCenter(numPlayers);
            return (players[playerToLeft].getChips() < players[playerToRight].getChips()) ? playerToLeft : playerToRight;
        }
    }
}