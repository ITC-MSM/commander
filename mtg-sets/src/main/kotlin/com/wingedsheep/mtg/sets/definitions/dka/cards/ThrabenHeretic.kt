package com.wingedsheep.mtg.sets.definitions.dka.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Thraben Heretic
 * {1}{W}
 * Creature — Human Wizard
 * 2/2
 * {T}: Exile target creature card from a graveyard.
 */
val ThrabenHeretic = card("Thraben Heretic") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Wizard"
    oracleText = "{T}: Exile target creature card from a graveyard."
    power = 2
    toughness = 2

    activatedAbility {
        cost = Costs.Tap
        val t = target("target creature card from a graveyard", Targets.CreatureCardInGraveyard)
        effect = Effects.Move(t, Zone.EXILE)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "26"
        artist = "James Ryman"
        flavorText =
            "\"Let them decry me for burning the dead. I'm not giving those ghoulcallers any more fuel for their madness.\""
        imageUri =
            "https://cards.scryfall.io/normal/front/f/8/f8cc36df-040b-4f29-bcc1-f5600803f71d.jpg?1783940850"
    }
}
