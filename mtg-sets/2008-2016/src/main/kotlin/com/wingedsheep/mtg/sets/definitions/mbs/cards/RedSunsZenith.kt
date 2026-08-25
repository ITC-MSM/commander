package com.wingedsheep.mtg.sets.definitions.mbs.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.effects.MarkExileOnDeathEffect
import com.wingedsheep.sdk.scripting.targets.AnyTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Red Sun's Zenith — Mirrodin Besieged #74 (canonical / earliest real printing, 2011)
 * {X}{R} · Sorcery
 *
 * Red Sun's Zenith deals X damage to any target. If a creature dealt damage this way would die
 * this turn, exile it instead. Shuffle Red Sun's Zenith into its owner's library.
 *
 * Carbonize's shape exactly: `AnyTarget()` plus [MarkExileOnDeathEffect], the CR 614 death
 * replacement that lasts the turn. The mark goes on *before* the damage, as on Carbonize, so a
 * creature killed by this very spell is exiled rather than merely a creature that survives it and
 * dies later. Marking a player or planeswalker is a no-op — "a creature dealt damage this way" is
 * the only thing the clause speaks to.
 */
val RedSunsZenith = card("Red Sun's Zenith") {
    manaCost = "{X}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Red Sun's Zenith deals X damage to any target. If a creature dealt damage this " +
        "way would die this turn, exile it instead. Shuffle Red Sun's Zenith into its owner's library."

    spell {
        val t = target("any target", AnyTarget())
        effect = MarkExileOnDeathEffect(t) then DealDamageEffect(DynamicAmount.XValue, t)
        selfShuffleIntoLibrary()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "74"
        artist = "Svetlin Velinov"
        imageUri = "https://cards.scryfall.io/normal/front/3/7/373eb109-0e30-41c1-b2df-6bc78d968890.jpg?1783941377"
        ruling(
            "2011-06-01",
            "If this spell doesn't resolve, none of its effects occur. In particular, it will go " +
                "to the graveyard rather than to its owner's library."
        )
    }
}
