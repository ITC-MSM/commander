package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.CantBeRegeneratedEffect

/**
 * Verduran Emissary
 * {2}{G}
 * Creature — Human Wizard
 * 2/3
 * Kicker {1}{R} (You may pay an additional {1}{R} as you cast this spell.)
 * When this creature enters, if it was kicked, destroy target artifact. It can't be regenerated.
 */
val VerduranEmissary = card("Verduran Emissary") {
    manaCost = "{2}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 3
    oracleText = "Kicker {1}{R} (You may pay an additional {1}{R} as you cast this spell.)\n" +
        "When this creature enters, if it was kicked, destroy target artifact. It can't be regenerated."

    keywordAbility(KeywordAbility.kicker("{1}{R}"))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = WasKicked
        val t = target("artifact", Targets.Artifact)
        effect = CantBeRegeneratedEffect(t) then Effects.Destroy(t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "221"
        artist = "Alton Lawson"
        imageUri = "https://cards.scryfall.io/normal/front/5/5/55f3361b-e2e7-4297-85c2-94323f90cc90.jpg?1562912510"
    }
}
