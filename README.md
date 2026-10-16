# RaindanceMC/arena

- **`arena-api`** — Rain's Flexible Modules — Arena API: What the arena server and the survival server's gate both have to understand: what a fight is (mode, kit, rounds, stake, waves) and the words they say to each other over RainsCore's server link. No module in it, so shading it into a plugin adds nothing that runs.
- **`arena-module`** — Rain's Flexible Modules — Arena: A PvP arena in worlds of its own: duels and team fights by invite or queue, mob waves, kits or your own gear, stakes paid by both sides — and nothing anybody carries in is ever lost, whatever happens.
- **`arena-standalone`** — Rain's Arena: Rain's Arena, as a plugin of its own. Nothing here but a build file and a descriptor: the arena module and the standard wrapper are shaded together, and that is the whole plugin.

Part of [RaindanceMC](https://github.com/RaindanceMC); built on [RainsCore](https://github.com/RaindanceMC/RainsCore) and the shared parent in [framework](https://github.com/RaindanceMC/framework). Split out of `rains-flexible-modules` on 16.10.2026, history kept.

## Build

```
mvn install
```

Needs Java 25 and read access to `packages.tstieh.de` (server ids `packages-releases` / `packages-snapshots` in `~/.m2/settings.xml`).

## Plugins

- `arena-standalone/target/RainsArena-0.14.3.jar` — also published as `de.raindancer.modules:arena-standalone:0.14.3`

## Versions

A module's version is a property in this repository's root `pom.xml`, a plugin's is `plugin.version` in its own pom. CI publishes every version that is not in the registry yet on the next push to master.
