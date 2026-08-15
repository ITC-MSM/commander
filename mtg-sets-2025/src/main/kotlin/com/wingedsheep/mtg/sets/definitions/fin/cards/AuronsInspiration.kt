package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget


/**
 * Auron's Inspiration
 * {2}{W}
 * Instant
 * Attacking creatures get +2/+0 until end of turn.
 * Flashback {2}{W}{W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)
 */
val AuronsInspiration = card("Auron's Inspiration") {
    manaCost = "{2}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Attacking creatures get +2/+0 until end of turn.\nFlashback {2}{W}{W} (You may cast this card from your graveyard for its flashback cost. Then exile it.)"
    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.attacking()),
            Effects.ModifyStats(2, 0, EffectTarget.Self)
        )
    }
    keywordAbility(KeywordAbility.flashback("{2}{W}{W}"))
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "8"
        artist = "Fang Xinyu"
        flavorText = "\"Now is the time to choose! Now is the time to shape your stories! Your fate is in your hands!\"\n—Auron"
        imageUri = "https://cards.scryfall.io/normal/front/7/7/77d82764-563c-4bc2-b568-625ec7215e0d.jpg"
    }
}
