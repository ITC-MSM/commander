package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Earthshaker
 * {4}{R}{R}
 * Creature — Spirit
 * 4/5
 * Whenever you cast a Spirit or Arcane spell, this creature deals 2 damage to each creature without flying.
 *
 * The Kamigawa "Whenever you cast a Spirit or Arcane spell" trigger is a `SpellCastEvent` watching
 * *your* casts with an OR over the two subtypes. `withAnySubtype` builds the single
 * `CardPredicate.Or` the grammar expects, in one call — the `or` infix collapses to the same
 * predicate here, but only because both branches are homogeneous, so the direct spelling is the
 * one all thirteen CHK cards in this family share. `Triggers.youCastSpell` supplies `Player.You`
 * and `TriggerBinding.ANY`, so Earthshaker also triggers off its own cast.
 *
 * The payoff is a group sweep, not a targeted burn: `ForEachInGroup` over every creature lacking
 * flying, with `EffectTarget.Self` inside the body resolving to the current iteration entity.
 * Earthshaker has no flying itself, so it is in its own blast radius (and survives it at 4/5).
 */
val Earthshaker = card("Earthshaker") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast a Spirit or Arcane spell, this creature deals 2 damage to each creature without flying."
    power = 4
    toughness = 5
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        effect = Effects.ForEachInGroup(
            GroupFilter.AllCreatures.withoutKeyword(Keyword.FLYING),
            Effects.DealDamage(2, EffectTarget.Self)
        )
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "165"
        artist = "Ron Spencer"
        flavorText = "It scaled the Sokenzan Mountains in search of Kumano's secret. The mountain shook for two days, and the kami never returned."
        imageUri = "https://cards.scryfall.io/normal/front/2/8/283b47a9-c21e-4c0a-9c70-38b8ebbffab3.jpg?1783944301"
    }
}
