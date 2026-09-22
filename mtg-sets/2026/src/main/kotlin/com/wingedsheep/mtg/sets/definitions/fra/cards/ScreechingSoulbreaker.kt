package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val ScreechingSoulbreaker = card("Screeching Soulbreaker") {
    manaCost = "{2}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Siren Bard"
    oracleText = "Flying\nWhenever this creature attacks, it deals 1 damage to each opponent and you gain 1 life."
    power = 1
    toughness = 4

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.Attacks
        effect = Effects.Composite(
            Effects.DealDamage(1, EffectTarget.PlayerRef(Player.EachOpponent)),
            Effects.GainLife(1)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "65"
        artist = "Pavel Kolomeyets"
        flavorText = "She weaves brutal insults into hypnotizing melodies, enthralling her victims to stay and listen."
        imageUri = "https://cards.scryfall.io/normal/front/7/3/738667a1-c184-43ea-829f-49fbb69b6fc0.jpg?1789385960"
        inBooster = false
    }
}
