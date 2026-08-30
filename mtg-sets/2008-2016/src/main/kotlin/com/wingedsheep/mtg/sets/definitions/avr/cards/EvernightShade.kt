package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Evernight Shade
 * {3}{B}
 * Creature — Shade
 * 1 / 1
 *
 * {B}: This creature gets +1/+1 until end of turn.
 * Undying (When this creature dies, if it had no +1/+1 counters on it, return it to the battlefield under its owner's control with a +1/+1 counter on it.)
 *
 * The Shade pump is the family's canonical shape: a bare [Costs.Mana] atom and
 * [Effects.ModifyStats] on [EffectTarget.Self] with the default until-end-of-turn duration.
 * Undying is a printed [Keyword] the engine reads on its own.
 */
val EvernightShade = card("Evernight Shade") {
    manaCost = "{3}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Shade"
    power = 1
    toughness = 1
    oracleText = "{B}: This creature gets +1/+1 until end of turn.\n" +
        "Undying (When this creature dies, if it had no +1/+1 counters on it, return it to the " +
        "battlefield under its owner's control with a +1/+1 counter on it.)"

    keywords(Keyword.UNDYING)

    activatedAbility {
        cost = Costs.Mana("{B}")
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "101"
        artist = "Nic Klein"
        imageUri = "https://cards.scryfall.io/normal/front/1/0/1091fadf-97c4-4f87-8466-6a1246a72226.jpg?1783940698"
    }
}
