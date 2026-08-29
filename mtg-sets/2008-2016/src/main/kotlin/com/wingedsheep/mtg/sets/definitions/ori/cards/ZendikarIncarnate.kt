package com.wingedsheep.mtg.sets.definitions.ori.cards

import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.references.Player
import com.wingedsheep.sdk.scripting.values.DynamicAmount

/**
 * Zendikar Incarnate
 * {2}{R}{G}
 * Creature — Elemental
 * * / 4
 *
 * Zendikar Incarnate's power is equal to the number of lands you control.
 *
 * Only the *power* is characteristic-defining (CR 604.3), so this uses [dynamicPower] rather than
 * `dynamicStats` — the printed toughness stays the fixed 4. `AggregateBattlefield` is the corpus's
 * one-battlefield tally, and [Filters.Land] is the bare `IsLand` predicate, so every land you
 * control counts regardless of subtype.
 */
val ZendikarIncarnate = card("Zendikar Incarnate") {
    manaCost = "{2}{R}{G}"
    colorIdentity = "RG"
    typeLine = "Creature — Elemental"
    oracleText = "Zendikar Incarnate's power is equal to the number of lands you control."
    toughness = 4

    dynamicPower(DynamicAmount.AggregateBattlefield(Player.You, Filters.Land))

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "219"
        artist = "Lucas Graciano"
        flavorText = "\"Her people angered Zendikar, and they faced the land's wrath. That is why " +
            "Nissa is the last of the animists.\"\n" +
            "—Numa, Joraga chieftain"
        imageUri = "https://cards.scryfall.io/normal/front/e/b/eb12b1d8-c53e-4d48-89e5-2168ff34a853.jpg"
    }
}
