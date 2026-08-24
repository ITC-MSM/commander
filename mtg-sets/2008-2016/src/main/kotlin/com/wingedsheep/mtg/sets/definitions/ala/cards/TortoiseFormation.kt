package com.wingedsheep.mtg.sets.definitions.ala.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Tortoise Formation
 * {3}{U}
 * Instant
 * Creatures you control gain shroud until end of turn. (They can't be the targets of spells or abilities.)
 *
 * One group, one thing said about it, so it is the single-clause group facade:
 * [Patterns.Group]`.grantKeywordToAll(`[Keyword.SHROUD]`, `[GroupFilter.AllCreaturesYouControl]`)`,
 * which lowers to one `ForEachInGroup` granting the keyword to each member with the default
 * `Duration.EndOfTurn`. The group is snapshotted before iteration, so a creature that enters after
 * the spell resolves gains nothing — exactly the printed one-shot.
 */
val TortoiseFormation = card("Tortoise Formation") {
    manaCost = "{3}{U}"
    colorIdentity = "U"
    typeLine = "Instant"
    oracleText = "Creatures you control gain shroud until end of turn. (They can't be the targets of spells or abilities.)"

    spell {
        effect = Patterns.Group.grantKeywordToAll(Keyword.SHROUD, GroupFilter.AllCreaturesYouControl)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "61"
        artist = "Mark Zug"
        flavorText = "At sea, the Jhessian fleet strikes swiftly and decisively. On land, facing the elite cavalry of Valeron, its marines must rely on more cautious strategies."
        imageUri = "https://cards.scryfall.io/normal/front/5/9/59ff8324-42b8-4eeb-8d0b-ea679c984933.jpg"
    }
}
