package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Jade Idol
 * {4}
 * Artifact
 * Whenever you cast a Spirit or Arcane spell, this artifact becomes a 4/4 Spirit artifact creature until end of turn.
 *
 * The Kamigawa "Whenever you cast a Spirit or Arcane spell" trigger is a `SpellCastEvent` watching
 * *your* casts with an OR over the two subtypes — `withAnySubtype` builds the single
 * `CardPredicate.Or` the grammar expects, rather than the `anyOf` branch list that the `or` infix
 * on `GameObjectFilter` would produce. `Triggers.youCastSpell` supplies `Player.You` and
 * `TriggerBinding.ANY`, so Jade Idol also animates off its own cast — though it is not itself a
 * Spirit or Arcane spell, so in practice that only matters for a copy or a text-changing effect.
 *
 * The animation is additive: `BecomeCreature` with no `removeTypes` leaves the Artifact type in
 * place and `addTypes = setOf("ARTIFACT")` restates it, matching the printed "artifact creature".
 * The default `target` is `EffectTarget.Self` (the Idol) and the default duration is end of turn.
 */
val JadeIdol = card("Jade Idol") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "Whenever you cast a Spirit or Arcane spell, this artifact becomes a 4/4 Spirit artifact creature until end of turn."
    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        effect = Effects.BecomeCreature(
            power = 4,
            toughness = 4,
            creatureTypes = setOf(Subtype.SPIRIT.value),
            addTypes = setOf("ARTIFACT")
        )
    }
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "256"
        artist = "Ben Thompson"
        flavorText = "Before the Kami War, the shishi were symbolic guardians, protecting the shrines where they stood. But after the kami turned on the material world, shishi began pouncing from their perches to attack would-be supplicants."
        imageUri = "https://cards.scryfall.io/normal/front/a/5/a58f63c5-cdc5-4079-a727-cff45668d546.jpg?1783944278"
    }
}
