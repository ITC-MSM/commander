package com.wingedsheep.mtg.sets.definitions.thb.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Pharika's Libation
 * {2}{B}
 * Instant
 *
 * Choose one —
 * • Target opponent sacrifices a creature of their choice.
 * • Target opponent sacrifices an enchantment of their choice.
 *
 * Each mode names its *own* target opponent, so the requirement is declared inside the mode rather
 * than lifted to the spell: only the chosen mode's target is announced, and the two modes are
 * independent edicts. "Of their choice" is [Effects.Sacrifice]'s default — the named player picks
 * what to sacrifice, not the spell's controller.
 */
val PharikasLibation = card("Pharika's Libation") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Choose one —\n" +
        "• Target opponent sacrifices a creature of their choice.\n" +
        "• Target opponent sacrifices an enchantment of their choice."

    spell {
        modal {
            mode("Target opponent sacrifices a creature of their choice") {
                val opponent = target("target", Targets.Opponent)
                effect = Effects.Sacrifice(filter = GameObjectFilter.Creature, target = opponent)
            }
            mode("Target opponent sacrifices an enchantment of their choice") {
                val opponent = target("target", Targets.Opponent)
                effect = Effects.Sacrifice(filter = GameObjectFilter.Enchantment, target = opponent)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "111"
        artist = "Jason Felix"
        flavorText = "\"If you will not pour your drink out for me, I shall pour mine out for you.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/3/0307bb5c-0a46-4f6b-b6d5-58cf31987bb5.jpg"
    }
}
