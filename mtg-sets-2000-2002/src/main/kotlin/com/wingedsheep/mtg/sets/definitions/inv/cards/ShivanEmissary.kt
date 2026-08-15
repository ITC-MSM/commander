package com.wingedsheep.mtg.sets.definitions.inv.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.conditions.WasKicked
import com.wingedsheep.sdk.scripting.effects.CantBeRegeneratedEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Shivan Emissary
 * {2}{R}
 * Creature — Human Wizard
 * 1/1
 * Kicker {1}{B} (You may pay an additional {1}{B} as you cast this spell.)
 * When this creature enters, if it was kicked, destroy target nonblack creature.
 * It can't be regenerated.
 */
val ShivanEmissary = card("Shivan Emissary") {
    manaCost = "{2}{R}"
    colorIdentity = "RB"
    typeLine = "Creature — Human Wizard"
    power = 1
    toughness = 1
    oracleText = "Kicker {1}{B} (You may pay an additional {1}{B} as you cast this spell.)\n" +
        "When this creature enters, if it was kicked, destroy target nonblack creature. It can't be regenerated."

    keywordAbility(KeywordAbility.kicker("{1}{B}"))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = WasKicked
        val creature = target(
            "target nonblack creature",
            TargetCreature(filter = TargetFilter.Creature.notColor(Color.BLACK))
        )
        effect = CantBeRegeneratedEffect(creature) then Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "166"
        artist = "Paolo Parente"
        imageUri = "https://cards.scryfall.io/normal/front/9/4/945c596e-492e-4cf5-857c-4ddbbdd78485.jpg?1562924931"
    }
}
