package com.wingedsheep.mtg.sets.definitions.m19.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule

/**
 * Draconic Disciple
 * {1}{R}{G}
 * Creature — Human Shaman
 * 2/2
 * {T}: Add one mana of any color.
 * {7}, {T}, Sacrifice this creature: Create a 5/5 red Dragon creature token with flying.
 */
val DraconicDisciple = card("Draconic Disciple") {
    manaCost = "{1}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Human Shaman"
    power = 2
    toughness = 2
    oracleText = "{T}: Add one mana of any color.\n" +
        "{7}, {T}, Sacrifice this creature: Create a 5/5 red Dragon creature token with flying."

    activatedAbility {
        cost = Costs.Tap
        effect = Effects.AddAnyColorMana(1)
        manaAbility = true
        timing = TimingRule.ManaAbility
    }

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{7}"), Costs.Tap, Costs.SacrificeSelf)
        effect = Effects.CreateToken(
            power = 5,
            toughness = 5,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Dragon"),
            keywords = setOf(Keyword.FLYING)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Yongjae Choi"
        flavorText = "\"If I am to die, I will die in the embrace of immeasurable flame.\""
        imageUri = "https://cards.scryfall.io/normal/front/a/3/a353510a-30de-4891-97b9-d7d556531c41.jpg"
    }
}
