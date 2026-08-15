package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Frenzied Goblin
 * {R}
 * Creature — Goblin Berserker
 * 1/1
 * Whenever this creature attacks, you may pay {R}. If you do, target creature can't block this turn.
 */
val FrenziedGoblin = card("Frenzied Goblin") {
    manaCost = "{R}"
    colorIdentity = "R"
    typeLine = "Creature — Goblin Berserker"
    power = 1
    toughness = 1
    oracleText = "Whenever this creature attacks, you may pay {R}. If you do, target creature can't block this turn."

    triggeredAbility {
        trigger = Triggers.Attacks
        val creature = target("creature", TargetCreature())
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{R}"),
            effect = Effects.CantBlock(creature)
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "125"
        artist = "Carl Critchlow"
        flavorText = "The upside to not thinking about the consequences is that you'll always surprise those who do."
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d307d8c7-b9b5-4f8f-933d-f1c64cbbf92f.jpg?1782717468"
    }
}
