package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.events.RecipientFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Dinosaur Hunter
 * {1}{B}
 * Creature — Human Pirate
 * 2/2
 * Whenever this creature deals damage to a Dinosaur, destroy that creature.
 *
 * "That creature" is the damage recipient, so the destroy reads
 * [EffectTarget.TriggeringEntity] rather than a target.
 */
val DinosaurHunter = card("Dinosaur Hunter") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Pirate"
    oracleText = "Whenever this creature deals damage to a Dinosaur, destroy that creature."
    power = 2
    toughness = 2

    triggeredAbility {
        trigger = Triggers.dealsDamage(
            recipient = RecipientFilter.Matching(
                GameObjectFilter.Permanent.withSubtype(Subtype.DINOSAUR)
            )
        )
        effect = Effects.Destroy(EffectTarget.TriggeringEntity)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "67"
        artist = "Tianhua X"
        flavorText = "\"Aye, the foul beast chomped me, but I got away. You'd best believe when I " +
            "find it again, it won't get away so easy.\""
        imageUri = "https://cards.scryfall.io/normal/front/9/3/93ff5a4e-9ec5-4b52-90f5-b8b6753c958d.jpg?1783935315"
        ruling(
            "2018-01-19",
            "If an opponent's Dinosaur has an enrage ability and your Dinosaur Hunter deals " +
                "damage to it during your turn, that ability resolves before Dinosaur Hunter's " +
                "ability destroys the Dinosaur. If it's that opponent's turn, the Dinosaur is " +
                "destroyed first, but its enrage ability still resolves afterwards."
        )
    }
}
