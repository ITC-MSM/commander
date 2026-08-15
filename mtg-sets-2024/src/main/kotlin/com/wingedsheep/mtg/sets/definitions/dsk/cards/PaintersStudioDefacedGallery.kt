package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.CardLayout
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.MayPlayExpiry
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Painter's Studio // Defaced Gallery (DSK 147) — split-layout Room (CR 709.5).
 *
 * Painter's Studio {2}{R} — Enchantment — Room
 *   When you unlock this door, exile the top two cards of your library. You may play them until
 *   the end of your next turn.
 *
 * Defaced Gallery {1}{R} — Enchantment — Room
 *   Whenever you attack, attacking creatures you control get +1/+0 until end of turn.
 *
 * Cast each half separately; the cast face enters unlocked, the other locked. Pay the locked
 * face's printed mana cost as a sorcery-speed special action to unlock it (CR 709.5e). The
 * Painter's Studio door-unlock impulse uses [MayPlayExpiry.UntilEndOfNextTurn] for the
 * "until the end of your next turn" play window.
 */
val PaintersStudioDefacedGallery = card("Painter's Studio // Defaced Gallery") {
    layout = CardLayout.SPLIT
    colorIdentity = "R"

    face("Painter's Studio") {
        manaCost = "{2}{R}"
        typeLine = "Enchantment — Room"
        oracleText = "When you unlock this door, exile the top two cards of your library. You may " +
            "play them until the end of your next turn."

        triggeredAbility {
            trigger = Triggers.OnDoorUnlocked
            effect = Patterns.Exile.impulse(count = 2, expiry = MayPlayExpiry.UntilEndOfNextTurn)
            description = "When you unlock this door, exile the top two cards of your library. You " +
                "may play them until the end of your next turn."
        }
    }

    face("Defaced Gallery") {
        manaCost = "{1}{R}"
        typeLine = "Enchantment — Room"
        oracleText = "Whenever you attack, attacking creatures you control get +1/+0 until end of turn."

        triggeredAbility {
            trigger = Triggers.YouAttack
            effect = Patterns.Group.modifyStatsForAll(
                power = 1,
                toughness = 0,
                filter = GroupFilter.AllCreaturesYouControl.attacking(),
                duration = Duration.EndOfTurn
            )
            description = "Whenever you attack, attacking creatures you control get +1/+0 until end of turn."
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "147"
        artist = "Marc Simonetti"
        imageUri = "https://cards.scryfall.io/normal/front/e/9/e96901eb-5b57-43f4-a7b1-ae3b809bc36e.jpg?1726780710"
    }
}
