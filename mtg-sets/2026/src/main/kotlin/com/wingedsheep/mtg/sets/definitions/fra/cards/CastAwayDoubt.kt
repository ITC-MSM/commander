package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val CastAwayDoubt = card("Cast Away Doubt") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Sorcery"
    oracleText = "Draw two cards. Cast Away Doubt deals 2 damage to each player."

    spell {
        effect = Effects.DrawCards(2)
            .then(Effects.DealDamage(2, EffectTarget.PlayerRef(Player.Each)))
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "51"
        artist = "Zara Alfonso"
        flavorText = "Every part of Jace's mind that objected to his plan had been cut away. The Theorist was all that remained."
        imageUri = "https://cards.scryfall.io/normal/front/3/b/3b5b28a4-0abd-4dc2-9856-c9a8d27f6a2d.jpg?1788353191"
        inBooster = false
    }
}
