package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility

val GolgariThug = card("Golgari Thug") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Creature — Human Warrior"
    oracleText = "When this creature dies, put target creature card from your graveyard on top of your library.\nDredge 4 (If you would draw a card, you may mill four cards instead. If you do, return this card from your graveyard to your hand.)"
    power = 1
    toughness = 1

    keywordAbility(KeywordAbility.dredge(4))

    triggeredAbility {
        trigger = Triggers.Dies
        val creature = target("creature", Targets.CreatureCardInYourGraveyard)
        effect = Effects.PutOnTopOfLibrary(creature)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "87"
        artist = "Kev Walker"
        imageUri = "https://cards.scryfall.io/normal/front/8/7/87c39e21-3e2f-4cbf-9f99-69e977924a73.jpg?1783943669"
    }
}
