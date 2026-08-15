package com.wingedsheep.mtg.sets.definitions.fin.cards

import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ModifyStats
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.AnyTarget


/**
 * Lion Heart
 * {4}
 * Artifact — Equipment
 * When this Equipment enters, it deals 2 damage to any target.
 * Equipped creature gets +2/+1.
 * Equip {2}
 */
val LionHeart = card("Lion Heart") {
    manaCost = "{4}"
    colorIdentity = ""
    typeLine = "Artifact — Equipment"
    oracleText = "When this Equipment enters, it deals 2 damage to any target.\nEquipped creature gets +2/+1.\nEquip {2}"
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", AnyTarget())
        effect = DealDamageEffect(2, t)
    }
    staticAbility {
        ability = ModifyStats(2, 1)
    }
    equipAbility("{2}")
    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "261"
        artist = "Mushk Rizvi"
        flavorText = "\"Lions are known for their great strength and pride.\""
        imageUri = "https://cards.scryfall.io/normal/front/0/4/04d327a3-1699-4556-b681-a957671ad142.jpg"
    }
}
