package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Judge Magister Gabranth
 * {W}{B}
 * Legendary Creature — Human Advisor Knight
 * 2/2
 * Menace
 * Whenever another creature or artifact you control dies, put a +1/+1 counter on Judge Magister
 *   Gabranth.
 *
 * The dies trigger uses [Triggers.leavesBattlefield] with `to = GRAVEYARD` and
 * [TriggerBinding.OTHER] ("another") over the [GameObjectFilter.CreatureOrArtifact] you control, so
 * Gabranth's own death never triggers it.
 */
val JudgeMagisterGabranth = card("Judge Magister Gabranth") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Legendary Creature — Human Advisor Knight"
    power = 2
    toughness = 2
    oracleText = "Menace\n" +
        "Whenever another creature or artifact you control dies, put a +1/+1 counter on Judge Magister Gabranth."

    keywords(Keyword.MENACE)

    triggeredAbility {
        trigger = Triggers.leavesBattlefield(
            filter = GameObjectFilter.CreatureOrArtifact.youControl(),
            to = Zone.GRAVEYARD,
            binding = TriggerBinding.OTHER
        )
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "230"
        artist = "Josu Hernaiz"
        flavorText = "\"I slew your king. I slew your country. Do these deeds not demand vengeance?\""
        imageUri = "https://cards.scryfall.io/normal/front/f/9/f9e64cb6-48f7-41d3-99e7-4b0bc3b33fd7.jpg?1748706634"
    }
}
