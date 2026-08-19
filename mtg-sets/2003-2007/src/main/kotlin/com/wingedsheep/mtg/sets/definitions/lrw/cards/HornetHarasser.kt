package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Hornet Harasser
 * {2}{B}{B}
 * Creature — Goblin Shaman
 * 2/2
 * When this creature dies, target creature gets -2/-2 until end of turn.
 */
val HornetHarasser = card("Hornet Harasser") {
    manaCost = "{2}{B}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Goblin Shaman"
    power = 2
    toughness = 2
    oracleText = "When this creature dies, target creature gets -2/-2 until end of turn."

    triggeredAbility {
        trigger = Triggers.Dies
        val creature = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(-2, -2, creature)
        description = "When this creature dies, target creature gets -2/-2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "118"
        artist = "Nils Hamm"
        flavorText = "\"And though she didn't get her honey, Auntie found something far more interesting.\"\n—A tale of Auntie Grub"
        imageUri = "https://cards.scryfall.io/normal/front/4/5/45598727-a8b1-4b3e-87c6-70c36f0d4fe8.jpg?1783942889"
    }
}
