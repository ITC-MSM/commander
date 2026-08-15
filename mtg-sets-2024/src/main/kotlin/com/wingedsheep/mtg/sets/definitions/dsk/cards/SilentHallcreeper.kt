package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CantBeBlocked
import com.wingedsheep.sdk.scripting.Duration
import com.wingedsheep.sdk.scripting.effects.ModalEffect
import com.wingedsheep.sdk.scripting.effects.Mode
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Silent Hallcreeper
 * {1}{U}
 * Enchantment Creature — Horror
 * 1/1
 *
 * This creature can't be blocked.
 * Whenever this creature deals combat damage to a player, choose one that hasn't been chosen —
 * • Put two +1/+1 counters on this creature.
 * • Draw a card.
 * • This creature becomes a copy of another target creature you control.
 *
 * "Choose one that hasn't been chosen" is modeled with [ModalEffect.chooseOneNotYetChosen]: the
 * engine remembers which modes this Hallcreeper has already chosen and never offers them again, so
 * across multiple combat-damage triggers each mode is used at most once. Once all three have been
 * chosen the ability does nothing.
 *
 * The copy mode targets "another target creature you control" ([Targets.OtherCreatureYouControl])
 * and makes this creature ([EffectTarget.Self], `affected`) become a copy of that target until it
 * leaves play (the copy is permanent per the card — no duration clause), copying copiable values
 * only (Rule 707).
 */
val SilentHallcreeper = card("Silent Hallcreeper") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment Creature — Horror"
    power = 1
    toughness = 1
    oracleText = "This creature can't be blocked.\n" +
        "Whenever this creature deals combat damage to a player, choose one that hasn't been chosen —\n" +
        "• Put two +1/+1 counters on this creature.\n" +
        "• Draw a card.\n" +
        "• This creature becomes a copy of another target creature you control."

    staticAbility {
        ability = CantBeBlocked()
    }

    triggeredAbility {
        trigger = Triggers.DealsCombatDamageToPlayer
        effect = ModalEffect.chooseOneNotYetChosen(
            // • Put two +1/+1 counters on this creature.
            Mode.noTarget(
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self),
                "Put two +1/+1 counters on this creature"
            ),
            // • Draw a card.
            Mode.noTarget(
                Effects.DrawCards(1),
                "Draw a card"
            ),
            // • This creature becomes a copy of another target creature you control.
            Mode.withTarget(
                Effects.EachPermanentBecomesCopyOfTarget(
                    target = EffectTarget.ContextTarget(0),
                    duration = Duration.Permanent,
                    affected = EffectTarget.Self,
                ),
                Targets.OtherCreatureYouControl,
                "This creature becomes a copy of another target creature you control"
            )
        )
        description = "Whenever this creature deals combat damage to a player, choose one that " +
            "hasn't been chosen — Put two +1/+1 counters on this creature; or draw a card; or this " +
            "creature becomes a copy of another target creature you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "72"
        artist = "Joshua Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/a/a/aac4f0cc-63be-4f08-956e-39839c9735ba.jpg?1726286122"
    }
}
