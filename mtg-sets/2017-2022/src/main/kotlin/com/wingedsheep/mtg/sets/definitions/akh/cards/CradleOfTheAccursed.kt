package com.wingedsheep.mtg.sets.definitions.akh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Cradle of the Accursed
 *
 * Land — Desert
 * {T}: Add {C}.
 * {3}, {T}, Sacrifice this land: Create a 2/2 black Zombie creature token. Activate only as a sorcery.
 *
 * "Activate only as a sorcery" is [TimingRule.SorcerySpeed]. The token-making ability is not a mana
 * ability even though the first one is.
 */
val CradleOfTheAccursed = card("Cradle of the Accursed") {
    typeLine = "Land — Desert"
    oracleText = "{T}: Add {C}.\n" +
        "{3}, {T}, Sacrifice this land: Create a 2/2 black Zombie creature token. Activate only as a sorcery."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddColorlessMana(1)
        manaAbility = true
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.CreateToken(
            power = 2,
            toughness = 2,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Zombie"),
            imageUri = "https://cards.scryfall.io/normal/front/b/5/b5bd6905-79be-4d2c-a343-f6e6a181b3e6.jpg?1783936411"
        )
        timing = TimingRule.SorcerySpeed
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "241"
        artist = "Noah Bradley"
        flavorText = "Many such monuments dot the wasteland known as Ifnir."
        imageUri = "https://cards.scryfall.io/normal/front/4/1/41713e82-c3d3-4c2f-b075-f684cbd68ce8.jpg?1783936446"
    }
}
