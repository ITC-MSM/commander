package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.support.ScenarioTestBase
import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe

/** Shared state-based harness; concrete specs remain one card per file. */
abstract class HobAutomaticCardScenarioTest(cardName: String) : ScenarioTestBase() {
    private data class Shape(val spell: Boolean = false, val triggers: Int = 0, val activated: Int = 0, val statics: Int = 0, val equip: Boolean = false)

    private val expected = mapOf(
        "Attercop" to Shape(triggers=1), "Bilbo's Deadly Slice" to Shape(spell=true),
        "Confusticate and Bebother" to Shape(spell=true), "Crude Bent Blade" to Shape(triggers=1,activated=1,statics=1,equip=true),
        "Desolation Prowler" to Shape(activated=1), "Dwarven Provisioner" to Shape(activated=1),
        "Elven Raft-Steerer" to Shape(triggers=1), "Elvenking's Harper" to Shape(activated=1),
        "Fateful Discovery" to Shape(triggers=1), "Front Porch Sentries" to Shape(triggers=1),
        "Giant's Boulder" to Shape(triggers=1,activated=2), "Gnashing of Teeth" to Shape(spell=true),
        "Guardian of the Halls" to Shape(activated=1), "Hobbit Hole" to Shape(activated=1), "Large Bear" to Shape(),
        "Magnificent End" to Shape(spell=true,statics=1), "Mirkwood Pathmaker" to Shape(),
        "Nasty Little Rabbit" to Shape(triggers=1), "Nighthowl Pursuer" to Shape(triggers=1),
        "Nori, Teller of Tales" to Shape(triggers=1), "Old Fat Spider" to Shape(triggers=1,statics=1),
        "Old Thrush" to Shape(triggers=1), "Ordinary Bear" to Shape(), "Quarrel" to Shape(spell=true),
        "Ragged Short Spear" to Shape(triggers=1,activated=1,statics=1,equip=true), "Ravening Warg" to Shape(triggers=1),
        "Reverent Howl" to Shape(spell=true), "Smaug's Fury" to Shape(spell=true), "Stony-Voiced Goblins" to Shape(triggers=1),
        "Thorin's Last Stand" to Shape(spell=true), "Thrór's Map" to Shape(triggers=1,activated=1),
        "Troll Negotiations" to Shape(spell=true), "Uneasy Partings" to Shape(spell=true,statics=1),
        "Warg Tactics" to Shape(spell=true), "Wargling" to Shape(triggers=1),
        "Well-Worn Spatula" to Shape(triggers=1,activated=1,statics=1,equip=true), "Wilderland Scrounger" to Shape(triggers=1),
    ).getValue(cardName)

    init {
        test("$cardName exposes its rules behavior to the engine") {
            val card = cardRegistry.requireCard(cardName)
            withClue("spell behavior") { (card.spellEffect != null) shouldBe expected.spell }
            withClue("trigger behavior") { card.triggeredAbilities.size shouldBe expected.triggers }
            withClue("activated behavior") { card.activatedAbilities.size shouldBe expected.activated }
            withClue("continuous behavior") { card.staticAbilities.size shouldBe expected.statics }
            withClue("equip behavior") { (card.equipCost != null) shouldBe expected.equip }
            if (card.typeLine.isPermanent) {
                val game = scenario().withPlayers().withCardOnBattlefield(1, cardName)
                    .withCardInLibrary(1, "Forest").withCardInLibrary(2, "Forest").build()
                val id = game.findPermanent(cardName)
                withClue("permanent is represented in projected battlefield state") {
                    (id != null) shouldBe true
                    (game.state.projectedState.getProjectedValues(id!!) != null) shouldBe true
                }
                card.creatureStats?.takeUnless { it.isDynamic }?.let { stats ->
                    withClue("creature characteristics apply through projection") {
                        game.state.projectedState.getPower(id!!) shouldBe stats.basePower
                        game.state.projectedState.getToughness(id) shouldBe stats.baseToughness
                    }
                }
            }
        }
    }
}
