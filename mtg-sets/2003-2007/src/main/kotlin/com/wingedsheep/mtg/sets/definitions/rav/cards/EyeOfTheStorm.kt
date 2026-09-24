package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.effects.GatherCardsEffect
import com.wingedsheep.sdk.scripting.events.SpellCastPredicate
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Eye of the Storm — Ravnica: City of Guilds #48
 * {5}{U}{U} · Enchantment
 *
 * Whenever a player casts an instant or sorcery card, exile it. Then that player copies each
 * instant or sorcery card exiled with this enchantment. For each copy, the player may cast the
 * copy without paying its mana cost.
 *
 * - "casts an instant or sorcery **card**" is [SpellCastPredicate.IsCard]: the copies this
 *   ability hands out are cast too (CR 707.12), and a copy of a card is not a card, so they don't
 *   re-trigger it (the card's third ruling).
 * - "exile it" is [Effects.ExileTriggeringSpell] linked to the enchantment — the spell leaves the
 *   stack without resolving and joins the pile `CardSource.FromLinkedExile()` reads.
 * - "that player copies … may cast" runs inside `ForEachPlayer(TriggeringPlayer)`, which rebinds
 *   the controller to the caster, so the copies are theirs and the cast choices are theirs. The
 *   pile includes cards other players cast (the fourth ruling). Copies are cast one at a time in
 *   the order the player picks them; any left uncast cease to exist (CR 707.10a).
 */
val EyeOfTheStorm = card("Eye of the Storm") {
    manaCost = "{5}{U}{U}"
    colorIdentity = "U"
    typeLine = "Enchantment"
    oracleText = "Whenever a player casts an instant or sorcery card, exile it. Then that player " +
        "copies each instant or sorcery card exiled with this enchantment. For each copy, the " +
        "player may cast the copy without paying its mana cost."

    triggeredAbility {
        trigger = Triggers.anyPlayerCasts(
            spellFilter = GameObjectFilter.InstantOrSorcery,
            requires = setOf(SpellCastPredicate.IsCard)
        )
        effect = Effects.Composite(
            Effects.ExileTriggeringSpell(linkToSource = true),
            Effects.ForEachPlayer(
                Player.TriggeringPlayer,
                listOf(
                    GatherCardsEffect(
                        source = CardSource.FromLinkedExile(),
                        storeAs = "eyeExiled"
                    ),
                    // Only instants and sorceries ever enter this pile, so no type filter.
                    Effects.CopyCollectionIntoCollection(from = "eyeExiled", storeAs = "eyeCopies"),
                    Effects.CastAnyNumberFromCollectionWithoutPayingCost(from = "eyeCopies")
                )
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "48"
        artist = "Hideaki Takamura"
        imageUri = "https://cards.scryfall.io/normal/front/4/9/49967eb9-5020-4f0a-8775-5114f6d96d75.jpg?1783943687"
    }
}
