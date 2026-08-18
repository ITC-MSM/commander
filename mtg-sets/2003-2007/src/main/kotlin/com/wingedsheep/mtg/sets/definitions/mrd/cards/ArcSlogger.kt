package com.wingedsheep.mtg.sets.definitions.mrd.cards

import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.AnyTarget

/**
 * Arc-Slogger — Mirrodin #85 (canonical printing)
 * {3}{R}{R} · Creature — Beast · 4/5
 *
 * {R}, Exile the top ten cards of your library: This creature deals 2 damage to any target.
 *
 * The whole card is its cost. Ten cards off the top is a *cost*, not an effect, and the difference
 * is the one thing an implementation can get wrong: CR 118.3 says a player can't pay a cost without
 * the resources to pay it fully, so a nine-card library can't activate this at all — it does not
 * exile nine and fire. [Costs.ExileTopOfLibrary] carries that gate, which is also why it isn't
 * [Costs.Mill] with the destination swapped: the cards go to exile, and no mill replacement
 * (Bruvac) touches the count.
 *
 * The cards are the top ten, not ten of the player's choosing, so the payment takes no selection —
 * the same shape as milling as a cost.
 */
val ArcSlogger = card("Arc-Slogger") {
    manaCost = "{3}{R}{R}"
    colorIdentity = "R"
    typeLine = "Creature — Beast"
    power = 4
    toughness = 5
    oracleText = "{R}, Exile the top ten cards of your library: This creature deals 2 damage to any target."

    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{R}"), Costs.ExileTopOfLibrary(10))
        val victim = target("any target", AnyTarget())
        effect = Effects.DealDamage(2, victim)
        description = "{R}, Exile the top ten cards of your library: This creature deals 2 damage to any target."
    }

    metadata {
        rarity = Rarity.RARE
        collectorNumber = "85"
        artist = "Jeff Easley"
        flavorText = "A shuffling sound and the smell of ozone follow the slogger as surely as its electric tail."
        imageUri = "https://cards.scryfall.io/normal/front/d/3/d3dd67e0-72b4-4c55-b49b-c69950feccb1.jpg?1783944543"
    }
}
