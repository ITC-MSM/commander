package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Silvergill Douser
 * {1}{U}
 * Creature — Merfolk Wizard
 * 1/1
 * {T}: Target creature gets -X/-0 until end of turn, where X is the number of Merfolk and/or Faeries you control.
 *
 * "Merfolk and/or Faeries" is a subtype union over permanents you control, so it is one
 * [GameObjectFilter.withAnySubtype] count — a changeling is counted once, not twice. The penalty is
 * the Spontaneous Mutation idiom, `Multiply(count, -1)`, since [DynamicAmount] has no negation of its
 * own. The Douser itself is a Merfolk and counts, so the floor is -1/-0.
 */
val SilvergillDouser = card("Silvergill Douser") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Merfolk Wizard"
    power = 1
    toughness = 1
    oracleText = "{T}: Target creature gets -X/-0 until end of turn, where X is the number of Merfolk and/or Faeries you control."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(
            power = DynamicAmount.Multiply(
                DynamicAmount.AggregateBattlefield(
                    Player.You,
                    GameObjectFilter.Permanent.withAnySubtype(Subtype.MERFOLK.value, Subtype.FAERIE.value)
                ),
                -1
            ),
            toughness = DynamicAmount.Fixed(0),
            target = creature
        )
        description = "Target creature gets -X/-0 until end of turn, where X is the number of Merfolk and/or Faeries you control."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "87"
        artist = "Daren Bader"
        flavorText = "\"The Silvergill school monitors traffic on the Lanes, ensuring that the riffraff don't interfere with travelers.\""
        imageUri = "https://cards.scryfall.io/normal/front/8/8/8875c883-8d56-406b-bd89-42199a6e79f5.jpg?1783942898"
    }
}
