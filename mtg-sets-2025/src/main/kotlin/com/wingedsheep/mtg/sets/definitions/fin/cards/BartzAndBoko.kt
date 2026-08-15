package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.DynamicAmount
import com.wingedsheep.sdk.scripting.values.EntityNumericProperty
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Bartz and Boko
 * {3}{G}{G}
 * Legendary Creature — Human Bird
 * 4/3
 * Affinity for Birds (This spell costs {1} less to cast for each Bird you control.)
 * When Bartz and Boko enters, each other Bird you control deals damage equal to its power to
 * target creature an opponent controls.
 *
 * The ETB declares the single victim ("target creature an opponent controls") and then loops over
 * every *other* Bird you control, each dealing damage equal to its own power — read per-iteration
 * via [EntityReference.IterationEntity], with [EffectTarget.Self] as the per-Bird damage source.
 * Mirrors Coordinated Clobbering (DSK), minus the per-source targeting (here the Birds are a group,
 * not chosen targets).
 */
val BartzAndBoko = card("Bartz and Boko") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Human Bird"
    oracleText = "Affinity for Birds (This spell costs {1} less to cast for each Bird you control.)\n" +
        "When Bartz and Boko enters, each other Bird you control deals damage equal to its power " +
        "to target creature an opponent controls."
    power = 4
    toughness = 3

    keywordAbility(KeywordAbility.AffinityForSubtype(Subtype.BIRD))

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val victim = target("target creature an opponent controls", Targets.CreatureOpponentControls)
        effect = Effects.ForEachInGroup(
            filter = GroupFilter.AllCreaturesYouControl.withSubtype(Subtype.BIRD).other(),
            effect = Effects.DealDamage(
                amount = DynamicAmount.EntityProperty(
                    EntityReference.IterationEntity,
                    EntityNumericProperty.Power,
                ),
                target = victim,
                damageSource = EffectTarget.Self,
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "175"
        artist = "Ryuichi Sakuma"
        flavorText = "\"Call me Bartz. Me and my chocobo just go wherever the trail leads us...\""
        imageUri = "https://cards.scryfall.io/normal/front/d/8/d818d574-2832-4a7a-a13b-aa6e695fdaa5.jpg?1748706414"
    }
}
