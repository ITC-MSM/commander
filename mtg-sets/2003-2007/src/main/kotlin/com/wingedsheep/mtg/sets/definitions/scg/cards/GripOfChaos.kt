package com.wingedsheep.mtg.sets.definitions.scg.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val GripOfChaos = card("Grip of Chaos") {
    manaCost = "{4}{R}{R}"
    colorIdentity = "R"
    typeLine = "Enchantment"
    oracleText = "Whenever a spell or ability is put onto the stack, if it has a single target, reselect its target at random. (Select from among all legal targets.)"

    triggeredAbility {
        trigger = Triggers.AnySpellOrAbilityOnStack
        effect = Effects.ReselectTargetRandomly()
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "98"
        artist = "Mark Tedin"
        flavorText = "When the world is consumed by chaos, the skilled and the foolish are on equal footing."
        imageUri = "https://cards.scryfall.io/normal/front/d/e/defbbd3a-0e7d-4af2-b25f-9003ddad0bf5.jpg?1562535751"
    }
}
