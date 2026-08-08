package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.teamwork
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ForEachTargetEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject
import com.wingedsheep.sdk.scripting.targets.TargetPlayer
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Atlantis Attacks — Marvel Super Heroes #46
 * {5}{U}{U} · Sorcery · Common
 *
 * Teamwork 4 (As an additional cost to cast this spell, you may tap any number of creatures you
 * control with total power 4 or more.)
 * Choose one. If this spell was cast using teamwork, choose both instead.
 * • Target player creates a 6/5 blue Leviathan creature token with hexproof.
 * • Return one or two target nonland permanents to their owners' hands.
 *
 * The modal shape of teamwork (CR 702.194c): the cast-time [DynamicAmount.Conditional] on
 * [Conditions.TeamworkWasPaid] narrows the printed `chooseCount = 2` to 1 unless the teamwork cost
 * was declared.
 *
 * "One or two target nonland permanents" is a single requirement with `count = 2, minCount = 1`,
 * so the two chosen permanents must be different (CR 601.2c) and the bounce runs per surviving
 * target via [ForEachTargetEffect] — one target becoming illegal does not strand the other.
 */
val AtlantisAttacks = card("Atlantis Attacks") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Sorcery"
    oracleText = "Teamwork 4 (As an additional cost to cast this spell, you may tap any number of " +
        "creatures you control with total power 4 or more.)\n" +
        "Choose one. If this spell was cast using teamwork, choose both instead.\n" +
        "• Target player creates a 6/5 blue Leviathan creature token with hexproof.\n" +
        "• Return one or two target nonland permanents to their owners' hands."

    teamwork(4)

    spell {
        modal(
            chooseCount = 2,
            minChooseCount = 1,
            dynamicChooseCount = DynamicAmount.Conditional(
                condition = Conditions.TeamworkWasPaid,
                ifTrue = DynamicAmount.Fixed(2),
                ifFalse = DynamicAmount.Fixed(1),
            ),
        ) {
            mode("Target player creates a 6/5 blue Leviathan creature token with hexproof") {
                val player = target("target player", TargetPlayer())
                effect = Effects.CreateToken(
                    power = 6,
                    toughness = 5,
                    colors = setOf(Color.BLUE),
                    creatureTypes = setOf("Leviathan"),
                    keywords = setOf(Keyword.HEXPROOF),
                    controller = player,
                    imageUri = "https://cards.scryfall.io/normal/front/7/1/71211c95-8698-4570-abf7-3579988a329e.jpg?1783902803",
                )
            }
            mode("Return one or two target nonland permanents to their owners' hands") {
                target(
                    "one or two target nonland permanents",
                    TargetObject(count = 2, minCount = 1, filter = TargetFilter.NonlandPermanent),
                )
                effect = ForEachTargetEffect(
                    listOf(Effects.ReturnToHand(EffectTarget.ContextTarget(0))),
                )
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "46"
        artist = "Alexander Skripnikov"
        imageUri = "https://cards.scryfall.io/normal/front/4/0/40bc4380-055d-4913-93cb-280c9c1d1a87.jpg?1783902962"
    }
}
