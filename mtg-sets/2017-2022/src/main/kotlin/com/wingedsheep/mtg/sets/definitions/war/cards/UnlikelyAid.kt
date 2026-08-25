package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.targets.TargetCreature

/**
 * Unlikely Aid
 * {1}{B}
 * Instant
 * Target creature gets +2/+0 and gains indestructible until end of turn. (Damage and effects
 * that say "destroy" don't destroy it.)
 *
 * One sentence, two effects over the same bound target: the pump and the keyword grant. Both
 * default to `Duration.EndOfTurn`, which is what the printed "until end of turn" says.
 */
val UnlikelyAid = card("Unlikely Aid") {
    manaCost = "{1}{B}"
    colorIdentity = "B"
    typeLine = "Instant"
    oracleText = "Target creature gets +2/+0 and gains indestructible until end of turn. (Damage and effects that say \"destroy\" don't destroy it.)"

    spell {
        val t = target("target", TargetCreature())
        effect = Effects.Composite(
            Effects.ModifyStats(2, 0, t),
            Effects.GrantKeyword(Keyword.INDESTRUCTIBLE, t)
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "109"
        artist = "Viktor Titov"
        flavorText = "\"No one will ever ride me again, Gideon. Expect no further favors.\"\n—Rakdos"
        imageUri = "https://cards.scryfall.io/normal/front/9/c/9c260c5c-e796-4f81-9e15-0c5be75106b9.jpg"
    }
}
