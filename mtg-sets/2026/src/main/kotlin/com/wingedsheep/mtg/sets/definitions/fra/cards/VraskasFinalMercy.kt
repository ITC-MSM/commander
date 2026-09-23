package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val VraskasFinalMercy = card("Vraska's Final Mercy") {
    manaCost = "{B}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Choose one —\n" +
        "• You lose 2 life. Destroy target creature or planeswalker.\n" +
        "• You lose 2 life. Empower Jace 6. (Put six loyalty counters on a Jace token you control. If you don't control one, first create a blue Jace planeswalker token with \"[−1]: Surveil 1\" and \"[−3]: Draw a card.\")"

    spell {
        modal(chooseCount = 1) {
            mode("You lose 2 life. Destroy target creature or planeswalker") {
                val victim = target("target creature or planeswalker", Targets.CreatureOrPlaneswalker)
                effect = Effects.LoseLife(2, EffectTarget.Controller).then(Effects.Destroy(victim))
            }
            mode("You lose 2 life. Empower Jace 6") {
                effect = Effects.LoseLife(2, EffectTarget.Controller).then(Patterns.Mechanic.empowerJace(6))
            }
        }
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "71"
        artist = "Magali Villeneuve"
        imageUri = "https://cards.scryfall.io/normal/front/9/9/992bd991-7cfb-459f-bafd-9a44f3c925c5.jpg?1789699369"
        inBooster = false
    }
}
