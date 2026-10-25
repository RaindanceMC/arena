package de.raindancer.modules.arena.rules;

import de.raindancer.modules.arena.api.MatchSpec;
import de.raindancer.modules.arena.api.Mode;
import de.raindancer.modules.arena.model.Stats;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("What the Arena screens say")
class ScreenRuleTest {

    private final ScreenRule rule = new ScreenRule();

    @Test
    @DisplayName("somebody who never fought is pointed to /arena")
    void neverFought() {
        assertThat(rule.you(null)).contains("Your arena record").contains("/arena");
        assertThat(rule.you(Stats.none(UUID.randomUUID(), "Ada"))).contains("/arena");
    }

    @Test
    @DisplayName("a record shows the fights, the win rate, kills with their ratio and the best wave")
    void record() {
        String text = rule.you(new Stats(UUID.randomUUID(), "Ada", 6, 3, 1, 20, 8, 12));
        assertThat(text).contains("<gray>Wins <white>6 <dark_gray>· <gray>Losses <white>3 <dark_gray>· <gray>Draws <white>1")
                .contains("<gray>Win rate <white>60%")
                .contains("<gray>Kills <white>20 <dark_gray>· <gray>K/D <white>2.50")
                .contains("<gray>Best wave <white>12");
    }

    @Test
    @DisplayName("no best wave line without a wave run, no K/D division by zero")
    void leftOut() {
        String text = rule.you(new Stats(UUID.randomUUID(), "Ada", 1, 0, 0, 4, 0, 0));
        assertThat(text).doesNotContain("Best wave").contains("K/D <white>4.00").contains("Win rate <white>100%");
    }

    @Test
    @DisplayName("what is on: fights running, people waiting and the queues that wait for more")
    void live() {
        MatchSpec teams = new MatchSpec(Mode.TEAMS, "gladiator", 1, 500, 2, null);
        MatchSpec ffa = new MatchSpec(Mode.FFA, "gladiator", 1, 0, 1, null);
        String text = rule.live(2, 3, List.of(new OpenQueueRule.Open(teams, List.of(UUID.randomUUID()), 4),
                new OpenQueueRule.Open(ffa, List.of(UUID.randomUUID(), UUID.randomUUID()), 3)));
        assertThat(text).contains("<gold><bold>Arena now</bold></gold>")
                .contains("<gray>Fights running <white>2").contains("<gray>Waiting in the queue <white>3")
                .contains("<white>Teams <dark_gray>· <white>1</white><dark_gray>/<white>4 <dark_gray>· <yellow>stake")
                .contains("<white>Free for all <dark_gray>· <white>2</white><dark_gray>/<white>3");
    }

    @Test
    @DisplayName("nothing on says so, and only the first few open queues are listed")
    void quietAndCrowded() {
        assertThat(rule.live(0, 0, List.of())).contains("Fights running <white>0").contains("Nobody is waiting");
        MatchSpec ffa = new MatchSpec(Mode.FFA, "gladiator", 1, 0, 1, null);
        List<OpenQueueRule.Open> many = java.util.Collections.nCopies(9, new OpenQueueRule.Open(ffa,
                List.of(UUID.randomUUID()), 3));
        assertThat(rule.live(0, 9, many).lines().count()).isLessThanOrEqualTo(10);
    }
}
