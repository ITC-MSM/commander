package com.wingedsheep.mtg.sets.definitions.lrw.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

val HuntDown = card("Hunt Down") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Sorcery"
    oracleText = "Target creature blocks target creature this turn if able."

    spell {
        val blocker = target("creature that must block", Targets.Creature)
        val attacker = target("creature to be blocked", Targets.Creature)
        effect = Effects.ForceBlock(blocker, attacker)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "218"
        artist = "Christopher Moeller"
        flavorText = "\"Springjacks and faeries can be difficult to hunt, but my favorite prey are the flamekin. They never fail to put up a worthy fight when cornered.\""
        imageUri = "https://cards.scryfall.io/normal/front/c/b/cb783652-6450-4789-8ec1-b057b39c6a4b.jpg?1783942862"
        ruling("2007-10-01", "If the first creature targeted by Hunt Down can’t block the second targeted creature (for example, because the second creature has flying and the first doesn’t, or because both creatures are controlled by the same player), the ability does nothing and the first creature is free to block whichever creature its controller chooses, or block no creatures at all.")
    }
}
