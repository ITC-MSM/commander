package com.wingedsheep.mtg.sets.definitions.dtk.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Coat with Venom
 * {B}
 * Instant
 *
 * Target creature gets +1/+2 and gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy it.)
 *
 * One sentence, two clauses joined by "and", so one [Effects.Composite] over the pump and the
 * grant — both pointed at the same bound target. The printed noun is a bare "target creature",
 * with no controller restriction, so [Targets.Creature] is the whole requirement.
 */
val CoatWithVenom = card("Coat with Venom") {
    manaCost = "{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+2 and gains deathtouch until end of turn. (Any amount of damage it deals to a creature is enough to destroy it.)"

    spell {
        val t = target("target", Targets.Creature)
        effect = Effects.Composite(
            Effects.ModifyStats(1, 2, t),
            Effects.GrantKeyword(Keyword.DEATHTOUCH, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "91"
        artist = "Johann Bodin"
        flavorText = "\"Every Silumgar blade carries the blessing of our dragonlord.\"\n—Xathi the Infallible"
        imageUri = "https://cards.scryfall.io/normal/front/8/c/8cc8e012-7043-405c-b6cd-b3b38f8a8d54.jpg?1783938600"
    }
}
