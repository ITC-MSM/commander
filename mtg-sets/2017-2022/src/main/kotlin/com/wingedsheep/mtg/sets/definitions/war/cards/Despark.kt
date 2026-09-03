package com.wingedsheep.mtg.sets.definitions.war.cards

import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.filters.unified.TargetFilter
import com.wingedsheep.sdk.scripting.targets.TargetPermanent

/**
 * Despark — War of the Spark #190 (canonical printing)
 * {W}{B}
 * Instant
 * Exile target permanent with mana value 4 or greater.
 *
 * The mana-value floor rides on the target filter, not on the effect: the restriction is a
 * legality condition re-checked on resolution (CR 608.2b), so a permanent whose mana value
 * drops below 4 in response is no longer a legal target and the spell is countered by the game
 * rules. Exiling with [Effects.Exile] and not destroying is the point of the card — it answers
 * indestructible permanents and planeswalkers alike.
 */
val Despark = card("Despark") {
    manaCost = "{W}{B}"
    colorIdentity = "WB"
    typeLine = "Instant"
    oracleText = "Exile target permanent with mana value 4 or greater."

    spell {
        val permanent = target("target", TargetPermanent(filter = TargetFilter.Permanent.manaValueAtLeast(4)))
        effect = Effects.Exile(permanent)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "190"
        artist = "Slawomir Maniak"
        flavorText = "Liliana whispered to whatever consciousness Oketra and Bontu had left. \"You are the gods. He is the usurper. You know what to do.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/2/32b17dfc-b916-4134-ba77-501cff435e7e.jpg"
    }
}
