package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivationRestriction
import com.wingedsheep.sdk.scripting.CantBeBlockedBy
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.values.EntityReference

/**
 * Prehistoric Pet
 * {W}
 * Creature — Dinosaur Ninja
 * 1/2
 *
 * This creature can't be blocked by creatures with greater power.
 * {1}{W}, {T}: Return another target creature you control to its owner's hand.
 * Activate only during your turn.
 */
val PrehistoricPet = card("Prehistoric Pet") {
    manaCost = "{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dinosaur Ninja"
    oracleText = "This creature can't be blocked by creatures with greater power.\n{1}{W}, {T}: Return another target creature you control to its owner's hand. Activate only during your turn."
    power = 1
    toughness = 2

    // "can't be blocked by creatures with greater power" — blockers whose power exceeds
    // this creature's own power.
    staticAbility {
        ability = CantBeBlockedBy(
            GameObjectFilter.Creature.powerGreaterThanEntity(EntityReference.Source)
        )
    }

    activatedAbility {
        val creature = target("another target creature you control", Targets.OtherCreatureYouControl)
        cost = Costs.Composite(Costs.Mana("{1}{W}"), Costs.Tap)
        effect = Effects.ReturnToHand(creature)
        restrictions = listOf(ActivationRestriction.OnlyDuringYourTurn)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "22"
        artist = "Jakob Eirich"
        flavorText = "\"As far as I'm concerned, Pepperoni's one of us!\"\n—Raphael"
        imageUri = "https://cards.scryfall.io/normal/front/1/4/148e6acd-a96a-4bea-8cfe-a129cd8d1003.jpg?1769005558"
    }
}
