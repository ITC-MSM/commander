package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Tenderize
 * {1}{G}
 * Instant
 *
 * Target creature you control deals damage equal to its power to
 * target creature an opponent controls.
 */
val Tenderize = card("Tenderize") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature you control deals damage equal to its power to target creature an opponent controls."

    spell {
        val myCreature = target("creature you control", Targets.CreatureYouControl)
        val theirCreature = target("creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.DealDamage(
            amount = DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.Power),
            target = theirCreature,
            damageSource = myCreature
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "133"
        artist = "Jo Cordisco"
        flavorText = "\"Get back, all ya'll! Stay away from me! I ain't safe, ya hear! Ain't no friend! I'm a monster!\""
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc19807b-09e8-4923-abc3-d4bbe8cde5fc.jpg?1771502744"
    }
}
