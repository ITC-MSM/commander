package com.wingedsheep.mtg.sets.definitions.fra.cards

import com.wingedsheep.sdk.core.Counters
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Zone
import com.wingedsheep.sdk.dsl.Costs
import com.wingedsheep.sdk.dsl.Effects
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.TimingRule
import com.wingedsheep.sdk.scripting.targets.TargetCreature

val CryotheoryAdept = card("Cryotheory Adept") {
    manaCost = "{1}{U}"
    colorIdentity = "U"
    typeLine = "Creature — Human Wizard"
    power = 2
    toughness = 1
    oracleText = "Prowess (Whenever you cast a noncreature spell, this creature gets +1/+1 until end of turn.)\n{3}{U}, Exile this card from your graveyard: Tap target creature and put a stun counter on it. Activate only as a sorcery. (If a permanent with a stun counter would become untapped, remove one from it instead.)"

    keywords(Keyword.PROWESS)
    activatedAbility {
        cost = Costs.Composite(Costs.Mana("{3}{U}"), Costs.ExileSelf)
        activateFromZone = Zone.GRAVEYARD
        timing = TimingRule.SorcerySpeed
        val creature = target("creature", TargetCreature())
        effect = Effects.Composite(Effects.Tap(creature), Effects.AddCounters(Counters.STUN, 1, creature))
        description = "Tap target creature and put a stun counter on it."
    }

    metadata {
        rarity = Rarity.COMMON
        collectorNumber = "27"
        artist = "Kai Carpenter"
        imageUri = "https://cards.scryfall.io/normal/front/9/b/9ba1f7ce-3404-4932-9795-22967707f762.jpg?1789556701"
        inBooster = false
    }
}
