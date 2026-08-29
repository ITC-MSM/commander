package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Eyes of the Wisent
 * {1}{G}
 * Kindred Enchantment — Elemental
 * Whenever an opponent casts a blue spell during your turn, you may create a 4/4 green
 * Elemental creature token.
 *
 * "During your turn" is a CR 603.2 restriction on the trigger event, not an intervening "if" —
 * it is checked only when the spell is cast, so a trigger that fires on your turn still resolves
 * if the turn somehow changes underneath it. Hence [triggerRestriction], not `interveningIf`.
 */
val EyesOfTheWisent = card("Eyes of the Wisent") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Kindred Enchantment — Elemental"
    oracleText = "Whenever an opponent casts a blue spell during your turn, you may create a " +
        "4/4 green Elemental creature token."

    triggeredAbility {
        trigger = Triggers.opponentCasts(GameObjectFilter.Any.withColor(Color.BLUE))
        triggerRestriction = Conditions.IsYourTurn
        optional = true
        effect = Effects.CreateToken(
            power = 4,
            toughness = 4,
            colors = setOf(Color.GREEN),
            creatureTypes = setOf("Elemental"),
            imageUri = "https://cards.scryfall.io/normal/front/8/b/8b4ef48d-a328-4eb9-a2e3-925c1cd38b1a.jpg?1783942837",
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "210"
        artist = "Aleksi Briclot"
        flavorText = "Calm as a wisent's watch\n—Elvish expression meaning \"safe\""
        imageUri = "https://cards.scryfall.io/normal/front/c/6/c6725b96-7390-4599-9d5f-ba08755605af.jpg?1783942865"
    }
}
