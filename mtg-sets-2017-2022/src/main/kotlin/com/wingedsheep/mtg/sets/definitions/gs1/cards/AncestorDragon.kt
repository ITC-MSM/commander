package com.wingedsheep.mtg.sets.definitions.gs1.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Ancestor Dragon — Global Series: Jiang Yanggu & Mu Yanling #12 (canonical printing; reprinted
 * in Foundations #489).
 * {4}{W}{W} · Creature — Dragon · 5/6
 *
 * Flying.
 * Whenever one or more creatures you control attack, you gain 1 life for each attacking creature.
 *
 * The second ability is a single batch trigger (CR 508.3) that fires once per combat no matter how
 * many creatures attack, and gains life equal to the number of attacking creatures you control at
 * resolution — the same shape as Grand Warlord Radha, gaining life instead of mana.
 */
val AncestorDragon = card("Ancestor Dragon") {
    manaCost = "{4}{W}{W}"
    colorIdentity = "W"
    typeLine = "Creature — Dragon"
    power = 5
    toughness = 6
    oracleText = "Flying\n" +
        "Whenever one or more creatures you control attack, you gain 1 life for each attacking creature."

    keywords(Keyword.FLYING)

    triggeredAbility {
        trigger = Triggers.YouAttack
        effect = Effects.GainLife(
            DynamicAmounts.battlefield(Player.You, GameObjectFilter.Creature.attacking()).count()
        )
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "12"
        artist = "Shinchuen Chen"
        flavorText = "It is said that Yinglong gave birth to the qilin and the phoenix, and after " +
            "them, all the hairy and winged beings in the world. Thus it is known as the Ancestor Dragon."
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9ba9d1f0-a864-490c-b258-6bf9de251c4b.jpg?1783934632"
    }
}
