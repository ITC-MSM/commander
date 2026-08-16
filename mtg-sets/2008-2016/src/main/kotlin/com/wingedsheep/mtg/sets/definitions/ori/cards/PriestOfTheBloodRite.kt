package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Priest of the Blood Rite
 * {3}{B}{B}
 * Creature — Human Cleric
 * 2/2
 * When this creature enters, create a 5/5 black Demon creature token with flying.
 * At the beginning of your upkeep, you lose 2 life.
 *
 * The Demon token is the same 5/5 black flier Skirsdag High Priest mints, so it reuses that
 * token's art. The upkeep drawback is unconditional — no "unless", no sacrifice.
 */
val PriestOfTheBloodRite = card("Priest of the Blood Rite") {
    manaCost = "{3}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Cleric"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, create a 5/5 black Demon creature token with flying.\nAt the beginning of your upkeep, you lose 2 life." +
        "At the beginning of your upkeep, you lose 2 life."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 5,
            toughness = 5,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Demon"),
            keywords = setOf(Keyword.FLYING),
            imageUri = "https://cards.scryfall.io/normal/front/7/7/771ae1f8-70b3-40da-8352-421a36c7abb5.jpg?1783940883"
        )
        description = "When this creature enters, create a 5/5 black Demon creature token with flying."
    }

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = Effects.LoseLife(2, EffectTarget.Controller)
        description = "At the beginning of your upkeep, you lose 2 life."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "112"
        artist = "David Palumbo"
        flavorText = "Some will sacrifice everything for but a taste of power."
        imageUri = "https://cards.scryfall.io/normal/front/1/1/118ba6f2-a2f0-4554-ad87-a932c951cdd4.jpg?1783938338"
    }
}
