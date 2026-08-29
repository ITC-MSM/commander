package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.CostReductionSource
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget
import com.wingedsheep.sdk.scripting.effects.CardSource
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Cemetery Prowler
 * {1}{G}{G}
 * Creature — Wolf
 * 3/4
 * Vigilance
 * Whenever this creature enters or attacks, exile a card from a graveyard.
 * Spells you cast cost {1} less to cast for each card type they share with cards exiled with this
 * creature.
 *
 * The green half of the Crimson Vow cemetery cycle. Unlike [CemeteryGatekeeper] and
 * [CemeteryProtector] the Prowler keeps exiling — one card per enter *and* per attack — so its
 * payoff reads the whole linked-exile pile rather than a single "the exiled card".
 *
 * The reduction is [CostReductionSource.SharedCardTypesWithLinkedExile], the one reduction source
 * whose amount depends on the spell being cast. Per the card's own ruling it counts *card types*,
 * not cards: two exiled creature cards still make a creature spell {1} cheaper.
 */
val CemeteryProwler = card("Cemetery Prowler") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 3
    toughness = 4
    oracleText = "Vigilance\n" +
        "Whenever this creature enters or attacks, exile a card from a graveyard.\n" +
        "Spells you cast cost {1} less to cast for each card type they share with cards exiled " +
        "with this creature."

    keywords(Keyword.VIGILANCE)

    // Whenever this creature enters or attacks, exile a card from a graveyard.
    triggeredAbility {
        trigger = Triggers.or(Triggers.EntersBattlefield, Triggers.Attacks)
        effect = Effects.Pipeline {
            val graveyardCards = gather(
                CardSource.FromZone(Zone.GRAVEYARD, Player.Each, GameObjectFilter.Any)
            )
            val picked = chooseExactly(
                1,
                from = graveyardCards,
                useTargetingUI = true,
                prompt = "Exile a card from a graveyard",
                selectedLabel = "Exile",
                name = "prowlerExile",
            )
            exile(picked, linkToSource = true)
        }
        description = "Whenever Cemetery Prowler enters or attacks, exile a card from a graveyard."
    }

    // Spells you cast cost {1} less to cast for each card type they share with cards exiled with
    // this creature.
    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any),
            modification = CostModification.ReduceGenericBy(
                CostReductionSource.SharedCardTypesWithLinkedExile()
            )
        )
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "191"
        artist = "Daarken"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b124ccc4-76e3-41a4-92b2-8f1d06ea9cb8.jpg?1783924818"
        ruling(
            "2021-11-19",
            "Card types that can be exiled from a graveyard include artifact, creature, " +
                "enchantment, land, planeswalker, instant, and sorcery. Legendary, basic, and snow " +
                "are supertypes, not card types. Human, Equipment, and Aura are subtypes, not card types."
        )
        ruling(
            "2021-11-19",
            "Cemetery Prowler's last ability checks the number of card types among cards exiled " +
                "with it, not the number of cards exiled. For example, if Cemetery Prowler has " +
                "exiled two creature cards, creature spells you cast cost {1} less, not {2} less."
        )
    }
}
