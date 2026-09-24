package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.NoncombatDamageBonus

val TomikIzzetSparkmage = card("Tomik, Izzet Sparkmage") {
    manaCost = "{1}{R}"
    colorIdentity = "R"
    typeLine = "Legendary Creature — Human Wizard"
    power = 1
    toughness = 2
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n" +
        "If a source you control would deal noncombat damage to an opponent or a permanent an opponent controls, it deals that much damage plus 1 instead."

    prowess()

    staticAbility {
        ability = NoncombatDamageBonus(1)
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "253"
        artist = "Randy Vargas"
        flavorText = "\"Laws are made to be broken. This includes physics and thermodynamics.\""
        imageUri = "https://cards.scryfall.io/normal/front/5/c/5c5afd5f-6f37-4c3e-83f0-68fdcea98810.jpg?1789729773"
        inBooster = false
    }
}
