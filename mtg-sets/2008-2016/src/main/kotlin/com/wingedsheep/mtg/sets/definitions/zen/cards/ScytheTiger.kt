package com.wingedsheep.mtg.sets.definitions.zen.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Scythe Tiger
 * {G}
 * Creature — Cat
 * 3/2
 * Shroud (This creature can't be the target of spells or abilities.)
 * When this creature enters, sacrifice it unless you sacrifice a land.
 *
 * "Sacrifice it unless you …" is the punisher shape: the sacrifice is the *suffer* branch, and
 * shroud does not stop it because the trigger doesn't target.
 */
val ScytheTiger = card("Scythe Tiger") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Cat"
    power = 3
    toughness = 2
    oracleText = "Shroud (This creature can't be the target of spells or abilities.)\n" +
        "When this creature enters, sacrifice it unless you sacrifice a land."

    keywords(Keyword.SHROUD)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = PayOrSufferEffect(
            cost = Costs.pay.Sacrifice(GameObjectFilter.Land),
            suffer = SacrificeSelfEffect,
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "183"
        artist = "Michael Komarck"
        flavorText = "Instead of adapting to its environment, it adapts its environment to itself."
        imageUri = "https://cards.scryfall.io/normal/front/9/d/9d289043-edb5-4a7e-801b-4881709edb32.jpg"
    }
}
