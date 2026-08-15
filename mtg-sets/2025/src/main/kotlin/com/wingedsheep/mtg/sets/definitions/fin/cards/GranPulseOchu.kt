package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.targets.EffectTarget

/**
 * Gran Pulse Ochu
 * {G}
 * Creature — Plant Beast
 * 1/1
 * Deathtouch
 * {8}: Until end of turn, this creature gets +1/+1 for each permanent card in your graveyard.
 */
val GranPulseOchu = card("Gran Pulse Ochu") {
    manaCost = "{G}"
    colorIdentity = "G"
    typeLine = "Creature — Plant Beast"
    power = 1
    toughness = 1
    oracleText = "Deathtouch\n" +
        "{8}: Until end of turn, this creature gets +1/+1 for each permanent card in your graveyard."

    keywords(Keyword.DEATHTOUCH)

    activatedAbility {
        cost = Costs.Mana("{8}")
        val permanentCards = DynamicAmounts.zone(Player.You, Zone.GRAVEYARD, GameObjectFilter.Permanent).count()
        effect = Effects.ModifyStats(permanentCards, permanentCards, EffectTarget.Self)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "189"
        artist = "Domenico Cava"
        flavorText = "A mysterious beast that has grown large on its journey across Gran Pulse. " +
            "Its children, known as microchus, accompany the ochu in combat against adventurers."
        imageUri = "https://cards.scryfall.io/normal/front/4/d/4dced21f-478c-4500-9484-af5864dea5cc.jpg?1748706468"
    }
}
