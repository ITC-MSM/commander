package com.wingedsheep.mtg.sets.definitions.tmt.cards

import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.CreateDelayedTriggerEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Casey Jones, Vigilante
 * {1}{R}{R}
 * Legendary Creature — Human Berserker
 * 4/3
 *
 * When Casey Jones enters, draw three cards. At the beginning of your
 * next upkeep, discard three cards at random.
 */
val CaseyJonesVigilante = card("Casey Jones, Vigilante") {
    manaCost = "{1}{R}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Berserker"
    oracleText = "When Casey Jones enters, draw three cards. At the beginning of your next upkeep, discard three cards at random."
    power = 4
    toughness = 3

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.Composite(
            listOf(
                Effects.DrawCards(3),
                CreateDelayedTriggerEffect(
                    step = Step.UPKEEP,
                    fireOnPlayer = EffectTarget.PlayerRef(Player.You),
                    effect = Patterns.Hand.discardRandom(3),
                ),
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "88"
        artist = "Xavier Ribeiro"
        flavorText = "\"Goongala!\""
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a6a3258d-2e9b-4862-b2e3-bbfae9bd4d33.jpg?1769005927"
    }
}
