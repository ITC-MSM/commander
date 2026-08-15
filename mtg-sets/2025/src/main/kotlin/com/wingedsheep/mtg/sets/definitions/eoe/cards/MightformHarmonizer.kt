package com.wingedsheep.mtg.sets.definitions.eoe.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ModifyStatsEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Mightform Harmonizer
 * {2}{G}{G}
 * Creature — Insect Druid
 * Landfall — Whenever a land you control enters, double the power of target creature you control until end of turn.
 * Warp {2}{G} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)
 * 4/4
 */
val MightformHarmonizer = card("Mightform Harmonizer") {
    manaCost = "{2}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Insect Druid"
    oracleText = "Landfall — Whenever a land you control enters, double the power of target creature you control until end of turn.\n" +
        "Warp {2}{G} (You may cast this card from your hand for its warp cost. Exile this creature at the beginning of the next end step, then you may cast it from exile on a later turn.)"
    power = 4
    toughness = 4

    // Landfall triggered ability: double power of target creature you control until end of turn
    triggeredAbility {
        trigger = Triggers.LandYouControlEnters
        val creature = target("target creature you control", Targets.CreatureYouControl)
        effect = ModifyStatsEffect(
            powerModifier = DynamicAmount.EntityProperty(EntityReference.Target(0), EntityNumericProperty.Power),
            toughnessModifier = DynamicAmount.Fixed(0),
            target = EffectTarget.ContextTarget(0)
        )
        description = "Whenever a land you control enters, double the power of target creature you control until end of turn."
    }

    // Warp ability
    warp = "{2}{G}"

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "200"
        artist = "Bartek Fedyczak"
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f32302f1-b54f-4489-9d0b-9b771e59da06.jpg?1752947370"
    }
}
