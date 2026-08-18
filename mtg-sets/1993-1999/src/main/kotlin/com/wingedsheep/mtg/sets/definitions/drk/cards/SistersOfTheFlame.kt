package com.wingedsheep.mtg.sets.definitions.drk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Sisters of the Flame
 * {1}{R}{R}
 * Creature — Human Shaman
 * 2/2
 * {T}: Add {R}.
 */
val SistersOfTheFlame = card("Sisters of the Flame") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "{T}: Add {R}."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddMana(Color.RED)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "74"
        artist = "Jesper Myrfors"
        flavorText = "We are many wicks sharing a common tallow; we feed the skies with the ashes of our prey."
        imageUri = "https://cards.scryfall.io/normal/front/5/6/564e0ccd-decb-48d2-981f-cefa8045340f.jpg?1783947934"
    }
}
