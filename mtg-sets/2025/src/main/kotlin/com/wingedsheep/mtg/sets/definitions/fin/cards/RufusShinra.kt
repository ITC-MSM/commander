package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.dsl.Conditions
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect

/**
 * Rufus Shinra
 * {1}{W}{B}
 * Legendary Creature — Human Noble
 * 2/4
 * Whenever Rufus Shinra attacks, if you don't control a creature named Darkstar, create
 * Darkstar, a legendary 2/2 white and black Dog creature token.
 *
 * The "if you don't control …" clause is an intervening-if condition (CR 603.4): the
 * trigger only fires — and only resolves — while no Darkstar is on your battlefield.
 */
val RufusShinra = card("Rufus Shinra") {
    manaCost = "{1}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Human Noble"
    power = 2
    toughness = 4
    oracleText = "Whenever Rufus Shinra attacks, if you don't control a creature named Darkstar, create Darkstar, a legendary 2/2 white and black Dog creature token."

    triggeredAbility {
        trigger = Triggers.Attacks
        triggerCondition = Conditions.YouControl(
            GameObjectFilter.Creature.named("Darkstar"),
            negate = true
        )
        effect = CreateTokenEffect(
            count = 1,
            power = 2,
            toughness = 2,
            colors = setOf(Color.WHITE, Color.BLACK),
            creatureTypes = setOf("Dog"),
            name = "Darkstar",
            legendary = true,
            imageUri = "https://cards.scryfall.io/normal/front/1/9/19faca08-0eef-4da4-ab6f-c3aed63ac77b.jpg?1748704096"
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "238"
        artist = "Ittoku"
        flavorText = "\"I'll control the world with fear. It takes too much to do it like my old man.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/5/f5fff00b-c9a0-4e90-abc0-349f8716c885.jpg?1748706668"
    }
}
