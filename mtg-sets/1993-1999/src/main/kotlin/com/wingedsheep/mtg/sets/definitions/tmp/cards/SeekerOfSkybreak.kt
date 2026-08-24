package com.wingedsheep.mtg.sets.definitions.tmp.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Seeker of Skybreak
 * {1}{G}
 * Creature — Elf
 * 2/1
 * {T}: Untap target creature.
 */
val SeekerOfSkybreak = card("Seeker of Skybreak") {
    manaCost = "{1}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Elf"
    power = 2
    toughness = 1
    oracleText = "{T}: Untap target creature."

    activatedAbility {
        cost = Costs.Tap
        val creature = target("target", Targets.Creature)
        effect = Effects.Untap(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "254"
        artist = "Daren Bader"
        flavorText = "\"We shun them not for their dream but for their refusal to let such a noble dream die a noble death.\"\n" +
            "—Eladamri, Lord of Leaves"
        imageUri = "https://cards.scryfall.io/normal/front/b/4/b4bb98ba-d599-4597-911c-2b472fa8817c.jpg"
    }
}
