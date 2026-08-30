package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Herald of Dromoka
 * {1}{W}
 * Creature — Human Warrior
 * 2 / 2
 *
 * Vigilance
 * Other Warrior creatures you control have vigilance.
 *
 * A lord, so the second line is a [GrantKeyword] *static* ability, not a one-shot grant: the
 * affected set is recomputed by the projection layer every time it runs. "Other" is the group's
 * `excludeSelf`, and the printed noun is "Warrior creatures", so the group narrows the creature
 * filter by subtype rather than matching permanents.
 */
val HeraldOfDromoka = card("Herald of Dromoka") {
    manaCost = "{1}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Human Warrior"
    power = 2
    toughness = 2
    oracleText = "Vigilance\n" +
        "Other Warrior creatures you control have vigilance."

    keywords(Keyword.VIGILANCE)

    staticAbility {
        ability = GrantKeyword(
            keyword = Keyword.VIGILANCE,
            filter = GroupFilter.OtherCreaturesYouControl.withSubtype("Warrior")
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "22"
        artist = "Zack Stella"
        flavorText = "The trumpeters of Arashin are ever alert in their watch over the Great Aerie."
        imageUri = "https://cards.scryfall.io/normal/front/8/9/8987b2af-66d6-4271-a139-37e544cdec62.jpg?1783938615"
    }
}
