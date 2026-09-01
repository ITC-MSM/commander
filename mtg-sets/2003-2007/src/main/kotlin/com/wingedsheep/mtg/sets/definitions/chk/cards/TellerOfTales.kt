package com.wingedsheep.mtg.sets.definitions.chk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect

/**
 * Teller of Tales
 * {3}{U}{U}
 * Creature — Spirit
 * 3/3
 * Flying
 * Whenever you cast a Spirit or Arcane spell, you may tap or untap target creature.
 *
 * The shared CHK "Whenever you cast a Spirit or Arcane spell" trigger — [Triggers.youCastSpell]
 * over a homogeneous OR of the two subtype filters, binding `ANY`.
 *
 * "You may tap or untap target creature" is the corpus' Granite Witness idiom: the printed
 * "you may" is the builder's `optional = true` (lowering to a `Gate.MayDecide`), and the
 * tap-or-untap half is a two-[Mode] [ModalEffect] over the single declared target with
 * `countsAsModalSpell = false` — CR 700.2 modality is a property of a *spell*, and this choice is
 * made on resolution. The SDK deliberately has no "tap or untap" effect: [TapUntapEffect] carries
 * the direction as a boolean, so the choice between two fixed directions is what a modal already
 * spells.
 *
 * The target is locked in when the trigger goes on the stack while the direction is not, so an
 * opponent who taps the creature in response doesn't strand you on the useless half.
 */
val TellerOfTales = card("Teller of Tales") {
    manaCost = "{3}{U}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Spirit"
    oracleText = "Flying\n" +
        "Whenever you cast a Spirit or Arcane spell, you may tap or untap target creature."
    power = 3
    toughness = 3

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            spellFilter = GameObjectFilter.Any.withAnySubtype("Spirit", "Arcane")
        )
        val creature = target("target", Targets.Creature)
        effect = ModalEffect(
            modes = listOf(
                Mode.noTarget(TapUntapEffect(creature, tap = true)),
                Mode.noTarget(TapUntapEffect(creature, tap = false))
            ),
            chooseCount = 1,
            countsAsModalSpell = false
        )
        optional = true
        description = "Whenever you cast a Spirit or Arcane spell, you may tap or untap target " +
            "creature."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "95"
        artist = "Jim Murray"
        flavorText = "Words never uttered by mortals flowed incessantly from its many mouths."
        imageUri = "https://cards.scryfall.io/normal/front/c/9/c914db7d-c907-4d25-a180-f68b274a5cb7.jpg?1783944319"
    }
}
