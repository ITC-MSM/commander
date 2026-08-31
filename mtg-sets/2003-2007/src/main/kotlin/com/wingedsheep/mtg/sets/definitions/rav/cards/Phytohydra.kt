package com.wingedsheep.mtg.sets.definitions.rav.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.EventPattern
import com.wingedsheep.sdk.scripting.ReplaceDamageWithCounters
import com.wingedsheep.sdk.scripting.events.RecipientFilter

/**
 * Phytohydra — Ravnica: City of Guilds #218 (canonical printing; reprinted in RVR)
 * {2}{G}{W}{W} · Creature — Plant Hydra · 1/1
 *
 * If damage would be dealt to this creature, put that many +1/+1 counters on it instead.
 *
 * One [ReplaceDamageWithCounters] with `RecipientFilter.Self` — the Anti-Venom clause, unchanged.
 * The "instead" matters and the primitive already gets it right (CR 614.1a): the damage is
 * *replaced*, never dealt and never prevented. Two rulings fall out of that for free:
 *
 * - "Phytohydra's ability doesn't prevent damage. Damage that can't be prevented will still be
 *   replaced by +1/+1 counters" (2024-01-12) — a replacement, not a prevention shield, so
 *   Excruciator-style unpreventable damage still turns into counters.
 * - First strike gets the counters in the first-strike damage step, before Phytohydra deals its own
 *   damage in the second (2005-10-01) — nothing special-cased; the replacement runs per damage
 *   event on whichever step deals it.
 *
 * The counter recipient defaults to the replacement's own host, which here is also the damaged
 * permanent, so the default is the printed "on it".
 */
val Phytohydra = card("Phytohydra") {
    manaCost = "{2}{G}{W}{W}"
    colorIdentity = "GW"
    typeLine = "Creature — Plant Hydra"
    power = 1
    toughness = 1
    oracleText = "If damage would be dealt to this creature, put that many +1/+1 counters on it instead."

    replacementEffect(
        ReplaceDamageWithCounters(
            counterType = Counters.PLUS_ONE_PLUS_ONE,
            appliesTo = EventPattern.DamageEvent(recipient = RecipientFilter.Self),
        )
    )

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "218"
        artist = "Jim Murray"
        flavorText = "\"Look at this creature, so like the Conclave it serves. Cut one head and it grows two. Damage it and it flourishes.\"\n—Veszka, Selesnya evangel"
        imageUri = "https://cards.scryfall.io/normal/front/8/6/86c336d2-cc14-4b74-a6f9-aab7a084d0de.jpg?1783943615"
        ruling("2024-01-12", "Phytohydra's ability doesn't prevent damage. Damage that can't be prevented will still be replaced by +1/+1 counters being put on Phytohydra.")
        ruling("2005-10-01", "If another effect would prevent damage from being dealt to Phytohydra or replace it with something else, Phytohydra's controller chooses which effect to apply first.")
        ruling("2005-10-01", "If Phytohydra blocks or is blocked by a creature with first strike or double strike, Phytohydra will get the counters during the first-strike combat damage step before it deals its own combat damage during the second combat damage step.")
    }
}
