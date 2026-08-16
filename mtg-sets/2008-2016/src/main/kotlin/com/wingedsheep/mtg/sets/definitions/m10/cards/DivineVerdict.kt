package com.wingedsheep.mtg.sets.definitions.m10.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Divine Verdict
 * {3}{W}
 * Instant
 * Destroy target attacking or blocking creature.
 */
val DivineVerdict = card("Divine Verdict") {
    manaCost = "{3}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Destroy target attacking or blocking creature."

    spell {
        val creature = target("creature", TargetCreature(filter = TargetFilter.AttackingOrBlockingCreature))
        effect = Effects.Destroy(creature)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "8"
        artist = "Kev Walker"
        flavorText = "Giants and pit spawn attempted to topple the cathedral's pillars. The fine grit of their remains still swirls in the breeze outside."
        imageUri = "https://cards.scryfall.io/normal/front/4/8/48444e14-c73b-47d1-9c55-0ff4dc3c6034.jpg?1783942404"
        ruling("2012-07-01", "Destroying a blocking creature won't cause any of the creatures it was blocking to become unblocked. They won't deal combat damage to the defending player or planeswalker (unless they have trample).")
        ruling("2009-10-01", "An “attacking creature” is one that has been declared as an attacker this combat, or one that was put onto the battlefield attacking this combat. Unless that creature leaves combat, it continues to be an attacking creature through the end of combat step, even if the player it was attacking has left the game, or the planeswalker it was attacking has left combat.")
        ruling("2009-10-01", "A “blocking creature” is one that has been declared as a blocker this combat, or one that was put onto the battlefield blocking this combat. Unless that creature leaves combat, it continues to be a blocking creature through the end of combat step, even if the creature or creatures it was blocking are no longer on the battlefield or have otherwise left combat.")
    }
}
