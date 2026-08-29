package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Dimir Guildmage
 * {U/B}{U/B}
 * Creature — Human Wizard
 * 2/2
 * ({U/B} can be paid with either {U} or {B}.)
 * {3}{U}: Target player draws a card. Activate only as a sorcery.
 * {3}{B}: Target player discards a card. Activate only as a sorcery.
 */
val DimirGuildmage = card("Dimir Guildmage") {
    manaCost = "{U/B}{U/B}"
    colorIdentity = "UB"
    typeLine = "Creature — Human Wizard"
    oracleText = "({U/B} can be paid with either {U} or {B}.)\n" +
        "{3}{U}: Target player draws a card. Activate only as a sorcery.\n" +
        "{3}{B}: Target player discards a card. Activate only as a sorcery."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Mana("{3}{U}")
        val p = target("target player", Targets.Player)
        effect = Effects.DrawCards(1, p)
        timing = TimingRule.SorcerySpeed
    }

    activatedAbility {
        cost = Costs.Mana("{3}{B}")
        val p = target("target player", Targets.Player)
        effect = Patterns.Hand.discardCards(1, p)
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "245"
        artist = "Adam Rex"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69b822aa-4144-400a-b993-f146cbeed54f.jpg"
    }
}
