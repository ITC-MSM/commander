package com.wingedsheep.mtg.sets.definitions.stx.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hall Monitor — Strixhaven: School of Mages #105 (canonical printing)
 * {R} · Creature — Lizard Shaman · 1/1
 *
 * Haste
 * {1}{R}, {T}: Target creature can't block this turn.
 *
 * The activated ability is the Blood Aspirant shape: a [Costs.Composite] of mana plus tap, and
 * [Effects.CantBlock] over the single bound creature target, which defaults to end-of-turn.
 */
val HallMonitor = card("Hall Monitor") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Lizard Shaman"
    oracleText =
        "Haste\n" +
        "{1}{R}, {T}: Target creature can't block this turn."
    power = 1
    toughness = 1

    keywords(Keyword.HASTE)

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{1}{R}"), Costs.Tap)
        val creature = target("target", Targets.Creature)
        effect = Effects.CantBlock(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "105"
        artist = "Forrest Imel"
        flavorText = "\"No unauthorized summoning. No writing in the library books. And absolutely no indoor dueling!\""
        imageUri = "https://cards.scryfall.io/normal/front/0/2/02fdc551-0b22-49f4-8765-143ad82f16a3.jpg?1783927354"
    }
}
