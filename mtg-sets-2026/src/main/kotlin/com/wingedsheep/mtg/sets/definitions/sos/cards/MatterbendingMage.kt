package com.wingedsheep.mtg.sets.definitions.sos.cards

import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Matterbending Mage
 * {2}{U}
 * Creature — Human Wizard
 * 2/2
 * When this creature enters, return up to one other target creature to its owner's hand.
 * Whenever you cast a spell with {X} in its mana cost, this creature can't be blocked this turn.
 *
 * The ETB bounce is the standard "up to one other target creature" + `ReturnToHand` (see
 * Rimekin Recluse). The second ability triggers on casting a spell whose *printed cost* has `{X}`
 * (`SpellCastPredicate.HasXInCost`, see Geometer's Arthropod) — X=0 still counts — and grants
 * itself the `CANT_BE_BLOCKED` keyword for the turn via `Effects.GrantKeyword` (a floating keyword
 * grant the projection / blocker-evasion path reads, like Bria, Riptide Rogue — not a granted printed
 * static ability, which the keyword projection wouldn't pick up).
 */
val MatterbendingMage = card("Matterbending Mage") {
    manaCost = "{2}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 2
    oracleText = "When this creature enters, return up to one other target creature to its owner's " +
        "hand.\nWhenever you cast a spell with {X} in its mana cost, this creature can't be " +
        "blocked this turn."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val creature = target(
            "other creature",
            TargetCreature(
                optional = true,
                filter = TargetFilter.OtherCreature
            )
        )
        effect = Effects.ReturnToHand(creature)
    }

    triggeredAbility {
        trigger = Triggers.youCastSpell(
            requires = setOf(SpellCastPredicate.HasXInCost),
        )
        effect = Effects.GrantKeyword(
            AbilityFlag.CANT_BE_BLOCKED,
            EffectTarget.Self,
            Duration.EndOfTurn
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "59"
        artist = "Flavio Greco Paglia"
        imageUri = "https://cards.scryfall.io/normal/front/4/6/460c6afd-cddf-4fea-925f-b27517ff250a.jpg?1775937321"
    }
}
