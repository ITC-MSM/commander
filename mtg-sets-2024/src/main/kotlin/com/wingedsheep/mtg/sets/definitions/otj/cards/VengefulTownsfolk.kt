package com.wingedsheep.mtg.sets.definitions.otj.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Vengeful Townsfolk
 * {2}{W}
 * Creature — Human Citizen
 * 3/3
 *
 * Whenever one or more other creatures you control die, put a +1/+1 counter on this creature.
 *
 * The trigger is a once-per-batch shape ([Triggers.OneOrMoreCreaturesYouControlDie]): a board
 * wipe that destroys several of your creatures at once adds a single +1/+1 counter, not one per
 * creature. `excludeSelf = true` models the "other" — Vengeful Townsfolk dying alongside them
 * does not count toward its own trigger.
 */
val VengefulTownsfolk = card("Vengeful Townsfolk") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Citizen"
    power = 3
    toughness = 3
    oracleText = "Whenever one or more other creatures you control die, put a +1/+1 counter on this creature."

    // Whenever one or more other creatures you control die, put a +1/+1 counter on this creature.
    triggeredAbility {
        trigger = Triggers.OneOrMoreCreaturesYouControlDie(excludeSelf = true)
        effect = Effects.AddCounters(Counters.PLUS_ONE_PLUS_ONE, 1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "37"
        artist = "Irina Nordsol"
        flavorText = "\"The Sterling Company says they protect us, but where are they when the Hellspurs burn half the town? Counting their vaults!\""
        imageUri = "https://cards.scryfall.io/normal/front/2/d/2df404af-571a-4867-83f5-bb4163b433ff.jpg?1712355378"
    }
}
