package com.wingedsheep.mtg.sets.definitions.roe.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Harmless Assault
 * {2}{W}{W}
 * Instant
 *
 * Prevent all combat damage that would be dealt this turn by attacking creatures.
 *
 * Modeling notes:
 *  - A *source-side* shield, not a Fog: the printed line names no recipient, so the damage is
 *    silenced wherever it would land — attackers hitting the defending player, attackers trading
 *    with blockers, attackers hitting a planeswalker. That is `Effects.PreventCombatDamageFrom`,
 *    which is the unified `PreventDamageEffect` with `PreventionDirection.FromTarget` (no recipient
 *    clause) and a `PreventionSourceFilter.FromGroup` naming the eligible sources — exactly the
 *    shape Assay compiles this text to.
 *  - `PreventionScope.CombatOnly` is baked into that facade and is load-bearing here: the card says
 *    "combat damage", so an attacking creature's activated damage ability still resolves normally.
 *  - `GroupFilter.AttackingCreatures` is the pre-built "creatures that are attacking" group. The
 *    group is re-evaluated at the moment each damage instance would be dealt, so a creature that
 *    stops attacking (or one put onto the battlefield attacking after this resolves) is judged as
 *    it is when combat damage happens, which is what "attacking creatures" means.
 */
val HarmlessAssault = card("Harmless Assault") {
    manaCost = "{2}{W}{W}"
    colorIdentity = "W"
    typeLine = "Instant"
    oracleText = "Prevent all combat damage that would be dealt this turn by attacking creatures."

    spell {
        effect = Effects.PreventCombatDamageFrom(GroupFilter.AttackingCreatures)
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "24"
        artist = "Chippy"
        flavorText = "After encasing it in a paralyzing beam of light, the angel studied the Eldrazi as a child would study a bug, curiously and without fear."
        imageUri = "https://cards.scryfall.io/normal/front/a/7/a77fef2c-227e-474e-896e-c0ebe227f494.jpg?1783942008"
    }
}
