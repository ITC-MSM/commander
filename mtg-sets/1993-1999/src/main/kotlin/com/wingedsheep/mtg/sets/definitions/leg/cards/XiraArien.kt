package com.wingedsheep.mtg.sets.definitions.leg.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Xira Arien
 * {B}{R}{G}
 * Legendary Creature — Insect Wizard
 * 1/2
 *
 * Flying
 * {B}{R}{G}, {T}: Target player draws a card.
 */
val XiraArien = card("Xira Arien") {
    manaCost = "{B}{R}{G}"
    colorIdentity = "BGR"
    typeLine = "Legendary Creature — Insect Wizard"
    power = 1
    toughness = 2
    oracleText = "Flying\n{B}{R}{G}, {T}: Target player draws a card."

    keywords(Keyword.FLYING)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{B}{R}{G}"), Costs.Tap)
        val player = target("target player", Targets.Player)
        effect = Effects.DrawCards(1, player)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "270"
        artist = "Melissa A. Benson"
        flavorText = "A regular guest at the Royal Masquerade, Arien is the envy of the Court. She appears in a " +
            "new costume every hour."
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cc6c7d89-32e7-4c3f-ac90-7db3a46eed4b.jpg?1783948030"
    }
}
