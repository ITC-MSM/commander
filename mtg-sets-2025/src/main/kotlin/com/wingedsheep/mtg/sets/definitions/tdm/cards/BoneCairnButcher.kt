package com.wingedsheep.mtg.sets.definitions.tdm.cards

import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.dsl.card
import com.wingedsheep.sdk.dsl.mobilize
import com.wingedsheep.sdk.model.Rarity
import com.wingedsheep.sdk.scripting.GrantKeyword
import com.wingedsheep.sdk.scripting.GameObjectFilter
import com.wingedsheep.sdk.scripting.filters.unified.GroupFilter

/**
 * Bone-Cairn Butcher — Tarkir: Dragonstorm #173
 * {1}{R}{W}{B} · Creature — Demon · 4/4
 *
 * Mobilize 2 (Whenever this creature attacks, create two tapped and attacking 1/1 red Warrior
 * creature tokens. Sacrifice them at the beginning of the next end step.)
 * Attacking tokens you control have deathtouch.
 *
 * The `mobilize(n)` builder helper adds the display-only "Mobilize 2" keyword ability plus the
 * attack-triggered ability that creates two tapped-and-attacking Warrior tokens and schedules
 * their sacrifice at the next end step. The deathtouch clause is a continuous static
 * [GrantKeyword] over the group of attacking tokens you control — since only creatures attack,
 * the group is constrained to creature tokens you control that are attacking, evaluated each time
 * the static is applied so the freshly mobilized Warriors gain deathtouch for that combat.
 */
val BoneCairnButcher = card("Bone-Cairn Butcher") {
    manaCost = "{1}{R}{W}{B}"
    colorIdentity = "RWB"
    typeLine = "Creature — Demon"
    power = 4
    toughness = 4
    oracleText = "Mobilize 2 (Whenever this creature attacks, create two tapped and attacking 1/1 red Warrior creature tokens. Sacrifice them at the beginning of the next end step.)\n" +
        "Attacking tokens you control have deathtouch."

    mobilize(2)

    staticAbility {
        ability = GrantKeyword(
            Keyword.DEATHTOUCH,
            GroupFilter((GameObjectFilter.Creature and GameObjectFilter.Token).youControl()).attacking()
        )
    }

    metadata {
        rarity = Rarity.UNCOMMON
        collectorNumber = "173"
        artist = "David Palumbo"
        imageUri = "https://cards.scryfall.io/normal/front/7/8/78bf36bc-6702-4c5d-b52d-ab7217cc8787.jpg?1743204667"
    }
}
