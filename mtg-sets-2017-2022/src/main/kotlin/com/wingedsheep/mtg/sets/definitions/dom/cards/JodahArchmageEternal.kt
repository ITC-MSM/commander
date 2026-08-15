package com.wingedsheep.mtg.sets.definitions.dom.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantAlternativeCastingCost

/**
 * Jodah, Archmage Eternal
 * {1}{U}{R}{W}
 * Legendary Creature — Human Wizard
 * 4/3
 * Flying
 * You may pay {W}{U}{B}{R}{G} rather than pay the mana cost for spells you cast.
 */
val JodahArchmageEternal = card("Jodah, Archmage Eternal") {
    manaCost = "{1}{U}{R}{W}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Creature — Human Wizard"
    power = 4
    toughness = 3
    oracleText = "Flying\nYou may pay {W}{U}{B}{R}{G} rather than pay the mana cost for spells you cast."

    keywords(Keyword.FLYING)

    staticAbility {
        ability = GrantAlternativeCastingCost(cost = "{W}{U}{B}{R}{G}")
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "198"
        artist = "Yongjae Choi"
        flavorText = "\"Chronicles across the ages describe Jodah. They likely refer not to one mage, but to a family or an arcane title.\" —Arkol, Argivian scholar"
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1ee86efa-248e-4251-b734-f8ad3e8a0344.jpg?1562732370"
        ruling("2018-04-27", "Jodah's ability is an alternative cost to cast a spell. You can't combine this with other alternative costs, such as flashback.")
        ruling("2018-04-27", "If you apply Jodah's alternative cost to a spell with {X} in its mana cost, X is 0.")
    }
}
