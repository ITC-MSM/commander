package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.TriggerBinding
import com.wingedsheep.sdk.scripting.TriggerSpec

val GolgariBrownscale = card("Golgari Brownscale") {
    manaCost = "{1}{G}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Lizard"
    power = 2
    toughness = 3
    oracleText = "When this card is put into your hand from your graveyard, you gain 2 life.\nDredge 2 (If you would draw a card, you may mill two cards instead. If you do, return this card from your graveyard to your hand.)"

    keywordAbility(KeywordAbility.dredge(2))
    triggeredAbility {
        triggerZone = Zone.GRAVEYARD
        trigger = TriggerSpec(
            event = EventPattern.ZoneChangeEvent(from = Zone.GRAVEYARD, to = Zone.HAND),
            binding = TriggerBinding.SELF
        )
        effect = Effects.GainLife(2)
    }
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "166"
        artist = "Carl Critchlow"
        imageUri = "https://cards.scryfall.io/normal/front/6/9/690cfc79-da41-4ac0-b0cc-719aa650b207.jpg?1783943638"
    }
}
