package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked

/**
 * Benalish Emissary
 * {2}{W}
 * Creature — Human Wizard
 * 1/4
 * Kicker {1}{G} (You may pay an additional {1}{G} as you cast this spell.)
 * When this creature enters, if it was kicked, destroy target land.
 */
val BenalishEmissary = card("Benalish Emissary") {
    manaCost = "{2}{W}"
    colorIdentity = "WG"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 4
    oracleText = "Kicker {1}{G} (You may pay an additional {1}{G} as you cast this spell.)\n" +
        "When this creature enters, if it was kicked, destroy target land."

    keywordAbility(KeywordAbility.kicker("{1}{G}"))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = WasKicked
        val t = target("land", Targets.Land)
        effect = Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "5"
        artist = "Randy Gallegos"
        imageUri = "https://cards.scryfall.io/normal/front/6/b/6b82d56e-80d7-4be9-ac22-de3257efc458.jpg?1562916610"
    }
}
