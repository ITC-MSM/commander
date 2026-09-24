package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ActivatedAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Vraska, Soul of Stone — the Sculpture Treasure is a plain [CreateTokenEffect]: a colorless 1/1
 * *artifact* creature whose subtypes are Sculpture and Treasure (so "sacrifice a Treasure" and
 * "Treasures you control" effects see it), carrying the Treasure mana ability as a granted
 * activated mana ability. As a creature it has summoning sickness, so it can't tap for mana the
 * turn it's created (unless it gains haste).
 */
val VraskaSoulOfStone = card("Vraska, Soul of Stone") {
    manaCost = "{U}{R}{W}"
    colorIdentity = "URW"
    typeLine = "Legendary Creature — Gorgon Wizard"
    power = 3
    toughness = 3
    oracleText = "Artifact creatures you control have vigilance.\n" +
        "Whenever you cast a noncreature spell, create a 1/1 colorless Sculpture Treasure artifact " +
        "creature token with \"{T}, Sacrifice this token: Add one mana of any color.\""

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.VIGILANCE,
            filter = GroupFilter(GameObjectFilter.ArtifactCreature.youControl()),
        )
    }

    triggeredAbility {
        trigger = Triggers.YouCastNoncreature
        effect = CreateTokenEffect(
            power = 1,
            toughness = 1,
            colors = emptySet(),
            creatureTypes = setOf("Sculpture", "Treasure"),
            artifactToken = true,
            activatedAbilities = listOf(
                ActivatedAbility(
                    cost = Costs.Composite(Costs.Tap, Costs.SacrificeSelf),
                    effect = Effects.AddAnyColorMana(1),
                    isManaAbility = true,
                    timing = TimingRule.ManaAbility,
                    descriptionOverride = "{T}, Sacrifice this token: Add one mana of any color.",
                )
            ),
            imageUri = "https://cards.scryfall.io/normal/front/a/0/a0fec445-c6fb-4ca2-b63f-1d608c418a22.jpg?1789735148",
        )
        description = "Whenever you cast a noncreature spell, create a 1/1 colorless Sculpture Treasure " +
            "artifact creature token with \"{T}, Sacrifice this token: Add one mana of any color.\""
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "277"
        artist = "Kieran Yanner"
        flavorText = "\"Paths to peace are carved in flesh and stone. This is how we reform what has " +
            "failed before.\""
        imageUri = "https://cards.scryfall.io/normal/front/f/3/f3869752-eade-4e7a-8dd1-68cafb9e10be.jpg?1789128008"
        inBooster = false
    }
}
