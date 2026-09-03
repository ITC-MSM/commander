package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Desperate Lunge
 * {1}{W}
 * Instant
 * Target creature gets +2/+2 and gains flying until end of turn. You gain 2 life.
 *
 * Sentence-shaped nesting: the first sentence is one composite (pump plus keyword grant over the
 * same bound target), the life gain is the second sentence and defaults to the controller.
 */
val DesperateLunge = card("Desperate Lunge") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 and gains flying until end of turn. You gain 2 life."

    spell {
        val t = target("target", TargetCreature())
        effect = Effects.Composite(
            Effects.Composite(
                Effects.ModifyStats(2, 2, t),
                Effects.GrantKeyword(Keyword.FLYING, t)
            ),
            Effects.GainLife(2)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "266"
        artist = "Deruchenko Alexander"
        flavorText = "Ravnica held its breath as the hero of the resistance—their last hope—flew through the sky, his dark sword ready to strike a god."
        imageUri = "https://cards.scryfall.io/normal/front/6/9/69ce134a-25ef-4f8d-a385-7c29fc5707dc.jpg"
    }
}
