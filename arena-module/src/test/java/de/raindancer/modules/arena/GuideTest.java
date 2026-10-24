package de.raindancer.modules.arena;

import de.raindancer.core.ui.help.HelpLint;
import de.raindancer.modules.api.ModuleCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class GuideTest {

    @Test
    @DisplayName("every page reads, every setting it quotes exists, and every command is explained")
    void theGuideIsComplete() {
        assertThat(HelpLint.of(Path.of("src/main/resources/rains-help"))
                .module("arena")
                .settings(ArenaSettings.class, ArenaSettings.DEFAULTS)
                .commands(new ArenaModule().commands().stream().map(ModuleCommand::name).toList())
                .problems()).isEmpty();
    }
}
