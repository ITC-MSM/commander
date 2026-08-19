package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Battlewand Oak
 * {2}{G}
 * Creature — Treefolk Warrior
 * 1/3
 * Whenever a Forest you control enters, this creature gets +2/+2 until end of turn.
 * Whenever you cast a Treefolk spell, this creature gets +2/+2 until end of turn.
 *
 * The land trigger reads *Forest the land type*, so a nonbasic land with the Forest subtype counts
 * (and a basic Forest is the common case). `TriggerBinding.ANY` because the entering permanent is
 * another object, not Battlewand Oak itself.
 */
val BattlewandOak = card("Battlewand Oak") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Treefolk Warrior"
    power = 1
    toughness = 3
    oracleText = "Whenever a Forest you control enters, this creature gets +2/+2 until end of turn.\n" +
        "Whenever you cast a Treefolk spell, this creature gets +2/+2 until end of turn."

    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = Filters.ForestCard.youControl(),
            binding = TriggerBinding.ANY
        )
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Whenever a Forest you control enters, this creature gets +2/+2 until end of turn."
    }

    triggeredAbility {
        trigger = Triggers.YouCastSubtype(Subtype.TREEFOLK)
        effect = Effects.ModifyStats(2, 2, EffectTarget.Self)
        description = "Whenever you cast a Treefolk spell, this creature gets +2/+2 until end of turn."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "197"
        artist = "Steve Prescott"
        imageUri = "https://cards.scryfall.io/normal/front/8/a/8a128ea6-cd00-4410-8dc4-cf66ce6f0fa1.jpg?1783942868"
    }
}
