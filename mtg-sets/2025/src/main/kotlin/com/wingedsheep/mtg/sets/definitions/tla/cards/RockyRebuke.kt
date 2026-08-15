package com.wingedsheep.mtg.sets.definitions.tla.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Rocky Rebuke
 * {1}{G}
 * Instant
 *
 * Target creature you control deals damage equal to its power to target creature an opponent controls.
 */
val RockyRebuke = card("Rocky Rebuke") {
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
        collectorNumber = "193"
        artist = "Hokyoung Kim"
        flavorText = "\"I am the greatest Earthbender in the world; don't you two dunderheads ever forget it!\""
        imageUri = "https://cards.scryfall.io/normal/front/5/2/52fd910f-6d42-41f5-a4be-f375aa254ea2.jpg?1764121313"
    }
}
