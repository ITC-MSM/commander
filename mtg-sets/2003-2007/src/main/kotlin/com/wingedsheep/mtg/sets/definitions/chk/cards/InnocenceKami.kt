package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Innocence Kami
 * {3}{W}{W}
 * Creature — Spirit
 * 2/3
 * {W}, {T}: Tap target creature.
 * Whenever you cast a Spirit or Arcane spell, untap this creature.
 *
 * The Kamigawa "Whenever you cast a Spirit or Arcane spell" trigger is a `SpellCastEvent` watching
 * *your* casts with an OR over the two subtypes — `withAnySubtype` builds the single
 * `CardPredicate.Or` the grammar expects, rather than the `anyOf` branch list that the `or` infix
 * on `GameObjectFilter` would produce. `Triggers.youCastSpell` supplies `Player.You` and
 * `TriggerBinding.ANY`, so Innocence Kami also triggers off its own cast.
 *
 * Both halves lower to the same `TapUntapEffect` atom: the activated ability taps the chosen
 * target, the trigger untaps the source (`EffectTarget.Self`), which is what lets the tapper fire
 * repeatedly in a Spirit-heavy turn.
 */
val InnocenceKami = card("Innocence Kami") {
    manaCost = "{3}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Spirit"
    oracleText = "{W}, {T}: Tap target creature.\n" +
        "Whenever you cast a Spirit or Arcane spell, untap this creature."
    power = 2
    toughness = 3
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{W}"), Costs.Tap)
        val t = target("target", TargetCreature(filter = TargetFilter.Creature))
        effect = Effects.Tap(t)
    }
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        effect = Effects.Untap(EffectTarget.Self)
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "18"
        artist = "Mark Zug"
        flavorText = "Her voice was light, her substance music."
        imageUri = "https://cards.scryfall.io/normal/front/d/f/df62efbb-2313-4667-82e6-3b474d998ef5.jpg?1783944338"
    }
}
