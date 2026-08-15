package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Break Down the Door
 * {2}{G}
 * Instant
 * Choose one —
 * • Exile target artifact.
 * • Exile target enchantment.
 * • Manifest dread. (Look at the top two cards of your library. Put one onto the battlefield
 *   face down as a 2/2 creature and the other into your graveyard. Turn it face up any time
 *   for its mana cost if it's a creature card.)
 *
 * "Choose one" modal spell, same shape as [TwistReality]. The two exile modes target an artifact
 * or enchantment respectively (each a `target` slot resolved with [EffectTarget.ContextTarget]),
 * and the third mode reuses the shared [Patterns.Library.manifestDread] recipe (CR 701.62).
 */
val BreakDownTheDoor = card("Break Down the Door") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Exile target artifact.\n" +
        "• Exile target enchantment.\n" +
        "• Manifest dread. (Look at the top two cards of your library. Put one onto the " +
        "battlefield face down as a 2/2 creature and the other into your graveyard. Turn it " +
        "face up any time for its mana cost if it's a creature card.)"

    spell {
        modal(chooseCount = 1) {
            mode("Exile target artifact") {
                target("target", Targets.Artifact)
                effect = Effects.Exile(EffectTarget.ContextTarget(0))
            }
            mode("Exile target enchantment") {
                target("target", Targets.Enchantment)
                effect = Effects.Exile(EffectTarget.ContextTarget(0))
            }
            mode(
                "Manifest dread. (Look at the top two cards of your library. Put one onto the " +
                    "battlefield face down as a 2/2 creature and the other into your graveyard. Turn " +
                    "it face up any time for its mana cost if it's a creature card.)"
            ) {
                effect = Patterns.Library.manifestDread()
            }
        }
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "170"
        artist = "Ralph Horsley"
        imageUri = "https://cards.scryfall.io/normal/front/2/0/209e9bbc-a15d-47fc-b149-6b0c57dd09ea.jpg?1726286491"
    }
}
