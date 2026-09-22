package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.ManaCost
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.targets.EffectTarget

val UndulatingWitness = card("Undulating Witness") {
    manaCost = "{4}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Serpent"
    oracleText = "Flying\n{2}: This creature gets +1/-1 until end of turn.\nBasic landcycling {2} ({2}, Discard this card: Search your library for a basic land card, reveal it, put it into your hand, then shuffle.)"
    power = 3
    toughness = 5

    keywords(Keyword.FLYING)
    keywordAbility(KeywordAbility.basicLandcycling(ManaCost.parse("{2}")))

    activatedAbility {
        cost = Costs.Mana("{2}")
        effect = Effects.ModifyStats(1, -1, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "45"
        artist = "Alexander Ostrowski"
        flavorText = "Twisting in the wind like a strange banner, it keeps watch over the horizon and tower alike."
        imageUri = "https://cards.scryfall.io/normal/front/0/a/0adbb4b2-a142-48da-8f4b-fa91529dbac4.jpg?1789556706"
        inBooster = false
    }
}
