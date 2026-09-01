package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kami of the Hunt
 * {2}{G}
 * Creature — Spirit
 * 2/2
 * Whenever you cast a Spirit or Arcane spell, this creature gets +1/+1 until end of turn.
 *
 * The Kamigawa "Whenever you cast a Spirit or Arcane spell" trigger is a `SpellCastEvent` watching
 * *your* casts with an OR over the two subtypes — `withAnySubtype` builds the single
 * `CardPredicate.Or` the grammar expects, rather than the `anyOf` branch list that the `or` infix
 * on `GameObjectFilter` would produce. `Triggers.youCastSpell` supplies `Player.You` and
 * `TriggerBinding.ANY`, so Kami of the Hunt also triggers off its own cast.
 *
 * The pump names `EffectTarget.Self` rather than the triggering entity: the boost belongs to the
 * Kami, not to the spell that set it off.
 */
val KamiOfTheHunt = card("Kami of the Hunt") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Spirit"
    oracleText = "Whenever you cast a Spirit or Arcane spell, this creature gets +1/+1 until end of turn."
    power = 2
    toughness = 2
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        effect = Effects.ModifyStats(1, 1, EffectTarget.Self)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "219"
        artist = "Alex Horley-Orlandelli"
        flavorText = "\"Don't worry, Jiro. The kami would never attack us this close to home . . . . Jiro?\"\n—Hoto, temple guardian, last words"
        imageUri = "https://cards.scryfall.io/normal/front/5/a/5ace3c4e-3287-4975-b4d0-91f009c0cf5b.jpg?1783944287"
    }
}
