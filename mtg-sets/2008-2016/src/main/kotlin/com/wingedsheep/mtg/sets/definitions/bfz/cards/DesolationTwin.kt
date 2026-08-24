package com.wingedsheep.mtg.sets.definitions.bfz.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Desolation Twin
 * {10}
 * Creature — Eldrazi
 * 10/10
 * When you cast this spell, create a 10/10 colorless Eldrazi creature token.
 *
 * "When you cast this spell" is a cast trigger, so the 10/10 token arrives while the
 * Twin is still on the stack — countering the Twin does not undo it.
 */
val DesolationTwin = card("Desolation Twin") {
    manaCost = "{10}"
    colorIdentity = ""
    typeLine = "Creature — Eldrazi"
    power = 10
    toughness = 10
    oracleText = "When you cast this spell, create a 10/10 colorless Eldrazi creature token."

    triggeredAbility {
        trigger = Triggers.WhenYouCastThisSpell()
        effect = Effects.CreateToken(
            power = 10,
            toughness = 10,
            creatureTypes = setOf("Eldrazi"),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "6"
        artist = "Jack Wang"
        flavorText = "\"With precise coordination and enough blood spilled, one can be driven off, even brought " +
            "down. But two . . . that's a lot of blood.\"\n" +
            "—Munda, ambush leader"
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4d229d8d-5e64-4403-a4ae-a0a186a83935.jpg?1783938224"
    }
}
