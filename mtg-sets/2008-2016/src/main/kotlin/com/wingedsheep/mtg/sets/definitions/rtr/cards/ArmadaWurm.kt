package com.wingedsheep.mtg.sets.definitions.rtr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Armada Wurm
 * {2}{G}{G}{W}{W}
 * Creature — Wurm
 * 5/5
 *
 * Trample
 * When this creature enters, create a 5/5 green Wurm creature token with trample.
 *
 * Canonical printing: Return to Ravnica, the card's earliest real printing.
 *
 * An enters trigger over the plain [Effects.CreateToken] facade. The token copies the printed
 * body's stats and keyword but is a separate object — it is not a copy of this permanent.
 */
val ArmadaWurm = card("Armada Wurm") {
    manaCost = "{2}{G}{G}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Wurm"
    oracleText = "Trample\n" +
        "When this creature enters, create a 5/5 green Wurm creature token with trample."
    power = 5
    toughness = 5

    keywords(Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 5,
            toughness = 5,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Wurm"),
            keywords = setOf(Keyword.TRAMPLE),
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "143"
        artist = "Volkan Baǵa"
        flavorText = "No one in the Conclave acts alone."
        imageUri = "https://cards.scryfall.io/normal/front/5/0/50cb4bf3-70d1-4acc-a1fb-49f4ea74ca16.jpg?1783940344"
    }
}
