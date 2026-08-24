package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

/**
 * Ballista Charger
 * {5}
 * Artifact — Vehicle
 * 6/6
 * Whenever this Vehicle attacks, it deals 1 damage to any target.
 * Crew 3 (Tap any number of creatures you control with total power 3 or more: This Vehicle becomes
 * an artifact creature until end of turn.)
 *
 * "This Vehicle" is the source, so the attack trigger is the plain [Triggers.Attacks] with an
 * [Targets.Any] slot and no explicit damage source — the engine already attributes the damage to
 * the ability's source. Crew is the engine-owned [KeywordAbility.crew] ability.
 */
val BallistaCharger = card("Ballista Charger") {
    manaCost = "{5}"
    typeLine = "Artifact — Vehicle"
    oracleText = "Whenever this Vehicle attacks, it deals 1 damage to any target.\n" +
        "Crew 3 (Tap any number of creatures you control with total power 3 or more: This Vehicle becomes an artifact creature until end of turn.)"
    power = 6
    toughness = 6

    triggeredAbility {
        trigger = Triggers.Attacks
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(1, victim)
    }

    keywordAbility(KeywordAbility.crew(3))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "196"
        artist = "Sung Choi"
        imageUri = "https://cards.scryfall.io/normal/front/f/b/fbd3f376-1075-42c9-91df-80dc2d5faacf.jpg?1783937163"
    }
}
