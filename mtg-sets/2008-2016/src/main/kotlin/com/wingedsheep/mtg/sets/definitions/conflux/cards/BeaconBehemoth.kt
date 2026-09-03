package com.wingedsheep.mtg.sets.definitions.conflux.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Beacon Behemoth
 * {3}{G}{G}
 * Creature — Beast
 * 5 / 3
 * {1}: Target creature with power 5 or greater gains vigilance until end of turn.
 *
 * The power threshold is a restriction on the *target*, not a condition on the effect —
 * `TargetFilter.Creature.powerAtLeast(5)`, so it is checked on announcement and rechecked against
 * projected state on resolution. The Behemoth is itself a 5/3, so it is a legal target for its own
 * ability. The grant is [Effects.GrantKeyword] with its default `Duration.EndOfTurn`, which is
 * exactly the printed "until end of turn". Same shape as Bloodthorn Taunter, with a mana cost in
 * place of the tap.
 */
val BeaconBehemoth = card("Beacon Behemoth") {
    manaCost = "{3}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Beast"
    power = 5
    toughness = 3
    oracleText = "{1}: Target creature with power 5 or greater gains vigilance until end of turn."

    activatedAbility {
        cost = Costs.Mana("{1}")
        val creature = target("target", TargetCreature(filter = TargetFilter.Creature.powerAtLeast(5)))
        effect = Effects.GrantKeyword(Keyword.VIGILANCE, creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "78"
        artist = "Jesper Ejsing"
        flavorText = "When its smoky plumes light Naya's sky, every creature from the smallest pip fawn to the largest rannet heeds the warning."
        imageUri = "https://cards.scryfall.io/normal/front/0/c/0cc42e33-7489-4a32-bb30-adc80ec13521.jpg"
    }
}
