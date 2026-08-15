package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetObject

/**
 * Rat King, Verminister
 * {1}{B}
 * Legendary Creature — Rat Avatar
 * 1/1
 *
 * Disappear — At the beginning of your end step, if a permanent left the battlefield
 * under your control this turn, create a 1/1 black Rat creature token and put a +1/+1
 * counter on Rat King.
 * {T}, Sacrifice three Rats: Return target creature card and all other cards with the
 * same name as that card from your graveyard to the battlefield tapped.
 */
val RatKingVerminister = card("Rat King, Verminister") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Rat Avatar"
    oracleText = "Disappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, create a 1/1 black Rat creature token and put a +1/+1 counter on Rat King.\n{T}, Sacrifice three Rats: Return target creature card and all other cards with the same name as that card from your graveyard to the battlefield tapped."
    power = 1
    toughness = 1

    triggeredAbility {
        trigger = Triggers.YourEndStep
        triggerCondition = Conditions.YouHadPermanentLeaveBattlefieldThisTurn
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.BLACK),
            creatureTypes = setOf("Rat")
        ).then(Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self))
        description = "Disappear — At the beginning of your end step, if a permanent left the battlefield under your control this turn, create a 1/1 black Rat creature token and put a +1/+1 counter on Rat King."
    }

    activatedAbility {
        target(
            "target creature card in your graveyard",
            TargetObject(filter = TargetFilter(GameObjectFilter.Creature.ownedByYou(), zone = Zone.GRAVEYARD))
        )
        cost = Costs.Composite(
            Costs.Tap,
            Costs.SacrificeMultiple(3, GameObjectFilter.Creature.withSubtype(Subtype("Rat")))
        )
        effect = Effects.ReturnSameNamedFromGraveyard()
        description = "{T}, Sacrifice three Rats: Return target creature card and all other cards with the same name as that card from your graveyard to the battlefield tapped."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Miklós Ligeti"
        imageUri = "https://cards.scryfall.io/normal/front/b/e/be464d88-8933-46e8-97b0-3be05f1976a3.jpg?1769005882"
    }
}
