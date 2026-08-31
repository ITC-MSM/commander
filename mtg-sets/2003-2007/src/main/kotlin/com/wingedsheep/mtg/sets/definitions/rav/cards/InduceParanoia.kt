package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.ConditionalEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Induce Paranoia — Ravnica: City of Guilds #56
 * {2}{U}{U} · Instant
 *
 * Counter target spell. If {B} was spent to cast this spell, that spell's controller mills X cards,
 * where X is the spell's mana value.
 *
 * Another of Ravnica's "if {X} was spent" riders: the card is mono-blue and the black mana is a
 * *payment* question, so a Dimir land or an any-colour source switches the mill on.
 *
 * Both halves of the rider read the countered spell *after* it has already been put into its
 * owner's graveyard, which is why they use the last-known-information references rather than a live
 * battlefield lookup: `Player.ControllerOf` resolves the spell's controller as of the counter, and
 * `targetManaValue` reads the mana value off the card it became. "The spell's mana value" is the
 * countered spell's, not Induce Paranoia's — X for an X spell on the stack counts as the value
 * chosen for it (CR 202.3b).
 */
val InduceParanoia = card("Induce Paranoia") {
    manaCost = "{2}{U}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Counter target spell. If {B} was spent to cast this spell, that spell's " +
        "controller mills X cards, where X is the spell's mana value."

    spell {
        target("target spell", Targets.Spell)
        effect = Effects.CounterSpell()
            .then(
                ConditionalEffect(
                    condition = Conditions.ManaSpentToCastIncludes(requiredBlack = 1),
                    effect = Patterns.Library.mill(
                        DynamicAmounts.targetManaValue(0),
                        EffectTarget.PlayerRef(Player.ControllerOf("target spell"))
                    )
                )
            )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "56"
        artist = "Jim Murray"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc462b75-8b08-47a3-be22-d7b5c062ec5b.jpg?1783943682"
    }
}
