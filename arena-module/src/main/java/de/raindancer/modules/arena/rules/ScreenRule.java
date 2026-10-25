package de.raindancer.modules.arena.rules;

import de.raindancer.modules.arena.model.Stats;

import java.util.List;
import java.util.Locale;

/** What the Arena screens say: somebody's own record, and what is on right now. */
public final class ScreenRule implements IArenaRule {

    private static final int OPEN_SHOWN = 5;

    /** @param record null for somebody the arena has never seen */
    public String you(Stats record) {
        StringBuilder out = new StringBuilder("<gold><bold>Your arena record</bold></gold>");
        if (record == null || record.wins() + record.losses() + record.draws() + record.kills() + record.bestWave() == 0) {
            return out.append("\n<gray>No fight yet — <white>/arena").toString();
        }
        int fights = record.wins() + record.losses() + record.draws();
        out.append("\n<gray>Wins <white>").append(record.wins()).append(" <dark_gray>· <gray>Losses <white>")
                .append(record.losses()).append(" <dark_gray>· <gray>Draws <white>").append(record.draws());
        if (fights > 0) {
            out.append("\n<gray>Win rate <white>").append(record.wins() * 100 / fights).append("%");
        }
        out.append("\n<gray>Kills <white>").append(record.kills()).append(" <dark_gray>· <gray>K/D <white>")
                .append(String.format(Locale.ROOT, "%.2f", (double) record.kills() / Math.max(1, record.deaths())));
        if (record.bestWave() > 0) {
            out.append("\n<gray>Best wave <white>").append(record.bestWave());
        }
        return out.toString();
    }

    public String live(int fights, int waiting, List<OpenQueueRule.Open> open) {
        StringBuilder out = new StringBuilder("<gold><bold>Arena now</bold></gold>");
        out.append("\n<gray>Fights running <white>").append(fights);
        if (waiting == 0) {
            return out.append("\n<gray>Nobody is waiting").toString();
        }
        out.append("\n<gray>Waiting in the queue <white>").append(waiting);
        for (OpenQueueRule.Open each : open.stream().limit(OPEN_SHOWN).toList()) {
            out.append("\n<white>").append(each.spec().mode().title()).append(" <dark_gray>· <white>")
                    .append(each.waiting().size()).append("</white><dark_gray>/<white>").append(each.needed());
            if (each.spec().stake() > 0) {
                out.append(" <dark_gray>· <yellow>stake");
            }
        }
        return out.toString();
    }

    @Override
    public String describe() {
        return "what the Arena screens say";
    }
}
