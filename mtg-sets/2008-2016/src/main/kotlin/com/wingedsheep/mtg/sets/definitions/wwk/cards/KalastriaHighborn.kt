package com.wingedsheep.mtg.sets.definitions.wwk.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Kalastria Highborn
 * {B}{B}
 * Creature — Vampire Shaman
 * 2/2
 * Whenever this creature or another Vampire you control dies, you may pay {B}. If you do,
 *   target player loses 2 life and you gain 2 life.
 *
 * Canonical printing lives in Worldwake (the earliest real printing); Foundations is a
 * [com.wingedsheep.sdk.model.Printing] row (see `.../definitions/fdn/cards/KalastriaHighbornReprint.kt`).
 *
 * "This creature or another Vampire you control" is exactly "a Vampire you control" — the source is
 * itself a Vampire — so the trigger is a Vampire-filtered dies event with [TriggerBinding.ANY], which
 * fires off the source's own death via last-known information. The target player is chosen when the
 * ability goes on the stack; the {B} payment is the resolution-time gate ([MayPayManaEffect]) for the
 * drain.
 */
val KalastriaHighborn = card("Kalastria Highborn") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Vampire Shaman"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature or another Vampire you control dies, you may pay {B}. " +
        "If you do, target player loses 2 life and you gain 2 life."

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.Creature.withSubtype("Vampire").youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.ANY,
        )
        val player = target("target player", Targets.Player)
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{B}"),
            effect = Effects.Composite(
                Effects.LoseLife(2, player),
                Effects.GainLife(2, EffectTarget.Controller),
            ),
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "59"
        artist = "D. Alexander Gregory"
        flavorText = "Each of Malakir's great families boasts a contingent of nulls appropriate " +
            "to its rank in society."
        imageUri = "https://cards.scryfall.io/normal/front/f/1/f1efd1dd-903c-47a0-b746-5571a3ea1755.jpg?1782715561"
    }
}
