package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Stampede
 * {1}{G}{G}
 * Instant
 *
 * Attacking creatures get +1/+0 and gain trample until end of turn.
 *
 * Overrun's shape over the attackers instead of the creatures you control: **one** iteration
 * carrying a `Composite` of both effects, not two passes over the same group — the sentence names
 * its group once and says two things about those creatures.
 */
val Stampede = card("Stampede") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Attacking creatures get +1/+0 and gain trample until end of turn."

    spell {
        effect = Effects.ForEachInGroup(
            GroupFilter(GameObjectFilter.Creature.attacking()),
            Effects.Composite(
                Effects.ModifyStats(1, 0, EffectTarget.Self),
                Effects.GrantKeyword(Keyword.TRAMPLE, EffectTarget.Self)
            )
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "265"
        artist = "Jeff A. Menges"
        flavorText = "\"We could see the horizon blacken with the great beasts, but it was too late. The icefield offered no immediate safety, but luckily most of us reached a crevasse in which we could take cover.\"\n—Disa the Restless, journal entry"
        imageUri = "https://cards.scryfall.io/normal/front/b/c/bc8265a1-4621-4d25-8f7f-f0179951a694.jpg"
    }
}
