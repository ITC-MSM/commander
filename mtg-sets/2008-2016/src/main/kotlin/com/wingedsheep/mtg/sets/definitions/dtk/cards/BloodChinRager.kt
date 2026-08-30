package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Patterns
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Blood-Chin Rager
 * {1}{B}
 * Creature — Human Warrior
 * 2 / 2
 *
 * Whenever this creature attacks, Warrior creatures you control gain menace until end of turn. (They can't be blocked except by two or more creatures.)
 *
 * The printed noun is "Warrior **creatures**", so the group is the creature filter narrowed by
 * subtype — `AllCreaturesYouControl.withSubtype("Warrior")` — not a bare permanent-with-subtype
 * group. One sentence names its group once, so this is a single
 * [Patterns.Group.grantKeywordToAll] pass (`ForEach` over the group, granting menace to each
 * member) rather than a hand-rolled pipeline; the until-end-of-turn duration is that facade's
 * default.
 */
val BloodChinRager = card("Blood-Chin Rager") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Whenever this creature attacks, Warrior creatures you control gain menace until end of turn. (They can't be blocked except by two or more creatures.)"

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Patterns.Group.grantKeywordToAll(
            keyword = Keyword.MENACE,
            filter = GroupFilter.AllCreaturesYouControl.withSubtype("Warrior")
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "89"
        artist = "Karl Kopinski"
        flavorText = "Kolaghan blades rarely stay clean for long."
        imageUri = "https://cards.scryfall.io/normal/front/e/8/e8e93684-a587-4510-b48b-ecf27ff695f4.jpg?1783938600"
    }
}
