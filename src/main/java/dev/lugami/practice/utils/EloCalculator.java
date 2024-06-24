package dev.lugami.practice.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EloCalculator {

    private static final int K_FACTOR = 32;

    /**
     * Calculates the new ELO ratings for two players after a match.
     *
     * @param player1Elo The current ELO rating of Player 1.
     * @param player2Elo The current ELO rating of Player 2.
     * @param player1Won Whether Player 1 won the match.
     * @return An array containing the new ELO ratings [newPlayer1Elo, newPlayer2Elo].
     */
    public int[] calculateElo(int player1Elo, int player2Elo, boolean player1Won) {
        double expectedScorePlayer1 = 1 / (1 + Math.pow(10, (player2Elo - player1Elo) / 400.0));
        double expectedScorePlayer2 = 1 / (1 + Math.pow(10, (player1Elo - player2Elo) / 400.0));

        int actualScorePlayer1 = player1Won ? 1 : 0;
        int actualScorePlayer2 = player1Won ? 0 : 1;

        int newPlayer1Elo = (int) Math.round(player1Elo + K_FACTOR * (actualScorePlayer1 - expectedScorePlayer1));
        int newPlayer2Elo = (int) Math.round(player2Elo + K_FACTOR * (actualScorePlayer2 - expectedScorePlayer2));

        return new int[] { newPlayer1Elo, newPlayer2Elo };
    }
}
