package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val TitanbonesToweringHeart = card("Titanbones, Towering Heart") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Skeleton Druid"
    oracleText = "Reach\n" +
        "Whenever you gain life, put two +1/+1 counters on Titanbones.\n" +
        "When you discard this card, you gain 3 life."
    power = 4
    toughness = 3

    keywords(Keyword.REACH)

    triggeredAbility {
        trigger = Triggers.YouGainLife
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 2, EffectTarget.Self)
        description = "Whenever you gain life, put two +1/+1 counters on Titanbones."
    }

    triggeredAbility {
        trigger = Triggers.YouDiscardThis
        effect = Effects.GainLife(3)
        description = "When you discard this card, you gain 3 life."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "266"
        artist = "Jesper Ejsing"
        flavorText = "Titanbones found he received the greatest gifts in the littlest things."
        imageUri = "https://cards.scryfall.io/normal/front/e/d/edea6f70-a5a7-475d-b7f2-97933d0f32cf.jpg?1788329375"
        inBooster = false
    }
}
