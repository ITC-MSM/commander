package com.wingedsheep.mtg.sets.definitions.ltr.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AbilityCost
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.effects.CreateTokenEffect
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Horn of Gondor
 * {3}
 * Legendary Artifact
 *
 * When Horn of Gondor enters, create a 1/1 white Human Soldier creature token.
 * {3}, {T}: Create X 1/1 white Human Soldier creature tokens, where X is the number of Humans you control.
 */
val HornOfGondor = card("Horn of Gondor") {
    manaCost = "{3}"
    typeLine = "Legendary Artifact"
    oracleText = "When Horn of Gondor enters, create a 1/1 white Human Soldier creature token.\n{3}, {T}: Create X 1/1 white Human Soldier creature tokens, where X is the number of Humans you control."

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/a/6/a6181330-7521-4ec6-be6c-b35487c2d2d4.jpg?1699974464"
        )
    }

    activatedAbility {
        cost = AbilityCost.Composite(
            listOf(
                Costs.Mana(ManaCost.parse("{3}")),
                AbilityCost.Tap
            )
        )
        effect = CreateTokenEffect(
            count = DynamicAmount.Count(
                Player.You,
                Zone.BATTLEFIELD,
                GameObjectFilter.Creature.withSubtype("Human")
            ),
            power = 1,
            toughness = 1,
            colors = setOf(Color.WHITE),
            creatureTypes = setOf("Human", "Soldier"),
            imageUri = "https://cards.scryfall.io/normal/front/a/6/a6181330-7521-4ec6-be6c-b35487c2d2d4.jpg?1699974464"
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "240"
        artist = "Yigit Koroglu"
        imageUri = "https://cards.scryfall.io/normal/front/b/1/b14eaeec-d7ce-462a-90d3-2ae5ff605fdb.jpg?1686970170"
    }
}
