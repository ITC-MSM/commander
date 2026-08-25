package com.wingedsheep.mtg.sets.definitions.tsp.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.PayOrSufferEffect
import com.wingedsheep.sdk.scripting.effects.SacrificeSelfEffect

/**
 * Drifter il-Dal
 * {U}
 * Creature — Human Wizard
 * 2/1
 * Shadow (This creature can block or be blocked by only creatures with shadow.)
 * At the beginning of your upkeep, sacrifice this creature unless you pay {U}.
 *
 * Shadow is read straight off the keyword by the block-evasion rules (symmetric: a shadow
 * creature can neither block nor be blocked by anything without shadow). The upkeep clause is the
 * standard cumulative-rent shape — [PayOrSufferEffect] with [SacrificeSelfEffect] as the punisher.
 */
val DrifterIlDal = card("Drifter il-Dal") {
    manaCost = "{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "Shadow (This creature can block or be blocked by only creatures with shadow.)\n" +
        "At the beginning of your upkeep, sacrifice this creature unless you pay {U}."

    keywords(Keyword.SHADOW)

    triggeredAbility {
        trigger = Triggers.YourUpkeep
        effect = PayOrSufferEffect(cost = Costs.pay.Mana("{U}"), suffer = SacrificeSelfEffect)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "59"
        artist = "Justin Sweet"
        flavorText = "They study the deeds of their traitorous ancestors, hoping the stories may reveal a way back to the physical world."
        imageUri = "https://cards.scryfall.io/normal/front/4/c/4c3de909-b47e-45d7-9922-8d5e08a76ec9.jpg"
    }
}
