package com.wingedsheep.mtg.sets.definitions.dsk.cards

import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.Triggers
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.TriggerBinding

/**
 * Gremlin Tamer
 * {W}{U}
 * Creature — Human Scout
 * 2/2
 *
 * Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room,
 * create a 1/1 red Gremlin creature token.
 */
val GremlinTamer = card("Gremlin Tamer") {
    manaCost = "{W}{U}"
    colorIdentity = "WU"
    typeLine = "Creature — Human Scout"
    power = 2
    toughness = 2
    oracleText = "Eerie — Whenever an enchantment you control enters and whenever you fully unlock a Room, " +
        "create a 1/1 red Gremlin creature token."

    keywords(Keyword.EERIE)

    // Eerie trigger — part 1: whenever an enchantment you control enters.
    triggeredAbility {
        trigger = Triggers.entersBattlefield(
            filter = GameObjectFilter.Enchantment.youControl(),
            binding = TriggerBinding.ANY,
        )
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Gremlin"),
            imageUri = "https://cards.scryfall.io/normal/front/d/9/d948b503-890a-49d5-a3cf-cb6e604851b8.jpg?1726236661",
        )
        description = "Eerie — Whenever an enchantment you control enters, create a 1/1 red Gremlin creature token."
    }

    // Eerie trigger — part 2: whenever you fully unlock a Room.
    triggeredAbility {
        trigger = Triggers.RoomFullyUnlocked
        effect = Effects.CreateToken(
            power = 1,
            toughness = 1,
            colors = setOf(Color.RED),
            creatureTypes = setOf("Gremlin"),
            imageUri = "https://cards.scryfall.io/normal/front/d/9/d948b503-890a-49d5-a3cf-cb6e604851b8.jpg?1726236661",
        )
        description = "Eerie — Whenever you fully unlock a Room, create a 1/1 red Gremlin creature token."
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "215"
        artist = "Billy Christian"
        flavorText = "\"Do they cause trouble? Sure. But if you're nice to them, the trouble happens to other folks.\""
        imageUri = "https://cards.scryfall.io/normal/front/3/5/3593a222-21c5-4f91-bde1-763ea08071da.jpg?1726286668"
    }
}
