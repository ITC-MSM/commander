package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Targets
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity

/**
 * Baton of Morale
 * {2}
 * Artifact
 *
 * {2}: Target creature gains banding until end of turn. (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding a player controls are blocking or being blocked by a creature, that player divides that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)
 *
 * A plain [Effects.GrantKeyword] at its default end-of-turn duration — the whole card is one
 * mana-only activated ability, so no tap cost is written.
 */
val BatonOfMorale = card("Baton of Morale") {
    manaCost = "{2}"
    colorIdentity = ""
    typeLine = "Artifact"
    oracleText = "{2}: Target creature gains banding until end of turn. (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding a player controls are blocking or being blocked by a creature, that player divides that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)"

    activatedAbility {
        cost = Costs.Mana("{2}")
        val t = target("target", Targets.Creature)
        effect = Effects.GrantKeyword(Keyword.BANDING, t)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "313"
        artist = "Douglas Shuler"
        flavorText = "\"The Goblins would kill to get ahold of this one.\"\n—Arcum Dagsson, Soldevi Machinist"
        imageUri = "https://cards.scryfall.io/normal/front/8/b/8bc29872-b1a2-4851-9eca-f3e67ae6e14c.jpg"
    }
}
