package com.wingedsheep.mtg.sets.definitions.woe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Redtooth Genealogist
 * {2}{G}
 * Creature — Elf Advisor
 * 2/3
 *
 * When this creature enters, create a Royal Role token attached to another target creature you
 * control. (If you control another Role on it, put that one into the graveyard. Enchanted creature
 * gets +1/+1 and has ward {1}.)
 *
 * "Another target creature you control" — the Genealogist can't crown itself, hence
 * [Targets.OtherCreatureYouControl] rather than the plain creature-you-control filter. With no
 * other creature on board the trigger simply has no legal target and is removed from the stack.
 * The one-Role-per-creature rule lives behind [Effects.CreateRoleToken].
 */
val RedtoothGenealogist = card("Redtooth Genealogist") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf Advisor"
    power = 2
    toughness = 3
    oracleText = "When this creature enters, create a Royal Role token attached to another target " +
        "creature you control. (If you control another Role on it, put that one into the graveyard. " +
        "Enchanted creature gets +1/+1 and has ward {1}.)"

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("another target creature you control", Targets.OtherCreatureYouControl)
        effect = Effects.CreateRoleToken("Royal Role", t)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "179"
        artist = "Gaboleps"
        flavorText = "Some family trees are thornier than others."
        imageUri = "https://cards.scryfall.io/normal/front/9/9/99c81440-66eb-4443-a83f-e2f15cb68a3e.jpg?1783915079"

        ruling(
            "2023-09-01",
            "If a permanent has more than one Role attached to it controlled by the same player, each of " +
                "those Roles except the one with the most recent timestamp is put into its owner's graveyard. " +
                "This is a state-based action."
        )
        ruling(
            "2023-09-01",
            "Some spells and abilities that create Role tokens require targets. If each target chosen is an " +
                "illegal target as that spell or ability tries to resolve, it won't resolve. The Role token " +
                "won't be created."
        )
    }
}
