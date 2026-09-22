package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val TethermagesAdvantage = card("Tethermage's Advantage") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+2 and gains reach until end of turn. Untap it."

    spell {
        val creature = target("creature", Targets.Creature)
        effect = Effects.ModifyStats(2, 2, creature)
            .then(Effects.GrantKeyword(Keyword.REACH, creature))
            .then(Effects.Untap(creature))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "117"
        artist = "Javier Charro"
        flavorText = "Konstrari students harness leyline energy to animate their constructs. The more determined the tethermage, the more effective the puppetbeast."
        imageUri = "https://cards.scryfall.io/normal/front/3/8/38589a7c-9cfb-4bcc-845e-9dc205095853.jpg?1789129937"
        inBooster = false
    }
}
