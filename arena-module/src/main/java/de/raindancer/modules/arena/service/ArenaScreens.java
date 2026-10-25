package de.raindancer.modules.arena.service;

import de.raindancer.core.ui.hologram.ScreenKind;
import de.raindancer.core.ui.hologram.Screens;
import de.raindancer.modules.arena.model.Stats;
import de.raindancer.modules.arena.rules.BoardRule;
import de.raindancer.modules.arena.rules.ScreenRule;
import de.raindancer.modules.arena.store.StatsStore;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/**
 * The Arena's screens for Core's {@code /screen}: everybody's own record, the champions (the text of the floating
 * boards), and what is on right now. Records come from the database, so each is kept a little while.
 */
public final class ArenaScreens {

    public static final List<String> KINDS = List.of("arena-you", "arena-top", "arena-live");
    public static final int BOARD_SHOWN = 5;
    private static final long KEPT_MILLIS = 20_000;
    private static final MiniMessage MINI = MiniMessage.miniMessage();

    private record Kept(long at, Optional<Stats> stats) {
    }

    private final StatsStore stats;
    private final MatchService matches;
    private final QueueService queue;
    private final LongSupplier clock;
    private final ScreenRule rule = new ScreenRule();
    private final BoardRule board = new BoardRule();
    private final Map<UUID, Kept> kept = new ConcurrentHashMap<>();

    public ArenaScreens(StatsStore stats, MatchService matches, QueueService queue, LongSupplier clock) {
        this.stats = stats;
        this.matches = matches;
        this.queue = queue;
        this.clock = clock;
    }

    public void register(Screens screens, Plugin owner) {
        screens.register(owner, ScreenKind.personal("arena-you", "Your arena record",
                "Everybody's own wins, losses, kills and best wave.", Material.IRON_SWORD,
                viewer -> MINI.deserialize(rule.you(own(viewer.id()).orElse(null)))));
        screens.register(owner, ScreenKind.everyone("arena-top", "Arena champions",
                "The most wins, and who got furthest in the mob waves.", Material.GOLDEN_SWORD, this::champions));
        screens.register(owner, ScreenKind.everyone("arena-live", "Arena now",
                "Fights running, people waiting and the queues that still need players.", Material.CLOCK,
                () -> MINI.deserialize(rule.live(matches.fights().size(), queue.waitingCount(), queue.openQueues()))));
    }

    /** The champions' text, for the screen and for the floating boards alike. */
    public net.kyori.adventure.text.Component champions() {
        return MINI.deserialize(board.text(stats.top(false, BOARD_SHOWN), stats.top(true, BOARD_SHOWN)));
    }

    private Optional<Stats> own(UUID player) {
        long now = clock.getAsLong();
        Kept held = kept.get(player);
        if (held != null && now - held.at() < KEPT_MILLIS) {
            return held.stats();
        }
        kept.values().removeIf(old -> now - old.at() >= KEPT_MILLIS);
        Optional<Stats> read = stats.of(player);
        kept.put(player, new Kept(now, read));
        return read;
    }
}
