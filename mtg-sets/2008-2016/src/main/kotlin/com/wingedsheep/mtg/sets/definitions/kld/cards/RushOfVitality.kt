package com.wingedsheep.mtg.sets.definitions.kld.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Rush of Vitality
 * {1}{B}
 * Instant
 * Target creature gets +1/+0 and gains lifelink and indestructible until end of turn. (Damage
 * dealt by that creature also causes its controller to gain that much life, and it can't be
 * destroyed by damage or effects that say "destroy.")
 *
 * One sentence, three effects over the same bound target: the pump plus a keyword grant per
 * printed keyword. All three default to `Duration.EndOfTurn`, which is what the printed "until
 * end of turn" says.
 */
val RushOfVitality = card("Rush of Vitality") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets +1/+0 and gains lifelink and indestructible until end of turn. (Damage dealt by that creature also causes its controller to gain that much life, and it can't be destroyed by damage or effects that say \"destroy.\")"

    spell {
        val t = target("target", TargetCreature())
        effect = Effects.Composite(
            Effects.ModifyStats(1, 0, t),
            Effects.GrantKeyword(Keyword.LIFELINK, t),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "99"
        artist = "Lindsey Look"
        flavorText = "Short lives lead to forbidden choices."
        imageUri = "https://cards.scryfall.io/normal/front/1/e/1e53cd8b-18f8-4950-84d4-7aafa26c7ae4.jpg?1783937201"
    }
}
