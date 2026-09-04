package com.wingedsheep.mtg.sets.definitions.rix.cards

import com.wingedsheep.sdk.core.Subtype
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.CostModification
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.ModifySpellCost
import com.wingedsheep.sdk.scripting.SpellCostTarget

/**
 * Knight of the Stampede
 * {3}{G}
 * Creature — Human Knight
 * 2/4
 * Dinosaur spells you cast cost {2} less to cast.
 *
 * "Dinosaur spells" is any card type carrying the subtype, so the filter is
 * [GameObjectFilter.Any] — a hypothetical noncreature Dinosaur spell would be discounted too.
 */
val KnightOfTheStampede = card("Knight of the Stampede") {
    manaCost = "{3}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Human Knight"
    oracleText = "Dinosaur spells you cast cost {2} less to cast."
    power = 2
    toughness = 4

    staticAbility {
        ability = ModifySpellCost(
            target = SpellCostTarget.YouCast(GameObjectFilter.Any.withSubtype(Subtype.DINOSAUR)),
            modification = CostModification.ReduceGeneric(2),
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "138"
        artist = "Steve Argyle"
        flavorText = "\"My whisper becomes a thousand roars.\""
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e81565a4-cbbe-4820-8473-15ceda42d553.jpg?1783935284"
        ruling(
            "2018-01-19",
            "If an effect refers to a \"[subtype] spell\" or \"[subtype] card,\" it refers only " +
                "to a spell or card that has that subtype. For example, March of the Drowned is " +
                "a card that benefits Pirates and features Pirates in its illustration, but it " +
                "isn't a Pirate card."
        )
    }
}
