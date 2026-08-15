package com.wingedsheep.engine.scenarios

import com.wingedsheep.engine.core.ActivateAbility
import com.wingedsheep.engine.mechanics.layers.StateProjector
import com.wingedsheep.engine.support.GameTestDriver
import com.wingedsheep.engine.support.TestCards
import com.wingedsheep.mtg.sets.definitions.lea.cards.ControlMagic
import com.wingedsheep.mtg.sets.definitions.lea.cards.Crusade
import com.wingedsheep.mtg.sets.definitions.lea.cards.Flight
import com.wingedsheep.mtg.sets.definitions.lea.cards.UnholyStrength
import com.wingedsheep.mtg.sets.definitions.lea.cards.WallOfBone
import com.wingedsheep.mtg.sets.definitions.lea.cards.WhiteWard
import com.wingedsheep.sdk.core.Color
import com.wingedsheep.sdk.core.Keyword
import com.wingedsheep.sdk.core.Step
import com.wingedsheep.sdk.model.Deck
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

/**
 * Behavioural net for the mtgish-tooling Aura emitter + self-regeneration emitter (PR #505).
 *
 * The auto-gen drafts compile and capability-match, but `coverage-verify` proves only compile +
 * capabilities, not behaviour. These tests pin the runtime behaviour of every NEW emitter path the
 * Alpha batch introduced — one card per path — so a future emitter/bridge change that subtly breaks
 * the render surfaces as a red test rather than a wrong live card:
 *   - `staticHostBlock` AdjustPT  -> `ModifyStats(p, t)`        (Unholy Strength)
 *   - `staticHostBlock` AddAbility -> `GrantKeyword`            (Flight)
 *   - `staticHostBlock` protection -> `GrantProtection(Color)`  (White Ward — the Alpha Ward cycle)
 *   - `staticHostBlock` SetController -> `ControlEnchantedPermanent` (Control Magic)
 *   - `staticLordBlock`            -> filtered `ModifyStats`     (Crusade)
 *   - `RegeneratePermanent`(self)  -> `RegenerateEffect(EffectTarget.Self)` (Wall of Bone)
 */
class LeaAutogenAuraScenarioTest : FunSpec({

    val projector = StateProjector()

    fun createDriver(): GameTestDriver {
        val driver = GameTestDriver()
        driver.registerCards(TestCards.all)
        driver.registerCard(UnholyStrength)
        driver.registerCard(Flight)
        driver.registerCard(WhiteWard)
        driver.registerCard(ControlMagic)
        driver.registerCard(Crusade)
        driver.registerCard(WallOfBone)
        return driver
    }

    test("Unholy Strength grants the enchanted creature +2/+1") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3
        val aura = driver.putCardInHand(p1, "Unholy Strength")
        driver.giveMana(p1, Color.BLACK, 1)
        driver.castSpell(p1, aura, listOf(creature))
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.getPower(creature) shouldBe 5
        projected.getToughness(creature) shouldBe 4
    }

    test("Flight grants the enchanted creature flying") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        // Centaur Courser has no evasion of its own.
        projector.project(driver.state).hasKeyword(creature, Keyword.FLYING) shouldBe false

        val aura = driver.putCardInHand(p1, "Flight")
        driver.giveMana(p1, Color.BLUE, 1)
        driver.castSpell(p1, aura, listOf(creature))
        driver.bothPass()

        projector.project(driver.state).hasKeyword(creature, Keyword.FLYING) shouldBe true
    }

    test("White Ward grants the enchanted creature protection from white only") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val creature = driver.putCreatureOnBattlefield(p1, "Centaur Courser")
        val aura = driver.putCardInHand(p1, "White Ward")
        driver.giveMana(p1, Color.WHITE, 1)
        driver.castSpell(p1, aura, listOf(creature))
        driver.bothPass()

        val projected = projector.project(driver.state)
        projected.hasKeyword(creature, "PROTECTION_FROM_WHITE") shouldBe true
        projected.hasKeyword(creature, "PROTECTION_FROM_BLACK") shouldBe false
        projected.hasKeyword(creature, "PROTECTION_FROM_BLUE") shouldBe false
    }

    test("Control Magic gives its caster control of the enchanted creature") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Island" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        // The opponent's creature is the one we steal.
        val creature = driver.putCreatureOnBattlefield(p2, "Centaur Courser")
        projector.project(driver.state).getController(creature) shouldBe p2

        val aura = driver.putCardInHand(p1, "Control Magic") // {2}{U}{U}
        driver.giveMana(p1, Color.BLUE, 4)
        driver.castSpell(p1, aura, listOf(creature))
        driver.bothPass()

        projector.project(driver.state).getController(creature) shouldBe p1
    }

    test("Crusade buffs only white creatures") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Plains" to 40))
        val p1 = driver.activePlayer!!
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val whiteCreature = driver.putCreatureOnBattlefield(p1, "Savannah Lions")  // 1/1 white
        val greenCreature = driver.putCreatureOnBattlefield(p1, "Centaur Courser") // 3/3 green
        driver.putPermanentOnBattlefield(p1, "Crusade")

        val projected = projector.project(driver.state)
        // White creature gets +1/+1.
        projected.getPower(whiteCreature) shouldBe 2
        projected.getToughness(whiteCreature) shouldBe 2
        // Green creature is untouched.
        projected.getPower(greenCreature) shouldBe 3
        projected.getToughness(greenCreature) shouldBe 3
    }

    test("Wall of Bone regenerates itself from lethal combat damage") {
        val driver = createDriver()
        driver.initMirrorMatch(deck = Deck.of("Swamp" to 40))
        val p1 = driver.activePlayer!!
        val p2 = driver.getOpponent(p1)
        driver.passPriorityUntil(Step.PRECOMBAT_MAIN)

        val attacker = driver.putCreatureOnBattlefield(p1, "Force of Nature") // 5/5
        val wall = driver.putCreatureOnBattlefield(p2, "Wall of Bone")        // 1/4
        driver.removeSummoningSickness(attacker)

        // p2 raises a regeneration shield via Wall of Bone's own "{B}: Regenerate this" ability.
        driver.passPriority(p1)
        driver.giveMana(p2, Color.BLACK, 1)
        val regenAbilityId = WallOfBone.script.activatedAbilities.first().id
        driver.submit(ActivateAbility(playerId = p2, sourceId = wall, abilityId = regenAbilityId))
            .isSuccess shouldBe true
        driver.bothPass() // resolve the regenerate ability -> shield up (lasts until end of turn)

        // Combat: the 5/5 attacks and the 1/4 Wall blocks, taking lethal (5 > 4) damage.
        driver.passPriorityUntil(Step.DECLARE_ATTACKERS)
        driver.declareAttackers(p1, listOf(attacker), p2).isSuccess shouldBe true
        driver.passPriorityUntil(Step.DECLARE_BLOCKERS)
        driver.declareBlockers(p2, mapOf(wall to listOf(attacker))).isSuccess shouldBe true
        driver.passPriorityUntil(Step.POSTCOMBAT_MAIN)

        // The regeneration shield replaced destruction: the Wall survives and is tapped.
        driver.findPermanent(p2, "Wall of Bone").shouldNotBeNull()
        driver.isTapped(wall) shouldBe true
    }
})
