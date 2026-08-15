package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EntersWithDynamicCounters
import com.wingedsheep.sdk.scripting.effects.DealDamageEffect
import com.wingedsheep.sdk.scripting.targets.TargetOpponent
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Morlun, Devourer of Spiders
 * {X}{B}{B}
 * Legendary Creature — Vampire Villain
 * 2/1
 * Lifelink
 * Morlun enters with X +1/+1 counters on him.
 * When Morlun enters, he deals X damage to target opponent.
 */
val MorlunDevourerOfSpiders = card("Morlun, Devourer of Spiders") {
    manaCost = "{X}{B}{B}"
    colorIdentity = "B"
    typeLine = "Legendary Creature — Vampire Villain"
    oracleText = "Lifelink\nMorlun enters with X +1/+1 counters on him.\nWhen Morlun enters, he deals X damage to target opponent."
    power = 2
    toughness = 1
    keywords(Keyword.LIFELINK)
    replacementEffect(EntersWithDynamicCounters(count = DynamicAmount.XValue))
    triggeredAbility {
        trigger = Triggers.EntersBattlefield
        val t = target("target", TargetOpponent())
        effect = DealDamageEffect(DynamicAmount.XValue, t)
    }
    metadata {
        rarity = Rarity.RARE
        collectorNumber = "59"
        artist = "Randy Gallegos"
        flavorText = "The Inheritors stalk the Spiders across worlds, feeding on their life force for sustenance."
        imageUri = "https://cards.scryfall.io/normal/front/1/b/1beb2eb9-90b5-43ba-8b04-cfce7dcb744b.jpg?1758203885"
    }
}
