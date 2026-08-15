package com.wingedsheep.mtg.sets.definitions.spm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.KeywordAbility
import com.wingedsheep.sdk.scripting.effects.WardCost

/**
 * Spider-Rex, Daring Dino
 * {4}{G}{G}
 * Legendary Creature — Spider Dinosaur Hero
 * 6/6
 * Reach, trample
 * Ward {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)
 */
val SpiderRexDaringDino = card("Spider-Rex, Daring Dino") {
    manaCost = "{4}{G}{G}"
    colorIdentity = "G"
    typeLine = "Legendary Creature — Spider Dinosaur Hero"
    oracleText = "Reach, trample\nWard {2} (Whenever this creature becomes the target of a spell or ability an opponent controls, counter it unless that player pays {2}.)"
    power = 6
    toughness = 6
    keywords(Keyword.REACH, Keyword.TRAMPLE)
    keywordAbility(KeywordAbility.Ward(WardCost.Mana("{2}")))
    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "116"
        artist = "Narendra Bintara Adi"
        flavorText = "On Earth-66, Pter Ptarker learned that with GRRRR power comes GRRAHHHsibility."
        imageUri = "https://cards.scryfall.io/normal/front/5/b/5b6e0bea-f126-4adb-8808-901950a77c7b.jpg?1757377539"
    }
}
