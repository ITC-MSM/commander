package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.effects.MayPayManaEffect
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val ProftConsultingDetective = card("Proft, Consulting Detective") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Legendary Creature — Human Detective"
    power = 2
    toughness = 2
    oracleText = "Whenever you scry or surveil, you may pay {2}. If you do, put a +1/+1 counter on Proft and draw a card. (Draw after you scry or surveil.)"

    triggeredAbility {
        trigger = Triggers.WheneverYouScryOrSurveil
        effect = MayPayManaEffect(
            cost = ManaCost.parse("{2}"),
            effect = Effects.Composite(
                Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self),
                Effects.DrawCards(1),
            )
        )
        description = "Whenever you scry or surveil, you may pay {2}. If you do, put a +1/+1 counter on Proft and draw a card."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "218"
        artist = "Lindsey Look"
        flavorText = "When the crime is violent, call the Boros. When it's legalistic, call the Azorius. When it's complex, call him."
        imageUri = "https://cards.scryfall.io/normal/front/b/8/b8466593-40fe-4557-89b2-760c1c92087b.jpg?1789127178"
        inBooster = false
    }
}
