package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardDestination
import com.wingedsheep.sdk.scripting.effects.CardOrder
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.CollectionFilter
import com.wingedsheep.sdk.scripting.effects.FilterCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ForEachPlayerCollectingEffect
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.effects.MoveCollectionEffect
import com.wingedsheep.sdk.scripting.effects.ZonePlacement
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Warp World — Ravnica: City of Guilds #150
 * {5}{R}{R}{R} · Sorcery
 *
 * Each player shuffles all permanents they own into their library, then reveals that many cards
 * from the top of their library. Each player puts all artifact, creature, and land cards revealed
 * this way onto the battlefield, then does the same for enchantment cards, then puts all cards
 * revealed this way that weren't put onto the battlefield on the bottom of their library.
 *
 * The phases interleave *across* players — every player's artifacts, creatures and lands enter
 * together, and only then every player's enchantments (so an Aura can enchant a creature Warp
 * World just put onto the battlefield). So the per-player half — count, shuffle, reveal, sort —
 * runs in a [ForEachPlayerCollectingEffect] whose sorted piles are appended into shared
 * collections, and the two battlefield entries run once each, afterwards, over everyone's cards:
 *
 * 1. Per player: gather every permanent they **own** (tokens included — each token still earns a
 *    card, the ruling), shuffle the cards into their library and put the tokens (which are about
 *    to cease to exist, CR 111.7) on the bottom, then reveal that many cards from the top. Tokens
 *    are never revealed cards, so a small library that reaches them filters them out.
 * 2. The revealed cards split into artifact/creature/land, enchantment, and the rest; the rest
 *    go to the bottom in an order the owner picks.
 * 3. All artifact/creature/land cards enter under their owners' control, then all enchantments;
 *    each owner picks what their Auras enchant (CR 303.4f).
 * 4. An Aura with nothing to enchant stays where it is (CR 303.4g) — the top of its owner's
 *    library — so the enchantments that did *not* move are put on the bottom (owner-routed,
 *    CR 400.3).
 *
 * An Aura can't enchant an enchantment entering alongside it (the ruling): `MoveCollection`
 * excludes every card of the batch from its Auras' host choices.
 */
val WarpWorld = card("Warp World") {
    manaCost = "{5}{R}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Each player shuffles all permanents they own into their library, then reveals " +
        "that many cards from the top of their library. Each player puts all artifact, creature, " +
        "and land cards revealed this way onto the battlefield, then does the same for enchantment " +
        "cards, then puts all cards revealed this way that weren't put onto the battlefield on the " +
        "bottom of their library."

    val artifactCreatureOrLand =
        GameObjectFilter.Artifact or GameObjectFilter.Creature or GameObjectFilter.Land

    spell {
        effect = Effects.Composite(
            ForEachPlayerCollectingEffect(
                players = Player.Each,
                effects = listOf(
                    GatherCardsEffect(
                        source = CardSource.BattlefieldMatching(GameObjectFilter.Any.ownedByYou(), Player.Each),
                        storeAs = "owned"
                    ),
                    FilterCollectionEffect(
                        from = "owned",
                        filter = CollectionFilter.MatchesFilter(GameObjectFilter.Any.token()),
                        storeMatching = "ownedTokens",
                        storeNonMatching = "ownedCards"
                    ),
                    MoveCollectionEffect(
                        from = "ownedCards",
                        destination = CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Shuffled)
                    ),
                    MoveCollectionEffect(
                        from = "ownedTokens",
                        destination = CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Bottom)
                    ),
                    GatherCardsEffect(
                        source = CardSource.TopOfLibrary(DynamicAmount.VariableReference("owned_count")),
                        storeAs = "revealed",
                        revealed = true
                    ),
                    FilterCollectionEffect(
                        from = "revealed",
                        filter = CollectionFilter.MatchesFilter(GameObjectFilter.Any.nontoken()),
                        storeMatching = "revealedCards"
                    ),
                    FilterCollectionEffect(
                        from = "revealedCards",
                        filter = CollectionFilter.MatchesFilter(artifactCreatureOrLand),
                        storeMatching = "acl",
                        storeNonMatching = "notAcl"
                    ),
                    FilterCollectionEffect(
                        from = "notAcl",
                        filter = CollectionFilter.MatchesFilter(GameObjectFilter.Enchantment),
                        storeMatching = "enchantments",
                        storeNonMatching = "rest"
                    ),
                    MoveCollectionEffect(
                        from = "rest",
                        destination = CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Bottom),
                        order = CardOrder.ControllerChooses
                    )
                ),
                collectCollections = mapOf("acl" to "warpAcl", "enchantments" to "warpEnchantments")
            ),
            MoveCollectionEffect(
                from = "warpAcl",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                underOwnersControl = true
            ),
            MoveCollectionEffect(
                from = "warpEnchantments",
                destination = CardDestination.ToZone(Zone.BATTLEFIELD),
                underOwnersControl = true
            ),
            // Read where each enchantment actually is rather than a moved-set: an Aura's enchant
            // choice pauses the move, and whatever didn't make it is simply not on the battlefield.
            FilterCollectionEffect(
                from = "warpEnchantments",
                filter = CollectionFilter.MatchesFilter(GameObjectFilter.Any.onBattlefield()),
                storeMatching = "warpEnchantmentsEntered",
                storeNonMatching = "warpStranded"
            ),
            MoveCollectionEffect(
                from = "warpStranded",
                destination = CardDestination.ToZone(Zone.LIBRARY, Player.You, ZonePlacement.Bottom)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "150"
        artist = "Ron Spencer"
        imageUri = "https://cards.scryfall.io/normal/front/f/d/fdbf743a-6e28-47c4-acfe-1c5d42f80eee.jpg?1783943644"
    }
}

