package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.RegenerateEffect
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Horizon Seed
 * {4}{W}
 * Creature — Spirit
 * 2/1
 * Whenever you cast a Spirit or Arcane spell, regenerate target creature.
 *
 * The Kamigawa "Whenever you cast a Spirit or Arcane spell" trigger is a `SpellCastEvent` watching
 * *your* casts with an OR over the two subtypes — `withAnySubtype` builds the single
 * `CardPredicate.Or` the grammar expects, rather than the `anyOf` branch list that the `or` infix
 * on `GameObjectFilter` would produce. `Triggers.youCastSpell` supplies `Player.You` and
 * `TriggerBinding.ANY`, so Horizon Seed also triggers off its own cast.
 *
 * Regeneration has no `Effects.` facade — `RegenerateEffect` is the corpus spelling (Reknit,
 * Asceticism), and it lays down the one-shot destruction shield rather than acting immediately.
 */
val HorizonSeed = card("Horizon Seed") {
    manaCost = "{4}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast a Spirit or Arcane spell, regenerate target creature."
    power = 2
    toughness = 1
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = RegenerateEffect(t)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "15"
        artist = "Matt Cavotta"
        flavorText = "In peaceful times, these beings escorted the honored kami to their new shrines. In the Kami War, they became the medics of an unstoppable army."
        imageUri = "https://cards.scryfall.io/normal/front/7/e/7e6317b9-a426-453b-a82a-e63905a77019.jpg?1783944339"
    }
}
