package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.DynamicAmounts
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Marshal of the Lost
 * {2}{W}{B}
 * Creature — Orc Warrior
 * 3/3
 *
 * Deathtouch
 * Whenever you attack, target creature gets +X/+X until end of turn, where X is
 * the number of attacking creatures.
 */
val MarshalOfTheLost = card("Marshal of the Lost") {
    manaCost = "{2}{W}{B}"
    colorIdentity = "WB"
    typeLine = "Creature — Orc Warrior"
    power = 3
    toughness = 3
    oracleText = "Deathtouch\nWhenever you attack, target creature gets +X/+X until end of turn, where X is the number of attacking creatures."

    keywords(Keyword.DEATHTOUCH)

    // X is the number of attacking creatures. Only one player attacks per combat, and
    // "whenever you attack" fires for this card's controller, so the attacking creatures
    // are exactly the ones controlled by that player.
    val attackerCount = DynamicAmounts.attackingCreaturesYouControl()

    triggeredAbility {
        trigger = Triggers.YouAttack
        val t = target("target creature", Targets.Creature)
        effect = Effects.ModifyStats(
            power = attackerCount,
            toughness = attackerCount,
            target = t
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "207"
        artist = "Andreas Zafiratos"
        flavorText = "Gvar carved his way through the Ancestral Maelstrom, seeking to restore the withered Kin-Tree and soothe the raging spirits."
        imageUri = "https://cards.scryfall.io/normal/front/6/4/64fbaa16-67c3-4ed2-9545-39abbbde61dc.jpg?1743204817"
    }
}
