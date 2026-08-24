package com.wingedsheep.mtg.sets.definitions.ice.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Filters
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.ConditionalStaticAbility
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.conditions.Exists
import com.wingedsheep.sdk.scripting.references.Player

/**
 * Dire Wolves
 * {2}{G}
 * Creature — Wolf
 * 2/2
 *
 * This creature has banding as long as you control a Plains.
 *
 * The Kird Ape shape with a keyword instead of a stat bump: a `ConditionalStaticAbility` wrapping
 * `GrantKeyword(BANDING, Filters.Self)`, gated on an `Exists` over your own battlefield. `Exists` is
 * re-asked on every projection, so losing the Plains turns banding off mid-combat as the rules
 * require.
 */
val DireWolves = card("Dire Wolves") {
    manaCost = "{2}{G}"
    colorIdentity = "G"
    typeLine = "Creature — Wolf"
    power = 2
    toughness = 2
    oracleText = "This creature has banding as long as you control a Plains. (Any creatures with banding, and up to one without, can attack in a band. Bands are blocked as a group. If any creatures with banding you control are blocking or being blocked by a creature, you divide that creature's combat damage, not its controller, among any of the creatures it's being blocked by or is blocking.)"

    staticAbility {
        ability = ConditionalStaticAbility(
            ability = GrantKeyword(Keyword.BANDING, Filters.Self),
            condition = Exists(Player.You, Zone.BATTLEFIELD, GameObjectFilter.Land.withSubtype("Plains"))
        )
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "230"
        artist = "Ron Spencer"
        flavorText = "\"It's amazing how scared a city kid can get at a dog. Now, of course, I'd cross Terisiare alone, and keep no watch if I had a pack of greys hanging on my flanks as I went.\"\n—Oddveig Ulfsson, caravan scout"
        imageUri = "https://cards.scryfall.io/normal/front/a/6/a602c93d-e00f-4b4f-a7ff-95316b7e7641.jpg"
    }
}
