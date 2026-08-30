package com.wingedsheep.mtg.sets.definitions.avr.cards

import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantTriggeredAbility
import com.wingedsheep.sdk.scripting.TriggeredAbility
import com.wingedsheep.sdk.scripting.effects.MayEffect
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.effects.TapUntapEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Ghostly Touch
 * {1}{U}
 * Enchantment — Aura
 *
 * Enchant creature
 * Enchanted creature has "Whenever this creature attacks, you may tap or untap target permanent."
 *
 * The Contaminated Bond shape: the quoted trigger is granted *to* the enchanted creature via
 * [GrantTriggeredAbility] (default attached-creature filter) with [Triggers.Attacks]' own event and
 * SELF binding passed through verbatim. An ATTACHED-bound attack trigger is not indexed by the
 * engine, so installing it on the creature is what makes it fire; it also puts "you" on the
 * creature's controller, which is the printed reading of a granted ability.
 *
 * "Tap or untap" is the Pestermite idiom — a [MayEffect] over a two-[Mode] [ModalEffect] with
 * `countsAsModalSpell = false`, since the choice is made on resolution and is not CR 700.2
 * modality. The permanent is targeted when the trigger goes on the stack; the direction is chosen
 * afterwards, so an opponent responding by tapping it doesn't strand you on the dead half.
 */
val GhostlyTouch = card("Ghostly Touch") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment — Aura"
    oracleText = "Enchant creature\n" +
        "Enchanted creature has \"Whenever this creature attacks, you may tap or untap target " +
        "permanent.\""

    auraTarget = Targets.Creature

    staticAbility {
        ability = GrantTriggeredAbility(
            TriggeredAbility.create(
                trigger = Triggers.Attacks.event,
                binding = Triggers.Attacks.binding,
                effect = MayEffect(
                    ModalEffect(
                        modes = listOf(
                            Mode.noTarget(TapUntapEffect(EffectTarget.ContextTarget(0), tap = true)),
                            Mode.noTarget(TapUntapEffect(EffectTarget.ContextTarget(0), tap = false))
                        ),
                        chooseCount = 1,
                        countsAsModalSpell = false
                    )
                ),
                targetRequirement = Targets.Permanent
            )
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "58"
        artist = "Jason Felix"
        flavorText = "\"Geists wish to make themselves known. I'm merely the vessel for their efforts.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/e/3ebae54a-47e0-4e82-8a29-b5d9354a748b.jpg?1783940718"
    }
}
