package com.wingedsheep.mtg.sets.definitions.mid.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.TargetCreatureOrPlaneswalker
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Voldaren Ambusher
 * {2}{R}
 * Creature — Vampire Archer
 * 2/2
 * When this creature enters, if an opponent lost life this turn, it deals X damage to up to one
 * target creature or planeswalker, where X is the number of Vampires you control.
 *
 * Intervening-if ETB ([Conditions.OpponentLostLifeThisTurn]) with an optional single target;
 * damage is [DynamicAmount.AggregateBattlefield] counting Vampires you control (the source itself
 * counts, matching the printed rulings). The source of the damage defaults to this creature.
 */
val VoldarenAmbusher = card("Voldaren Ambusher") {
    manaCost = "{2}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire Archer"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, if an opponent lost life this turn, it deals X damage to " +
        "up to one target creature or planeswalker, where X is the number of Vampires you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        triggerCondition = Conditions.OpponentLostLifeThisTurn
        val t = target(
            "up to one target creature or planeswalker",
            TargetCreatureOrPlaneswalker(optional = true)
        )
        effect = Effects.DealDamage(
            DynamicAmount.AggregateBattlefield(Player.You, GameObjectFilter.Creature.withSubtype("Vampire")),
            t
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "166"
        artist = "Evyn Fong"
        imageUri = "https://cards.scryfall.io/normal/front/c/c/cceb4303-5d02-45f2-86f8-7e8deb774d58.jpg?1782703623"
    }
}
