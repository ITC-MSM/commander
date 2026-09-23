package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.Aggregation
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Karn, Gilded Guardian — the count is read on resolution from projected colors, excluding Karn
 * itself ("other artifacts"), so a five-color Karn contributes nothing and a colorless artifact
 * adds no color.
 */
val KarnGildedGuardian = card("Karn, Gilded Guardian") {
    manaCost = "{2/W}{2/U}{2/B}{2/R}{2/G}"
    colorIdentity = "WUBRG"
    typeLine = "Legendary Artifact Creature — Golem"
    power = 5
    toughness = 5
    oracleText = "Vigilance, trample\n" +
        "When Karn enters, draw a card for each color among other artifacts you control."

    keywords(Keyword.VIGILANCE, Keyword.TRAMPLE)

    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        effect = Effects.DrawCards(
            DynamicAmount.AggregateBattlefield(
                player = Player.You,
                filter = GameObjectFilter.Artifact,
                aggregation = Aggregation.DISTINCT_COLORS,
                excludeSelf = true,
            )
        )
        description = "When Karn enters, draw a card for each color among other artifacts you control."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "272"
        artist = "Scott M. Fischer"
        flavorText = "Karn forged the empty world of Mirrodin and filled it with peaceful creations."
        imageUri = "https://cards.scryfall.io/normal/front/3/a/3abcae65-5b21-4c98-adad-34b8bc76ea3a.jpg?1789014541"
        inBooster = false
    }
}
