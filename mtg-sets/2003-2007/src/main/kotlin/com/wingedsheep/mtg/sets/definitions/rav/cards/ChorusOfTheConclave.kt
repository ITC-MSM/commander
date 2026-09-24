package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.AdditionalManaForEntryCounters
import com.wingedsheep.sdk.scripting.GameObjectFilter

/**
 * Chorus of the Conclave
 * {4}{G}{G}{W}{W}
 * Legendary Creature — Dryad
 * 3/8
 *
 * Forestwalk
 * As an additional cost to cast creature spells, you may pay any amount of mana. If you do, that
 * creature enters with that many additional +1/+1 counters on it.
 *
 * The second ability is [AdditionalManaForEntryCounters]: while Chorus is on the battlefield, each
 * creature spell you cast offers an optional `{N}` generic additional cost, announced while casting;
 * the spell records N and the creature enters with N extra +1/+1 counters. Per the rulings it works
 * only while Chorus is on the battlefield (so never for Chorus itself), only for creature *spells*
 * as they are cast (never a creature put onto the battlefield by an effect), and — being generic
 * mana — it can be paid with convoke like any other part of the total cost.
 */
val ChorusOfTheConclave = card("Chorus of the Conclave") {
    manaCost = "{4}{G}{G}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Legendary Creature — Dryad"
    power = 3
    toughness = 8
    oracleText = "Forestwalk (This creature can't be blocked as long as defending player controls a Forest.)\n" +
        "As an additional cost to cast creature spells, you may pay any amount of mana. If you do, " +
        "that creature enters with that many additional +1/+1 counters on it."

    keywords(Keyword.FORESTWALK)

    staticAbility {
        ability = AdditionalManaForEntryCounters(spellFilter = GameObjectFilter.Creature)
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "195"
        artist = "Brian Despain"
        flavorText = "\"We are many, yet one. We are separate in body, yet speak with a single voice. " +
            "Join us in our chorus.\""
        imageUri = "https://cards.scryfall.io/normal/front/d/2/d2792033-19e1-4629-8155-85c6c2d89106.jpg?1783943626"
        ruling("2005-10-01", "Chorus of the Conclave's ability works only while it's on the battlefield, so you can't use it to put +1/+1 counters on itself.")
        ruling("2005-10-01", "Chorus of the Conclave's ability applies to creature spells only as they're being cast. You can't pay mana to put counters on creatures being put onto the battlefield by an effect.")
        ruling("2005-10-01", "Chorus of the Conclave's ability combines well with the convoke mechanic, effectively letting you tap creatures to put +1/+1 counters on the creature with convoke that you're casting, if you choose to do so.")
    }
}
