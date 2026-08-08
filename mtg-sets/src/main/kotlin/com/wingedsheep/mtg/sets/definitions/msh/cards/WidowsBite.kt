package com.wingedsheep.mtg.sets.definitions.msh.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.teamwork
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Widow's Bite — Marvel Super Heroes #122
 * {1}{B} · Instant · Common
 *
 * Teamwork 3 (As an additional cost to cast this spell, you may tap any number of creatures you
 * control with total power 3 or more.)
 * Choose one. If this spell was cast using teamwork, choose both instead.
 * • Target creature gains deathtouch until end of turn.
 * • Target creature gets -2/-2 until end of turn.
 *
 * The modal shape of teamwork (CR 702.194c): `chooseCount = 2, minChooseCount = 1` is the printed
 * maximum and floor, and the cast-time [DynamicAmount.Conditional] on
 * [Conditions.TeamworkWasPaid] narrows the effective count to exactly 1 without the declaration
 * and exactly 2 with it. Each mode carries its own "target creature", so the teamwork cast may
 * point them at two different creatures — or at the same one, since separate mode requirements are
 * separate instances of the word "target" (CR 601.2c).
 */
val WidowsBite = card("Widow's Bite") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Teamwork 3 (As an additional cost to cast this spell, you may tap any number of " +
        "creatures you control with total power 3 or more.)\n" +
        "Choose one. If this spell was cast using teamwork, choose both instead.\n" +
        "• Target creature gains deathtouch until end of turn.\n" +
        "• Target creature gets -2/-2 until end of turn."

    teamwork(3)

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
            mode("Target creature gains deathtouch until end of turn") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.GrantKeyword(Keyword.DEATHTOUCH, creature)
            }
            mode("Target creature gets -2/-2 until end of turn") {
                val creature = target("target creature", Targets.Creature)
                effect = Effects.ModifyStats(-2, -2, creature)
            }
        }
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "122"
        artist = "Borja Pindado"
        imageUri = "https://cards.scryfall.io/normal/front/d/b/dbee18af-9ade-4251-81a1-f6e7ffbf480f.jpg?1783902935"
    }
}
