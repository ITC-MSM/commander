package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Raging Regisaur
 * {2}{R}{G}
 * Creature — Dinosaur
 * 4/4
 *
 * Whenever this creature attacks, it deals 1 damage to any target.
 */
val RagingRegisaur = card("Raging Regisaur") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "GR"
    typeLine = "Creature — Dinosaur"
    oracleText = "Whenever this creature attacks, it deals 1 damage to any target."
    power = 4
    toughness = 4

    triggeredAbility {
        trigger = Triggers.Attacks
        val victim = target("any target", Targets.Any)
        effect = Effects.DealDamage(1, victim)
        description = "Whenever this creature attacks, it deals 1 damage to any target."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "168"
        artist = "Bayard Wu"
        flavorText = "Its breath is a gale. Its roar is a volcano. Its anger tears soul from flesh."
        imageUri = "https://cards.scryfall.io/normal/front/a/d/ad6c43a7-e9c5-4b1c-9a6d-0d8798303045.jpg?1783935270"
    }
}
