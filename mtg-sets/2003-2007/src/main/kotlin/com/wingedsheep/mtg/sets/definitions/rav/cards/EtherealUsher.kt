package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.transmute
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.core.AbilityFlag
import com.wingedsheep.sdk.dsl.Costs

val EtherealUsher = card("Ethereal Usher") {
    manaCost = "{5}{U}"
    typeLine = "Creature — Spirit"
    oracleText = "{U}, {T}: Target creature can't be blocked this turn.\nTransmute {1}{U}{U} ({1}{U}{U}, Discard this card: Search your library for a card with the same mana value as this card, reveal it, put it into your hand, then shuffle. Transmute only as a sorcery.)"
    colorIdentity = "U"
    power = 2
    toughness = 3

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{U}"), Costs.Tap)
        val creature = target("target creature", Targets.Creature)
        effect = Effects.GrantKeyword(AbilityFlag.CANT_BE_BLOCKED, creature)
    }
    transmute("{1}{U}{U}")

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "47"
        artist = "Mark A. Nelson"
        imageUri = "https://cards.scryfall.io/normal/front/9/0/908901b7-fb40-4358-bca5-5e71bdafcbe7.jpg?1783943688"
    }
}
