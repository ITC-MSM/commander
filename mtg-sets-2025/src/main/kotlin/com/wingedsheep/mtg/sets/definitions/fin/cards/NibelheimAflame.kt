package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Nibelheim Aflame
 * {2}{R}{R}
 * Sorcery
 *
 * Choose target creature you control. It deals damage equal to its power to each other creature.
 * If this spell was cast from a graveyard, discard your hand and draw four cards.
 * Flashback {5}{R}{R}
 *
 * The chosen creature deals damage equal to its power to each OTHER creature: a
 * [Effects.ForEachInGroup] over every creature except the chosen one
 * (`GroupFilter(...).otherThanTarget()`), each iterated creature ([EffectTarget.Self]) taking
 * [DynamicAmounts.targetPower] damage *from the chosen creature itself* (`damageSource = chosen`),
 * so its combat keywords and "dealt damage by" triggers see the correct source. The graveyard-cast
 * rider is a [ConditionalEffect] gated on [Conditions.WasCastFromGraveyard] — true when the
 * flashback cast resolves.
 */
val NibelheimAflame = card("Nibelheim Aflame") {
    manaCost = "{2}{R}{R}"
    colorIdentity = "R"
    typeLine = "Sorcery"
    oracleText = "Choose target creature you control. It deals damage equal to its power to each other creature. " +
        "If this spell was cast from a graveyard, discard your hand and draw four cards.\n" +
        "Flashback {5}{R}{R} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"

    spell {
        val chosen = target("target creature you control", Targets.CreatureYouControl)
        effect = Effects.Composite(
            Effects.ForEachInGroup(
                filter = GroupFilter(GameObjectFilter.Creature).otherThanTarget(),
                effect = DealDamageEffect(
                    amount = DynamicAmounts.targetPower(0),
                    target = EffectTarget.Self,
                    damageSource = chosen,
                ),
            ),
            ConditionalEffect(
                condition = Conditions.WasCastFromGraveyard,
                effect = Effects.Composite(
                    Patterns.Hand.discardHand(),
                    Effects.DrawCards(4),
                ),
            ),
        )
    }

    keywordAbility(KeywordAbility.flashback("{5}{R}{R}"))

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "146"
        artist = "Arou"
        imageUri = "https://cards.scryfall.io/normal/front/3/d/3d3e926a-74af-4996-849f-d31e0fdedeae.jpg?1748706306"
    }
}
