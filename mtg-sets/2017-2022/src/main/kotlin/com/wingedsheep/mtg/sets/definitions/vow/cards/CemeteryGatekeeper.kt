package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Cemetery Gatekeeper
 * {1}{R}
 * Creature — Vampire
 * 2/1
 * First strike
 * When this creature enters, exile a card from a graveyard.
 * Whenever a player plays a land or casts a spell, if it shares a card type with the exiled card,
 * this creature deals 2 damage to that player.
 *
 * The exile is *linked* to the Gatekeeper (CR 607), so "the exiled card" is
 * [EntityReference.LinkedExiledCard] — the same read-side handle Mirrodin's imprint cards use.
 *
 * The payoff is written as **two** triggered abilities, one per event, rather than as one ability
 * over an `EventPattern.AnyOf`. The two are behaviourally identical — a land play is never also a
 * spell cast, so the pair cannot double-fire — and the split form is what the corpus writes
 * (sixty cards to the union's three) and therefore what Argentum Assay reads and prints. Writing
 * the union here would put the card outside the differential gate for no gain.
 *
 * "If it shares a card type with the exiled card" is a genuine intervening-"if" (CR 603.4), not a
 * trigger filter: it is checked again on resolution, so a Gatekeeper whose exiled card has since
 * left exile does nothing.
 */
val CemeteryGatekeeper = card("Cemetery Gatekeeper") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Vampire"
    power = 2
    toughness = 1
    oracleText = "First strike\n" +
        "When this creature enters, exile a card from a graveyard.\n" +
        "Whenever a player plays a land or casts a spell, if it shares a card type with the " +
        "exiled card, this creature deals 2 damage to that player."

    keywords(Keyword.FIRST_STRIKE)

    // When this creature enters, exile a card from a graveyard.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Pipeline {
            val graveyards = gather(
                CardSource.FromZone(Zone.GRAVEYARD, Player.Each, GameObjectFilter.Any),
                name = "graveyards",
            )
            val exiled = chooseExactly(
                1,
                from = graveyards,
                useTargetingUI = true,
                prompt = "Exile a card from a graveyard",
                selectedLabel = "Exile",
                name = "exiled",
            )
            exile(exiled, linkToSource = true)
        }
        description = "When Cemetery Gatekeeper enters, exile a card from a graveyard."
    }

    // Whenever a player plays a land …
    triggeredAbility {
        trigger = Triggers.anyPlayerPlaysLand()
        interveningIf = Conditions.TriggeringSpellMatches(
            GameObjectFilter.Any.sharingCardTypeWith(EntityReference.LinkedExiledCard())
        )
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.TriggeringPlayer))
        description = "Whenever a player plays a land, if it shares a card type with the exiled " +
            "card, Cemetery Gatekeeper deals 2 damage to that player."
    }

    // … or casts a spell, if it shares a card type with the exiled card, deal 2 to that player.
    triggeredAbility {
        trigger = Triggers.AnyPlayerCastsSpell
        interveningIf = Conditions.TriggeringSpellMatches(
            GameObjectFilter.Any.sharingCardTypeWith(EntityReference.LinkedExiledCard())
        )
        effect = Effects.DealDamage(2, EffectTarget.PlayerRef(Player.TriggeringPlayer))
        description = "Whenever a player casts a spell, if it shares a card type with the exiled " +
            "card, Cemetery Gatekeeper deals 2 damage to that player."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "148"
        artist = "Tyler Jacobson"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/457086c4-1b4e-4f79-8f2a-10b16174c8bb.jpg?1783924842"
        ruling(
            "2021-11-19",
            "Card types that can be exiled from a graveyard include artifact, creature, " +
                "enchantment, land, planeswalker, instant, and sorcery. Legendary, basic, and snow " +
                "are supertypes, not card types. Human, Equipment, and Aura are subtypes, not card types."
        )
    }
}
