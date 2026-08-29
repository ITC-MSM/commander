package com.wingedsheep.mtg.sets.definitions.vow.cards

import com.wingedsheep.sdk.core.Color
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
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Cemetery Protector
 * {2}{W}{W}
 * Creature — Human Soldier
 * 3/4
 * Flash
 * When this creature enters, exile a card from a graveyard.
 * Whenever you play a land or cast a spell, if it shares a card type with the exiled card, create
 * a 1/1 white Human creature token.
 *
 * The white half of the Crimson Vow cemetery cycle — the same linked exile (CR 607) and
 * intervening-"if" over the triggering object as [CemeteryGatekeeper], scoped to your own plays
 * rather than every player's.
 */
val CemeteryProtector = card("Cemetery Protector") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Soldier"
    power = 3
    toughness = 4
    oracleText = "Flash\n" +
        "When this creature enters, exile a card from a graveyard.\n" +
        "Whenever you play a land or cast a spell, if it shares a card type with the exiled card, " +
        "create a 1/1 white Human creature token."

    keywords(Keyword.FLASH)

    // When this creature enters, exile a card from a graveyard.
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
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
                name = "protectorExile",
            )
            exile(picked, linkToSource = true)
        }
        description = "When Cemetery Protector enters, exile a card from a graveyard."
    }

    // Whenever you play a land or cast a spell, if it shares a card type with the exiled card,
    // create a 1/1 white Human creature token.
    triggeredAbility {
        trigger = Triggers.or(
            Triggers.youPlayLand(),
            Triggers.YouCastSpell,
        )
        interveningIf = Conditions.TriggeringSpellMatches(
            GameObjectFilter.Any.sharingCardTypeWith(EntityReference.LinkedExiledCard())
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human"),
            name = "Human",
            imageUri = "https://cards.scryfall.io/normal/front/7/d/7d13a93a-a43d-4cf5-8300-8341f3b7f1b1.jpg?1783924701",
        )
        description = "Whenever you play a land or cast a spell, if it shares a card type with " +
            "the exiled card, create a 1/1 white Human creature token."
    }

    metadata {
        rarity = Rarity.MYTHIC
        collectorNumber = "6"
        artist = "Chris Rallis"
        imageUri = "https://cards.scryfall.io/normal/front/c/0/c00731eb-69fe-42f3-9919-66a4cdec00f7.jpg?1783924926"
        ruling(
            "2021-11-19",
            "Card types that can be exiled from a graveyard include artifact, creature, " +
                "enchantment, land, planeswalker, instant, and sorcery. Legendary, basic, and snow " +
                "are supertypes, not card types. Human, Equipment, and Aura are subtypes, not card types."
        )
    }
}
