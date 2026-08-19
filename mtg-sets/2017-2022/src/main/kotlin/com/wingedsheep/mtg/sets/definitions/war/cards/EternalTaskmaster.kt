package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersTapped
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect

/**
 * Eternal Taskmaster
 * {1}{B}
 * Creature — Zombie
 * 2/3
 *
 * This creature enters tapped.
 * Whenever this creature attacks, you may pay {2}{B}. If you do, return target creature card from your graveyard to your hand.
 */
val EternalTaskmaster = card("Eternal Taskmaster") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Zombie"
    oracleText = "This creature enters tapped.\nWhenever this creature attacks, you may pay {2}{B}. If you do, return target creature card from your graveyard to your hand."
    power = 2
    toughness = 3

    replacementEffect(EntersTapped())

    triggeredAbility {
        trigger = Triggers.Attacks
        val card = target("target creature card in your graveyard", Targets.CreatureCardInYourGraveyard)
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}{B}"),
            effect = Effects.ReturnToHand(card),
        )
        description = "Whenever this creature attacks, you may pay {2}{B}. If you do, return target " +
            "creature card from your graveyard to your hand."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "90"
        artist = "Tomasz Jedruszek"
        flavorText = "\"They are called Eternals. They will never stop.\"\n—Jace Beleren"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/7789188e-5caa-4500-b3e4-bb95f7657903.jpg?1783933446"
    }
}
